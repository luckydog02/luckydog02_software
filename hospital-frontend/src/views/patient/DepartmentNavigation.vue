<!--
 * 科室导航页面
 *
-->
<template>
    <div class="department-nav-container">
        <h2 class="page-title">本院全部科室</h2>
        
        <!-- 科室分类标签 -->
        <div class="category-tabs">
            <div 
                v-for="category in categories" 
                :key="category.name"
                :class="['category-tab', { 'active': activeCategory === category.name }]"
                @click="activeCategory = category.name"
            >
                {{ category.name }}
            </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" style="text-align: center; padding: 40px;">
            <i class="el-icon-loading" style="font-size: 24px;"></i>
            <p>加载中...</p>
        </div>

        <!-- 空数据提示 -->
        <el-empty v-if="!loading && filteredDepartments.length === 0" description="暂无科室数据"></el-empty>

        <!-- 科室卡片网格 -->
        <div v-if="!loading" class="department-grid">
            <div 
                v-for="dept in filteredDepartments" 
                :key="dept.name"
                class="department-card"
            >
                <div class="dept-icon" :style="{ borderColor: dept.color }">
                    <i class="el-icon-plus" :style="{ color: dept.color }"></i>
                </div>
                <h3 class="dept-name">{{ dept.name }}</h3>
                <p class="dept-doctor-count">医生{{ dept.doctorCount }}位</p>
                <p class="dept-description">{{ dept.description }}</p>
                <el-button type="success" class="view-detail-btn" @click="viewDepartmentDetail(dept)">
                    查看详情
                </el-button>
            </div>
        </div>
    </div>
</template>

<script>
import request from "@/utils/request.js";

export default {
    name: "DepartmentNavigation",
    data() {
        return {
            activeCategory: "内科",
            categories: [
                "内科", "外科", "妇产科学", "儿科学", "皮肤性病科", 
                "中医学", "中西医结合科", "病理科", "介入医学科", "其他科室",
                "骨外科", "眼科学", "口腔科学", "五官科", "肿瘤科",
                "感染中心", "康复医学科", "营养科", "麻醉医学科", "医学影像科"
            ],
            departments: [],
            loading: false
        };
    },
    computed: {
        filteredDepartments() {
            return this.departments.filter(dept => {
                // 根据分类匹配
                if (this.activeCategory === "内科") {
                    return dept.category === "内科";
                } else if (this.activeCategory === "外科") {
                    return dept.category === "外科";
                } else if (this.activeCategory === "骨外科") {
                    return dept.category === "骨外科";
                } else {
                    return dept.category === this.activeCategory;
                }
            });
        }
    },
    methods: {
        viewDepartmentDetail(dept) {
            this.$message.info(`查看${dept.name}详情`);
            // 这里可以跳转到科室详情页面或打开详情对话框
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
                if (data.length === 0) {
                    this.$message.warning("暂无科室数据");
                    this.loading = false;
                    return;
                }
                // 转换数据格式以适配前端显示
                this.departments = data.map(dept => ({
                    name: dept.sectionName,
                    category: dept.category || "其他科室",
                    color: dept.color || this.getDefaultColor(dept.category),
                    doctorCount: dept.doctorCount || 0,
                    description: dept.description || "专业医疗服务"
                }));
            }).catch(err => {
                console.error("科室列表请求失败:", err);
                // 如果请求失败，显示更详细的错误信息
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
        getDefaultColor(category) {
            const colorMap = {
                "内科": "#f56c6c",
                "外科": "#409EFF",
                "骨外科": "#67c23a",
                "妇产科学": "#F56C6C",
                "儿科学": "#409EFF",
                "眼科学": "#409EFF",
                "口腔科学": "#67C23A",
                "五官科": "#909399",
                "中医学": "#E6A23C",
                "其他科室": "#909399"
            };
            return colorMap[category] || "#909399";
        }
    },
    created() {
        this.loadDepartmentData();
    }
};
</script>

<style scoped lang="scss">
.department-nav-container {
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

.category-tabs {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 30px;
    padding-bottom: 15px;
    border-bottom: 1px solid #e4e7ed;
    
    .category-tab {
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

.department-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
    
    .department-card {
        background: #fff;
        border: 1px solid #e4e7ed;
        border-radius: 8px;
        padding: 20px;
        text-align: center;
        transition: all 0.3s;
        
        &:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            transform: translateY(-2px);
        }
        
        .dept-icon {
            width: 80px;
            height: 80px;
            margin: 0 auto 15px;
            border: 2px solid;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            
            i {
                font-size: 40px;
            }
        }
        
        .dept-name {
            font-size: 18px;
            font-weight: 600;
            color: #333;
            margin: 10px 0;
        }
        
        .dept-doctor-count {
            font-size: 14px;
            color: #909399;
            margin: 5px 0 15px;
        }
        
        .dept-description {
            font-size: 13px;
            color: #606266;
            line-height: 1.6;
            margin-bottom: 15px;
            text-align: left;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .view-detail-btn {
            width: 100%;
            background-color: #67c23a;
            border-color: #67c23a;
            
            &:hover {
                background-color: #85ce61;
                border-color: #85ce61;
            }
        }
    }
}
</style>

