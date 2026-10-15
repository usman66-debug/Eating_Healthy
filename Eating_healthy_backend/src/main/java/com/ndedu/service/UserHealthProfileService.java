package com.ndedu.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ndedu.entity.UserHealthProfile;
import com.ndedu.mapper.UserHealthProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserHealthProfileService {
    @Autowired
    private UserHealthProfileMapper userHealthProfileMapper;

    //获取用户健康档案
    public UserHealthProfile getUserHealthProfile(Long userId) {
        return userHealthProfileMapper.selectOne(new LambdaQueryWrapper<UserHealthProfile>()
                .eq(UserHealthProfile::getUserId, userId));
    }

}
