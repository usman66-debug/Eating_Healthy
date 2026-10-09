package com.ndedu.controller;


import com.ndedu.common.Result;
import com.ndedu.entity.FoodCategory;
import com.ndedu.service.FoodCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
