package com.shanzhu.hospital.entity.vo;

import com.shanzhu.hospital.entity.po.Review;
import lombok.Data;

import java.util.List;

/**
 * 评价返回对象
 *
 */
@Data
public class ReviewVo {

    /**
     * 评价列表
     */
    private List<Review> reviews;

    /**
     * 总条数
     */
    private Long total;
}

