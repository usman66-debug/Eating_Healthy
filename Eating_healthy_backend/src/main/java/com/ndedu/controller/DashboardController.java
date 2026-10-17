package com.ndedu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ndedu.common.Result;
import com.ndedu.entity.DietPlan;
import com.ndedu.entity.DietPlanItem;
import com.ndedu.entity.FoodCategory;
import com.ndedu.entity.FoodInfo;
import com.ndedu.entity.FoodRecipe;
import com.ndedu.entity.RecommendRecord;
import com.ndedu.entity.SysUser;
import com.ndedu.entity.UserHealthProfile;
import com.ndedu.mapper.DietPlanItemMapper;
import com.ndedu.mapper.DietPlanMapper;
import com.ndedu.mapper.FoodCategoryMapper;
import com.ndedu.mapper.FoodInfoMapper;
import com.ndedu.mapper.FoodRecipeMapper;
import com.ndedu.mapper.RecommendRecordMapper;
import com.ndedu.mapper.SysUserMapper;
import com.ndedu.mapper.UserHealthProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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
    @Autowired
    private DietPlanMapper dietPlanMapper;
    @Autowired
    private DietPlanItemMapper dietPlanItemMapper;

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

    @GetMapping("/user/overview")
    public Result<Map<String, Object>> userOverview() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> data = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        List<DietPlan> todayPlans = dietPlanMapper.selectList(
                new LambdaQueryWrapper<DietPlan>()
                        .eq(DietPlan::getUserId, userId)
                        .eq(DietPlan::getPlanDate, today));

        BigDecimal totalCalories = BigDecimal.ZERO;
        int todayFoodCount = 0;
        for (DietPlan plan : todayPlans) {
            List<DietPlanItem> items = dietPlanItemMapper.selectList(
                    new LambdaQueryWrapper<DietPlanItem>().eq(DietPlanItem::getPlanId, plan.getId()));
            for (DietPlanItem item : items) {
                if (item.getCalories() != null) {
                    totalCalories = totalCalories.add(item.getCalories());
                }
            }
            todayFoodCount += items.size();
        }

        UserHealthProfile profile = userHealthProfileMapper.selectOne(
                new LambdaQueryWrapper<UserHealthProfile>().eq(UserHealthProfile::getUserId, userId));

        data.put("todayCalories", totalCalories.intValue());
        data.put("todayPlanCount", todayPlans.size());
        data.put("todayFoodCount", todayFoodCount);
        data.put("hasProfile", profile != null);
        data.put("targetCalories", profile != null && profile.getTargetCalories() != null ? profile.getTargetCalories().intValue() : 0);
        data.put("recommendCount", recommendRecordMapper.selectCount(
                new LambdaQueryWrapper<RecommendRecord>().eq(RecommendRecord::getUserId, userId)));
        data.put("planTotal", dietPlanMapper.selectCount(
                new LambdaQueryWrapper<DietPlan>().eq(DietPlan::getUserId, userId)));

        return Result.ok(data);
    }
}