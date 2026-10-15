package com.ndedu.controller;


import com.ndedu.common.Result;
import com.ndedu.entity.RecommendRecord;
import com.ndedu.service.RecommendRecordService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/generate")
    public Result<RecommendRecord> generateRecommend(){
        Long userId = getCurrentUserId();
        RecommendRecord recommendRecord = recommendRecordService.generateRecommend(userId);
        return Result.ok(recommendRecord);
    }

    @PostMapping("/ai")
    public Result<RecommendRecord> aiRecommend(@RequestBody AiRecommendRequest request){
        Long userId = getCurrentUserId();
        RecommendRecord recommendRecord = recommendRecordService.aiGenerateRecommend(userId, request.getUserRequest());
        return Result.ok(recommendRecord);
    }

    //获取用户ID
    private Long getCurrentUserId(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (Long) principal;
    }

    @Data
    static class AiRecommendRequest {
        private String userRequest;
    }
}
