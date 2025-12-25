package com.shanzhu.hospital.entity.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shanzhu.hospital.entity.po.Review;
import com.shanzhu.hospital.entity.vo.base.PageBase;
import lombok.Data;

import java.util.List;

/**
 * 评价分页返回对象
 *
 */
@Data
public class ReviewPageVo extends PageBase {

    /**
     * 评价数据
     */
    private List<Review> reviews;

    /**
     * 填充分页信息
     *
     * @param iPage 分页对象
     */
    public void populatePage(IPage iPage) {
        super.populatePage(iPage);
        this.reviews = iPage.getRecords();
    }

}

