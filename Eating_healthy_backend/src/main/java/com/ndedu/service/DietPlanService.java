package com.ndedu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndedu.Exception.BussinessException;
import com.ndedu.entity.DietPlan;
import com.ndedu.entity.DietPlanItem;
import com.ndedu.entity.FoodInfo;
import com.ndedu.entity.RecommendRecord;
import com.ndedu.entity.SysUser;
import com.ndedu.mapper.DietPlanMapper;
import com.ndedu.mapper.DietPlanItemMapper;
import com.ndedu.mapper.FoodInfoMapper;
import com.ndedu.mapper.RecommendRecordMapper;
import com.ndedu.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Autowired
    private FoodInfoMapper foodInfoMapper;

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

    @Transactional(rollbackFor = Exception.class)
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
        if(parsed == null){
            throw new BussinessException("推荐内容不能为空",400);
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

        if(items.isEmpty()){
            throw new BussinessException("推荐内容缺少有效的餐次明细",400);
        }

        plan.setItems(items);
        calculateNutrition(plan);
        dietPlanMapper.insert(plan);
        saveItems(plan.getId(), items);
        return plan;
    }

    public DietPlan getDetail(Long id){
        DietPlan plan = dietPlanMapper.selectById(id);
        if(plan == null){
            throw new BussinessException("饮食计划不存在");
        }
        List<DietPlanItem> items = dietPlanItemMapper.selectList(new LambdaQueryWrapper<DietPlanItem>()
                .eq(DietPlanItem::getPlanId, id));
        plan.setItems(items);
        fillUserInfo(plan);
        return plan;
    }

    public void delete(Long id,Long userId){
        DietPlan plan = dietPlanMapper.selectById(id);
        if(plan == null){
            throw new BussinessException("饮食计划不存在");
        }
        if(userId !=null && !plan.getUserId().equals(userId)){
            throw new BussinessException("您没有权限操作该饮食计划");
        }
        dietPlanMapper.deleteById(id);
        dietPlanItemMapper.delete(new LambdaQueryWrapper<DietPlanItem>()
                .eq(DietPlanItem::getPlanId, id));
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
            if (mealData.containsKey("foodItems")) {
                if (!(mealData.get("foodItems") instanceof List<?> foods) || foods.isEmpty()) {
                    throw new BussinessException("推荐餐次缺少食材明细，请重新生成",400);
                }
                for (Object value : foods) {
                    if (!(value instanceof Map<?, ?> foodData)) {
                        throw new BussinessException("推荐食材明细格式错误",400);
                    }
                    Long foodId;
                    BigDecimal amount;
                    try {
                        foodId = new BigDecimal(String.valueOf(foodData.get("foodId"))).longValueExact();
                        amount = new BigDecimal(String.valueOf(foodData.get("amount")))
                                .setScale(2, RoundingMode.UNNECESSARY);
                    } catch (NumberFormatException | ArithmeticException e) {
                        throw new BussinessException("食材ID或克数格式错误，克数最多保留两位小数",400);
                    }
                    if (foodId <= 0 || amount.signum() <= 0
                            || amount.compareTo(new BigDecimal("99999999.99")) > 0) {
                        throw new BussinessException("食材ID和克数必须为有效正数",400);
                    }
                    String unit = String.valueOf(foodData.get("unit")).trim();
                    if (!"g".equalsIgnoreCase(unit) && !"克".equals(unit)) {
                        throw new BussinessException("营养计算需要以克(g)为单位，不能直接使用份、个或ml",400);
                    }
                    FoodInfo food = foodInfoMapper.selectById(foodId);
                    if (food == null || !Integer.valueOf(0).equals(food.getStatus())
                            || Integer.valueOf(1).equals(food.getDeleted())) {
                        throw new BussinessException("推荐食材不存在或已禁用，请重新生成",400);
                    }
                    for (BigDecimal nutrient : new BigDecimal[]{food.getCalories(), food.getProtein(),
                            food.getFat(), food.getCarbohydrate()}) {
                        if (nutrient == null || nutrient.signum() < 0) {
                            throw new BussinessException("食材“" + food.getFoodName() + "”的营养数据不完整或无效",400);
                        }
                    }
                    DietPlanItem item = new DietPlanItem();
                    item.setMealType(mealType);
                    item.setFoodId(foodId);
                    item.setFoodName(food.getFoodName());
                    item.setAmount(amount);
                    item.setUnit("g");
                    // 食材库为每100g营养值，明细保存实际克数对应的营养快照。
                    BigDecimal ratio = amount.movePointLeft(2);
                    item.setCalories(food.getCalories().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
                    item.setProtein(food.getProtein().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
                    item.setFat(food.getFat().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
                    item.setCarbohydrate(food.getCarbohydrate().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
                    items.add(item);
                }
                return;
            }
            // 兼容旧推荐：整餐热量可保留，缺失的营养值保持null，不伪造为0。
            DietPlanItem item = new DietPlanItem();
            item.setMealType(mealType);
            item.setFoodName(String.valueOf(mealData.getOrDefault("name", "")));
            Object cal = mealData.get("calories");
            if (cal instanceof Number) {
                item.setCalories(new BigDecimal(cal.toString()));
            } else {
                item.setCalories(BigDecimal.ZERO);
            }
            item.setAmount(BigDecimal.ONE);
            item.setUnit("份");
            items.add(item);
        }
    }

    // 汇总明细快照，任一旧明细缺少营养数据时，将三项营养总量标记为未知。
    private void calculateNutrition(DietPlan plan) {
        BigDecimal totalCal = BigDecimal.ZERO;
        BigDecimal totalPro = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        BigDecimal totalCarb = BigDecimal.ZERO;
        boolean completeNutrition = true;

        if (plan.getItems() != null) {
            for (DietPlanItem item : plan.getItems()) {
                if (item.getCalories() != null) {
                    if (item.getCalories().signum() < 0) {
                        throw new BussinessException("餐次热量不能为负数",400);
                    }
                    totalCal = totalCal.add(item.getCalories());
                }
                if (item.getProtein() == null || item.getFat() == null || item.getCarbohydrate() == null) {
                    completeNutrition = false;
                } else {
                    totalPro = totalPro.add(item.getProtein());
                    totalFat = totalFat.add(item.getFat());
                    totalCarb = totalCarb.add(item.getCarbohydrate());
                }
            }
        }
        plan.setTotalCalories(totalCal.setScale(2, RoundingMode.HALF_UP));
        plan.setTotalProtein(completeNutrition ? totalPro.setScale(2, RoundingMode.HALF_UP) : null);
        plan.setTotalFat(completeNutrition ? totalFat.setScale(2, RoundingMode.HALF_UP) : null);
        plan.setTotalCarbohydrate(completeNutrition ? totalCarb.setScale(2, RoundingMode.HALF_UP) : null);
    }

    // 新推荐落库前使用同一计算流程校正营养值，foods仍保留为前端需要的文本数组。
    String normalizeRecommendNutrition(String content) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> parsed;
        try {
            parsed = mapper.readValue(content, Map.class);
        } catch (Exception e) {
            throw new BussinessException("AI推荐内容格式错误，请重新生成",400);
        }
        if (parsed == null) {
            throw new BussinessException("AI推荐内容不能为空",400);
        }
        List<DietPlanItem> allItems = new ArrayList<>();
        for (String mealType : List.of("breakfast", "lunch", "dinner")) {
            if (!(parsed.get(mealType) instanceof Map<?, ?> meal) || !meal.containsKey("foodItems")) {
                throw new BussinessException("AI推荐缺少食材ID和克数，请重新生成",400);
            }
            List<DietPlanItem> mealItems = new ArrayList<>();
            parseMeal(parsed, mealType, mealItems);
            DietPlan mealPlan = new DietPlan();
            mealPlan.setItems(mealItems);
            calculateNutrition(mealPlan);
            Map<String, Object> mealData = (Map<String, Object>) meal;
            mealData.put("calories", mealPlan.getTotalCalories());
            mealData.put("protein", mealPlan.getTotalProtein());
            mealData.put("fat", mealPlan.getTotalFat());
            mealData.put("carbohydrate", mealPlan.getTotalCarbohydrate());
            mealData.put("foods", mealItems.stream()
                    .map(item -> item.getFoodName() + " " + item.getAmount().stripTrailingZeros().toPlainString() + "g")
                    .toList());
            allItems.addAll(mealItems);
        }
        DietPlan dayPlan = new DietPlan();
        dayPlan.setItems(allItems);
        calculateNutrition(dayPlan);
        parsed.put("totalCalories", dayPlan.getTotalCalories());
        parsed.put("totalProtein", dayPlan.getTotalProtein());
        parsed.put("totalFat", dayPlan.getTotalFat());
        parsed.put("totalCarbohydrate", dayPlan.getTotalCarbohydrate());
        try {
            return mapper.writeValueAsString(parsed);
        } catch (Exception e) {
            throw new BussinessException("推荐营养数据处理失败",400);
        }
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
