package edu.scu.zhongruan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.scu.zhongruan.dao.PatientDao;
import edu.scu.zhongruan.entity.PatientEntity;
import edu.scu.zhongruan.service.PatientService;
import edu.scu.zhongruan.utils.PageUtils;
import edu.scu.zhongruan.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;


@Service("patientService")
public class PatientServiceImpl extends ServiceImpl<PatientDao, PatientEntity> implements PatientService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PatientEntity> page = this.page(
                new Query<PatientEntity>().getPage(params),
                new QueryWrapper<PatientEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 新建病人
     * @param patient
     */
    @Override
    public void add(PatientEntity patient) {
        //校验病人是否已经存在
        PatientEntity entity = baseMapper.selectById(patient.getId());
        if(Objects.nonNull(entity)){
            throw new IllegalArgumentException("病人已经存在");
        }
        patient.setLastModify(new Date());
        //添加到数据库
        baseMapper.insert(patient);
    }

}