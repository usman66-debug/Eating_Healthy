package com.ndedu.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ndedu.entity.FoodInfo;
import com.ndedu.entity.FoodRecipe;
import com.ndedu.entity.RecommendRecord;
import com.ndedu.entity.UserHealthProfile;
import com.ndedu.mapper.FoodInfoMapper;
import com.ndedu.mapper.FoodRecipeItemMapper;
import com.ndedu.mapper.FoodRecipeMapper;
import com.ndedu.mapper.RecommendRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendRecordService {
    @Autowired
    private RecommendRecordMapper recommendRecordMapper;
    @Autowired
    private UserHealthProfileService userHealthProfileService;
    @Autowired
    private FoodInfoMapper foodInfoMapper;
    @Autowired
    private FoodRecipeMapper foodRecipeMapper;
    @Autowired
    private AiService aiService;

    public RecommendRecord getTodayRecommend(Long userId){
        RecommendRecord recommendRecord = recommendRecordMapper.selectOne(new LambdaQueryWrapper<RecommendRecord>()
                .eq(RecommendRecord::getUserId, userId)
                .eq(RecommendRecord::getRecommendDate, LocalDate.now())
                .eq(RecommendRecord::getSource, "AI")
        .orderByDesc(RecommendRecord::getCreateTime)
        .last("LIMIT 1"));
        if(recommendRecord != null){
            return recommendRecord;
        }
        //调用AI大模型推荐内容
        return generateRecommend(userId);
    }

    public RecommendRecord generateRecommend(Long userId){
        //获取用户健康档案
        UserHealthProfile userHealthProfile = userHealthProfileService.getUserHealthProfile(userId);
        //构建用户提示词
        String prompt = buildDailyPrompt(userHealthProfile);
        //构建系统提示词
        String systemPrompt = "**你是一个专业的营养师。请根据用户的健康档案数据为其推荐今日三餐饮食方案。 请严格按照指定的JSON格式返回，不要添加任何额外文字说明。**";
        //调用AI大模型推荐
        String jsonResult = aiService.chatForJson(systemPrompt, prompt);
        RecommendRecord record = new RecommendRecord();
        record.setUserId(userId);
        record.setRecommendDate(LocalDate.now());
        record.setContent(jsonResult);
        record.setSource("AI");
        record.setStatus(0);
        recommendRecordMapper.insert(record);
        return record;
    }

    public String buildDailyPrompt(UserHealthProfile profile){
        StringBuilder sb = new StringBuilder();
        sb.append("基于以下用户信息推荐今日三餐饮食方案：\n");

        if (profile != null) {
            sb.append("- 身高：").append(profile.getHeight()).append("cm，体重：").append(profile.getWeight()).append("kg");
            if (profile.getBmi() != null) {
                sb.append("，BMI：").append(profile.getBmi());
            }
            sb.append("\n");
            sb.append("- 年龄：").append(profile.getAge()).append("，性别：").append(profile.getGender() == 1 ? "男" : "女").append("\n");
            sb.append("- 活动量：").append(getActivityLabel(profile.getActivityLevel())).append("\n");
            if (profile.getTargetCalories() != null) {
                sb.append("- 每日目标热量：").append(profile.getTargetCalories()).append("kcal\n");
            }
            if (StringUtils.hasText(profile.getDietPreference())) {
                sb.append("- 饮食偏好：").append(profile.getDietPreference()).append("\n");
            }
            if (StringUtils.hasText(profile.getAllergens())) {
                sb.append("- 过敏源（需避免）：").append(profile.getAllergens()).append("\n");
            }
        } else {
            sb.append("- 用户未填写健康档案，请提供一份通用的均衡饮食方案\n");
        }

        // 附带食材库信息
        appendFoodList(sb);

        sb.append("\n请按以下JSON格式返回推荐方案：\n");
        sb.append("{\n");
        sb.append("  \"breakfast\": {\"name\": \"\", \"foods\": [], \"calories\": 0, \"description\": \"\"},\n");
        sb.append("  \"lunch\": {\"name\": \"\", \"foods\": [], \"calories\": 0, \"description\": \"\"},\n");
        sb.append("  \"dinner\": {\"name\": \"\", \"foods\": [], \"calories\": 0, \"description\": \"\"},\n");
        sb.append("  \"totalCalories\": 0,\n");
        sb.append("  \"nutritionAdvice\": \"\"\n");
        sb.append("}");

        return sb.toString();
    }

    private String getActivityLabel(String level) {
        if (level == null) return "中等";
        switch (level) {
            case "sedentary":   return "久坐不动";
            case "light":       return "轻度活动";
            case "moderate":    return "中等活动";
            case "active":      return "高度活动";
            case "very_active": return "极高活动";
            default:            return "中等活动";
        }
    }

    /** 附带食材库信息 */
    private void appendFoodList(StringBuilder sb) {
        // 取出部分食材名称供AI参考
        List<FoodInfo> foods = foodInfoMapper.selectList(new LambdaQueryWrapper<FoodInfo>()
                .eq(FoodInfo::getStatus,0)
                .last("LIMIT 50"));
        if(!foods.isEmpty()){
            String foodNames = foods.stream().map(FoodInfo::getFoodName).collect(Collectors.joining("、"));
            sb.append("-可选食材库：").append(foodNames).append("\n");
        }
        //取出部分食谱名称供AI参考
        List<FoodRecipe> recipes = foodRecipeMapper.selectList(new LambdaQueryWrapper<FoodRecipe>()
                .eq(FoodRecipe::getStatus, 0)
                .last("LIMIT 20"));
        if(!recipes.isEmpty()){
            String recipeNames = recipes.stream().map(FoodRecipe::getRecipeName).collect(Collectors.joining("、"));
            sb.append("-可选食谱库：").append(recipeNames).append("\n");
        }
    }
}
