package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.vo.ReviewPageVo;
import com.shanzhu.hospital.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评价相关 控制层
 *
 */
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
    @RequestMapping("add")
    public R<Boolean> addReview(com.shanzhu.hospital.entity.po.Review review) {
        if (reviewService.addReview(review)) {
            return R.ok("评价提交成功");
        }
        return R.error("评价提交失败");
    }
}

