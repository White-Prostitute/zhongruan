package edu.scu.zhongruan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.scu.zhongruan.controller.dto.QueryPatientDto;
import edu.scu.zhongruan.controller.dto.QueryTaskDto;
import edu.scu.zhongruan.controller.request.QueryPatientRequest;
import edu.scu.zhongruan.dao.PatientDao;
import edu.scu.zhongruan.entity.PatientEntity;
import edu.scu.zhongruan.service.PatientService;
import edu.scu.zhongruan.utils.PageUtils;
import edu.scu.zhongruan.utils.Query;
import edu.scu.zhongruan.utils.ValidateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service("patientService")
public class PatientServiceImpl extends ServiceImpl<PatientDao, PatientEntity> implements PatientService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PatientEntity> page = this.page(
                new Query<PatientEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 新建病人
     * @param patient
     */
    @Override
    public void add(PatientEntity patient) {
        //校验身份证有效性
        if(!ValidateUtil.isValidIdentificationNumber(patient.getId())){
            throw new IllegalArgumentException("提供的身份号码无效");
        }
        //校验病人是否已经存在
        PatientEntity entity = baseMapper.selectById(patient.getId());
        if(Objects.nonNull(entity)){
            throw new IllegalArgumentException("病人已经存在");
        }
        patient.setLastModify(new Date());
        //添加到数据库
        baseMapper.insert(patient);
    }

    //分页查询病人
    @Override
    public QueryPatientDto queryPatient(QueryPatientRequest request) {
        if(Objects.isNull(request)){
            throw new IllegalArgumentException("参数异常");
        }
        QueryWrapper<PatientEntity> wrapper = new QueryWrapper<>();
        //添加查询条件
        QueryPatientRequest.Filter filter = request.getFilter();
        if(Objects.nonNull(filter)){
            if(Objects.nonNull(filter.getName())){
                wrapper.like("name", filter.getName());
            }
            if(Objects.nonNull(filter.getAddress())){
                wrapper.like("address", filter.getAddress());
            }
            if(Objects.nonNull(filter.getSex())){
                wrapper.eq("sex", filter.getSex());
            }
        }
        //TODO 分页查询优化
        Page<PatientEntity> page = new Page<>(request.getPageIndex(), request.getPageSize());
        baseMapper.selectPage(page, wrapper);
        int count = baseMapper.selectCount(wrapper);
        List<PatientEntity> records = page.getRecords();
        QueryPatientDto dto = new QueryPatientDto();
        dto.setData(records);
        dto.setCount(records.size());
        dto.setTotal(count);
        return dto;
    }

}