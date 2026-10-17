package com.ndedu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ndedu.common.Result;
import com.ndedu.entity.FoodCategory;
import com.ndedu.entity.FoodInfo;
import com.ndedu.entity.FoodRecipe;
import com.ndedu.entity.RecommendRecord;
import com.ndedu.entity.SysUser;
import com.ndedu.mapper.FoodCategoryMapper;
import com.ndedu.mapper.FoodInfoMapper;
import com.ndedu.mapper.FoodRecipeMapper;
import com.ndedu.mapper.RecommendRecordMapper;
import com.ndedu.mapper.SysUserMapper;
import com.ndedu.mapper.UserHealthProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private FoodInfoMapper foodInfoMapper;
    @Autowired
    private FoodRecipeMapper foodRecipeMapper;
    @Autowired
    private RecommendRecordMapper recommendRecordMapper;
    @Autowired
    private UserHealthProfileMapper userHealthProfileMapper;
    @Autowired
    private FoodCategoryMapper foodCategoryMapper;

    @GetMapping("/admin/stats")
    public Result<Map<String, Object>> adminStats() {
        Map<String, Object> data = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        data.put("userCount", sysUserMapper.selectCount(null));
        data.put("foodCount", foodInfoMapper.selectCount(null));
        data.put("recipeCount", foodRecipeMapper.selectCount(null));
        data.put("todayRecommendCount", recommendRecordMapper.selectCount(
                new LambdaQueryWrapper<RecommendRecord>().apply("DATE(create_time) = {0}", todayStr)));
        data.put("todayNewUsers", sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().apply("DATE(create_time) = {0}", todayStr)));
        data.put("profileCount", userHealthProfileMapper.selectCount(null));

        return Result.ok(data);
    }

    @GetMapping("/admin/charts")
    public Result<Map<String, Object>> adminCharts() {
        Map<String, Object> data = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        List<Map<String, Object>> recommendTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", recommendRecordMapper.selectCount(
                    new LambdaQueryWrapper<RecommendRecord>().apply("DATE(create_time) = {0}", date.toString())));
            recommendTrend.add(item);
        }
        data.put("recommendTrend", recommendTrend);

        List<Map<String, Object>> categoryDistribution = new ArrayList<>();
        List<FoodCategory> categories = foodCategoryMapper.selectList(null);
        for (FoodCategory category : categories) {
            Long count = foodInfoMapper.selectCount(
                    new LambdaQueryWrapper<FoodInfo>().eq(FoodInfo::getCategoryId, category.getId()));
            if (count > 0) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("name", category.getCategoryName());
                item.put("value", count);
                categoryDistribution.add(item);
            }
        }
        data.put("categoryDistribution", categoryDistribution);

        return Result.ok(data);
    }
}