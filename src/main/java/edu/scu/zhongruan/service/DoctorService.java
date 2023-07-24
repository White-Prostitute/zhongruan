package edu.scu.zhongruan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.scu.zhongruan.entity.DoctorEntity;
import edu.scu.zhongruan.utils.PageUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-06-10 14:56:17
 */
public interface DoctorService extends IService<DoctorEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(DoctorEntity entity) throws IllegalAccessException;

    String login(DoctorEntity entity);

    void uploadAvatar(MultipartFile avatar, String account) throws IOException;
}

