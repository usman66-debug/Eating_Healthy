package com.ndedu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.common.Result;
import com.ndedu.entity.DietPlan;
import com.ndedu.service.DietPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diet-plan")
public class DietPlanController {
    @Autowired
    private DietPlanService dietPlanService;

    @GetMapping("/list")
    public Result<Page<DietPlan>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String date
    ){
    Page<DietPlan> page = new Page<>(pageNum, pageSize);
    Page<DietPlan> result = dietPlanService.listAll(page, keyword, source, date);
    return Result.ok(result);
    }

    @PostMapping("/from-recommend/{recordId}")
    public Result<DietPlan> fromRecommend(@PathVariable Long recordId){
        Long userId = getCurrentUserId();
        DietPlan plan = dietPlanService.createFromRecommend(recordId,userId);
        return Result.ok(plan);
    }

    //获取用户ID
    private Long getCurrentUserId(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (Long) principal;
    }
}
