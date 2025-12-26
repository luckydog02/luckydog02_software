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
import org.springframework.util.StringUtils;

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
        try {
            // 设置评价时间
            if (review.getRTime() == null) {
                review.setRTime(java.time.LocalDateTime.now());
            }
            
            // 如果评价内容为空，设置默认值
            if (!StringUtils.hasText(review.getRContent())) {
                review.setRContent("暂无评价内容");
            }
            
            log.info("保存评价数据：pId={}, dId={}, oId={}, rStar={}, rContent={}", 
                    review.getPId(), review.getDId(), review.getOId(), 
                    review.getRStar(), review.getRContent());
            
            // 保存评价
            boolean result = this.save(review);
            
            if (result) {
                log.info("评价保存成功，评价ID：{}", review.getRId());
            } else {
                log.error("评价保存失败：pId={}, dId={}, oId={}", 
                        review.getPId(), review.getDId(), review.getOId());
            }
            
            return result;
        } catch (Exception e) {
            log.error("保存评价异常：", e);
            throw e;
        }
    }

    /**
     * 检查订单是否已评价
     *
     * @param oId 订单ID
     * @return 是否已评价
     */
    @Override
    public Boolean checkReviewByOId(Integer oId) {
        if (oId == null) {
            return false;
        }
        LambdaQueryWrapper<Review> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Review::getOId, oId);
        long count = this.count(wrapper);
        return count > 0;
    }
}

