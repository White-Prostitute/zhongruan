package edu.scu.zhongruan.controller.dto;

import edu.scu.zhongruan.vo.TaskVo;
import lombok.Data;

import java.util.List;

//相应前端的任务查询结果
@Data
public class QueryTaskDto {
    //数据总数
    Integer total;

    //实际获取到数据量
    Integer count;

    //数据
    List<TaskVo> data;
}
