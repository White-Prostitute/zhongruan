package edu.scu.zhongruan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.scu.zhongruan.entity.ModelEntity;
import edu.scu.zhongruan.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-06-10 14:56:17
 */
public interface ModeService extends IService<ModelEntity> {

    List<ModelEntity> allModel();

    PageUtils queryPage(Map<String, Object> params);
}

