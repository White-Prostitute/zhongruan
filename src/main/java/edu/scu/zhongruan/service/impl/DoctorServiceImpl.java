package edu.scu.zhongruan.service.impl;

import edu.scu.zhongruan.config.ConstantConfig;
import edu.scu.zhongruan.exception.RepeatAccountException;
import edu.scu.zhongruan.utils.AliOss;
import edu.scu.zhongruan.utils.PageUtils;
import edu.scu.zhongruan.utils.Query;
import edu.scu.zhongruan.utils.UsuUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import edu.scu.zhongruan.dao.DoctorDao;
import edu.scu.zhongruan.entity.DoctorEntity;
import edu.scu.zhongruan.service.DoctorService;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


@Service("doctorService")
public class DoctorServiceImpl extends ServiceImpl<DoctorDao, DoctorEntity> implements DoctorService, InitializingBean {

    @Resource
    RedisTemplate<String, String> template;

    private static final Set<String> validAvatarFileType = new HashSet<>();

    @Override
    public void afterPropertiesSet() {
        validAvatarFileType.add("png");
        validAvatarFileType.add("jpeg");
        validAvatarFileType.add("jpg");
        validAvatarFileType.add("bmp");
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<DoctorEntity> page = this.page(
                new Query<DoctorEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 注册
     * @param entity
     */
    @Override
    public void register(DoctorEntity entity) {
        //检查账号是否重复
        DoctorEntity doctorEntity = baseMapper.selectById(entity.getAccount());
        //检查密码是否为空
        if(Objects.isNull(entity.getPassword()) || Objects.isNull(entity.getAccount())){
            throw new IllegalArgumentException("账号或密码为空");
        }
        if(doctorEntity != null){//账号重复
            throw new IllegalArgumentException("账号重复");
        }
        //添加到数据库
        baseMapper.insert(entity);
    }

    /**
     * 医生登录，登录成功，返回token
     * 记住登录状态格式:token:account
     * 防止重复生成token,应该在记录一个键为医生账号的键 格式:account:token
     * @param entity
     * @return token
     */
    @Override
    public String login(DoctorEntity entity) {
        //检查账号密码是否正确
        QueryWrapper<DoctorEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("account", entity.getAccount());
        wrapper.eq("password", entity.getPassword());
        DoctorEntity doctorEntity = baseMapper.selectOne(wrapper);
        if(doctorEntity == null){//账号或者密码错误
            throw new IllegalArgumentException("账号或密码错误");
        }else{//生成token
            ValueOperations<String, String> ops = template.opsForValue();
            //先检查是否已经登陆过了，是就删除之前生成的token
            if(Boolean.TRUE.equals(template.hasKey(entity.getAccount()))){
                template.delete(Objects.requireNonNull(ops.get(entity.getAccount())));
            }
            UUID uuid = UUID.randomUUID();
            ops.set(uuid.toString(), entity.getAccount(), Duration.ofMinutes(30));
            ops.set(entity.getAccount(), uuid.toString(), Duration.ofMinutes(30));
            return uuid.toString();
        }
    }

    //医生上传头像
    @Override
    public void uploadAvatar(MultipartFile avatar, String account) throws IOException {
        if(Objects.isNull(avatar)||Objects.isNull(account)){
            throw new IllegalArgumentException("参数缺失");
        }
        DoctorEntity entity = baseMapper.selectById(account);
        if(Objects.isNull(entity)){
            throw new IllegalArgumentException("账号无效");
        }
        String suffix = UsuUtil.getFileSuffix(avatar.getOriginalFilename());
        if(!validAvatarFileType.contains(suffix)){
            throw new IllegalArgumentException("文件类型无效");
        }
        AliOss.upload(avatar.getInputStream(), account + "." + suffix);
        String avatarUrl = ConstantConfig.OSS_BASE_URL + account + "." + suffix;
        entity.setAvatarUrl(avatarUrl);
        baseMapper.updateById(entity);
    }

}