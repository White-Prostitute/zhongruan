package edu.scu.zhongruan.controller;

import java.util.*;

import edu.scu.zhongruan.utils.PageUtils;
import edu.scu.zhongruan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import edu.scu.zhongruan.entity.ModelEntity;
import edu.scu.zhongruan.service.ModeService;



/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-06-10 14:56:17
 */
@Slf4j
@RestController
@RequestMapping("zhongruan/model")
public class ModelController {
    @Autowired
    private ModeService modeService;

    @GetMapping("/all")
    public R allModel(){
        List<ModelEntity> modelEntities;
        try{
             modelEntities = modeService.allModel();
        }catch (Exception e){
            log.error("获取模型列表失败", e);
            return Objects.requireNonNull(R.error().put("exception", e.toString())).put("msg", e.getMessage());
        }
        return R.ok().put("data", modelEntities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = modeService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id){
		ModelEntity mode = modeService.getById(id);

        return R.ok().put("mode", mode);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ModelEntity mode){
		modeService.save(mode);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody ModelEntity mode){
		modeService.updateById(mode);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
		modeService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
