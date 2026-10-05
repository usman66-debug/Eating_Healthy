package com.ndedu.controller;

import com.ndedu.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class SysUserController {
    @GetMapping("/list")
    public Result<?> list(){
        System.out.println("list");
        return null;
    }
}
