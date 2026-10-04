package com.ndedu.controller;

import com.ndedu.DTO.command.UserLoginCommandDTO;
import com.ndedu.common.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody UserLoginCommandDTO request) {
    System.out.println(request.getUsername());
        System.out.println(request.getPassword());

        return Result.ok("登录成功");
    }
}
