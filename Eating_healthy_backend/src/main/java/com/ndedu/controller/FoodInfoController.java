package com.ndedu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.common.Result;
import com.ndedu.entity.FoodCategory;
import com.ndedu.entity.FoodInfo;
import com.ndedu.mapper.FoodCategoryMapper;
import com.ndedu.service.FoodInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/food/info")
public class FoodInfoController {
    @Autowired
    private FoodInfoService foodInfoService;

    @GetMapping("/list")
    public Result<Page<FoodInfo>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String keyword
    ){
        Page<FoodInfo> page = new Page<>(pageNum, pageSize);
        Page<FoodInfo> result = foodInfoService.list(page, keyword, categoryId);
        return Result.ok(result);
    }


}
