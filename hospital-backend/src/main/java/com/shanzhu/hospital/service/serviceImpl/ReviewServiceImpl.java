package com.shanzhu.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shanzhu.hospital.entity.po.Review;
import com.shanzhu.hospital.entity.vo.ReviewPageVo;
import com.shanzhu.hospital.mapper.ReviewMapper;
import com.shanzhu.hospital.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 评价相关 服务层实现
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    private final ReviewMapper reviewMapper;

    /**
     * 获取评价列表 - 分页
     *
     * @param pageNum  分页页码
     * @param pageSize 分页大小
     * @param dId      医生ID（可选，用于筛选特定医生的评价）
     * @return 评价列表
     */
    @Override
    public ReviewPageVo getReviewList(Integer pageNum, Integer pageSize, Integer dId) {
        // 分页条件
        Page<Review> page = new Page<>(pageNum, pageSize);

        // 使用自定义查询方法，关联查询患者和医生信息
        IPage<Review> iPage = reviewMapper.selectReviewWithDetails(page, dId);

        // 组装分页返回对象
        ReviewPageVo reviewPageVo = new ReviewPageVo();
        reviewPageVo.populatePage(iPage);

        return reviewPageVo;
    }

    /**
     * 添加评价
     *
     * @param review 评价信息
     * @return 结果
     */
    @Override
    public Boolean addReview(Review review) {
        // 设置评价时间
        if (review.getRTime() == null) {
            review.setRTime(java.time.LocalDateTime.now());
        }
        // 保存评价
        return this.save(review);
    }
}

