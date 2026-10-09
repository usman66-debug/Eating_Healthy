package com.ndedu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ndedu.Exception.BussinessException;
import com.ndedu.entity.FoodCategory;
import com.ndedu.mapper.FoodCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FoodCategoryService {

    @Autowired
    private FoodCategoryMapper foodCategoryMapper;

    public List<FoodCategory> tree() {
        LambdaQueryWrapper<FoodCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(FoodCategory::getSort);
        List<FoodCategory> allList = foodCategoryMapper.selectList(queryWrapper);
        return buildTree(allList,0L);
    }

    public void addCategory(FoodCategory category){
        LambdaQueryWrapper<FoodCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FoodCategory::getParentId, category.getParentId() != null ? category.getParentId() : 0L)
                .eq(FoodCategory::getCategoryName, category.getCategoryName());
        Long count = foodCategoryMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BussinessException("同级分类下已存在相同名称的分类");
        }
        foodCategoryMapper.insert(category);
    }


    //递归构建树形结构
    private List<FoodCategory>buildTree(List<FoodCategory> allList, Long parentId) {
        List<FoodCategory> tree = new ArrayList<>();
        for(FoodCategory category : allList){
            if(category.getParentId().equals(parentId)){
                category.setChildren(buildTree(allList,category.getId()));
                tree.add(category);
            }
        }
        return tree;
    }
}
