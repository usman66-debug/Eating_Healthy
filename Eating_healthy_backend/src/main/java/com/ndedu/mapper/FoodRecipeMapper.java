package com.ndedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ndedu.entity.FoodRecipe;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FoodRecipeMapper extends BaseMapper<FoodRecipe> {
}
