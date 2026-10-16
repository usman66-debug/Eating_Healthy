package com.ndedu.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.entity.SysUser;
import com.ndedu.entity.UserHealthProfile;
import com.ndedu.mapper.SysUserMapper;
import com.ndedu.mapper.UserHealthProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserHealthProfileService {
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

}
