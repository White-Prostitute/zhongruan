package edu.scu.zhongruan.service.impl;

import edu.scu.zhongruan.utils.PageUtils;
import edu.scu.zhongruan.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import edu.scu.zhongruan.dao.ModeDao;
import edu.scu.zhongruan.entity.ModelEntity;
import edu.scu.zhongruan.service.ModeService;


@Service("modeService")
public class ModeServiceImpl extends ServiceImpl<ModeDao, ModelEntity> implements ModeService {

    @Override
    public List<ModelEntity> allModel() {
        return baseMapper.selectList(null);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ModelEntity> page = this.page(
                new Query<ModelEntity>().getPage(params),
                new QueryWrapper<ModelEntity>()
        );

        return new PageUtils(page);
    }

}