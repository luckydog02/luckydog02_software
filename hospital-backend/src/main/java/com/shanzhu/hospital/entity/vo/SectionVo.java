package com.shanzhu.hospital.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 科室返回对象
 *
 */
@Data
public class SectionVo {

    /**
     * 科室名称
     */
    @JsonProperty("sectionName")
    private String sectionName;

    /**
     * 科室分类
     */
    @JsonProperty("category")
    private String category;

    /**
     * 医生数量
     */
    @JsonProperty("doctorCount")
    private Integer doctorCount;

    /**
     * 科室描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 显示颜色
     */
    @JsonProperty("color")
    private String color;
}

