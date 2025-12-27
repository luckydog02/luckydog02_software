package com.shanzhu.hospital.entity.vo;

import lombok.Data;
import java.util.List;

/**
 * 挂号排班 返回对象
 *
 */
@Data
public class OrderArrangeVo {

    /**
     * 挂号时间
     */
    private String orderDate;
    
    /**
     * 时间段列表（包含剩余号数）
     */
    private List<TimeSlotVo> timeSlots;
    
    // 手动添加 getter 和 setter 方法，确保编译通过
    public String getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
    
    public List<TimeSlotVo> getTimeSlots() {
        return timeSlots;
    }
    
    public void setTimeSlots(List<TimeSlotVo> timeSlots) {
        this.timeSlots = timeSlots;
    }
}

  