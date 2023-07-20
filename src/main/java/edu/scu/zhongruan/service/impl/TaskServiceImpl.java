package edu.scu.zhongruan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.scu.zhongruan.config.ConstantConfig;
import edu.scu.zhongruan.controller.dto.TaskCompleteDto;
import edu.scu.zhongruan.controller.dto.TaskPostDataDto;
import edu.scu.zhongruan.dao.TaskDao;
import edu.scu.zhongruan.entity.DoctorEntity;
import edu.scu.zhongruan.entity.TaskEntity;
import edu.scu.zhongruan.service.TaskService;
import edu.scu.zhongruan.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service("taskService")
public class TaskServiceImpl extends ServiceImpl<TaskDao, TaskEntity> implements TaskService {

    @Resource
    RedisTemplate<String, String> template;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<TaskEntity> page = this.page(
                new Query<TaskEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 创建新的任务
     * set : running_task
     * 上传到OSS的文件名:uuid_origin.type
     * @param files 文件
     */
    @Override
    public void newTask(TaskEntity entity,  MultipartFile[] files) {
        for (MultipartFile file : files) {
            log.info("接收文件，文件大小{}", file.getSize());
            //为文件生成唯一文件名
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_origin." + entity.getFileType();
            //将原始文件上传到OSS
            try{
                AliOss.upload(file.getInputStream(), fileName);
                //将文件传输到Python端
                CompletableFuture.runAsync(() -> {
                    try{
                        Map<String, String> param = new HashMap<>();
                        param.put("filename", fileName);
                        param.put("id", uuid.toString());
                        String fileDataBase64 = UsuUtil.transferSteamToBase64(file.getInputStream());
                        param.put("fileDataBase64", fileDataBase64);
                        String res = HttpClientUtil.postJson(param, ConstantConfig.PYTHON_URL);
                        log.info("收到Python响应{}, 任务id{}",res, uuid);
                    }catch (Exception e){
                        //TODO 任务异常状态处理
                        log.error("文件上传python端失败", e);
                    }
                });

                //生成任务，记录在redis,表示任务正在进行
                entity.setBeginTime(new Date());
                entity.setId(uuid.toString());
                baseMapper.insert(entity);
                //将任务添加到set集合中,表示正在执行的任务
                SetOperations<String, String> ops = template.opsForSet();
                ops.add("running_task", uuid.toString());
                log.info("新建任务, 任务id {}", uuid);
            }catch (Exception e){
                log.error("新建任务失败", e);
            }

        }
    }


    /**
     * 任务完成
     * 上传到OSS的文件名:uuid_complete.type
     */
    @Override
    public void completeTask(TaskCompleteDto dto) throws Exception {
        log.info("收到python端完成任务请求,任务id{}", dto.getId());
        //更新数据库中的任务信息
        TaskEntity entity = baseMapper.selectById(dto.getId());
        if(Objects.isNull(entity)){
            throw new IllegalArgumentException("id为" + dto.getId() + "的任务不存在");
        }
        entity.setEndTime(new Date());
        int costTime = (int) (entity.getEndTime().getTime() - entity.getBeginTime().getTime());
        entity.setCostTime(costTime);
        //删除redis中的任务id
        SetOperations<String, String> ops = template.opsForSet();
        ops.remove("running_task", dto.getId());
        //将处理完成的文件上传到OSS
        String fileName = dto.getId() + "_complete."+UsuUtil.getFileSuffix(dto.getFileType());
        String fileData = dto.getBase64fileData();
        if(Objects.nonNull(fileData)){
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(fileData);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            AliOss.upload(inputStream, fileName);
        }
        baseMapper.updateById(entity);
    }

    /**
     * 查询所有的任务
     * @param entity 医生
     * @return
     */
    @Override
    public List<TaskEntity> allTask(DoctorEntity entity) {
        QueryWrapper<TaskEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("creator_id", entity.getAccount());
        List<TaskEntity> list = baseMapper.selectList(wrapper);
        list.forEach(o->{//判读任务是否已经完成
            boolean isReceiveFile = o.getEndTime() != null;
            boolean isReceiveData = o.getMeasurementData() != null;
            if(isReceiveData && isReceiveFile){
                o.setStatus(2);
            }else if(isReceiveData || isReceiveFile){
                o.setStatus(1);
            }else{
                o.setStatus(0);
            }
        });
        return list;
    }

    /**
     * 医生书写医嘱或者备注
     * @param entity
     */
    @Override
    public void writeAdviceOrNotes(TaskEntity entity) {
        TaskEntity taskEntity = baseMapper.selectById(entity);
        if(Objects.isNull(taskEntity)){
            throw new IllegalArgumentException("任务不存在");
        }
        if(Objects.nonNull(entity.getAdvice())){
            taskEntity.setAdvice(entity.getAdvice());
        }
        if(Objects.nonNull(entity.getNotes())){
            taskEntity.setNotes(entity.getNotes());
        }
        baseMapper.updateById(taskEntity);
    }

    /**
     * 处理完的数据插入数据库
     * @param dto
     */
    @Override
    public void taskPostData(TaskPostDataDto dto) {
        log.info("收到python端回传的数据{}", JSONObject.toJSONString(dto));
        TaskEntity taskEntity = baseMapper.selectById(dto.getId());
        taskEntity.setMeasurement(JSONObject.toJSONString(dto.getMeasurement()));
        baseMapper.updateById(taskEntity);
    }

}