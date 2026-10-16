package com.ndedu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndedu.Exception.BussinessException;
import com.ndedu.entity.DietPlan;
import com.ndedu.entity.DietPlanItem;
import com.ndedu.entity.RecommendRecord;
import com.ndedu.entity.SysUser;
import com.ndedu.mapper.DietPlanMapper;
import com.ndedu.mapper.DietPlanItemMapper;
import com.ndedu.mapper.RecommendRecordMapper;
import com.ndedu.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DietPlanService {

    @Autowired
    private DietPlanMapper dietPlanMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private DietPlanItemMapper dietPlanItemMapper;
    @Autowired
    private RecommendRecordMapper recommendRecordMapper;

    public Page<DietPlan> listAll(Page<DietPlan> page, String keyword, String source, String date) {
        LambdaQueryWrapper<DietPlan> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.hasText(date)){
            queryWrapper.eq(DietPlan::getPlanDate, LocalDate.parse(date));
        }
        if(StringUtils.hasText(source)){
            queryWrapper.like(DietPlan::getSource, source);
        }
        queryWrapper.orderByDesc(DietPlan::getPlanDate);
        Page<DietPlan> result = dietPlanMapper.selectPage(page, queryWrapper);
        result.getRecords().forEach(item -> {
            fillUserInfo(item);
            attachDetail(item);
        });
        if(StringUtils.hasText(keyword)){
            result.getRecords().removeIf(plan ->
                    (plan.getNickname() == null || !plan.getNickname().contains(keyword))
            && (plan.getUsername() == null || !plan.getUsername().contains(keyword)));
        }
        return result;
    }

    public DietPlan createFromRecommend(Long recordId,Long userId){
        RecommendRecord record = recommendRecordMapper.selectById(recordId);
        if(record == null){
            throw new BussinessException("推荐记录不存在");
        }
        if(!record.getUserId().equals(userId)){
            throw new BussinessException("您没有权限操作该推荐记录");
        }
        //解析推荐内容
        String content = record.getContent();
        Map<String,Object> parsed;

        try{
            ObjectMapper mapper = new ObjectMapper();
            parsed = mapper.readValue(content, Map.class);
        } catch(Exception e){
            throw new BussinessException("推荐内容格式错误");
        }

        DietPlan plan = new DietPlan();
        plan.setUserId(userId);
        plan.setPlanDate(record.getRecommendDate());
        plan.setPlanType("day");
        plan.setSource("recommend");
        plan.setStatus(0);

        List<DietPlanItem> items = new ArrayList<>();
        parseMeal(parsed,"breakfast",items);
        parseMeal(parsed,"lunch",items);
        parseMeal(parsed,"dinner",items);

        plan.setItems(items);
        calculateNutrition(plan);
        dietPlanMapper.insert(plan);
        return plan;
    }

    //填充用户信息
    private void fillUserInfo(DietPlan item){
        if(item.getUserId() == null){
            return;
        }
        SysUser user = sysUserMapper.selectById(item.getUserId());
        if(user != null){
            item.setNickname(user.getNickname());
            item.setUsername(user.getUsername());
        }
    }

    //填充饮食计划详情
    public void attachDetail(DietPlan item){
        if(item.getId() == null){
            return;
        }
        List<DietPlanItem> items = dietPlanItemMapper.selectList(new LambdaQueryWrapper<DietPlanItem>()
                .eq(DietPlanItem::getPlanId, item.getId()));
        item.setItems(items);
    }

    //三餐解析方案
    private void parseMeal(Map<String, Object> parsed, String mealType, List<DietPlanItem> items) {
        Object meal = parsed.get(mealType);
        if (meal instanceof Map) {
            Map<String, Object> mealData = (Map<String, Object>) meal;
            DietPlanItem item = new DietPlanItem();
            item.setMealType(mealType);
            item.setFoodName(String.valueOf(mealData.getOrDefault("name", "")));
            Object cal = mealData.get("calories");
            if (cal instanceof Number) {
                item.setCalories(BigDecimal.valueOf(((Number) cal).doubleValue()));
            } else {
                item.setCalories(BigDecimal.ZERO);
            }
            item.setAmount(BigDecimal.ONE);
            item.setUnit("份");
            items.add(item);
        }
    }

    private void calculateNutrition(DietPlan plan) {
        BigDecimal totalCal = BigDecimal.ZERO;
        BigDecimal totalPro = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        BigDecimal totalCarb = BigDecimal.ZERO;

        if (plan.getItems() != null) {
            for (DietPlanItem item : plan.getItems()) {
                if (item.getCalories() != null) {
                    totalCal = totalCal.add(item.getCalories());
                }
            }
        }
        plan.setTotalCalories(totalCal);
        plan.setTotalProtein(totalPro);
        plan.setTotalFat(totalFat);
        plan.setTotalCarbohydrate(totalCarb);
    }

    private void saveItems(Long planId, List<DietPlanItem> items) {
        if (items == null) return;
        for (DietPlanItem item : items) {
            item.setId(null);
            item.setPlanId(planId);
            dietPlanItemMapper.insert(item);
        }
    }

}
