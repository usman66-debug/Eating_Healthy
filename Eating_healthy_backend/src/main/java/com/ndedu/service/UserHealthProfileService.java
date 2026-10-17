package com.ndedu.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.Exception.BussinessException;
import com.ndedu.entity.SysUser;
import com.ndedu.entity.UserHealthProfile;
import com.ndedu.mapper.SysUserMapper;
import com.ndedu.mapper.UserHealthProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class UserHealthProfileService {
    /** 活动系数常量 */
    private static final BigDecimal FACTOR_SEDENTARY   = new BigDecimal("1.2");
    private static final BigDecimal FACTOR_LIGHT       = new BigDecimal("1.375");
    private static final BigDecimal FACTOR_MODERATE     = new BigDecimal("1.55");
    private static final BigDecimal FACTOR_ACTIVE       = new BigDecimal("1.725");
    private static final BigDecimal FACTOR_VERY_ACTIVE  = new BigDecimal("1.9");

    @Autowired
    private UserHealthProfileMapper userHealthProfileMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    //获取用户健康档案
    public UserHealthProfile getUserHealthProfile(Long userId) {
        return userHealthProfileMapper.selectOne(new LambdaQueryWrapper<UserHealthProfile>()
                .eq(UserHealthProfile::getUserId, userId));
    }

    public Page<UserHealthProfile> listAll(Page<UserHealthProfile> page){
        LambdaQueryWrapper<UserHealthProfile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(UserHealthProfile::getUpdateTime);
        Page<UserHealthProfile> result = userHealthProfileMapper.selectPage(page, queryWrapper);
        result.getRecords().forEach(this::fillUserInfo);
        return result;
    }

    public void fillUserInfo(UserHealthProfile profile){
        if(profile.getUserId() != null){
            SysUser user = sysUserMapper.selectById(profile.getUserId());
            if(user != null){
                profile.setNickname(user.getUsername());
                profile.setUsername(user.getUsername());
            }
        }
    }

    public void createOrUpdateHealthProfile(UserHealthProfile profile){
        if(profile.getUserId() == null){
            throw new BussinessException("用户ID不能为空");
        }

        if(profile.getHeight() != null && profile.getWeight() != null && profile.getWeight().compareTo(BigDecimal.ZERO) > 0){
            BigDecimal bmi = calculateBmi(profile.getHeight(), profile.getWeight());
            profile.setBmi(bmi);
        }else{
            throw new BussinessException("身高和体重不能为空");
        }

        if(profile.getHeight() != null && profile.getWeight() != null && profile.getAge() != null && profile.getGender() != null){
            BigDecimal targetCalories = calculateTargetCalories(
                    profile.getHeight(), profile.getWeight(),
                    profile.getAge(), profile.getGender(), profile.getActivityLevel());
            profile.setTargetCalories(targetCalories);
        }

        //查询已有记录
        UserHealthProfile existingprofile = getUserHealthProfile(profile.getUserId());
        if(existingprofile != null){
            //更新记录
            profile.setId(existingprofile.getId());
            userHealthProfileMapper.updateById(profile);
        }else{
            //新增记录
            userHealthProfileMapper.insert(profile);
        }
    }

    // 计算BMI
    public BigDecimal calculateBmi(BigDecimal height, BigDecimal weight) {
        // BMI = 体重(kg) / (身高(m))^2
        BigDecimal heightInMeter = height.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal heightSquare = heightInMeter.multiply(heightInMeter);
        return weight.divide(heightSquare, 2, RoundingMode.HALF_UP);
    }

    // 计算每日推荐热量
    public BigDecimal calculateTargetCalories(
            BigDecimal height, BigDecimal weight,
            Integer age, Integer gender, String activityLevel) {
        // Mifflin-St Jeor 公式
        // 男性: BMR = 10 × 体重(kg) + 6.25 × 身高(cm) - 5 × 年龄 + 5
        // 女性: BMR = 10 × 体重(kg) + 6.25 × 身高(cm) - 5 × 年龄 - 161
        BigDecimal bmr;
        BigDecimal base = new BigDecimal("10").multiply(weight)
                .add(new BigDecimal("6.25").multiply(height))
                .subtract(new BigDecimal("5").multiply(new BigDecimal(age)));
        if (gender != null && gender == 2) {
            bmr = base.subtract(new BigDecimal("161"));
        } else {
            bmr = base.add(new BigDecimal("5"));
        }

        // 乘以活动系数
        BigDecimal factor = getActivityFactor(activityLevel);
        return bmr.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    /** 获取活动系数 */
    private BigDecimal getActivityFactor(String activityLevel) {
        if (!StringUtils.hasText(activityLevel)) {
            return FACTOR_MODERATE;
        }
        switch (activityLevel) {
            case "sedentary":    return FACTOR_SEDENTARY;
            case "light":        return FACTOR_LIGHT;
            case "active":       return FACTOR_ACTIVE;
            case "very_active":  return FACTOR_VERY_ACTIVE;
            default:             return FACTOR_MODERATE;
        }
    }
}
