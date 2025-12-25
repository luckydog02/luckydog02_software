<!--
 * 医生团队页面
 *
-->
<template>
    <div class="doctor-team-container">
        <h2 class="page-title">武汉协和医院医生列表</h2>
        
        <!-- 科室筛选 -->
        <div class="filter-section">
            <span class="filter-label">科室：</span>
            <div class="filter-tabs">
                <div 
                    :class="['filter-tab', { 'active': activeSection === '全部' }]"
                    @click="activeSection = '全部'"
                >
                    全部
                </div>
                <div 
                    v-for="section in sections" 
                    :key="section"
                    :class="['filter-tab', { 'active': activeSection === section }]"
                    @click="activeSection = section"
                >
                    {{ section }}
                </div>
            </div>
        </div>

        <!-- 医生列表 -->
        <div class="doctor-list">
            <div 
                v-for="doctor in filteredDoctors" 
                :key="doctor.dId"
                class="doctor-card"
            >
                <div class="doctor-avatar">
                    <img :src="doctor.avatar || defaultAvatar" alt="医生头像" />
                </div>
                <div class="doctor-info">
                    <h3 class="doctor-name">{{ doctor.dName }} {{ doctor.dPost }}</h3>
                    <p class="doctor-department">{{ doctor.dSection }} 武汉协和医院</p>
                    <div class="doctor-stats">
                        <span class="stat-item">好评率: {{ doctor.goodRate }}%</span>
                        <span class="stat-item">关注数: {{ doctor.followers }}</span>
                    </div>
                    <p class="doctor-specialty">擅长: {{ doctor.dIntroduction || '暂无介绍' }}</p>
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
        >
        </el-pagination>
    </div>
</template>

<script>
import request from "@/utils/request.js";

export default {
    name: "DoctorTeam",
    data() {
        return {
            activeSection: "全部",
            sections: [
                "内科", "外科", "妇产科学", "儿科学", "骨外科",
                "眼科学", "口腔科学", "五官科", "肿瘤科", "皮肤性病科", "中医学"
            ],
            doctors: [],
            filteredDoctors: [],
            pageNumber: 1,
            size: 10,
            total: 0,
            defaultAvatar: require("@/assets/logo.png")
        };
    },
    methods: {
        loadDoctors() {
            const params = {
                pageNumber: this.pageNumber,
                size: this.size,
                query: "",  // 医生姓名搜索
                arrangeDate: ""  // 排班日期（如果需要）
            };
            
            // 如果选择了科室，使用专门的科室查询接口
            if (this.activeSection !== "全部") {
                params.dSection = this.activeSection;
                request
                    .get("doctor/findDoctorBySectionPage", { params })
                    .then((res) => {
                        if (res.data.status !== 200) {
                            this.$message.error(res.data.msg || "获取医生列表失败");
                            this.doctors = [];
                            this.filteredDoctors = [];
                            this.total = 0;
                            return;
                        }
                        const data = res.data.data;
                        this.doctors = (data.doctors || []).map(doctor => ({
                            ...doctor,
                            goodRate: doctor.dAvgStar ? Math.round((doctor.dAvgStar / 5) * 100) : 0,
                            followers: Math.floor(Math.random() * 10000) + 1000 // 模拟关注数
                        }));
                        this.total = data.total || 0;
                        // 直接使用返回的数据，不需要再次筛选
                        this.filteredDoctors = this.doctors;
                    })
                    .catch((err) => {
                        console.error("医生列表请求失败:", err);
                        this.$message.error("请求失败，请稍后重试");
                        this.doctors = [];
                        this.filteredDoctors = [];
                        this.total = 0;
                    });
            } else {
                // 查询全部医生
                request
                    .get("admin/findAllDoctors", { params })
                    .then((res) => {
                        if (res.data.status !== 200) {
                            this.$message.error(res.data.msg || "获取医生列表失败");
                            this.doctors = [];
                            this.filteredDoctors = [];
                            this.total = 0;
                            return;
                        }
                        const data = res.data.data;
                        this.doctors = (data.doctors || []).map(doctor => ({
                            ...doctor,
                            goodRate: doctor.dAvgStar ? Math.round((doctor.dAvgStar / 5) * 100) : 0,
                            followers: Math.floor(Math.random() * 10000) + 1000 // 模拟关注数
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
                    });
            }
        },
        handleSizeChange(size) {
            this.size = size;
            this.pageNumber = 1;
            this.loadDoctors();
        },
        handleCurrentChange(page) {
            this.pageNumber = page;
            this.loadDoctors();
        }
    },
    watch: {
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
    padding: 30px;
    max-width: 1400px;
    margin: 0 auto;
    background: #fff;
    min-height: calc(100vh - 70px);
}

.page-title {
    font-size: 24px;
    font-weight: 600;
    color: #333;
    margin-bottom: 30px;
}

.filter-section {
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 1px solid #e4e7ed;
    
    .filter-label {
        font-size: 16px;
        color: #606266;
        margin-right: 15px;
    }
    
    .filter-tabs {
        display: inline-flex;
        flex-wrap: wrap;
        gap: 10px;
        
        .filter-tab {
            padding: 8px 20px;
            background: #fff;
            border: 1px solid #dcdfe6;
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.3s;
            font-size: 14px;
            color: #606266;
            
            &:hover {
                color: #409EFF;
                border-color: #409EFF;
            }
            
            &.active {
                background: #409EFF;
                color: #fff;
                border-color: #409EFF;
            }
        }
    }
}

.doctor-list {
    .doctor-card {
        display: flex;
        padding: 20px;
        margin-bottom: 20px;
        background: #fff;
        border: 1px solid #e4e7ed;
        border-radius: 8px;
        transition: all 0.3s;
        
        &:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
        
        .doctor-avatar {
            width: 100px;
            height: 100px;
            margin-right: 20px;
            flex-shrink: 0;
            
            img {
                width: 100%;
                height: 100%;
                border-radius: 50%;
                object-fit: cover;
            }
        }
        
        .doctor-info {
            flex: 1;
            
            .doctor-name {
                font-size: 18px;
                font-weight: 600;
                color: #333;
                margin: 0 0 10px 0;
            }
            
            .doctor-department {
                font-size: 14px;
                color: #909399;
                margin: 0 0 10px 0;
            }
            
            .doctor-stats {
                margin: 10px 0;
                
                .stat-item {
                    font-size: 14px;
                    color: #606266;
                    margin-right: 20px;
                }
            }
            
            .doctor-specialty {
                font-size: 14px;
                color: #606266;
                line-height: 1.6;
                margin: 10px 0 0 0;
            }
        }
    }
}

.pagination {
    margin-top: 30px;
    text-align: center;
}
</style>

