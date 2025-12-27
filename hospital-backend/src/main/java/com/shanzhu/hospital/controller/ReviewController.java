package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.po.Review;
import com.shanzhu.hospital.entity.vo.ReviewPageVo;
import com.shanzhu.hospital.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 评价相关 控制层
 *
 */
@Slf4j
@RestController
@RequestMapping("review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 获取评价列表 - 分页
     *
     * @param pageNumber 分页页码
     * @param size       分页大小
     * @param dId        医生ID（可选，用于筛选特定医生的评价）
     * @return 评价列表
     */
    @RequestMapping("list")
    public R<ReviewPageVo> getReviewList(
            @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "dId", required = false) Integer dId
    ) {
        ReviewPageVo reviewPageVo = reviewService.getReviewList(pageNumber, size, dId);
        return R.ok(reviewPageVo);
    }

    /**
     * 添加评价
     *
     * @param review 评价信息
     * @return 结果
     */
    @PostMapping("add")
    public R<Boolean> addReview(@RequestBody Review review) {
        try {
            // 参数验证
            if (review.getPId() == null) {
                log.warn("添加评价失败：患者ID为空");
                return R.error("患者ID不能为空");
            }
            if (review.getDId() == null) {
                log.warn("添加评价失败：医生ID为空");
                return R.error("医生ID不能为空");
            }
            if (review.getOId() == null) {
                log.warn("添加评价失败：订单ID为空");
                return R.error("订单ID不能为空");
            }
            if (review.getRStar() == null || review.getRStar() < 1 || review.getRStar() > 5) {
                log.warn("添加评价失败：评分无效，评分值：{}", review.getRStar());
                return R.error("评分必须在1-5之间");
            }
            
            log.info("添加评价：pId={}, dId={}, oId={}, rStar={}", 
                    review.getPId(), review.getDId(), review.getOId(), review.getRStar());
            
            if (reviewService.addReview(review)) {
                log.info("评价提交成功：pId={}, dId={}, oId={}", 
                        review.getPId(), review.getDId(), review.getOId());
                return R.ok("评价提交成功");
            } else {
                log.error("评价保存失败：pId={}, dId={}, oId={}", 
                        review.getPId(), review.getDId(), review.getOId());
                return R.error("评价提交失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("添加评价异常：", e);
            return R.error("评价提交失败：" + e.getMessage());
        }
    }

    /**
     * 检查订单是否已评价
     *
     * @param oId 订单ID
     * @return 是否已评价
     */
    @GetMapping("checkByOId")
    public R<Boolean> checkReviewByOId(@RequestParam("oId") Integer oId) {
        try {
            if (oId == null) {
                return R.error("订单ID不能为空");
            }
            Boolean hasReviewed = reviewService.checkReviewByOId(oId);
            return R.ok(hasReviewed);
        } catch (Exception e) {
            log.error("检查评价状态异常：oId={}", oId, e);
            return R.error("检查评价状态失败：" + e.getMessage());
        }
    }
}

