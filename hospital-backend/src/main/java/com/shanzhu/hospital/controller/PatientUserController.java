package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.po.Orders;
import com.shanzhu.hospital.entity.po.Patient;
import com.shanzhu.hospital.entity.vo.DoctorListVo;
import com.shanzhu.hospital.entity.vo.SectionVo;
import com.shanzhu.hospital.entity.vo.user.PatientUserVo;
import com.shanzhu.hospital.service.DoctorUserService;
import com.shanzhu.hospital.service.OrderService;
import com.shanzhu.hospital.service.PatientUserService;
import com.shanzhu.hospital.utils.PdfUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 病患相关 控制层
 *
 */
@RestController
@RequestMapping("patient")
@RequiredArgsConstructor
public class PatientUserController {

    private final DoctorUserService doctorUserService;

    private final PatientUserService patientUserService;

    private final OrderService orderService;

    /**
     * 病患登录
     *
     * @param pId       病患id（账号）
     * @param pPassword 密码
     * @return 病患信息
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public R<PatientUserVo> login(
            @RequestParam(value = "pId") Integer pId,
            @RequestParam(value = "pPassword") String pPassword
    ) {
        //登录病患
        PatientUserVo patientVo = patientUserService.login(pId, pPassword);

        if (patientVo == null) {
            return R.error("登录失败，密码或账号错误");
        }

        return R.ok(patientVo);
    }

    /**
     * 通过科室查询医生
     *
     * @param dSection 科室（支持dSection和section两个参数名）
     * @param section  科室（兼容参数名）
     * @return 医生列表
     */
    @RequestMapping("findDoctorBySection")
    public R<DoctorListVo> findDoctorBySection(
            @RequestParam(value = "dSection", required = false) String dSection,
            @RequestParam(value = "section", required = false) String section
    ) {
        // 支持dSection和section两个参数名，优先使用dSection
        String sectionName = dSection != null ? dSection : section;
        if (sectionName == null || sectionName.isEmpty()) {
            return R.error("科室参数不能为空");
        }
        return R.ok(doctorUserService.findDoctorBySection(sectionName));
    }

    /**
     * 添加挂号单
     *
     * @param order 挂号单信息
     * @param arId  排班id
     * @return 结果
     */
    @RequestMapping("addOrder")
    public R<Boolean> addOrder(Orders order, String arId) {
        try {
            if (BooleanUtils.isTrue(orderService.addOrder(order, arId))) {
                return R.ok("挂号成功");
            }
            return R.error("挂号失败, 当前时间段存在还未诊断的挂号单");
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            return R.error("挂号失败: " + e.getMessage());
        }
    }

    /**
     * 查询病患挂号
     *
     * @param pId 病患id
     * @return 挂号信息
     */
    @RequestMapping("findOrderByPid")
    public R<List<Orders>> findOrderByPid(@RequestParam(value = "pId") Integer pId) {
        return R.ok(orderService.findOrderByPid(pId));
    }

    /**
     * 添加病患
     *
     * @param patient 病患信息
     * @return 结果
     */
    @RequestMapping("addPatient")
    public R<Boolean> addPatient(Patient patient) {
        if (BooleanUtils.isTrue(patientUserService.addPatient(patient))) {
            return R.ok("添加成功");
        }

        return R.error("添加失败");
    }

    /**
     * 导出挂号单pdf
     *
     * @param response httpResponse
     * @param oId      挂号单id
     * @throws Exception 异常
     */
    @GetMapping("/pdf")
    public void exportPDF(HttpServletResponse response, Integer oId) throws Exception {
        Orders order = orderService.findOrderByOid(oId);
        //导出pdf
        PdfUtil.exportPatientOrder(response, order);
    }

    /**
     * 统计患者年龄分布
     *
     * @return 年龄分布
     */
    @RequestMapping("patientAge")
    public R<List<Integer>> patientAge() {
        return R.ok(patientUserService.patientAge());
    }

    /**
     * 获取所有科室列表（包含每个科室的医生数量）
     *
     * @return 科室列表
     */
    @RequestMapping("section/list")
    public R<List<com.shanzhu.hospital.entity.vo.SectionVo>> getSectionList() {
        return R.ok(doctorUserService.getAllSectionsWithDoctorCount());
    }
}
