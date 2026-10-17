package com.ndedu.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ndedu.entity.SysNotice;
import com.ndedu.mapper.SysNoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysNoticeService {
    @Autowired
    private SysNoticeMapper sysNoticeMapper;

    public Page<SysNotice> listAll(Page<SysNotice> page, String keyword, Integer status){
        LambdaQueryWrapper<SysNotice> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.hasText(keyword)){
            queryWrapper.eq(SysNotice::getTitle, keyword);
        }
        if(status != null){
            queryWrapper.eq(SysNotice::getStatus, status);
        }
        queryWrapper.orderByAsc(SysNotice::getSort).orderByDesc(SysNotice::getCreateTime);
        return sysNoticeMapper.selectPage(page, queryWrapper);
    }

    public void addNotice(SysNotice notice){
        sysNoticeMapper.insert(notice);
    }

    public void updateNotice(SysNotice notice){
        sysNoticeMapper.updateById(notice);
    }

    public void deleteById(Long id){
        sysNoticeMapper.deleteById(id);
    }

    public SysNotice getById(Long id){
        return sysNoticeMapper.selectById(id);
    }

    public List<SysNotice> getActiveNotices(){
        return sysNoticeMapper.selectList(new LambdaQueryWrapper<SysNotice>()
                .eq(SysNotice::getStatus, 0)
                .orderByAsc(SysNotice::getSort)
                .orderByDesc(SysNotice::getCreateTime)
        );
    }
}
