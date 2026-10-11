package com.ndedu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.common.Result;
import com.ndedu.entity.FoodRecipe;
import com.ndedu.service.FoodRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/food/recipe")
public class FoodRecipeController {
    @Autowired
    private FoodRecipeService foodRecipeService;
    @GetMapping("/list")
    public Result<Page<FoodRecipe>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String mealType
       ){
        Page<FoodRecipe> page = new Page<>(pageNum, pageSize);
        Page<FoodRecipe> recipes = foodRecipeService.listRecipes(page, keyword, mealType);
        return Result.ok(recipes);
    }

    @PostMapping
    public Result<?> add(@RequestBody FoodRecipe foodRecipe){
        foodRecipeService.addRecipe(foodRecipe);
        return Result.ok("添加成功");
    }
}
