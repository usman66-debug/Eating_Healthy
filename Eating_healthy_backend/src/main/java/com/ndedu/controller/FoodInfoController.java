package com.ndedu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.common.Result;
import com.ndedu.entity.FoodCategory;
import com.ndedu.entity.FoodInfo;
import com.ndedu.mapper.FoodCategoryMapper;
import com.ndedu.service.FoodInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public Result add(@RequestBody FoodInfo food){
        foodInfoService.addFood(food);
        return Result.ok("添加成功");
    }

    @PutMapping
    public Result update(@RequestBody FoodInfo food){
        foodInfoService.updateFood(food);
        return Result.ok("更新成功");
    }

    @GetMapping("/{id}")
    public Result<FoodInfo> detail(@PathVariable Long id){
        FoodInfo foodInfo = foodInfoService.getDetail(id);
        return Result.ok(foodInfo);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        foodInfoService.deleteById(id);
        return Result.ok("删除成功");
    }
}
