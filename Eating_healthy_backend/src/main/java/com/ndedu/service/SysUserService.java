package com.ndedu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.DTO.response.UserLoginResponseDTO;
import com.ndedu.Exception.BussinessException;
import com.ndedu.common.ResultCode;
import com.ndedu.entity.SysMenu;
import com.ndedu.entity.SysUser;
import com.ndedu.mapper.SysMenuMapper;
import com.ndedu.mapper.SysRoleMapper;
import com.ndedu.mapper.SysUserMapper;
import com.ndedu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

        if(sysUser == null){
            throw new BussinessException("用户名或密码错误", ResultCode.ERROR.getCode());
        }

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
        responseDTO.setUserInfo(convertUserInfoToDTO(sysUser));
        responseDTO.setMenus(convertMenuToDTO(menuTree));

        return responseDTO;
    }

    //转换菜单为DTO列表
    private List<UserLoginResponseDTO.Menu> convertMenuToDTO(List<SysMenu> menus){
        return menus.stream()
                .map(this::convertSignlMenu)
                .collect(Collectors.toList());
    }

    //递归转换每个菜单
    private UserLoginResponseDTO.Menu convertSignlMenu(SysMenu menu){
        UserLoginResponseDTO.Menu dto = new UserLoginResponseDTO.Menu();
        dto.setId(menu.getId());
        dto.setMenuName(menu.getMenuName());
        dto.setPath(menu.getPath());
        dto.setComponent(menu.getComponent());
        dto.setIcon(menu.getIcon());
        dto.setSort(menu.getSort());
        dto.setMenuType(menu.getMenuType());
        dto.setPermission(menu.getPermission());
        dto.setVisible(menu.getVisible());
        dto.setStatus(menu.getStatus());
        dto.setCreateTime(menu.getCreateTime());
        dto.setUpdateTime(menu.getUpdateTime());


        if(menu.getChildren() != null){
            dto.setChildren(convertMenuToDTO(menu.getChildren()));
        }
        return dto;
    }

    //处理用户的数据
    private UserLoginResponseDTO.UserInfo convertUserInfoToDTO(SysUser sysUser){
        UserLoginResponseDTO.UserInfo dto = new UserLoginResponseDTO.UserInfo();
        dto.setId(sysUser.getId());
        dto.setUsername(sysUser.getUsername());
        dto.setNickname(sysUser.getNickname());
        dto.setPhone(sysUser.getPhone());
        dto.setEmail(sysUser.getEmail());
        dto.setAvatar(sysUser.getAvatar());
        dto.setGender(sysUser.getGender());
        dto.setStatus(sysUser.getStatus());
        dto.setCreateTime(sysUser.getCreateTime());
        dto.setUpdateTime(sysUser.getUpdateTime());
        dto.setRoles(sysUser.getRoles());
        return dto;
    }

    //构建菜单树
    private List<SysMenu> buildMenuTree(List<SysMenu> menus, Long parentId){
        return menus.stream()
                .filter(m->parentId.equals(m.getParentId()))
                .peek(m->m.setChildren(buildMenuTree(menus,m.getId())))
                .sorted(Comparator.comparingInt(m->m.getSort() == null ? 0 : m.getSort()))
                .collect(Collectors.toList());
    }

    //分页查询用户列表
    public Page<SysUser> listUsers(Page<SysUser> page, String keyword, Integer status){
        //构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        //传入keyword
        if(StringUtils.hasText(keyword)){
            queryWrapper.and(
                    w->w.like(SysUser::getUsername,keyword)
                            .or().like(SysUser::getNickname,keyword)
                            .or().like(SysUser::getPhone,keyword)
            );
        }
        //传入status
        if(status != null){
            queryWrapper.eq(SysUser::getStatus,status);
        }

        queryWrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = sysUserMapper.selectPage(page,queryWrapper);
        result.getRecords().forEach(
                u->{
                    u.setPassword(null);
                    u.setRoles(sysRoleMapper.selectRoleKeysByUserId(u.getId()));
                }
        );
        return result;
    }
}
