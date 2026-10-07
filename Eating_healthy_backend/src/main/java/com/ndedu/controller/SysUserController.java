package com.ndedu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.DTO.command.UserFormCommandDTO;
import com.ndedu.common.Result;
import com.ndedu.entity.SysUser;
import com.ndedu.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/list")
    public Result<Page<SysUser>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ){
        //构建分页对象，设置当前页和每页数量
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        //调用服务层方法查询用户列表
        Page<SysUser> result = sysUserService.listUsers(page, keyword, status);
        //返回查询结果
        return Result.ok(result);
    }

    @PostMapping
    public Result<?> addUser(@Valid @RequestBody UserFormCommandDTO request){
        System.out.println(request.getUser());
        System.out.println(request.getRoleKey());
        sysUserService.addUser(request.getUser(),request.getRoleKey());
        return Result.ok();
    }

    @PutMapping
    public Result<?> updateUser(@Valid @RequestBody UserFormCommandDTO request){
        sysUserService.updateUser(request.getUser(),request.getRoleKey());
        return Result.ok();
    }
}
