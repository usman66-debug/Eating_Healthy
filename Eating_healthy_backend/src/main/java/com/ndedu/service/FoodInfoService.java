package com.ndedu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.Exception.BussinessException;
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

    public void addFood(FoodInfo food){
        long count = foodInfoMapper.selectCount(new LambdaQueryWrapper<FoodInfo>().eq(FoodInfo::getFoodName, food.getFoodName()));
        if(count > 0){
            throw new BussinessException("食材名称已存在");
        }
        foodInfoMapper.insert(food);
    }

    public void updateFood(FoodInfo food){
        long count = foodInfoMapper.selectCount(new LambdaQueryWrapper<FoodInfo>().eq(FoodInfo::getFoodName, food.getFoodName()).ne(FoodInfo::getId, food.getId()));
        if(count > 0){
            throw new BussinessException("食材名称已存在");
        }
        foodInfoMapper.updateById(food);
    }

    public FoodInfo getDetail(Long id){
        FoodInfo foodInfo = foodInfoMapper.selectById(id);
        if(foodInfo == null){
            throw new BussinessException("食材不存在");
        }
        fillCateGoryName(foodInfo);
        return foodInfo;
    }

    public void deleteById(Long id){
        foodInfoMapper.deleteById(id);
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
