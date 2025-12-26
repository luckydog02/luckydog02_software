package com.shanzhu.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.shanzhu.hospital.entity.po.Arrange;
import com.shanzhu.hospital.entity.po.Doctor;
import com.shanzhu.hospital.entity.vo.DoctorListVo;
import com.shanzhu.hospital.entity.vo.DoctorPageVo;
import com.shanzhu.hospital.entity.vo.SectionVo;
import com.shanzhu.hospital.entity.vo.user.DoctorUserVo;
import com.shanzhu.hospital.mapper.ArrangeMapper;
import com.shanzhu.hospital.mapper.DoctorUserMapper;
import com.shanzhu.hospital.service.DoctorUserService;
import com.shanzhu.hospital.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Map;

/**
 * 医生相关 服务层
 *
 */
@Slf4j
@Service("DoctorService")
@RequiredArgsConstructor
public class DoctorUserServiceImpl extends ServiceImpl<DoctorUserMapper, Doctor> implements DoctorUserService {

    private final DoctorUserMapper doctorUserMapper;

    private final ArrangeMapper arrangeMapper;

    /**
     * 医生登录
     *
     * @param dId       医生id (账号)
     * @param dPassword 密码
     * @return 医生用户信息
     */
    @Override
    public DoctorUserVo login(Integer dId, String dPassword) {
        //通过医生id（账号）查询医生
        Doctor doctor = this.getById(dId);

        //查询不到账号记录
        if (doctor == null) {
            return null;
        }

        //密码错误
        if (BooleanUtils.isFalse(
                doctor.getDPassword().equals(DigestUtils.md5DigestAsHex(dPassword.getBytes()))
        )) {
            return null;
        }

        //组装接口数据结果
        DoctorUserVo vo = new DoctorUserVo();
        vo.setDId(doctor.getDId());
        vo.setAName(doctor.getDName());
        vo.setToken(generateToken(doctor));

        return vo;
    }

    /**
     * 查询所有医护人员信息 - 分页
     *
     * @param pageNum  分页页码
     * @param pageSize 分页大小
     * @param query    查询条件
     * @return 医护人员信息
     */
    @Override
    public DoctorPageVo findDoctorPage(Integer pageNum, Integer pageSize, String query) {
        //分页条件
        Page<Doctor> page = new Page<>(pageNum, pageSize);

        //查询条件
        LambdaQueryWrapper<Doctor> lambadaQuery = Wrappers.<Doctor>lambdaQuery()
                //模糊匹配 姓名
                .like(Doctor::getDName, query)
                //是否在职倒序（优先展示在职的）
                .orderByDesc(Doctor::getDState);

        //分页查询
        IPage<Doctor> iPage = this.page(page, lambadaQuery);

        //组装分页返回对象
        DoctorPageVo doctorVo = new DoctorPageVo();
        doctorVo.populatePage(iPage);

        return doctorVo;
    }

    /**
     * 通过id查找医生
     *
     * @param dId 医生id（账号）
     * @return 医生信息
     */
    @Override
    public Doctor findDoctor(Integer dId) {
        return this.getById(dId);
    }

    /**
     * 添加医生
     *
     * @param doctor 医生信息
     * @return 结果
     */
    @Override
    public Boolean addDoctor(Doctor doctor) {
        //通过 dId（医生账号）查询数据库是否存在记录
        Doctor existDoctor = lambdaQuery()
                //dId 相等的医生数据
                .eq(Doctor::getDId, doctor.getDId())
                .one();

        //新增的医生账号已经存在
        if (BooleanUtils.isFalse(existDoctor == null)) {
            return Boolean.FALSE;
        }

        //密码加密
        String password = DigestUtils.md5DigestAsHex(doctor.getDPassword().getBytes());

        //设置医生数据
        doctor.setDPassword(password);
        doctor.setDState(1);
        doctor.setDStar(0.00);
        doctor.setDPeople(0);

        //保存
        return this.save(doctor);
    }

    /**
     * 删除医生
     *
     * @param dId 医生账号
     * @return 结果
     */
    @Override
    public Boolean deleteDoctor(int dId) {
        //更新为不在职状态
        Doctor doctor = new Doctor();
        doctor.setDId(dId);
        doctor.setDState(0);

        return this.updateById(doctor);
    }

    /**
     * 更新医生信息
     *
     * @param doctor 医生信息
     * @return 结果
     */
    @Override
    public Boolean updateDoctor(Doctor doctor) {
        //更新
        return this.updateById(doctor);
    }

    /**
     * 通过科室查询医生
     *
     * @param dSection 科室
     * @return 医生列表
     */
    @Override
    public DoctorListVo findDoctorBySection(String dSection) {
        //查询医生
        List<Doctor> doctors = lambdaQuery()
                //科室
                .eq(Doctor::getDSection, dSection)
                //在职状态
                .eq(Doctor::getDState, 1)
                .list();

        //组装数据
        DoctorListVo doctorListVo = new DoctorListVo();
        doctorListVo.setDoctors(doctors);
        // 设置分页信息（虽然这是列表查询，但为了兼容性添加）
        doctorListVo.setTotal((long) doctors.size());
        doctorListVo.setPageNumber(1L);
        doctorListVo.setPageSize(doctors.size());

        return doctorListVo;

    }

    /**
     * 通过科室查询所有医生信息 - 分页
     *
     * @param pageNum     分页页面
     * @param pageSize    分页大小
     * @param dName       查询条件（医生名字）
     * @param arrangeDate 排班时间
     * @param dSection    科室
     * @return 医生信息
     */
    @Override
    public DoctorPageVo findDoctorBySectionPage(
            Integer pageNum, Integer pageSize, String dName, String arrangeDate, String dSection
    ) {
        log.info("开始查询科室医生 - 科室: {}, 页码: {}, 每页: {}, 医生名: {}", 
                dSection, pageNum, pageSize, dName);
        
        //分页条件
        Page<Doctor> page = new Page<>(pageNum, pageSize);

        //查询条件
        LambdaQueryWrapper<Doctor> lambdaQuery = Wrappers.<Doctor>lambdaQuery()
                //模糊匹配医生名字（只有当dName不为空时才添加此条件）
                .like(org.springframework.util.StringUtils.hasText(dName), Doctor::getDName, dName)
                //科室（必须精确匹配）
                .eq(Doctor::getDSection, dSection)
                //在职状态（只查询在职医生）
                .eq(Doctor::getDState, 1)
                //按在职状态排序
                .orderByDesc(Doctor::getDState);

        //分页查询
        IPage<Doctor> iPage = this.page(page, lambdaQuery);
        
        log.info("查询到 {} 条记录，总记录数: {}", iPage.getRecords().size(), iPage.getTotal());

        //组装分页结果
        DoctorPageVo pageVo = new DoctorPageVo();
        pageVo.populatePage(iPage);

        //查询医生是否已排班（只有当arrangeDate不为空时才查询排班信息）
        if (org.springframework.util.StringUtils.hasText(arrangeDate)) {
            List<Doctor> records = iPage.getRecords();
            for (Doctor doctor : records) {
                //查询条件
                LambdaQueryWrapper<Arrange> arrangeQuery = Wrappers.<Arrange>lambdaQuery()
                        //排班时间
                        .eq(Arrange::getArTime, arrangeDate)
                        //医生id
                        .eq(Arrange::getDId, doctor.getDId());

                //查询医生排班
                Arrange arrange = arrangeMapper.selectOne(arrangeQuery);

                if (arrange != null) {
                    //设置医生的排班信息
                    doctor.setArrangeId(arrange.getArId());
                }
            }
        }

        return pageVo;
    }

    /**
     * 更新评价
     *
     * @param dId   医生id
     * @param dStar 评分
     * @return 结果
     */
    public Boolean updateStar(Integer dId, Double dStar) {
        return doctorUserMapper.updateStar(dId, dStar);
    }

    /**
     * 获取所有科室列表（包含每个科室的医生数量）
     *
     * @return 科室列表
     */
    @Override
    public List<SectionVo> getAllSectionsWithDoctorCount() {
        // 定义所有科室及其分类和描述
        Map<String, SectionVo> sectionMap = new java.util.HashMap<>();
        
        // 固定的科室列表
        List<String> fixedSections = java.util.Arrays.asList(
            "神经内科",
            "呼吸与危重症医学科",
            "内分泌科",
            "消化内科",
            "心血管内科",
            "肾内科",
            "发热门诊",
            "手足外科",
            "普通外科",
            "肛肠外科",
            "神经外科",
            "泌尿外科",
            "骨科",
            "烧伤整形外科",
            "妇科",
            "产科",
            "儿科",
            "儿童保健科",
            "耳鼻咽喉科",
            "眼科",
            "口腔科",
            "中医科",
            "康复医学科",
            "急诊科",
            "皮肤性病科",
            "功能科"
        );
        
        // 科室分类映射（按照新的分类标准）
        Map<String, String> categoryMap = new java.util.HashMap<>();
        // 内科
        categoryMap.put("神经内科", "内科");
        categoryMap.put("呼吸与危重症医学科", "内科");
        categoryMap.put("内分泌科", "内科");
        categoryMap.put("消化内科", "内科");
        categoryMap.put("心血管内科", "内科");
        categoryMap.put("肾内科", "内科");
        categoryMap.put("发热门诊", "内科");
        // 外科
        categoryMap.put("手足外科", "外科");
        categoryMap.put("普通外科", "外科");
        categoryMap.put("肛肠外科", "外科");
        categoryMap.put("神经外科", "外科");
        categoryMap.put("泌尿外科", "外科");
        categoryMap.put("骨科", "外科");
        categoryMap.put("烧伤整形外科", "外科");
        // 妇产科
        categoryMap.put("妇科", "妇产科");
        categoryMap.put("产科", "妇产科");
        // 儿科
        categoryMap.put("儿科", "儿科");
        categoryMap.put("儿童保健科", "儿科");
        // 五官科
        categoryMap.put("耳鼻咽喉科", "五官科");
        categoryMap.put("眼科", "五官科");
        categoryMap.put("口腔科", "五官科");
        // 中医科
        categoryMap.put("中医科", "中医科");
        // 其他
        categoryMap.put("康复医学科", "其他");
        categoryMap.put("急诊科", "其他");
        categoryMap.put("皮肤性病科", "其他");
        categoryMap.put("功能科", "其他");

        // 科室描述映射
        Map<String, String> descriptionMap = new java.util.HashMap<>();
        descriptionMap.put("血液科", "拥有精湛的医疗团队，采用新的诊疗模式，温馨的家庭式病房，花园式的就医环境，先进的医疗设备，为您提供个性化的医疗服务。");
        descriptionMap.put("神经内科", "以患者为中心，以质量为核心，医疗人员认真负责，精准治疗，帮助众多患者战胜病魔，获得患者及家属的一致好评。");
        descriptionMap.put("心血管内科", "拥有多位经验丰富的临床专家，不断发展壮大，学术地位不断提升，深受众多患者青睐，帮助众多患者战胜病魔。");
        descriptionMap.put("呼吸与危重症医学科", "专业治疗呼吸系统疾病，拥有先进的医疗设备和经验丰富的医疗团队。");
        descriptionMap.put("内分泌科", "专注于内分泌疾病的诊断和治疗，为患者提供专业的医疗服务。");
        descriptionMap.put("消化内科", "专业治疗消化系统疾病，拥有先进的诊疗技术和设备。");
        descriptionMap.put("肾内科", "专业治疗肾脏疾病，为患者提供全面的诊疗服务。");
        descriptionMap.put("发热门诊", "专门处理发热相关疾病，提供及时有效的诊疗服务。");
        descriptionMap.put("手足外科", "专业治疗手足部疾病和创伤，拥有精湛的手术技术。");
        descriptionMap.put("普通外科", "提供常见外科疾病的诊断和治疗服务。");
        descriptionMap.put("肛肠外科", "专业治疗肛肠疾病，拥有丰富的临床经验。");
        descriptionMap.put("神经外科", "专业治疗神经系统外科疾病，技术先进。");
        descriptionMap.put("泌尿外科", "专业治疗泌尿系统疾病，为患者提供优质服务。");
        descriptionMap.put("烧伤整形外科", "专业治疗烧伤和整形外科疾病，技术精湛。");
        descriptionMap.put("骨科", "专业治疗骨科疾病，拥有先进的医疗设备和技术。");
        descriptionMap.put("妇科", "专业治疗妇科疾病，为女性患者提供专业服务。");
        descriptionMap.put("产科", "提供专业的产科服务，保障母婴健康。");
        descriptionMap.put("儿科", "专业治疗儿童疾病，为儿童健康保驾护航。");
        descriptionMap.put("儿童保健科", "提供儿童保健服务，关注儿童健康成长。");
        descriptionMap.put("耳鼻咽喉科", "专业治疗耳鼻咽喉疾病，技术先进。");
        descriptionMap.put("眼科", "专业治疗眼科疾病，拥有先进的诊疗设备。");
        descriptionMap.put("口腔科", "提供口腔疾病的诊断和治疗服务。");
        descriptionMap.put("中医科", "运用传统中医理论和方法，为患者提供中医诊疗服务。");
        descriptionMap.put("急诊科", "24小时提供急诊医疗服务，及时救治急危重症患者。");
        descriptionMap.put("皮肤性病科", "专业治疗皮肤疾病和性病，拥有丰富的临床经验。");
        descriptionMap.put("康复医学科", "提供康复医疗服务，帮助患者恢复功能。");
        descriptionMap.put("功能科", "提供功能检查服务，为临床诊断提供支持。");

        // 查询所有在职医生的科室
        List<Doctor> doctors = lambdaQuery()
                .eq(Doctor::getDState, 1)
                .list();

        // 统计每个科室的医生数量
        Map<String, Integer> doctorCountMap = new java.util.HashMap<>();
        for (Doctor doctor : doctors) {
            String section = doctor.getDSection();
            if (section != null && !section.isEmpty()) {
                doctorCountMap.put(section, doctorCountMap.getOrDefault(section, 0) + 1);
            }
        }

        // 使用固定的科室列表，确保所有科室都显示
        java.util.Set<String> allSections = new java.util.HashSet<>(fixedSections);

        // 构建科室列表（按照固定顺序）
        List<SectionVo> sectionList = new java.util.ArrayList<>();
        for (String sectionName : fixedSections) {
            SectionVo sectionVo = new SectionVo();
            sectionVo.setSectionName(sectionName);
            sectionVo.setCategory(categoryMap.getOrDefault(sectionName, "其他"));
            sectionVo.setDoctorCount(doctorCountMap.getOrDefault(sectionName, 0));
            sectionVo.setDescription(descriptionMap.getOrDefault(sectionName, "专业医疗服务"));
            
            // 根据分类设置颜色
            String category = sectionVo.getCategory();
            if ("内科".equals(category)) {
                sectionVo.setColor("#f56c6c");
            } else if ("外科".equals(category)) {
                sectionVo.setColor("#409EFF");
            } else if ("妇产科".equals(category)) {
                sectionVo.setColor("#F56C6C");
            } else if ("儿科".equals(category)) {
                sectionVo.setColor("#67c23a");
            } else if ("五官科".equals(category)) {
                sectionVo.setColor("#E6A23C");
            } else if ("中医科".equals(category)) {
                sectionVo.setColor("#E6A23C");
            } else {
                sectionVo.setColor("#909399");
            }
            
            sectionList.add(sectionVo);
        }

        // 按科室名称排序
        sectionList.sort((a, b) -> a.getSectionName().compareTo(b.getSectionName()));

        return sectionList;
    }

    /**
     * 生成token
     *
     * @param doctor 医生信息
     * @return token
     */
    private String generateToken(Doctor doctor) {
        Map<String, String> doctorMap = Maps.newHashMap();
        doctorMap.put("dId", String.valueOf(doctor.getDId()));
        doctorMap.put("dName", doctor.getDName());
        return JwtUtil.generateToken(doctorMap);
    }

}
