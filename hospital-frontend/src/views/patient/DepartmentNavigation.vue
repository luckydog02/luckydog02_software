<!--
 * 科室导航页面
 *
-->
<template>
    <div class="department-nav-container">
        <h2 class="page-title">本院全部科室</h2>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
            <i class="el-icon-loading" style="font-size: 32px; color: #409EFF;"></i>
            <p>加载中...</p>
        </div>

        <!-- 科室分类容器 -->
        <div v-if="!loading" class="category-container">
            <div 
                v-for="category in categoryList" 
                :key="category.name"
                class="category-box"
            >
                <!-- 分类标题 -->
                <div class="category-header" :style="{ borderLeftColor: category.color }">
                    <h3 class="category-title">{{ category.name }}</h3>
                    <span class="category-count">{{ category.sections.length }}个科室</span>
                </div>
                
                <!-- 子科室网格 -->
                <div class="hos-all-ks clr">
                    <div 
                        v-for="(section, index) in category.sections" 
                        :key="section.name"
                        class="fll cell mb20 section-card"
                    >
                        <a 
                            href="javascript:;" 
                            class="db pt20 bsbb a pl15 pr15"
                            @click="viewDepartmentDetail(section)"
                        >
                            <div class="posr ks-name mb15">
                                <div class="icon-ks">
                                    <img 
                                        :src="getSectionImage(section.name)" 
                                        :alt="section.name"
                                        @error="handleImageError"
                                    />
                                </div>
                                <h4 class="fn ellipsis f20 lh30 mb5">{{ section.name }}</h4>
                                <p class="f14 fc9">医生{{ section.doctorCount }}位</p>
                            </div>
                            <div class="hid5 intro lh22 wbwr taj f14 fc6">{{ section.description }}</div>
                            <span class="auto tac db check-detail">查看详情</span>
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- 科室详情对话框 -->
        <el-dialog
            :title="currentDepartment ? currentDepartment.name + ' - 科室详情' : '科室详情'"
            :visible.sync="detailDialogVisible"
            width="70%"
            :before-close="handleCloseDetail"
            class="department-detail-dialog"
        >
            <div v-if="currentDepartmentDetail" class="department-detail-content">
                <!-- 科室介绍 -->
                <div class="detail-section">
                    <h3 class="detail-title">
                        <i class="el-icon-info"></i>
                        科室介绍
                    </h3>
                    <p class="detail-text">{{ currentDepartmentDetail.introduction }}</p>
                </div>

                <!-- 主治症状 -->
                <div class="detail-section">
                    <h3 class="detail-title">
                        <i class="el-icon-document"></i>
                        主治症状
                    </h3>
                    <p class="detail-text">{{ currentDepartmentDetail.symptoms }}</p>
                </div>

                <!-- 就诊须知 -->
                <div class="detail-section">
                    <h3 class="detail-title">
                        <i class="el-icon-warning"></i>
                        就诊须知
                    </h3>
                    <ul class="notice-list">
                        <li v-for="(notice, index) in currentDepartmentDetail.notices" :key="index" class="notice-item">
                            {{ notice }}
                        </li>
                    </ul>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="detailDialogVisible = false">关闭</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import request from "@/utils/request.js";

export default {
    name: "DepartmentNavigation",
    data() {
        return {
            // 固定的科室列表
            allSections: [
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
            ],
            // 科室分类映射
            sectionCategoryMap: {
                // 内科
                "神经内科": "内科",
                "呼吸与危重症医学科": "内科",
                "内分泌科": "内科",
                "消化内科": "内科",
                "心血管内科": "内科",
                "肾内科": "内科",
                "发热门诊": "内科",
                // 外科
                "手足外科": "外科",
                "普通外科": "外科",
                "肛肠外科": "外科",
                "神经外科": "外科",
                "泌尿外科": "外科",
                "骨科": "外科",
                "烧伤整形外科": "外科",
                // 妇产科
                "妇科": "妇产科",
                "产科": "妇产科",
                // 儿科
                "儿科": "儿科",
                "儿童保健科": "儿科",
                // 五官科
                "耳鼻咽喉科": "五官科",
                "眼科": "五官科",
                "口腔科": "五官科",
                // 中医科
                "中医科": "中医科",
                // 其他
                "康复医学科": "其他",
                "急诊科": "其他",
                "皮肤性病科": "其他",
                "功能科": "其他"
            },
            categoryList: [],
            loading: false,
            detailDialogVisible: false,
            currentDepartment: null,
            // 科室详情数据
            departmentDetails: {},
            // 科室图片映射
            sectionImages: {
                "神经内科": require("@/assets/departments/k1.png"),
                "呼吸与危重症医学科": require("@/assets/departments/k2.png"),
                "内分泌科": require("@/assets/departments/k3.png"),
                "消化内科": require("@/assets/departments/k4.png"),
                "心血管内科": require("@/assets/departments/k5.png"),
                "肾内科": require("@/assets/departments/k6.png"),
                "发热门诊": require("@/assets/departments/k7.png"),
                "手足外科": require("@/assets/departments/k8.png"),
                "普通外科": require("@/assets/departments/k9.png"),
                "肛肠外科": require("@/assets/departments/k10.png"),
                "神经外科": require("@/assets/departments/k11.png"),
                "泌尿外科": require("@/assets/departments/k12.png"),
                "骨科": require("@/assets/departments/k13.png"),
                "烧伤整形外科": require("@/assets/departments/k14.png"),
                "妇科": require("@/assets/departments/k15.png"),
                "产科": require("@/assets/departments/k16.png"),
                "儿科": require("@/assets/departments/k17.png"),
                "儿童保健科": require("@/assets/departments/k18.png"),
                "耳鼻咽喉科": require("@/assets/departments/k19.png"),
                "眼科": require("@/assets/departments/k20.png"),
                "口腔科": require("@/assets/departments/k21.png"),
                "中医科": require("@/assets/departments/k22.png"),
                "康复医学科": require("@/assets/departments/k23.png"),
                "急诊科": require("@/assets/departments/k24.png"),
                "皮肤性病科": require("@/assets/departments/k25.png"),
                "功能科": require("@/assets/departments/k26.png")
            }
        };
    },
    computed: {
        currentDepartmentDetail() {
            if (!this.currentDepartment) return null;
            return this.departmentDetails[this.currentDepartment.name] || null;
        }
    },
    methods: {
        // 获取科室图片
        getSectionImage(sectionName) {
            const imageUrl = this.sectionImages[sectionName];
            // 如果图片地址为空，使用默认logo
            if (!imageUrl || imageUrl.trim() === "") {
                return require("@/assets/logo.png");
            }
            return imageUrl;
        },
        // 图片加载错误处理
        handleImageError(event) {
            // 如果图片加载失败，使用默认logo
            event.target.src = require("@/assets/logo.jpg");
        },
        viewDepartmentDetail(dept) {
            this.currentDepartment = dept;
            this.detailDialogVisible = true;
        },
        handleCloseDetail(done) {
            this.currentDepartment = null;
            done();
        },
        initDepartmentDetails() {
            // 初始化科室详情数据
            this.departmentDetails = {
                "神经内科": {
                    introduction: "专注于中枢神经系统（脑、脊髓）、周围神经系统及肌肉疾病的诊断与治疗，如头痛、头晕、癫痫、中风、帕金森病、神经炎等。配备神经电生理检查（脑电图、肌电图）、头颅影像学检查等设备，擅长脑血管疾病急性期救治与慢性病管理。",
                    symptoms: "头晕、头痛、失眠、中风（脑梗死/脑出血）后遗症、癫痫、帕金森病、手足震颤。",
                    notices: [
                        "首次就诊建议携带既往病历、影像资料（CT、MRI 等）及药物清单；",
                        "癫痫、头痛等发作性疾病患者，建议记录发作时间、症状及诱因；",
                        "需空腹检查的项目（如血糖、血脂）请提前预约上午时段。"
                    ]
                },
                "呼吸与危重症医学科": {
                    introduction: "诊治呼吸系统疾病，包括肺炎、哮喘、慢性阻塞性肺疾病（COPD）、肺栓塞、呼吸衰竭等，拥有肺功能检测、支气管镜、呼吸机等设备，擅长危重症呼吸支持与肺部感染诊疗。",
                    symptoms: "感冒、咳嗽、哮喘、支气管炎、肺炎、咯血、胸闷、呼吸衰竭。",
                    notices: [
                        "做肺功能检查前需避免剧烈运动、吸烟及服用支气管扩张剂（遵医嘱）；",
                        "咳嗽、咳痰患者可提前留取痰液标本；",
                        "呼吸困难患者建议家属陪同就诊。"
                    ]
                },
                "内分泌科": {
                    introduction: "专注于糖尿病、甲状腺疾病（甲亢、甲减）、肾上腺疾病、垂体疾病、骨质疏松等代谢及内分泌腺体疾病的诊疗，提供血糖监测、胰岛素治疗方案制定等个性化服务。",
                    symptoms: "糖尿病、甲亢/甲减、骨质疏松、肥胖症、痛风、肾上腺疾病。",
                    notices: [
                        "糖尿病患者需空腹就诊（至少 8 小时未进食），携带近期血糖监测记录；",
                        "甲状腺疾病患者需提前停用含碘药物或食物（如海带、紫菜）1-2 周；",
                        "长期服药者需记录药物名称及剂量。"
                    ]
                },
                "消化内科": {
                    introduction: "诊治食管、胃、肠、肝、胆、胰等消化系统疾病，如胃炎、胃溃疡、肝炎、胆囊炎、结肠炎等，开展胃镜、肠镜等内镜检查及治疗。",
                    symptoms: "胃痛、胃胀、腹泻、便秘、胃炎、胃溃疡、肝硬化、胆囊炎、消化道出血。",
                    notices: [
                        "胃镜检查需空腹 6-8 小时，肠镜检查需提前遵医嘱进行肠道准备；",
                        "腹痛患者避免自行服用止痛药，以免掩盖病情；",
                        "肝功能检查需空腹，前一天避免饮酒及高脂饮食。"
                    ]
                },
                "心血管内科": {
                    introduction: "擅长冠心病、高血压、心力衰竭、心律失常、心肌病等心血管疾病的诊治，提供心电图、心脏超声、冠脉造影等检查，开展心脏介入治疗（如支架植入）。",
                    symptoms: "高血压、冠心病、心悸、胸痛、心律失常、心力衰竭。",
                    notices: [
                        "高血压患者就诊前需安静休息 15 分钟，携带近期血压记录；",
                        "服用抗凝药物（如阿司匹林）患者需提前告知医生；",
                        "心脏介入术后复查需携带出院小结及相关检查报告。"
                    ]
                },
                "肾内科": {
                    introduction: "诊治肾炎、肾病综合征、肾功能衰竭、尿路感染等肾脏疾病，开展血液透析、腹膜透析等肾脏替代治疗，擅长慢性肾病的分期管理。",
                    symptoms: "肾炎、尿毒症、水肿、蛋白尿、血尿、尿路感染、肾结石（内科治疗）。",
                    notices: [
                        "肾功能检查需空腹，建议早晨就诊；",
                        "透析患者需按预约时间就诊，携带透析记录；",
                        "水肿患者可记录每日体重变化。"
                    ]
                },
                "发热门诊": {
                    introduction: "专门接诊发热（体温≥37.3℃）患者，排查感染性疾病（如流感、新冠、细菌性肺炎等），提供快速检测与隔离诊疗服务，避免交叉感染。",
                    symptoms: "主治体温≥37.3℃的各类发热患者，及具有呼吸道症状的急症。",
                    notices: [
                        "就诊时需佩戴口罩，配合流行病学史调查；",
                        "如实告知近期旅行史、接触史及症状出现时间；",
                        "儿童、老年患者建议家属陪同，携带既往基础疾病资料。"
                    ]
                },
                "手足外科": {
                    introduction: "诊治手足外伤（骨折、肌腱损伤）、先天性手足畸形、甲沟炎、糖尿病足等，开展断肢再植、手足功能重建等手术。",
                    symptoms: "手脚外伤、断指再植、足畸形纠正。",
                    notices: [
                        "外伤患者需尽快就诊，避免伤口污染或延误治疗；",
                        "术后患者需遵医嘱进行功能锻炼，定期换药；",
                        "糖尿病足患者需提前控制血糖，携带血糖监测记录。"
                    ]
                },
                "普通外科": {
                    introduction: "涵盖腹部外科（阑尾炎、疝气、胆囊疾病）、乳腺疾病、甲状腺疾病等，开展腹腔镜微创手术，擅长腹部急症（如肠梗阻、消化道穿孔）的救治。",
                    symptoms: "甲状腺结节、乳腺肿块、阑尾炎、腹股沟疝（疝气）、胆结石、痔疮。",
                    notices: [
                        "腹部手术前需空腹，遵医嘱停用抗凝药物；",
                        "乳腺检查建议避开月经期，穿着宽松衣物；",
                        "术后注意伤口护理，观察有无出血、感染迹象。"
                    ]
                },
                "肛肠外科": {
                    introduction: "诊治痔疮、肛裂、肛瘘、肛周脓肿、结直肠息肉等肛肠疾病，开展痔疮微创手术、肠镜下息肉切除等治疗。",
                    symptoms: "复杂痔疮、肛瘘、肛周脓肿、便血。",
                    notices: [
                        "肛门检查前需清洁肛周，避免过度紧张；",
                        "术后需注意饮食清淡，保持排便通畅；",
                        "长期便血患者需提前告知有无贫血症状。"
                    ]
                },
                "神经外科": {
                    introduction: "处理脑外伤、脑肿瘤、脑出血、脊柱脊髓疾病等，开展开颅手术、脊柱微创手术，配备神经导航、显微镜等精密设备。",
                    symptoms: "脑出血（需手术）、脑肿瘤、脑外伤、垂体瘤。",
                    notices: [
                        "急性脑外伤患者需立即就诊，避免剧烈搬动；",
                        "术后患者需定期复查头颅 CT/MRI，记录意识、肢体活动变化；",
                        "癫痫病史患者需提前告知，避免术中诱发。"
                    ]
                },
                "泌尿外科": {
                    introduction: "诊治泌尿系统疾病（肾结石、前列腺增生、膀胱炎、尿道狭窄等），开展输尿管镜、腹腔镜等微创手术，擅长男性生殖系统疾病诊疗。",
                    symptoms: "肾结石、输尿管结石、前列腺疾病、尿频尿急、肾囊肿。",
                    notices: [
                        "泌尿系超声检查需憋尿（膀胱充盈）；",
                        "前列腺检查前需排空大便，避免性生活 1-2 天；",
                        "结石患者需携带既往结石成分分析报告（如有）。"
                    ]
                },
                "骨科": {
                    introduction: "涵盖骨折、关节炎、腰椎间盘突出、骨质疏松、股骨头坏死等疾病，开展关节置换、脊柱手术、创伤修复等治疗。",
                    symptoms: "骨折、脱臼、颈椎病、腰间盘突出、关节炎、运动损伤。",
                    notices: [
                        "骨折患者需固定伤肢，避免活动加重损伤；",
                        "关节置换术后需按计划进行康复训练；",
                        "腰痛患者就诊时可描述疼痛性质（如酸痛、刺痛）及诱发因素。"
                    ]
                },
                "烧伤整形外科": {
                    introduction: "处理烧伤、烫伤、冻伤及瘢痕修复，开展美容整形（如疤痕切除、皮肤移植）、先天畸形矫正（如唇腭裂）等手术。",
                    symptoms: "各类烫伤、烧伤修复、体表肿物切除、疤痕整形。",
                    notices: [
                        "急性烧伤患者需立即用冷水冲洗伤口（避免冰块直接接触），尽快就诊；",
                        "整形手术前需明确手术预期，了解术后恢复期注意事项；",
                        "瘢痕修复患者需携带既往治疗记录（如手术史、放疗史）。"
                    ]
                },
                "妇科": {
                    introduction: "诊治女性生殖系统疾病，如阴道炎、子宫肌瘤、卵巢囊肿、宫颈癌筛查等，开展宫腔镜、腹腔镜微创手术及妇科肿瘤治疗。",
                    symptoms: "阴道炎、盆腔炎、子宫肌瘤、月经不调、痛经、更年期综合征。",
                    notices: [
                        "妇科检查需避开月经期，检查前 24 小时避免性生活、阴道冲洗；",
                        "备孕或疑似怀孕者需提前告知医生；",
                        "宫颈筛查（TCT、HPV）建议在月经干净 3-7 天后进行。"
                    ]
                },
                "产科": {
                    introduction: "提供孕期保健、产前检查、自然分娩及剖宫产服务，处理妊娠期并发症（如妊娠糖尿病、高血压），开展产后康复指导。",
                    symptoms: "孕期检查、产前咨询、顺产/剖宫产、产后恢复。",
                    notices: [
                        "首次产检需携带身份证，建立孕期档案；",
                        "孕晚期（≥37 周）出现腹痛、见红、破水等症状需立即就诊；",
                        "分娩时需准备待产包（衣物、证件、产检报告等）。"
                    ]
                },
                "儿科": {
                    introduction: "诊治 0-14 岁儿童常见病（如感冒、肺炎、腹泻）、多发病及疑难病，涵盖呼吸、消化、神经等系统疾病，设儿童专用检查设备。",
                    symptoms: "14周岁以下儿童出现的各类内科疾病（发热、咳嗽、腹泻等）。",
                    notices: [
                        "婴幼儿就诊前需准备好喂养用品，避免过度饥饿哭闹；",
                        "记录患儿症状（如体温、饮食、精神状态）及发病时间；",
                        "疫苗接种后出现不适需携带接种记录就诊。"
                    ]
                },
                "儿童保健科": {
                    introduction: "专注于儿童生长发育监测（身高、体重、智力）、营养指导、心理行为评估，提供疫苗接种、早教咨询等服务。",
                    symptoms: "儿童生长发育评估、疫苗接种咨询、多动症、营养指导。",
                    notices: [
                        "定期体检需按年龄预约（如 1 月龄、3 月龄），携带保健手册；",
                        "生长迟缓、肥胖等问题需提前记录饮食、运动习惯；",
                        "疫苗接种前需告知儿童健康状况（如过敏史、发热史）。"
                    ]
                },
                "耳鼻咽喉科": {
                    introduction: "诊治中耳炎、鼻窦炎、咽炎、喉炎、耳鸣、耳聋、扁桃体炎等，开展喉镜、鼻内镜检查及听力测试。",
                    symptoms: "鼻炎、中耳炎、咽炎、打呼噜（鼾症）、听力下降、声嘶。",
                    notices: [
                        "听力检查前需清洁外耳道，避免佩戴耳机；",
                        "鼻出血患者可先自行按压鼻翼止血，就诊时告知出血量及频率；",
                        "喉镜检查前需空腹（避免呕吐）。"
                    ]
                },
                "眼科": {
                    introduction: "诊治近视、远视、白内障、青光眼、结膜炎、视网膜病变等，开展视力检查、眼压测量、眼底检查及眼科手术。",
                    symptoms: "近视、白内障、青光眼、结膜炎（红眼病）、视力模糊。",
                    notices: [
                        "视力检查前避免长时间用眼（如看手机、电脑）；",
                        "青光眼患者需定期监测眼压，携带既往检查报告；",
                        "眼部手术（如白内障）前需告知有无高血压、糖尿病等基础病。"
                    ]
                },
                "口腔科": {
                    introduction: "涵盖补牙、拔牙、洗牙、正畸（牙齿矫正）、种植牙、口腔颌面外科疾病（如智齿冠周炎）等诊疗服务。",
                    symptoms: "牙痛、拔牙、补牙、牙龈出血、口腔溃疡、牙齿矫正。",
                    notices: [
                        "拔牙前需避免空腹，女性避开月经期；",
                        "正畸初诊需预留充足时间（约 1-2 小时）；",
                        "口腔手术术后 24 小时内避免漱口、吸吮动作。"
                    ]
                },
                "中医科": {
                    introduction: "运用中医理论诊治内科、妇科、儿科等疾病，如失眠、脾胃虚弱、月经不调、颈肩腰腿痛等，提供中药调理、针灸、推拿、艾灸等治疗。",
                    symptoms: "各类慢性病及亚健康状态。",
                    notices: [
                        "服用中药期间需遵医嘱忌口（如避免辛辣、生冷食物）；",
                        "针灸治疗前需告知有无晕针史、皮肤过敏史；",
                        "慢性病调理需按疗程就诊，携带既往中药处方。"
                    ]
                },
                "康复医学科": {
                    introduction: "针对中风后遗症、骨折术后、关节炎、脑瘫等患者提供康复治疗，包括物理治疗（PT）、作业治疗（OT）、言语治疗等，改善功能障碍。",
                    symptoms: "偏瘫恢复、肌肉萎缩、功能障碍、术后康复训练。",
                    notices: [
                        "康复训练需按计划进行，穿宽松衣物及防滑鞋；",
                        "术后康复患者需携带手术记录及影像学资料；",
                        "老年患者建议家属陪同，避免跌倒。"
                    ]
                },
                "急诊科": {
                    introduction: "24 小时接诊急危重症患者，如心脏骤停、严重外伤、急性胸痛、脑卒中等，提供快速诊断与抢救服务，开通绿色通道。",
                    symptoms: "各类急性外伤、中毒、昏迷等危及生命的紧急情况。",
                    notices: [
                        "急重症患者优先就诊，家属需配合医生告知病情（如发病时间、症状）；",
                        "携带患者身份证、医保卡及既往病历（如有）；",
                        "轻微外伤可先到急诊清创室处理，无需排队。"
                    ]
                },
                "皮肤性病科": {
                    introduction: "诊治皮炎、湿疹、荨麻疹、银屑病（牛皮癣）、痤疮等皮肤病，及常见性传播疾病（如梅毒、尖锐湿疣），开展过敏原检测、光疗等治疗。",
                    symptoms: "湿疹、荨麻疹、皮炎、痤疮、脱发、各类性传播疾病。",
                    notices: [
                        "就诊前避免涂抹药膏（以免影响皮损观察）；",
                        "皮肤病患者需记录发病部位、症状变化及接触史；",
                        "性传播疾病诊疗严格保密，患者需如实告知病史。"
                    ]
                },
                "功能科": {
                    introduction: "提供影像学及功能检查服务，包括超声（B 超、彩超）、心电图、脑电图、肺功能检测等，为临床诊断提供依据。",
                    symptoms: "不直接接诊患者，由临床科室开具检查申请。",
                    notices: [
                        "腹部超声需空腹 8 小时，妇科 / 前列腺超声需憋尿；",
                        "心电图检查前避免剧烈运动、情绪激动；",
                        "检查报告通常当天可取，特殊项目需预约取单时间。"
                    ]
                }
            };
        },
        loadDepartmentData() {
            this.loading = true;
            request.get("patient/section/list").then(res => {
                if (res.data.status !== 200) {
                    this.$message.error(res.data.msg || "获取科室列表失败");
                    this.loading = false;
                    return;
                }
                const data = res.data.data || [];
                
                // 创建科室数据映射（从后端获取的）
                const backendSectionMap = {};
                data.forEach(dept => {
                    backendSectionMap[dept.sectionName] = {
                        doctorCount: dept.doctorCount || 0,
                        description: dept.description || "专业医疗服务",
                        color: dept.color || this.getDefaultColor(this.sectionCategoryMap[dept.sectionName])
                    };
                });
                
                // 使用固定的科室列表，确保所有科室都显示
                const allDepartments = this.allSections.map(sectionName => {
                    const backendData = backendSectionMap[sectionName] || {};
                    return {
                        name: sectionName,
                        category: this.sectionCategoryMap[sectionName] || "其他",
                        color: backendData.color || this.getDefaultColor(this.sectionCategoryMap[sectionName]),
                        doctorCount: backendData.doctorCount || 0,
                        description: backendData.description || this.getDefaultDescription(sectionName)
                    };
                });
                
                // 按分类组织数据
                this.organizeByCategory(allDepartments);
            }).catch(err => {
                console.error("科室列表请求失败:", err);
                // 如果请求失败，使用固定科室列表（医生数量为0）
                const allDepartments = this.allSections.map(sectionName => ({
                    name: sectionName,
                    category: this.sectionCategoryMap[sectionName] || "其他",
                    color: this.getDefaultColor(this.sectionCategoryMap[sectionName]),
                    doctorCount: 0,
                    description: this.getDefaultDescription(sectionName)
                }));
                this.organizeByCategory(allDepartments);
                if (err.response) {
                    this.$message.error(`请求失败: ${err.response.status} ${err.response.statusText}`);
                } else if (err.message) {
                    this.$message.error(`请求失败: ${err.message}`);
                } else {
                    this.$message.error("请求失败，请检查网络连接或联系管理员");
                }
            }).finally(() => {
                this.loading = false;
            });
        },
        organizeByCategory(departments) {
            // 定义分类配置
            const categoryConfig = [
                { name: "内科", color: "#f56c6c" },
                { name: "外科", color: "#409EFF" },
                { name: "妇产科", color: "#F56C6C" },
                { name: "儿科", color: "#67c23a" },
                { name: "五官科", color: "#E6A23C" },
                { name: "中医科", color: "#E6A23C" },
                { name: "其他", color: "#909399" }
            ];
            
            // 按分类组织
            this.categoryList = categoryConfig.map(category => {
                const sections = departments.filter(dept => dept.category === category.name);
                return {
                    name: category.name,
                    color: category.color,
                    sections: sections
                };
            }).filter(category => category.sections.length > 0); // 只显示有科室的分类
        },
        getDefaultDescription(sectionName) {
            const descriptions = {
                "神经内科": "专注于中枢神经系统（脑、脊髓）、周围神经系统及肌肉疾病的诊断与治疗，如头痛、头晕、癫痫、中风、帕金森病、神经炎等。配备神经电生理检查（脑电图、肌电图）、头颅影像学检查等设备，擅长脑血管疾病急性期救治与慢性病管理。",
                "呼吸与危重症医学科": "诊治呼吸系统疾病，包括肺炎、哮喘、慢性阻塞性肺疾病（COPD）、肺栓塞、呼吸衰竭等，拥有肺功能检测、支气管镜、呼吸机等设备，擅长危重症呼吸支持与肺部感染诊疗。",
                "内分泌科": "专注于内分泌疾病的诊断和治疗，为患者提供专业的医疗服务。",
                "消化内科": "专业治疗消化系统疾病，拥有先进的诊疗技术和设备。",
                "心血管内科": "拥有多位经验丰富的临床专家，不断发展壮大，学术地位不断提升。",
                "肾内科": "专业治疗肾脏疾病，为患者提供全面的诊疗服务。",
                "发热门诊": "专门处理发热相关疾病，提供及时有效的诊疗服务。",
                "手足外科": "专业治疗手足部疾病和创伤，拥有精湛的手术技术。",
                "普通外科": "提供常见外科疾病的诊断和治疗服务。",
                "肛肠外科": "专业治疗肛肠疾病，拥有丰富的临床经验。",
                "神经外科": "专业治疗神经系统外科疾病，技术先进。",
                "泌尿外科": "专业治疗泌尿系统疾病，为患者提供优质服务。",
                "骨科": "专业治疗骨科疾病，拥有先进的医疗设备和技术。",
                "烧伤整形外科": "专业治疗烧伤和整形外科疾病，技术精湛。",
                "妇科": "专业治疗妇科疾病，为女性患者提供专业服务。",
                "产科": "提供专业的产科服务，保障母婴健康。",
                "儿科": "专业治疗儿童疾病，为儿童健康保驾护航。",
                "儿童保健科": "提供儿童保健服务，关注儿童健康成长。",
                "耳鼻咽喉科": "专业治疗耳鼻咽喉疾病，技术先进。",
                "眼科": "专业治疗眼科疾病，拥有先进的诊疗设备。",
                "口腔科": "提供口腔疾病的诊断和治疗服务。",
                "中医科": "运用传统中医理论和方法，为患者提供中医诊疗服务。",
                "康复医学科": "提供康复医疗服务，帮助患者恢复功能。",
                "急诊科": "24小时提供急诊医疗服务，及时救治急危重症患者。",
                "皮肤性病科": "专业治疗皮肤疾病和性病，拥有丰富的临床经验。",
                "功能科": "提供功能检查服务，为临床诊断提供支持。"
            };
            return descriptions[sectionName] || "专业医疗服务";
        },
        getDefaultColor(category) {
            const colorMap = {
                "内科": "#f56c6c",
                "外科": "#409EFF",
                "妇产科": "#F56C6C",
                "儿科": "#67c23a",
                "五官科": "#E6A23C",
                "中医科": "#E6A23C",
                "其他": "#909399"
            };
            return colorMap[category] || "#909399";
        }
    },
    created() {
        this.initDepartmentDetails();
        this.loadDepartmentData();
    }
};
</script>

<style scoped lang="scss">
.department-nav-container {
    padding: 30px;
    max-width: 1600px;
    margin: 0 auto;
    background: #f5f7fa;
    min-height: calc(100vh - 70px);
}

.page-title {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 30px;
    text-align: center;
}

.loading-container {
    text-align: center;
    padding: 60px 20px;
    
    p {
        margin-top: 15px;
        color: #909399;
        font-size: 14px;
    }
}

.category-container {
    display: flex;
    flex-direction: column;
    gap: 30px;
}

.category-box {
    background: #fff;
    border-radius: 12px;
    padding: 25px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    
    &:hover {
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
        transform: translateY(-4px);
    }
}

.category-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-bottom: 20px;
    margin-bottom: 20px;
    border-bottom: 2px solid #f0f2f5;
    border-left: 4px solid;
    padding-left: 15px;
    
    .category-title {
        font-size: 22px;
        font-weight: 600;
        color: #303133;
        margin: 0;
    }
    
    .category-count {
        font-size: 14px;
        color: #909399;
        background: #f5f7fa;
        padding: 4px 12px;
        border-radius: 12px;
    }
}

// 新格式样式
.hos-all-ks {
    &::after {
        content: '';
        display: table;
        clear: both;
    }
}

.clr {
    &::after {
        content: '';
        display: table;
        clear: both;
    }
}

.fll {
    float: left;
}

.cell {
    width: calc((100% - 40px) / 3); // 确保每行正好3个，减去2个间距(20px * 2)
    margin-right: 20px;
    margin-bottom: 20px;
    box-sizing: border-box;
    
    &:nth-child(3n) {
        margin-right: 0;
    }
    
    // 确保每行正好3个，从第4个开始换行
    &:nth-child(3n+1) {
        clear: left;
    }
}

.mb20 {
    margin-bottom: 20px;
}

.pt20 {
    padding-top: 20px;
}

.pl15 {
    padding-left: 15px;
}

.pr15 {
    padding-right: 15px;
}

.mr20 {
    margin-right: 20px;
}

.bsbb {
    box-sizing: border-box;
}

.db {
    display: block;
}

.posr {
    position: relative;
}

.mb15 {
    margin-bottom: 15px;
}

.mb5 {
    margin-bottom: 5px;
}

.fn {
    font-weight: normal;
}

.ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.f20 {
    font-size: 20px;
}

.lh30 {
    line-height: 30px;
}

.f14 {
    font-size: 14px;
}

.fc9 {
    color: #999;
}

.fc6 {
    color: #666;
}

.hid5 {
    display: -webkit-box;
    -webkit-line-clamp: 5;
    -webkit-box-orient: vertical;
    overflow: hidden;
    min-height: 110px;
}

.intro {
    line-height: 22px;
    word-break: break-word;
    text-align: justify;
    margin-bottom: 15px;
}

.lh22 {
    line-height: 22px;
}

.wbwr {
    word-break: break-word;
}

.taj {
    text-align: justify;
}

.auto {
    margin: 0 auto;
}

.tac {
    text-align: center;
}

.section-card {
    background: #fff;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
    height: 100%;
    
    &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
        transition: left 0.5s;
        z-index: 1;
    }
    
    &:hover {
        transform: translateY(-6px);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
        border-color: transparent;
        
        &::before {
            left: 100%;
        }
        
        .icon-ks {
            transform: scale(1.1) rotate(5deg);
        }
        
        .ks-name h4 {
            color: #409EFF;
        }
        
        .check-detail {
            background: #409EFF;
            color: #fff;
        }
    }
    
    .a {
        text-decoration: none;
        color: inherit;
        display: block;
        height: 100%;
    }
    
    .ks-name {
        position: relative;
        padding-left: 70px;
        min-height: 60px;
        
        .icon-ks {
            position: absolute;
            left: 0;
            top: 0;
            width: 60px;
            height: 60px;
            border-radius: 8px;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            overflow: hidden;
            background: #f5f7fa;
            border: 1px solid rgba(0, 0, 0, 0.05);
            
            img {
                width: 100%;
                height: 100%;
                object-fit: cover;
                display: block;
            }
        }
        
        h4 {
            font-size: 20px;
            font-weight: 600;
            color: #303133;
            margin: 0 0 5px 0;
            transition: color 0.3s;
        }
        
        p {
            font-size: 14px;
            color: #999;
            margin: 0;
        }
    }
    
    .check-detail {
        display: block;
        width: 120px;
        margin: 15px auto 15px;
        padding: 8px 20px;
        background: #f5f7fa;
        color: #409EFF;
        border-radius: 4px;
        font-size: 14px;
        transition: all 0.3s ease;
    }
}

// 响应式设计
@media (max-width: 1200px) {
    .cell {
        width: calc(50% - 10px);
        
        &:nth-child(3n) {
            margin-right: 20px;
        }
        
        &:nth-child(2n) {
            margin-right: 0;
        }
    }
}

@media (max-width: 768px) {
    .department-nav-container {
        padding: 20px 15px;
    }
    
    .category-box {
        padding: 20px 15px;
    }
    
    .cell {
        width: 100%;
        margin-right: 0;
        
        &:nth-child(3n) {
            margin-right: 0;
        }
    }
    
    .section-card {
        .ks-name {
            padding-left: 60px;
            
            .icon-ks {
                width: 50px;
                height: 50px;
            }
            
            h4 {
                font-size: 18px;
            }
        }
        
        .intro {
            font-size: 13px;
            min-height: 90px;
        }
    }
}

// 科室详情对话框样式
.department-detail-dialog {
    .department-detail-content {
        max-height: 60vh;
        overflow-y: auto;
        padding: 10px 0;
    }

    .detail-section {
        margin-bottom: 30px;
        
        &:last-child {
            margin-bottom: 0;
        }
    }

    .detail-title {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 15px;
        padding-bottom: 10px;
        border-bottom: 2px solid #409EFF;
        display: flex;
        align-items: center;
        gap: 8px;

        i {
            color: #409EFF;
            font-size: 20px;
        }
    }

    .detail-text {
        font-size: 15px;
        line-height: 1.8;
        color: #606266;
        text-align: justify;
        margin: 0;
    }

    .notice-list {
        list-style: none;
        padding: 0;
        margin: 0;

        .notice-item {
            font-size: 14px;
            line-height: 1.8;
            color: #606266;
            padding: 10px 15px;
            margin-bottom: 10px;
            background: #f5f7fa;
            border-left: 3px solid #409EFF;
            border-radius: 4px;
            position: relative;
            padding-left: 25px;

            &::before {
                content: '•';
                position: absolute;
                left: 10px;
                color: #409EFF;
                font-weight: bold;
                font-size: 18px;
            }

            &:last-child {
                margin-bottom: 0;
            }
        }
    }
}
</style>

