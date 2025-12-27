package com.shanzhu.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shanzhu.hospital.entity.po.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评价相关 持久层（mapper）
 *
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    /**
     * 查询评价列表（关联患者和医生信息）
     *
     * @param page 分页对象
     * @param dId  医生ID（可选）
     * @return 评价列表
     */
    IPage<Review> selectReviewWithDetails(Page<Review> page, @Param("dId") Integer dId);
}

