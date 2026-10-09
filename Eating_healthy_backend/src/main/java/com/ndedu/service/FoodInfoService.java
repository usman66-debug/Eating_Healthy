package com.ndedu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.entity.FoodCategory;
import com.ndedu.entity.FoodInfo;
import com.ndedu.mapper.FoodCategoryMapper;
import com.ndedu.mapper.FoodInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class FoodInfoService {
    @Autowired
    private FoodInfoMapper foodInfoMapper;

    @Autowired
    private FoodCategoryMapper foodCategoryMapper;

    public Page<FoodInfo> list(Page<FoodInfo> page, String keyword, String categoryId){
        LambdaQueryWrapper<FoodInfo> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.hasText(keyword)){
            queryWrapper.like(FoodInfo::getFoodName, keyword);
        }

        if(categoryId != null){
            queryWrapper.eq(FoodInfo::getCategoryId, Long.parseLong(categoryId));
        }
        queryWrapper.orderByAsc(FoodInfo::getId);

        Page<FoodInfo> result = foodInfoMapper.selectPage(page, queryWrapper);
        result.getRecords().forEach(this::fillCateGoryName);
        return result;
    }



    private void fillCateGoryName(FoodInfo foodInfo){
        if (foodInfo.getCategoryId() != null) {
            FoodCategory category = foodCategoryMapper.selectById(foodInfo.getCategoryId());
            if(category != null){
                foodInfo.setCategoryName(category.getCategoryName());
            }
        }
    };

}
