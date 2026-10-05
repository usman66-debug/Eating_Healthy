package com.ndedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ndedu.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    @Select("select m.* from sys_menu m inner join sys_role_menu rm on rm.menu_id = m.id inner join sys_user_role ur on ur.role_id = rm.role_id where ur.user_id = '1' and m.status = #{userId order by m.sort")
    List<SysMenu> selectMenusByUserId(Long userId);
}

