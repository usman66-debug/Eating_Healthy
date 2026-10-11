package com.ndedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ndedu.entity.FoodRecipeItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FoodRecipeItemMapper extends BaseMapper<FoodRecipeItem> {
    @Select("select ri.*, fi.food_name, fi.calories, fi.protein, fi.carbohydrate, fi.fat from food_recipe_item ri "+
            "left join food_info fi on ri.food_id = fi.id "+
            "where ri.recipe_id = #{id}")
    List<FoodRecipeItem> selectRecipeItems(Long id);
}
