package edu.scu.zhongruan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.scu.zhongruan.controller.dto.QueryTaskDto;
import edu.scu.zhongruan.controller.dto.TaskCompleteDto;
import edu.scu.zhongruan.controller.dto.TaskPostDataDto;
import edu.scu.zhongruan.controller.request.QueryTaskRequest;
import edu.scu.zhongruan.entity.DoctorEntity;
import edu.scu.zhongruan.entity.TaskEntity;
import edu.scu.zhongruan.utils.PageUtils;
import edu.scu.zhongruan.vo.TaskVo;
import org.codehaus.jettison.json.JSONException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-06-10 14:56:17
 */
public interface TaskService extends IService<TaskEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void newTask(TaskEntity entity, MultipartFile[] files) throws IOException, JSONException;

    void completeTask(TaskCompleteDto dto) throws Exception;

    List<TaskVo> allTask(DoctorEntity entity);

    QueryTaskDto queryTask(QueryTaskRequest request);

    void writeAdviceOrNotes(TaskEntity entity);

    void taskPostData(TaskPostDataDto dto);

    void deleteTask(List<String> ids);
}

