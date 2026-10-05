package com.ndedu.controller;

import com.ndedu.DTO.command.UserLoginCommandDTO;
import com.ndedu.DTO.response.UserLoginResponseDTO;
import com.ndedu.common.Result;
import com.ndedu.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody UserLoginCommandDTO request) {
    System.out.println(request.getUsername());
        System.out.println(request.getPassword());
        UserLoginResponseDTO responseDTO = sysUserService.login(request.getUsername(),request.getPassword());
        return Result.ok(responseDTO);
    }
}
