package com.ndedu.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ndedu.entity.RecommendRecord;
import com.ndedu.mapper.RecommendRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RecommendRecordService {
    @Autowired
    private RecommendRecordMapper recommendRecordMapper;

    public RecommendRecord getTodayRecommend(Long userId){
        RecommendRecord recommendRecord = recommendRecordMapper.selectOne(new LambdaQueryWrapper<RecommendRecord>()
                .eq(RecommendRecord::getUserId, userId)
                .eq(RecommendRecord::getRecommendDate, LocalDate.now())
                .eq(RecommendRecord::getSource, "AI")
        .orderByDesc(RecommendRecord::getCreateTime)
        .last("LIMIT 1"));
        if(recommendRecord != null){
            return recommendRecord;
        }
        //调用AI大模型推荐内容
        return null;
    }
}
