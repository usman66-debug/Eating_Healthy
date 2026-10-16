package com.ndedu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.entity.DietPlan;
import com.ndedu.entity.DietPlanItem;
import com.ndedu.entity.SysUser;
import com.ndedu.mapper.DietPlanMapper;
import com.ndedu.mapper.DietPlanItemMapper;
import com.ndedu.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class DietPlanService {

    @Autowired
    private DietPlanMapper dietPlanMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private DietPlanItemMapper dietPlanItemMapper;

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
}
