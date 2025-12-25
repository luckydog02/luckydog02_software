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
        //分页条件
        Page<Doctor> page = new Page<>(pageNum, pageSize);

        //查询条件
        LambdaQueryWrapper<Doctor> lambdaQuery = Wrappers.<Doctor>lambdaQuery()
                //模糊匹配医生名字
                .like(Doctor::getDName, dName)
                //科室
                .eq(Doctor::getDSection, dSection)
                //按在职状态排序
                .orderByDesc(Doctor::getDState);

        //分页查询
        IPage<Doctor> iPage = this.page(page, lambdaQuery);

        //组装分页结果
        DoctorPageVo pageVo = new DoctorPageVo();
        pageVo.populatePage(iPage);

        //查询医生是否已排班
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
        
        // 科室分类映射
        Map<String, String> categoryMap = new java.util.HashMap<>();
        categoryMap.put("血液科", "内科");
        categoryMap.put("神经内科", "内科");
        categoryMap.put("心血管内科", "内科");
        categoryMap.put("呼吸与危重症医学科", "内科");
        categoryMap.put("内分泌科", "内科");
        categoryMap.put("消化内科", "内科");
        categoryMap.put("肾内科", "内科");
        categoryMap.put("发热门诊", "内科");
        categoryMap.put("手足外科", "外科");
        categoryMap.put("普通外科", "外科");
        categoryMap.put("肛肠外科", "外科");
        categoryMap.put("神经外科", "外科");
        categoryMap.put("泌尿外科", "外科");
        categoryMap.put("烧伤整形外科", "外科");
        categoryMap.put("骨科", "骨外科");
        categoryMap.put("妇科", "妇产科学");
        categoryMap.put("产科", "妇产科学");
        categoryMap.put("儿科", "儿科学");
        categoryMap.put("儿童保健科", "儿科学");
        categoryMap.put("耳鼻咽喉科", "五官科");
        categoryMap.put("眼科", "眼科学");
        categoryMap.put("口腔科", "口腔科学");
        categoryMap.put("中医科", "中医学");
        categoryMap.put("急诊科", "其他科室");
        categoryMap.put("皮肤性病科", "皮肤性病科");
        categoryMap.put("康复医学科", "康复医学科");
        categoryMap.put("营养科", "营养科");
        categoryMap.put("麻醉医学科", "麻醉医学科");
        categoryMap.put("医学影像科", "医学影像科");

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
        descriptionMap.put("营养科", "提供营养咨询和指导服务，促进患者健康。");
        descriptionMap.put("麻醉医学科", "提供麻醉医疗服务，保障手术安全。");
        descriptionMap.put("医学影像科", "提供医学影像检查服务，为临床诊断提供支持。");

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

        // 获取所有唯一的科室名称
        java.util.Set<String> allSections = new java.util.HashSet<>(doctorCountMap.keySet());
        // 添加可能没有医生的科室（从categoryMap中获取）
        allSections.addAll(categoryMap.keySet());

        // 构建科室列表
        List<SectionVo> sectionList = new java.util.ArrayList<>();
        for (String sectionName : allSections) {
            SectionVo sectionVo = new SectionVo();
            sectionVo.setSectionName(sectionName);
            sectionVo.setCategory(categoryMap.getOrDefault(sectionName, "其他科室"));
            sectionVo.setDoctorCount(doctorCountMap.getOrDefault(sectionName, 0));
            sectionVo.setDescription(descriptionMap.getOrDefault(sectionName, "专业医疗服务"));
            
            // 根据分类设置颜色
            String category = sectionVo.getCategory();
            if ("内科".equals(category)) {
                sectionVo.setColor("#f56c6c");
            } else if ("外科".equals(category)) {
                sectionVo.setColor("#409EFF");
            } else if ("骨外科".equals(category)) {
                sectionVo.setColor("#67c23a");
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
