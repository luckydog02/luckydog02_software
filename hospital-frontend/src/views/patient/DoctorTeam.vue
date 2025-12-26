<!--
 * 医生团队页面
 *
-->
<template>
    <div class="doctor-team-container">
        <!-- 顶部科室标签栏 -->
        <div class="clr f14 lh34 pl20 pr20 pb15 pt15">
            <div class="fll lable fc9">科室</div>
            <div class="flr main">
                <!-- 第一行：科室大类 -->
                <div class="body clr mb5 posr ovh Js_ksList" :class="{ 'maxHeight': isCollapsed }">
                    <ul class="clr">
                        <li class="fll">
                            <a 
                                href="javascript:;" 
                                :class="['a', 'a-hover', 'db', { 'on': activeCategory === '全部' }]"
                                @click="selectCategory('全部')"
                            >
                                全部
                            </a>
                        </li>
                        <li 
                            v-for="category in visibleCategories" 
                            :key="category"
                            class="fll"
                        >
                            <a 
                                href="javascript:;" 
                                :class="['a', 'a-hover', 'db', { 'on': activeCategory === category }]"
                                @click="selectCategory(category)"
                            >
                                {{ category }}
                            </a>
                        </li>
                        <li 
                            class="fll zhankai" 
                            :class="{ 'none': !isCollapsed }"
                            v-if="categories.length > defaultVisibleCount"
                        >
                            <a 
                                href="javascript:;" 
                                class="Js_ksListZhankai green"
                                v-show="isCollapsed"
                                @click="toggleCollapse"
                            >
                                展开
                            </a>
                            <a 
                                href="javascript:;" 
                                class="none Js_ksListZhankai green"
                                v-show="!isCollapsed"
                                @click="toggleCollapse"
                            >
                                收起
                            </a>
                        </li>
                    </ul>
                </div>
                
                <!-- 第二行：细分科室（当选择大类时显示） -->
                <transition name="slide-down">
                    <div v-if="activeCategory !== '全部'" class="son-body br6 pl15 pt5 pb5 clr mb5">
                        <ul>
                            <li class="fll">
                                <a 
                                    href="javascript:;" 
                                    :class="['son-a', 'fc9', 'mr40', 'db', { 'green': activeSection === '全部' }]"
                                    @click="selectSection('全部')"
                                >
                                    全部
                                </a>
                            </li>
                            <li 
                                v-for="section in currentSubSections" 
                                :key="section"
                                class="fll"
                            >
                                <a 
                                    href="javascript:;" 
                                    :class="['son-a', 'fc9', 'mr40', 'db', { 'green': activeSection === section }]"
                                    @click="selectSection(section)"
                                >
                                    {{ section }}
                                </a>
                            </li>
                        </ul>
                    </div>
                </transition>
            </div>
        </div>
        
        <!-- 主体内容 -->
        <div class="br12 bw mb20">
            <h3 class="f24 lh30 pt25 pr20 pl20">武汉协和医院医生列表</h3>

            <!-- 加载状态 -->
            <div v-if="loading" class="loading-container">
                <i class="el-icon-loading" style="font-size: 32px; color: #409EFF;"></i>
                <p>加载中...</p>
            </div>

            <!-- 空数据提示 -->
            <el-empty v-if="!loading && filteredDoctors.length === 0" description="暂无医生数据"></el-empty>

            <!-- 医生卡片列表 -->
            <div v-if="!loading && filteredDoctors.length > 0" class="doctors-doctor-list">
                <div 
                    v-for="(doctor, index) in filteredDoctors" 
                    :key="doctor.dId"
                    :class="['cell', 'pl20', 'pr20', index === 0 ? 'pt20' : 'pt25']"
                >
                    <div :class="['pb20', 'posr', 'doc', index < filteredDoctors.length - 1 ? 'bb1' : '']">
                        <a href="javascript:;" class="img ovh" @click="viewDoctorDetail(doctor)">
                            <img 
                                class="all-img img-cover img-hover" 
                                :src="doctor.avatar || defaultAvatar" 
                                :alt="doctor.dName"
                                @error="setDefaultDoctorImg"
                            />
                        </a>
                        <div class="f14 fc9 mb5 lh24">
                            <a href="javascript:;" class="f18 a-hover" @click="viewDoctorDetail(doctor)">
                                {{ doctor.dName }}
                            </a>
                            {{ doctor.dPost || '医师' }}
                        </div>
                        <div class="f14 fc9 mb5">{{ doctor.dSection }} 武汉协和医院</div>
                        <div class="lh22 f14 fc6 mb5 pt3">
                            <p class="dib flag">
                                好评率：<span class="yellow mr20">{{ doctor.goodRate }}%</span>
                                关注数：<span class="yellow">{{ doctor.followers }}</span>
                            </p>
                        </div>
                        <a 
                            href="javascript:;" 
                            class="f14 a-hover fc6 lh22 hid2 wbwr taj"
                            @click="viewDoctorDetail(doctor)"
                        >
                            擅长：{{ doctor.dIntroduction || '暂无介绍' }}
                        </a>
                        <a 
                            href="javascript:;" 
                            style="color:#FF7C85;border-color:#FF7C85;" 
                            class="check-detail tac f14 lh30 br6"
                            @click="handleConsult(doctor)"
                        >
                            在线咨询
                        </a>
                        <a 
                            href="javascript:;" 
                            style="top:40px;" 
                            class="check-detail tac f14 lh30 br6"
                            @click="handleBook(doctor)"
                        >
                            立即预约
                        </a>
                    </div>
                </div>
            </div>

            <!-- 分页 -->
            <el-pagination
                v-if="total > 0"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="pageNumber"
                :page-sizes="[10, 20, 30, 50]"
                :page-size="size"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                class="pagination"
                style="padding: 20px;"
            >
            </el-pagination>
        </div>
    </div>
</template>

<script>
import request from "@/utils/request.js";

export default {
    name: "DoctorTeam",
    data() {
        return {
            activeCategory: "全部", // 当前选中的大类
            activeSection: "全部", // 当前选中的细分科室
            isCollapsed: false,
            defaultVisibleCount: 6, // 收起时默认显示的大类数量（不包括"全部"）
            // 科室大类
            categories: [
                "内科", "外科", "妇产科学", "儿科学", "骨外科", "眼科学", 
                "口腔科学", "五官科", "肿瘤科", "皮肤性病科", "中医学"
            ],
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
            doctors: [],
            filteredDoctors: [],
            pageNumber: 1,
            size: 10,
            total: 0,
            loading: false,
            defaultAvatar: require("@/assets/logo.png")
        };
    },
    methods: {
        loadDoctors() {
            this.loading = true;
            const params = {
                pageNumber: this.pageNumber,
                size: this.size,
                query: "",  // 医生姓名搜索
                arrangeDate: ""  // 排班日期（如果需要）
            };
            
            // 如果选择了大类
            if (this.activeCategory !== "全部") {
                // 如果选择了具体细分科室
                if (this.activeSection !== "全部") {
                    params.dSection = this.activeSection;
                    console.log("请求参数（单个科室）:", params);
                    this.loadDoctorsBySection(params);
                } else {
                    // 查询该大类下的所有科室
                    const sectionsInCategory = this.currentSubSections;
                    if (sectionsInCategory.length > 0) {
                        this.loadDoctorsByCategory(sectionsInCategory);
                    } else {
                        this.loadAllDoctors(params);
                    }
                }
            } else {
                // 查询全部医生
                this.loadAllDoctors(params);
            }
        },
        loadDoctorsBySection(params) {
            this.loading = true;
            request
                .get("doctor/findDoctorBySectionPage", { params })
                .then((res) => {
                    console.log("响应数据:", res.data);
                    if (res.data.status !== 200) {
                        this.$message.error(res.data.msg || "获取医生列表失败");
                        this.doctors = [];
                        this.filteredDoctors = [];
                        this.total = 0;
                        this.loading = false;
                        return;
                    }
                    const data = res.data.data;
                    if (!data) {
                        this.$message.warning("该科室暂无医生");
                        this.doctors = [];
                        this.filteredDoctors = [];
                        this.total = 0;
                        this.loading = false;
                        return;
                    }
                    const doctorsList = data.doctors || [];
                    if (doctorsList.length === 0) {
                        this.$message.info("该科室暂无医生");
                    }
                    this.doctors = doctorsList.map(doctor => ({
                        ...doctor,
                        goodRate: doctor.dAvgStar ? Math.round((doctor.dAvgStar / 5) * 100) : 0,
                        followers: Math.floor(Math.random() * 10000) + 1000
                    }));
                    this.total = data.total || 0;
                    this.filteredDoctors = this.doctors;
                })
                .catch((err) => {
                    console.error("医生列表请求失败:", err);
                    this.$message.error("请求失败: " + (err.response?.data?.msg || err.message || "请稍后重试"));
                    this.doctors = [];
                    this.filteredDoctors = [];
                    this.total = 0;
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        loadDoctorsByCategory(sections) {
            this.loading = true;
            // 并行查询该分类下所有科室的医生
            const promises = sections.map(section => {
                const params = {
                    pageNumber: 1,
                    size: 1000, // 获取足够多的数据
                    query: "",
                    arrangeDate: "",
                    dSection: section
                };
                return request.get("doctor/findDoctorBySectionPage", { params })
                    .then(res => {
                        if (res.data.status === 200 && res.data.data && res.data.data.doctors) {
                            return res.data.data.doctors;
                        }
                        return [];
                    })
                    .catch(err => {
                        console.error(`查询科室 ${section} 失败:`, err);
                        return [];
                    });
            });
            
            Promise.all(promises)
                .then(results => {
                    // 合并所有科室的医生
                    const allDoctors = results.flat();
                    if (allDoctors.length === 0) {
                        this.$message.info("该分类下暂无医生");
                    }
                    this.doctors = allDoctors.map(doctor => ({
                        ...doctor,
                        goodRate: doctor.dAvgStar ? Math.round((doctor.dAvgStar / 5) * 100) : 0,
                        followers: Math.floor(Math.random() * 10000) + 1000
                    }));
                    this.total = allDoctors.length;
                    this.filteredDoctors = this.doctors;
                })
                .catch(err => {
                    console.error("查询分类医生失败:", err);
                    this.$message.error("请求失败，请稍后重试");
                    this.doctors = [];
                    this.filteredDoctors = [];
                    this.total = 0;
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        loadAllDoctors(params) {
            this.loading = true;
            request
                .get("admin/findAllDoctors", { params })
                .then((res) => {
                    if (res.data.status !== 200) {
                        this.$message.error(res.data.msg || "获取医生列表失败");
                        this.doctors = [];
                        this.filteredDoctors = [];
                        this.total = 0;
                        this.loading = false;
                        return;
                    }
                    const data = res.data.data;
                    this.doctors = (data.doctors || []).map(doctor => ({
                        ...doctor,
                        goodRate: doctor.dAvgStar ? Math.round((doctor.dAvgStar / 5) * 100) : 0,
                        followers: Math.floor(Math.random() * 10000) + 1000
                    }));
                    this.total = data.total || 0;
                    this.filteredDoctors = this.doctors;
                })
                .catch((err) => {
                    console.error("医生列表请求失败:", err);
                    this.$message.error("请求失败，请稍后重试");
                    this.doctors = [];
                    this.filteredDoctors = [];
                    this.total = 0;
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        handleSizeChange(size) {
            this.size = size;
            this.pageNumber = 1;
            this.loadDoctors();
        },
        handleCurrentChange(page) {
            this.pageNumber = page;
            this.loadDoctors();
        },
        viewDoctorDetail(doctor) {
            this.$message.info(`查看${doctor.dName}医生详情`);
            // 这里可以跳转到医生详情页面或打开详情对话框
        },
        handleConsult(doctor) {
            this.$message.info(`正在为${doctor.dName}医生建立在线咨询`);
            // 这里可以跳转到在线咨询页面或打开咨询对话框
        },
        handleBook(doctor) {
            // 跳转到预约页面
            this.$router.push({
                path: '/patient/bookNow',
                query: { dId: doctor.dId }
            });
        },
        setDefaultDoctorImg(event) {
            // 设置默认头像
            event.target.src = this.defaultAvatar;
        },
        toggleCollapse() {
            this.isCollapsed = !this.isCollapsed;
        },
        selectCategory(category) {
            this.activeCategory = category;
            this.activeSection = "全部"; // 切换大类时，重置细分科室为"全部"
            this.pageNumber = 1;
            this.loadDoctors();
        },
        selectSection(section) {
            this.activeSection = section;
            this.pageNumber = 1;
            this.loadDoctors();
        }
    },
    computed: {
        // 根据收起状态，计算可见的大类标签
        visibleCategories() {
            if (this.isCollapsed) {
                // 收起时，只显示前defaultVisibleCount个大类
                return this.categories.slice(0, this.defaultVisibleCount);
            } else {
                // 展开时，显示所有大类
                return this.categories;
            }
        },
        // 根据当前选中的大类，获取对应的细分科室
        currentSubSections() {
            if (this.activeCategory === "全部") {
                return [];
            }
            // 根据大类返回对应的细分科室
            return this.allSections.filter(section => {
                const sectionCategory = this.sectionCategoryMap[section];
                // 映射大类名称
                const categoryMap = {
                    "内科": "内科",
                    "外科": "外科",
                    "妇产科学": "妇产科",
                    "儿科学": "儿科",
                    "骨外科": "外科", // 骨外科属于外科
                    "眼科学": "五官科",
                    "口腔科学": "五官科",
                    "五官科": "五官科",
                    "肿瘤科": "其他",
                    "皮肤性病科": "其他",
                    "中医学": "中医科"
                };
                return sectionCategory === categoryMap[this.activeCategory];
            });
        }
    },
    watch: {
        activeCategory() {
            this.pageNumber = 1;
            this.loadDoctors();
        },
        activeSection() {
            this.pageNumber = 1;
            this.loadDoctors();
        }
    },
    created() {
        this.loadDoctors();
    }
};
</script>

<style scoped lang="scss">
.doctor-team-container {
    background:rgb(255, 255, 255);
    min-height: calc(100vh - 70px);
}

// 顶部科室标签栏样式（按照提供的HTML结构）
.clr {
    clear: both;
    &::after {
        content: '';
        display: table;
        clear: both;
    }
}

.f14 {
    font-size: 14px;
}

.lh34 {
    line-height: 34px;
}

.pl20 {
    padding-left: 20px;
}

.pr20 {
    padding-right: 20px;
}

.pb15 {
    padding-bottom: 15px;
}

.pt15 {
    padding-top: 15px;
}

.fll {
    float: left;
}

.flr {
    float: right;
}

.lable {
    width: 60px;
}

.fc9 {
    color: #999;
}

.main {
    width: calc(100% - 80px);
}

.body {
    position: relative;
    overflow: hidden;
    transition: max-height 0.3s ease;
    
    &.maxHeight {
        max-height: 34px;
    }
}

.mb5 {
    margin-bottom: 5px;
}

.posr {
    position: relative;
}

.ovh {
    overflow: hidden;
}

.body ul {
    list-style: none;
    margin: 0;
    padding: 0;
    
    li {
        margin-right: 20px;
        margin-bottom: 5px;
        
        .a {
            display: block;
            padding: 0 15px;
            color: #333;
            text-decoration: none;
            border-radius: 4px;
            transition: all 0.3s ease;
            
            &.a-hover:hover {
                color: #409EFF;
                background: #ecf5ff;
            }
            
            &.on {
                background: #409EFF;
                color: #fff;
                
                &:hover {
                    background: #66b1ff;
                    color: #fff;
                }
            }
        }
        
        &.zhankai {
            .green {
                color: #409EFF;
                text-decoration: none;
                cursor: pointer;
                
                &:hover {
                    color: #66b1ff;
                }
            }
            
            &.none {
                display: none;
            }
        }
    }
}

.son-body {
    background:#F2F5F7;
    border-radius: 6px;
    padding-left: 15px;
    padding-top: 5px;
    padding-bottom: 5px;
    
    ul {
        list-style: none;
        margin: 0;
        padding: 0;
        
        li {
            margin-right: 40px;
            
            .son-a {
                display: block;
                color: #999;
                text-decoration: none;
                transition: all 0.3s ease;
                
                &.green {
                    color: #66b1ff;
                    font-weight: 500;
                }
                
                &:hover {
                    color: #409EFF;
                }
            }
        }
    }
}

.mr40 {
    margin-right: 40px;
}

.db {
    display: block;
}

// 细分科室行滑入动画
.slide-down-enter-active, .slide-down-leave-active {
    transition: all 0.3s ease;
}

.slide-down-enter, .slide-down-leave-to {
    opacity: 0;
    transform: translateY(-10px);
    max-height: 0;
}

.slide-down-enter-to, .slide-down-leave {
    opacity: 1;
    transform: translateY(0);
    max-height: 200px;
}

// 通用样式类
.br12 {
    border-radius: 12px;
}

.bw {
    background: #fff;
}

.mb20 {
    margin-bottom: 20px;
}

.f24 {
    font-size: 24px;
}

.lh30 {
    line-height: 30px;
}

.pt25 {
    padding-top: 25px;
}

.pr20 {
    padding-right: 20px;
}

.pl20 {
    padding-left: 20px;
}

.pb20 {
    padding-bottom: 20px;
}

.pt20 {
    padding-top: 20px;
}

.f14 {
    font-size: 14px;
}

.f18 {
    font-size: 18px;
}

.fc9 {
    color: #999;
}

.fc6 {
    color: #666;
}

.mb5 {
    margin-bottom: 5px;
}

.lh24 {
    line-height: 24px;
}

.lh22 {
    line-height: 22px;
}

.pt3 {
    padding-top: 3px;
}

.dib {
    display: inline-block;
}

.mr20 {
    margin-right: 20px;
}

.hid2 {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.wbwr {
    word-break: break-word;
}

.taj {
    text-align: justify;
}

.tac {
    text-align: center;
}

.br6 {
    border-radius: 6px;
}

.posr {
    position: relative;
}

.bb1 {
    border-bottom: 1px solid #e4e7ed;
}

.yellow {
    color: #ffa500;
}

.a-hover {
    color: #333;
    text-decoration: none;
    transition: color 0.3s;
    
    &:hover {
        color: #409EFF;
    }
}

// 医生列表容器
.doctors-doctor-list {
    display: flex;
    flex-wrap: wrap;
    padding: 0 20px 20px;
}

.cell {
    width: calc(33.333% - 20px);
    margin-right: 20px;
    margin-bottom: 20px;
    float: left;
    
    &:nth-child(3n) {
        margin-right: 0;
    }
}

// 医生卡片内容
.doc {
    .img {
        display: block;
        width: 80px;
        height: 80px;
        border-radius: 50%;
        overflow: hidden;
        margin-bottom: 15px;
        cursor: pointer;
        
        img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.3s;
            
            &:hover {
                transform: scale(1.1);
            }
        }
    }
    
    .flag {
        margin: 0;
    }
    
    .check-detail {
        display: inline-block;
        width: calc(50% - 5px);
        margin-top: 15px;
        padding: 8px 0;
        border: 1px solid;
        text-decoration: none;
        cursor: pointer;
        transition: all 0.3s;
        
        &:first-of-type {
            margin-right: 10px;
        }
        
        &:hover {
            opacity: 0.8;
        }
    }
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

.pagination {
    margin-top: 30px;
    text-align: center;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
}

// 清除浮动
.doctors-doctor-list::after {
    content: "";
    display: table;
    clear: both;
}

// 响应式设计
@media (max-width: 1200px) {
    .cell {
        width: calc(50% - 15px);
        
        &:nth-child(3n) {
            margin-right: 20px;
        }
        
        &:nth-child(2n) {
            margin-right: 0;
        }
    }
}

@media (max-width: 768px) {
    .cell {
        width: 100%;
        margin-right: 0;
        
        &:nth-child(3n) {
            margin-right: 0;
        }
    }
    
    .doc {
        .check-detail {
            width: 100%;
            margin-right: 0;
            margin-bottom: 10px;
            
            &:last-child {
                margin-bottom: 0;
            }
        }
    }
}
</style>

