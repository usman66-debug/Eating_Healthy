package com.ndedu.controller;


import com.ndedu.common.Result;
import com.ndedu.entity.RecommendRecord;
import com.ndedu.service.RecommendRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommend/record")
public class RecommendRecordController {
    @Autowired
    private RecommendRecordService recommendRecordService;

    @GetMapping("/today")
    public Result<RecommendRecord> getTodayRecord(){
        Long userId = getCurrentUserId();
        RecommendRecord recommendRecord = recommendRecordService.getTodayRecommend(userId);
        return Result.ok(recommendRecord);
    }

    //获取用户ID
    private Long getCurrentUserId(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (Long) principal;
    }
}
