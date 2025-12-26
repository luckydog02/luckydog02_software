package com.shanzhu.hospital.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价实体
 *
 */
@Data
@TableName("review")
public class Review {

    /**
     * 评价ID
     */
    @TableId(value = "r_id", type = IdType.AUTO)
    @JsonProperty("rId")
    private Integer rId;

    /**
     * 患者ID
     */
    @TableField("p_id")
    @JsonProperty("pId")
    private Integer pId;

    /**
     * 医生ID
     */
    @TableField("d_id")
    @JsonProperty("dId")
    private Integer dId;

    /**
     * 关联挂号单ID
     */
    @TableField("o_id")
    @JsonProperty("oId")
    private Integer oId;

    /**
     * 评分(1-5)
     */
    @TableField("r_star")
    @JsonProperty("rStar")
    private Double rStar;

    /**
     * 评价内容
     */
    @TableField("r_content")
    @JsonProperty("rContent")
    private String rContent;

    /**
     * 患者印象标签(逗号分隔)
     */
    @TableField("r_impressions")
    @JsonProperty("rImpressions")
    private String rImpressions;

    /**
     * 评价时间
     */
    @TableField("r_time")
    @JsonProperty("rTime")
    private LocalDateTime rTime;

    /**
     * 患者名称 - 多表查询用
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    @JsonProperty("pName")
    private String pName;

    /**
     * 医生名称 - 多表查询用
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    @JsonProperty("dName")
    private String dName;

    /**
     * 医生职位 - 多表查询用
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    @JsonProperty("dPost")
    private String dPost;

    /**
     * 患者位置（用于显示，可以从患者信息获取或默认值）
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    @JsonProperty("location")
    private String location;

}

