package edu.scu.zhongruan.controller.dto;

import edu.scu.zhongruan.entity.PatientEntity;
import lombok.Data;

import java.util.List;

@Data
public class QueryPatientDto {

    //数据总数
    Integer total;

    //实际获取到数据量
    Integer count;

    //数据
    List<PatientEntity> data;
}
