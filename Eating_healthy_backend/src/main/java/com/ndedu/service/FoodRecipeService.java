package com.ndedu.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.entity.FoodRecipe;
import com.ndedu.mapper.FoodRecipeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FoodRecipeService {
    @Autowired
    private FoodRecipeMapper foodRecipeMapper;

    public Page<FoodRecipe> listRecipes(Page<FoodRecipe> page, String keyword, String mealType){
        LambdaQueryWrapper<FoodRecipe> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.hasText(keyword)){
            queryWrapper.like(FoodRecipe::getRecipeName, keyword);
        }
        if(StringUtils.hasText(mealType)){
            queryWrapper.eq(FoodRecipe::getMealType, mealType);
        }
        queryWrapper.orderByDesc(FoodRecipe::getCreateTime);
        return foodRecipeMapper.selectPage(page, queryWrapper);
    }
}
