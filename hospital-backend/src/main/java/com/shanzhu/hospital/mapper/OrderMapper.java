package com.shanzhu.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanzhu.hospital.entity.po.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 挂号相关 持久层（mapper）
 *
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

    /**
     * 统计挂号人数
     *
     * @return 人数
     * @Param oStart 时间
     */
    Integer orderPeople(String oStart);

    /**
     * 统计今天某个医生挂号人数
     *
     * @param oStart 时间
     * @param dId    医生id
     * @return 人数
     */
    Integer orderPeopleByDid(@Param("o_start") String oStart, @Param("d_id") int dId);

    /**
     * 统计挂号男女人数
     *
     * @return 人数
     */
    List<String> orderGender();

    /**
     * 根据挂号单号查询挂号
     */
    Orders findOrderByOid(int oId);

    /**
     * 增加诊断及医生意见
     *
     * @param order 挂号单信息
     * @return 结果
     */
    Integer updateOrderByAdd(Orders order);

    /**
     * 统计近20天科室人数
     *
     * @return 人数
     * @Param 开始时间
     * @Paarm 结束时间
     */
    List<String> orderSection(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 查看当天挂号
     *
     * @param dId    医生id
     * @param oStart 日期时间
     * @return 挂号数据
     */
    List<Orders> findOrderByNull(@Param("dId") Integer dId, @Param("oStart") String oStart);

    /**
     * 查询病患挂号
     *
     * @param pId 病患id
     * @return 挂号信息
     */
    List<Orders> findOrderByPid(Integer pId);

    /**
     * 统计某个医生某个日期某个时间段的预约数量
     *
     * @param dId 医生ID
     * @param date 日期（格式：yyyy-MM-dd）
     * @param timeSlot 时间段（格式：08:30-09:30）
     * @return 预约数量
     */
    Integer countOrdersByTimeSlot(@Param("dId") Integer dId, @Param("date") String date, @Param("timeSlot") String timeSlot);

    /**
     * 检查患者某天是否已有预约
     *
     * @param pId 患者ID
     * @param date 日期（格式：yyyy-MM-dd）
     * @return 预约数量
     */
    Integer countPatientOrdersByDate(@Param("pId") Integer pId, @Param("date") String date);

}
