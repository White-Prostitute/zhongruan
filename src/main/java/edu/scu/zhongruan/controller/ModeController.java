package edu.scu.zhongruan.controller;

import java.util.Arrays;
import java.util.Map;

import edu.scu.zhongruan.utils.PageUtils;
import edu.scu.zhongruan.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.scu.zhongruan.entity.ModelEntity;
import edu.scu.zhongruan.service.ModeService;



/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-06-10 14:56:17
 */
@RestController
@RequestMapping("zhongruan/mode")
public class ModeController {
    @Autowired
    private ModeService modeService;

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
