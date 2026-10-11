package com.ndedu.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.Exception.BussinessException;
import com.ndedu.entity.FoodRecipe;
import com.ndedu.entity.FoodRecipeItem;
import com.ndedu.mapper.FoodRecipeItemMapper;
import com.ndedu.mapper.FoodRecipeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class FoodRecipeService {
    @Autowired
    private FoodRecipeMapper foodRecipeMapper;
    @Autowired
    private FoodRecipeItemMapper foodRecipeItemMapper;

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

    public void addRecipe(FoodRecipe foodRecipe){
        //名称唯一性校验
        long count = foodRecipeMapper.selectCount(new LambdaQueryWrapper<FoodRecipe>()
                .eq(FoodRecipe::getRecipeName, foodRecipe.getRecipeName()));
        if(count > 0){
            throw new BussinessException("食谱名称已存在",400);
        }
        foodRecipeMapper.insert(foodRecipe);
        //批量保存食材关联
        addRecipeItems(foodRecipe.getId(), foodRecipe.getItems());
    }

    //批量保存食材关联
    public void addRecipeItems(Long recipeId, List<FoodRecipeItem> recipeItems){
        if(recipeItems != null && !recipeItems.isEmpty()){
            for(FoodRecipeItem item : recipeItems){
                item.setId(null);
                item.setRecipeId(recipeId);
                foodRecipeItemMapper.insert(item);
            }
        }
    }

    public void updateRecipe(FoodRecipe foodRecipe){
        //名称唯一性校验
        long count = foodRecipeMapper.selectCount(new LambdaQueryWrapper<FoodRecipe>()
                .eq(FoodRecipe::getRecipeName, foodRecipe.getRecipeName())
                .ne(FoodRecipe::getId, foodRecipe.getId()));
        if(count > 0){
            throw new BussinessException("食谱名称已存在",400);
        }
        foodRecipeMapper.updateById(foodRecipe);
        foodRecipeItemMapper.delete(new LambdaQueryWrapper<FoodRecipeItem>()
                .eq(FoodRecipeItem::getRecipeId, foodRecipe.getId()));
        addRecipeItems(foodRecipe.getId(), foodRecipe.getItems());
    }

    public void deleteRecipe(Long id){
        foodRecipeMapper.deleteById(id);
        foodRecipeItemMapper.delete(new LambdaQueryWrapper<FoodRecipeItem>()
                .eq(FoodRecipeItem::getRecipeId, id));
    }

    public FoodRecipe getRecipe(Long id){
        FoodRecipe recipe = foodRecipeMapper.selectById(id);
        if(recipe == null){
            throw new BussinessException("食谱不存在",404);
        }
        //查询食材关联
        List<FoodRecipeItem> items = foodRecipeItemMapper.selectRecipeItems(id);
        recipe.setItems(items);

        //计算总热量
        BigDecimal totalCalories = BigDecimal.ZERO;
        for(FoodRecipeItem item : items){
            if(item.getCalories() != null && item.getAmount() != null){
                BigDecimal amountCalories = item.getAmount().divide(BigDecimal.valueOf(100),4,RoundingMode.HALF_UP);
                totalCalories = totalCalories.add(item.getCalories().multiply(amountCalories));
            }
        }
        recipe.setTotalCalories(totalCalories.setScale(2, RoundingMode.HALF_UP));
        return recipe;
    }
}
