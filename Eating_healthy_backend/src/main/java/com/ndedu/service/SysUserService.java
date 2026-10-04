package com.ndedu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ndedu.DTO.response.UserLoginResponseDTO;
import com.ndedu.entity.SysUser;
import com.ndedu.mapper.SysRoleMapper;
import com.ndedu.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    public UserLoginResponseDTO login(String username,String password){
        //构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername,username).eq(SysUser::getPassword,password);
        //调用MP api查询
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        System.out.println(sysUser);

        //查询角色
        List<String> roles = sysRoleMapper.selectRoleKeysByUserId(sysUser.getId());
        System.out.println(roles);
        sysUser.setRoles(roles);
        System.out.println(sysUser);

        return null;
    }
}
