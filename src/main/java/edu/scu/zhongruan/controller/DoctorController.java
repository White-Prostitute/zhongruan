package edu.scu.zhongruan.controller;

import edu.scu.zhongruan.entity.DoctorEntity;
import edu.scu.zhongruan.service.DoctorService;
import edu.scu.zhongruan.utils.PageUtils;
import edu.scu.zhongruan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Map;


/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-06-10 14:56:17
 */
@Slf4j
@RestController
@RequestMapping("zhongruan/doctor")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;

    /**
     * 医生注册
     * @param entity 实体类
     * @return 操作成功与否
     */
    @PostMapping("/register")
    public R register(@RequestBody DoctorEntity entity){
        try{
            doctorService.register(entity);
        }catch (Exception e){
            log.error("注册失败", e);
            return R.error(e);
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody DoctorEntity entity){
        String token = "";
        try{
            token = doctorService.login(entity);
        } catch (Exception e){
            return R.error(e);
        }
        return R.ok().put("token", token);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = doctorService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{name}")
    public R info(@PathVariable("name") String name){
        DoctorEntity doctor;
        try{
            doctor = doctorService.getById(name);
        }catch (Exception e){
            return R.error(e);
        }
        return R.ok().put("doctor", doctor);
    }

    /**
     * 注册账号
     */
    @RequestMapping("/register")
    public R save(@RequestBody DoctorEntity doctor){
		doctorService.save(doctor);
        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public R update(@RequestBody DoctorEntity doctor){
        try{
            doctorService.updateById(doctor);
        }catch (Exception e){
            log.error("修改医生信息错误", e);
            return R.error(e);
        }
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody String[] names){
		doctorService.removeByIds(Arrays.asList(names));
        return R.ok();
    }

    @PutMapping("/avatar")
    public R uploadAvatar(MultipartFile avatar, String account){
        try{
            log.info("上传头像 size : {}  account : {}", avatar.getSize(), account);
            doctorService.uploadAvatar(avatar, account);
        }catch (Exception e){
            log.error("上传头像错误", e);
            return R.error(e);
        }
        return R.ok();
    }


}
