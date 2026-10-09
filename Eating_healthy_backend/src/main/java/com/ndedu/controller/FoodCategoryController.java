package com.ndedu.controller;


import com.ndedu.common.Result;
import com.ndedu.entity.FoodCategory;
import com.ndedu.service.FoodCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food/category")
public class FoodCategoryController {
    @Autowired
    private FoodCategoryService foodCategoryService;

    @GetMapping("/tree")
    public Result<List<FoodCategory>> tree() {
        List<FoodCategory> tree = foodCategoryService.tree();
        return Result.ok(tree);
    }

    @PostMapping
    public Result<?> add(@RequestBody FoodCategory category){
        foodCategoryService.addCategory(category);
        return Result.ok("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody FoodCategory category){
        foodCategoryService.updateCategory(category);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        foodCategoryService.deleteCategory(id);
        return Result.ok("删除成功");
    }
}
