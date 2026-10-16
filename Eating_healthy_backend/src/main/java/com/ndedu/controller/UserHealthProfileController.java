package com.ndedu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.common.Result;
import com.ndedu.entity.UserHealthProfile;
import com.ndedu.service.UserHealthProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health-profile")
public class UserHealthProfileController {
    @Autowired
    private UserHealthProfileService userHealthProfileService;
    @GetMapping("/list")
    public Result<Page<UserHealthProfile>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        Page<UserHealthProfile> page = new Page<>(pageNum, pageSize);
        Page<UserHealthProfile> result = userHealthProfileService.listAll(page);
        return Result.ok(result);
    }
}
