package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.po.Orders;
import com.shanzhu.hospital.entity.po.Patient;
import com.shanzhu.hospital.entity.vo.DoctorPageVo;
import com.shanzhu.hospital.entity.vo.user.DoctorUserVo;
import com.shanzhu.hospital.service.DoctorUserService;
import com.shanzhu.hospital.service.OrderService;
import com.shanzhu.hospital.service.PatientUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 医生相关 控制层
 *
 */
@Slf4j
@RestController
@RequestMapping("doctor")
@RequiredArgsConstructor
public class DoctorUserController {

    private final DoctorUserService doctorUserService;

    private final OrderService orderService;

    private final PatientUserService patientUserService;

    /**
     * 医生登录
     *
     * @param dId       医生id (账号)
     * @param dPassword 密码
     * @return 医生用户信息
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public R<DoctorUserVo> login(
            @RequestParam(value = "dId") Integer dId,
            @RequestParam(value = "dPassword") String dPassword
    ) {
        //登录医生
        DoctorUserVo doctorVo = doctorUserService.login(dId, dPassword);

        if (doctorVo == null) {
            return R.error("登录失败，密码或账号错误");
        }

        return R.ok(doctorVo);
    }

    /**
     * 查看医生当天挂号
     *
     * @param dId    医生id
     * @param oStart 日期时间
     * @return 挂号数据
     */
    @RequestMapping("findOrderByNull")
    public R<List<Orders>> findOrderByNull(
            @Param(value = "dId") Integer dId,
            @RequestParam(value = "oStart") String oStart) {

        return R.ok(orderService.findOrderByNull(dId, oStart));
    }

    /**
     * 查询患者信息
     *
     * @param pId 患者id
     * @return 患者信息
     */
    @RequestMapping("findPatientById")
    public R<Patient> findPatientById(int pId) {
        return R.ok(patientUserService.findPatientById(pId));
    }

    /**
     * 通过科室查询所有医生信息 - 分页
     *
     * @param pageNum     分页页面
     * @param pageSize    分页大小
     * @param dName       医生名字
     * @param arrangeDate 排班时间
     * @param dSection    科室
     * @return 医生信息
     */
    @RequestMapping("findDoctorBySectionPage")
    public R<DoctorPageVo> findDoctorBySectionPage(
            @RequestParam(value = "pageNumber") Integer pageNum,
            @RequestParam(value = "size") Integer pageSize,
            @RequestParam(value = "query", required = false, defaultValue = "") String dName,
            @RequestParam(value = "arrangeDate", required = false, defaultValue = "") String arrangeDate,
            @RequestParam(value = "dSection") String dSection
    ) {
        // 如果dSection为空，返回错误
        if (dSection == null || dSection.trim().isEmpty()) {
            log.warn("科室参数为空");
            return R.error("科室参数不能为空");
        }
        
        String trimmedSection = dSection.trim();
        log.info("查询科室医生 - 科室: [{}], 页码: {}, 每页: {}, 医生名: [{}], 排班日期: [{}]", 
                trimmedSection, pageNum, pageSize, dName, arrangeDate);
        
        DoctorPageVo result = doctorUserService.findDoctorBySectionPage(
                pageNum, pageSize, dName, arrangeDate, trimmedSection);
        
        log.info("查询结果 - 总数: {}, 当前页数据量: {}", 
                result.getTotal(), result.getDoctors() != null ? result.getDoctors().size() : 0);
        
        return R.ok(result);
    }

    /**
     * 更新评价
     *
     * @param dId   医生id
     * @param dStar 评分
     * @return 结果
     */
    @RequestMapping("updateStar")
    public R<Boolean> updateStar(Integer dId, Double dStar) {
        if (doctorUserService.updateStar(dId, dStar)) {
            return R.ok("评价成功");
        }

        return R.error("评价失败");
    }

    /**
     * 调用存储过程 calc_doctor_visit_count，统计医生已完成诊疗次数。
     *
     * @param dId 医生id
     * @return 完成诊疗次数
     */
    @RequestMapping("visitCount")
    public R<Integer> visitCount(@RequestParam("dId") Integer dId) {
        Integer count = orderService.calcDoctorVisitCount(dId);
        return R.ok(count);
    }

    /**
     * 调试接口：获取所有科室及其医生数量
     *
     * @return 科室统计信息
     */
    @RequestMapping("debug/allSections")
    public R<java.util.Map<String, Object>> debugAllSections() {
        // 使用getAllSectionsWithDoctorCount方法获取科室信息
        List<com.shanzhu.hospital.entity.vo.SectionVo> sections = doctorUserService.getAllSectionsWithDoctorCount();
        
        java.util.Map<String, Long> sectionCount = new java.util.HashMap<>();
        java.util.List<String> allSectionNames = new java.util.ArrayList<>();
        
        for (com.shanzhu.hospital.entity.vo.SectionVo section : sections) {
            sectionCount.put(section.getSectionName(), (long) section.getDoctorCount());
            allSectionNames.add(section.getSectionName());
        }
        
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("sections", sectionCount);
        result.put("allSectionNames", allSectionNames);
        result.put("totalSections", sections.size());
        
        log.info("调试接口返回 - 科室数量: {}, 科室列表: {}", sections.size(), allSectionNames);
        
        return R.ok(result);
    }

}
