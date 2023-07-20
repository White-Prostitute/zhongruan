package edu.scu.zhongruan.controller;

import edu.scu.zhongruan.entity.PatientEntity;
import edu.scu.zhongruan.service.PatientService;
import edu.scu.zhongruan.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-06-10 14:56:17
 */
@RestController
@RequestMapping("zhongruan/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

    /**
     * 获取病人接口
     * @return
     */
    @GetMapping("/list")
    public R list(){
        List<PatientEntity> list = patientService.list();
        return R.ok().put("data", list);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") String id){
		PatientEntity patient = patientService.getById(id);

        return R.ok().put("patient", patient);
    }

    /**
     * 保存
     */
    @PostMapping ("/save")
    public R save(@RequestBody PatientEntity patient){
        try{
            patientService.add(patient);
        }catch (IllegalArgumentException e){
            return R.error("参数有误").put("exception", e.toString());
        }catch (Exception e){
            return R.error().put("exception", e.toString());
        }
        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public R update(@RequestBody PatientEntity patient){
        patient.setLastModify(new Date());
		patientService.updateById(patient);
        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Map<String, Object> map){
        try{
            List<String> ids = (List<String>) map.get("ids");
            patientService.removeByIds(ids);
        }catch (Exception e){
            R.error().put("exception", e.toString());
        }
        return R.ok();
    }

}
