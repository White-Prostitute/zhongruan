package edu.scu.zhongruan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.scu.zhongruan.controller.dto.QueryPatientDto;
import edu.scu.zhongruan.controller.request.QueryPatientRequest;
import edu.scu.zhongruan.entity.PatientEntity;
import edu.scu.zhongruan.utils.PageUtils;

import java.util.Map;

/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-06-10 14:56:17
 */
public interface PatientService extends IService<PatientEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void add(PatientEntity patient);

    QueryPatientDto queryPatient(QueryPatientRequest request);
}

