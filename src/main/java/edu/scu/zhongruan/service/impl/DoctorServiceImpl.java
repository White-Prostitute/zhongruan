package edu.scu.zhongruan.service.impl;

import edu.scu.zhongruan.exception.RepeatAccountException;
import edu.scu.zhongruan.utils.PageUtils;
import edu.scu.zhongruan.utils.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import edu.scu.zhongruan.dao.DoctorDao;
import edu.scu.zhongruan.entity.DoctorEntity;
import edu.scu.zhongruan.service.DoctorService;

import javax.annotation.Resource;


@Service("doctorService")
public class DoctorServiceImpl extends ServiceImpl<DoctorDao, DoctorEntity> implements DoctorService {

    @Resource
    RedisTemplate<String, String> template;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<DoctorEntity> page = this.page(
                new Query<DoctorEntity>().getPage(params),
                new QueryWrapper<DoctorEntity>()
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
        if(doctorEntity != null){//账号重复
            throw new RepeatAccountException();
        }
        //检查密码是否为空
        if(Objects.isNull(entity.getPassword())){
            throw new IllegalArgumentException();
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
            throw new IllegalArgumentException();
        }else{//生成token
            ValueOperations<String, String> ops = template.opsForValue();
            //先检查是否已经登陆过了，是就删除之前生成的token
            if(template.hasKey(entity.getAccount())){
                template.delete(Objects.requireNonNull(ops.get(entity.getAccount())));
            }
            UUID uuid = UUID.randomUUID();
            ops.set(uuid.toString(), entity.getAccount(), Duration.ofMinutes(30));
            ops.set(entity.getAccount(), uuid.toString(), Duration.ofMinutes(30));
            return uuid.toString();
        }
    }

}