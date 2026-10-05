package com.ndedu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ndedu.DTO.response.UserLoginResponseDTO;
import com.ndedu.entity.SysMenu;
import com.ndedu.entity.SysUser;
import com.ndedu.mapper.SysMenuMapper;
import com.ndedu.mapper.SysRoleMapper;
import com.ndedu.mapper.SysUserMapper;
import com.ndedu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private JwtUtils jwtUtils;

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

        //生成token
        String token = jwtUtils.generateToken(sysUser.getId(),sysUser.getUsername());
        System.out.println(token);

        //根据用户查询菜单
        List<SysMenu> menus = sysMenuMapper.selectMenusByUserId(sysUser.getId());
        List<SysMenu> menuTree = buildMenuTree(menus,0L);

        UserLoginResponseDTO responseDTO = new UserLoginResponseDTO();
        responseDTO.setToken(token);
        responseDTO.setUserInfo(sysUser);
        responseDTO.setMenus(menuTree);

        return responseDTO;
    }

    //构建菜单树
    private List<SysMenu> buildMenuTree(List<SysMenu> menus, Long parentId){
        return menus.stream()
                .filter(m->parentId.equals(m.getParentId()))
                .peek(m->m.setChildren(buildMenuTree(menus,m.getId())))
                .sorted(Comparator.comparingInt(m->m.getSort() == null ? 0 : m.getSort()))
                .collect(Collectors.toList());
    }
}
