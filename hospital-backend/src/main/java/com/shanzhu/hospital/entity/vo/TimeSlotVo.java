package com.shanzhu.hospital.entity.vo;

import lombok.Data;

/**
 * 时间段信息
 */
@Data
public class TimeSlotVo {
    /**
     * 时间段（如：08:30-09:30）
     */
    private String timeSlot;
    
    /**
     * 剩余号数
     */
    private Integer remainingCount;
    
    /**
     * 总号数（固定为10）
     */
    private Integer totalCount = 10;
}

