package edu.scu.zhongruan.controller;

import edu.scu.zhongruan.controller.dto.QueryTaskDto;
import edu.scu.zhongruan.controller.dto.TaskCompleteDto;
import edu.scu.zhongruan.controller.dto.TaskPostDataDto;
import edu.scu.zhongruan.controller.request.QueryTaskRequest;
import edu.scu.zhongruan.entity.DoctorEntity;
import edu.scu.zhongruan.entity.TaskEntity;
import edu.scu.zhongruan.service.TaskService;
import edu.scu.zhongruan.utils.AliOss;
import edu.scu.zhongruan.utils.R;
import edu.scu.zhongruan.utils.UsuUtil;
import edu.scu.zhongruan.vo.TaskVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-06-10 14:56:17
 */
@Slf4j
@RestController
@RequestMapping("zhongruan/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(DoctorEntity entity){
        List<TaskVo> list;
        try{
            list = taskService.allTask(entity);
        }catch (Exception e){
            log.error("获取任务列表失败", e);
            return Objects.requireNonNull(R.error().put("exception", e.toString())).put("msg", e.getMessage());
        }
        return R.ok().put("data", list);
    }

    @PostMapping("/page")
    public R queryPage(@RequestBody QueryTaskRequest request){
        QueryTaskDto dto;
        try{
            dto = taskService.queryTask(request);
        }catch (Exception e){
            log.error("分页查询任务失败", e);
            return Objects.requireNonNull(R.error().put("exception", e.toString())).put("msg", e.getMessage());
        }
        return R.ok().put("data", dto);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") String id){
		TaskEntity task = taskService.getById(id);
        return R.ok().put("task", task);
    }


    /**
     * 上传文件，开始任务
     * @param files 文件
     * @return
     */
    @PostMapping("/submit")
    public R save( TaskEntity entity, @RequestBody MultipartFile[] files) {
        try{
            taskService.newTask(entity, files);
        }catch (Exception e){
            log.error("前端上传文件失败", e);
            return Objects.requireNonNull(R.error().put("exception", e.toString())).put("msg", e.getMessage());
        }
        return R.ok();
    }

    /**
     * 任务结束，拿到处理完的文件
     * @return 操作成功与否
     */
    @PostMapping("/py/complete")
    public R completeTask(@RequestBody TaskCompleteDto dto) {
        try{
            taskService.completeTask(dto);
        }catch (Exception e){
            log.error("python端文件回传失败", e);
            return Objects.requireNonNull(R.error().put("exception", e.toString())).put("msg", e.getMessage());
        }
        return R.ok();
    }

    /**
     * python回传处理数据
     * @return
     */
    @PostMapping("/py/complete/data")
    public R postTaskData(@RequestBody TaskPostDataDto dto){
        try{
            taskService.taskPostData(dto);
        }catch (Exception e) {
            log.error("python回传处理数失败");
            return Objects.requireNonNull(R.error().put("exception", e.toString())).put("msg", e.getMessage());
        }
        return R.ok();
    }

    /**
     * 获取任务处理前的源文件
     */
    @GetMapping("/file/origin")
    public void getOriginTaskFile(TaskEntity entity, HttpServletResponse response) throws IOException {
        String id = entity.getId();//任务Id，也是文件名
        String type = entity.getFileType();
        String fileName = id + "_origin." + type;
        InputStream file = AliOss.getFile(fileName);
        ServletOutputStream outputStream = response.getOutputStream();
        UsuUtil.transferStreamData(file, outputStream);
    }

    /**
     * 获取处理完成的文件
     * @param response
     */
    @GetMapping("/file/complete")
    public void getCompleteTaskFile(TaskEntity entity, HttpServletResponse response) throws IOException {
        String id = entity.getId();//任务Id，也是文件名
        String type = entity.getFileType();
        String fileName = id + "_complete." + type;
        InputStream file = AliOss.getFile(fileName);
        ServletOutputStream outputStream = response.getOutputStream();
        UsuUtil.transferStreamData(file, outputStream);
    }

    /**
     * 医生书写医嘱
     * @param entity
     * @return
     */
    @PutMapping("/adviceOrNotes")
    public R writeAdvice(@RequestBody TaskEntity entity){
        try{
            taskService.writeAdviceOrNotes(entity);
        }catch (IllegalArgumentException e){
            return R.error("参数有误").put("exception", e.toString());
        }catch (Exception e){
            return R.error().put("exception", e.toString());
        }
        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Map<String, Object> map){
        try{
            List<String> ids = (List<String>) map.get("ids");
            taskService.removeByIds(ids);
        }catch (Exception e){
            log.error("删除失败", e);
            return Objects.requireNonNull(R.error().put("exception", e.toString())).put("msg", e.getMessage());
        }
        return R.ok();
    }

}
