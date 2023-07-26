package edu.scu.zhongruan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.scu.zhongruan.config.ConstantConfig;
import edu.scu.zhongruan.controller.dto.QueryTaskDto;
import edu.scu.zhongruan.controller.dto.TaskCompleteDto;
import edu.scu.zhongruan.controller.dto.TaskPostDataDto;
import edu.scu.zhongruan.controller.request.QueryTaskRequest;
import edu.scu.zhongruan.dao.DoctorDao;
import edu.scu.zhongruan.dao.PatientDao;
import edu.scu.zhongruan.dao.TaskDao;
import edu.scu.zhongruan.entity.DoctorEntity;
import edu.scu.zhongruan.entity.PatientEntity;
import edu.scu.zhongruan.entity.TaskEntity;
import edu.scu.zhongruan.enums.TaskStatusEnum;
import edu.scu.zhongruan.service.TaskService;
import edu.scu.zhongruan.utils.*;
import edu.scu.zhongruan.vo.TaskVo;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
@Service("taskService")
public class TaskServiceImpl extends ServiceImpl<TaskDao, TaskEntity> implements TaskService {

    @Resource
    RedisTemplate<String, String> template;

    @Resource
    DoctorDao doctorDao;

    @Resource
    PatientDao patientDao;

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
                        // TODO python端回传文件相应标准化，进行失败重传
                    }catch (Exception e){
                        //设置任务状态为异常
                        TaskEntity task = baseMapper.selectById(uuid.toString());
                        task.setStatus(2);
                        baseMapper.updateById(task);
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
     * 任务完成,python端回传文件
     * 上传到OSS的文件名:uuid_complete.type
     */
    @Override
    public void completeTask(TaskCompleteDto dto) throws Exception {
        log.info("收到python端回传文件,任务id{}", dto.getId());
        //更新数据库中的任务信息
        TaskEntity entity = baseMapper.selectById(dto.getId());
        if(Objects.isNull(entity)){
            throw new IllegalArgumentException("id为" + dto.getId() + "的任务不存在");
        }
        if(entity.getStatus() == TaskStatusEnum.COMPLETE.getCode()){
            throw new IllegalArgumentException("任务id为" + dto.getId() + "的任务已经完成，请勿重复回传数据");
        }
        entity.setEndTime(new Date());
        int costTime = (int) (entity.getEndTime().getTime() - entity.getBeginTime().getTime());
        entity.setCostTime(costTime);
        //设置状态
        if(entity.getStatus() == TaskStatusEnum.RECEIVING_RESPONSE.getCode()){
            entity.setStatus(TaskStatusEnum.COMPLETE.getCode());
        }else{
            entity.setStatus(TaskStatusEnum.RECEIVING_RESPONSE.getCode());
        }
        //删除redis中的任务id
        SetOperations<String, String> ops = template.opsForSet();
        ops.remove("running_task", dto.getId());
        //将处理完成的文件上传到OSS
        String fileName = dto.getId() + "_complete."+dto.getFileType();
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
    public List<TaskVo> allTask(DoctorEntity entity) {
        QueryWrapper<TaskEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("creator_id", entity.getAccount());
        List<TaskEntity> list = baseMapper.selectList(wrapper);
        return buildFromEntityList(list);
    }

    /**
     * 分页查询任务信息
     */
    @Override
    public QueryTaskDto queryTask(QueryTaskRequest request) {
        if(Objects.isNull(request)){
            throw new IllegalArgumentException("参数异常");
        }
        //TODO 后期考虑添加缓存
        QueryWrapper<TaskEntity> wrapper = new QueryWrapper<>();
        QueryTaskRequest.Filter filter = request.getFilter();
        if(Objects.nonNull(filter)){
            //添加查询条件
            if(Objects.nonNull(filter.getModeType())){
                wrapper.eq("model_type", filter.getModeType());
            }
            if(Objects.nonNull(filter.getCreatorName())){
                QueryWrapper<DoctorEntity> doctorEntityQueryWrapper = new QueryWrapper<>();
                doctorEntityQueryWrapper.like("name", filter.getCreatorName());
                List<DoctorEntity> doctorEntities = doctorDao.selectList(doctorEntityQueryWrapper);
                List<String> doctorIds = doctorEntities.stream()
                        .map(DoctorEntity::getAccount)
                        .collect(Collectors.toList());
                wrapper.in("creator_id", doctorIds);
            }else if(Objects.nonNull(filter.getCreatorId())){
                wrapper.like("creator_id", filter.getCreatorId());
            }
        }
        Page<TaskEntity> page = new Page<>(request.getPageIndex(), request.getPageSize());
        //TODO 分页查询优化
        baseMapper.selectPage(page, wrapper);
        List<TaskEntity> records = page.getRecords();
        int total = baseMapper.selectCount(null);
        //构建vo
        List<TaskVo> taskVos = buildFromEntityList(records);
        QueryTaskDto dto = new QueryTaskDto();
        dto.setTotal(total);
        dto.setCount(taskVos.size());
        dto.setData(taskVos);
        return dto;
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
        if(Objects.isNull(taskEntity)){
            throw new IllegalArgumentException("任务id为" + dto.getId() + "的任务不存在");
        }
        if(taskEntity.getStatus() == TaskStatusEnum.COMPLETE.getCode()){
            throw new IllegalArgumentException("任务id为" + dto.getId() + "的任务已经完成，请勿重复回传数据");
        }
        taskEntity.setMeasurement(JSONObject.toJSONString(dto.getMeasurement()));
        if(taskEntity.getStatus() == TaskStatusEnum.RECEIVING_RESPONSE.getCode()){
            taskEntity.setStatus(TaskStatusEnum.COMPLETE.getCode());
        }else{
            taskEntity.setStatus(TaskStatusEnum.RECEIVING_RESPONSE.getCode());
        }
        baseMapper.updateById(taskEntity);
    }


    //通过entity构建vo
    private List<TaskVo> buildFromEntityList(List<TaskEntity> list){
        List<TaskVo> res = new ArrayList<>();
        if(Objects.nonNull(list)){
            list.forEach(o->{
                TaskVo vo = new TaskVo();
                //构建vo
                vo.buildFromEntity(o);
                DoctorEntity doctorEntity = doctorDao.selectById(o.getCreatorId());
                PatientEntity patientEntity = patientDao.selectById(o.getPatientId());
                vo.setDoctor(doctorEntity);
                vo.setPatient(patientEntity);
                res.add(vo);
            });
        }
        return res;
    }

}