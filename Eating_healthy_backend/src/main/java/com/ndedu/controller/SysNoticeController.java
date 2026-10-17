package com.ndedu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.common.Result;
import com.ndedu.entity.SysNotice;
import com.ndedu.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notice")
public class SysNoticeController {
    @Autowired
    private SysNoticeService sysNoticeService;

    @RequestMapping("/list")
    public Result<Page<SysNotice>> listAll(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ){
        Page<SysNotice> page = new Page<>(pageNum, pageSize);
        Page<SysNotice> result = sysNoticeService.listAll(page,keyword,status);
        return Result.ok(result);
    }

    @PostMapping
    public Result addNotice(SysNotice notice){
        sysNoticeService.addNotice(notice);
        return Result.ok("添加成功");
    }

    @PutMapping
    public Result updateNotice(SysNotice notice){
        sysNoticeService.updateNotice(notice);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result deleteNotice(@PathVariable Long id){
        sysNoticeService.deleteById(id);
        return Result.ok("删除成功");
    }

    @GetMapping("/{id}")
    public Result<SysNotice> getNotice(@PathVariable Long id){
        SysNotice notice = sysNoticeService.getById(id);
        return Result.ok(notice);
    }

    @GetMapping("/active")
    public Result<List<SysNotice>> getActiveNotices(){
        List<SysNotice> notices = sysNoticeService.getActiveNotices();
        return Result.ok(notices);
    }
}
