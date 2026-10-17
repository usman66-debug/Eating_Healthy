package com.ndedu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    @PostMapping("feedback")
    public Result<?> feedback(@RequestBody FeedbackRequest request){
        Long userId = getCurrentUserId();
        recommendRecordService.feedback(userId, request.getFeedback(),request.getRecordId());
        return Result.ok("反馈成功");
    }

    @GetMapping("/list")
    public Result<Page<RecommendRecord>> listRecommendRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String source
    ){
        Page<RecommendRecord> page = new Page<>(pageNum, pageSize);
        Page<RecommendRecord> records = recommendRecordService.listRecords(page, date, source);
        return Result.ok(records);
    }

    @GetMapping("/my")
    public Result<Page<RecommendRecord>> myRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String date
    ){
        Long userId = getCurrentUserId();
        Page<RecommendRecord> page = new Page<>(pageNum, pageSize);
        Page<RecommendRecord> records = recommendRecordService.listMyRecords(page, userId, date);
        return Result.ok(records);
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
    @Data
    static class FeedbackRequest {
        private String feedback;
        private Long recordId;
    }
}
