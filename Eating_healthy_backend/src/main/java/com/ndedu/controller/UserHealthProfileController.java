package com.ndedu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.common.Result;
import com.ndedu.entity.UserHealthProfile;
import com.ndedu.service.UserHealthProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/mine")
    public Result<UserHealthProfile> getMine(){
        Long userId = getCurrentUserId();
        UserHealthProfile profile = userHealthProfileService.getUserHealthProfile(userId);
        return Result.ok(profile);
    }

    @PostMapping("/mine")
    public Result<String> createOrUpdateMine(@RequestBody UserHealthProfile profile){
        Long userId = getCurrentUserId();
        profile.setUserId(userId);
        userHealthProfileService.createOrUpdateHealthProfile(profile);
        return Result.ok("保存成功");
    }

    //获取用户ID
    private Long getCurrentUserId(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (Long) principal;
    }
}
