package com.shanzhu.hospital.service;

import com.shanzhu.hospital.entity.vo.ReviewPageVo;

/**
 * 评价相关 服务层
 *
 */
public interface ReviewService {

    /**
     * 获取评价列表 - 分页
     *
     * @param pageNum  分页页码
     * @param pageSize 分页大小
     * @param dId      医生ID（可选，用于筛选特定医生的评价）
     * @return 评价列表
     */
    ReviewPageVo getReviewList(Integer pageNum, Integer pageSize, Integer dId);

    /**
     * 添加评价
     *
     * @param review 评价信息
     * @return 结果
     */
    Boolean addReview(com.shanzhu.hospital.entity.po.Review review);

    /**
     * 检查订单是否已评价
     *
     * @param oId 订单ID
     * @return 是否已评价
     */
    Boolean checkReviewByOId(Integer oId);

}

