<!--
 * 诊疗评价页面 - 瀑布流布局
 *
-->
<template>
    <div class="treatment-reviews-container">
        <!-- 顶部装饰性标题区域 -->
        <div class="header-section">
            <div class="title-wrapper">
                <h2 class="page-title">
                    <i class="el-icon-star-on"></i>
                    诊疗评价
                </h2>
                <p class="page-subtitle">聆听每一位患者的心声</p>
            </div>
            <div class="header-decoration">
                <div class="decoration-circle circle-1"></div>
                <div class="decoration-circle circle-2"></div>
                <div class="decoration-circle circle-3"></div>
            </div>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
            <div class="loading-spinner">
                <i class="el-icon-loading"></i>
            </div>
            <p>加载中...</p>
        </div>

        <!-- 空数据提示 -->
        <div v-if="!loading && reviews.length === 0" class="empty-container">
            <el-empty description="暂无评价数据">
                <i class="el-icon-chat-line-round empty-icon"></i>
            </el-empty>
        </div>

        <!-- 瀑布流评价列表 -->
        <div v-if="!loading && reviews.length > 0" class="masonry-container">
            <div 
                v-for="(review, index) in reviews" 
                :key="review.id"
                class="review-card"
                :style="{ animationDelay: `${index * 0.1}s` }"
            >
                <!-- 卡片顶部装饰条 -->
                <div class="card-top-bar" :class="getCardColorClass(index)"></div>
                
                <!-- 用户信息区域 -->
                <div class="review-header">
                    <div class="user-avatar-wrapper">
                        <div class="avatar-ring"></div>
                        <img 
                            :src="getRandomAvatar(review.id)" 
                            :alt="review.userName"
                            class="user-avatar"
                        />
                    </div>
                    <div class="user-info">
                        <div class="user-name">
                            <i class="el-icon-user"></i>
                            {{ review.userName }}
                        </div>
                        <div class="user-location">
                            <i class="el-icon-location"></i>
                            {{ review.location }}
                        </div>
                    </div>
                    <div class="review-rating">
                        <el-rate
                            v-model="review.rating"
                            disabled
                            show-score
                            text-color="#ff9900"
                            score-template="{value}"
                            class="rating-stars"
                        >
                        </el-rate>
                    </div>
                </div>
                
                <!-- 评价内容区域 -->
                <div class="review-content">
                    <div class="doctor-info-card">
                        <div class="doctor-icon">
                            <i class="el-icon-user-solid"></i>
                        </div>
                        <div class="doctor-details">
                            <span class="label">就诊医生</span>
                            <span class="value">{{ review.doctorName }} {{ review.doctorTitle }}</span>
                        </div>
                    </div>
                    
                    <div class="patient-impression" v-if="review.impressions && review.impressions.length > 0">
                        <span class="impression-label">
                            <i class="el-icon-collection-tag"></i>
                            患者印象
                        </span>
                        <div class="impression-tags">
                            <el-tag 
                                v-for="impression in review.impressions" 
                                :key="impression"
                                size="small"
                                :type="getTagType(impression)"
                                class="impression-tag"
                                effect="plain"
                            >
                                {{ impression }}
                            </el-tag>
                        </div>
                    </div>
                    
                    <div class="review-text">
                        <i class="el-icon-chat-line-round text-icon"></i>
                        <p>{{ review.content }}</p>
                    </div>
                    
                    <div class="review-time">
                        <i class="el-icon-time"></i>
                        {{ review.time }}
                    </div>
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

// 导入所有头像图片
const avatarImages = [
    require("@/assets/avatars/avatar1.png"),
    require("@/assets/avatars/avatar2.png"),
    require("@/assets/avatars/avatar3.png"),
    require("@/assets/avatars/avatar4.png"),
    require("@/assets/avatars/avatar5.png")
    // 根据实际图片数量继续添加
];

export default {
    name: "TreatmentReviews",
    data() {
        return {
            reviews: [],
            pageNumber: 1,
            size: 10,
            total: 0,
            loading: false,
            defaultAvatar: require("@/assets/hei.png")
        };
    },
    methods: {
        // 添加随机头像方法
        getRandomAvatar(reviewId) {
            // 使用评价ID作为种子，保证同一用户始终显示同一头像
            const seed = reviewId.toString().split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
            const index = seed % avatarImages.length;
            return avatarImages[index];
        },
        // 获取卡片颜色类
        getCardColorClass(index) {
            const colors = ['color-1', 'color-2', 'color-3', 'color-4', 'color-5'];
            return colors[index % colors.length];
        },
        // 获取标签类型
        getTagType(impression) {
            const typeMap = {
                '医德高尚': 'success',
                '医术高明': 'warning',
                '热情待人': 'primary',
                '诊断认真': 'info',
                '态度好': 'success',
                '认真负责': 'warning',
                '细致周到': 'primary'
            };
            return typeMap[impression] || 'info';
        },
        loadReviews() {
            this.loading = true;
            request.get("review/list", {
                params: {
                    pageNumber: this.pageNumber,
                    size: this.size
                }
            }).then(res => {
                if (res.data.status !== 200) {
                    this.$message.error(res.data.msg || "获取评价列表失败");
                    this.loading = false;
                    return;
                }
                const data = res.data.data;
                if (!data) {
                    this.reviews = [];
                    this.total = 0;
                    this.loading = false;
                    return;
                }
                // 转换数据格式以适配前端显示
                this.reviews = (data.reviews || []).map(review => ({
                    id: review.rId,
                    userName: review.pName ? review.pName.substring(0, 1) + "**" : "匿名",
                    location: review.location || "未知",
                    rating: review.rStar || 5,
                    doctorName: review.dName || "",
                    doctorTitle: review.dPost || "",
                    impressions: review.rImpressions ? review.rImpressions.split(",").filter(i => i.trim()) : [],
                    content: review.rContent || "暂无评价内容",
                    time: review.rTime ? new Date(review.rTime).toLocaleString("zh-CN") : ""
                }));
                this.total = data.total || 0;
            }).catch(err => {
                console.error("评价列表请求失败:", err);
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
        handleSizeChange(size) {
            this.size = size;
            this.pageNumber = 1;
            this.loadReviews();
        },
        handleCurrentChange(page) {
            this.pageNumber = page;
            this.loadReviews();
        }
    },
    created() {
        this.loadReviews();
    }
};
</script>

<style scoped lang="scss">
.treatment-reviews-container {
    padding: 40px 20px;
    max-width: 1600px;
    margin: 0 auto;
    background: #f5f7fa;
    min-height: calc(100vh - 70px);
    position: relative;
}

// 顶部标题区域
.header-section {
    position: relative;
    z-index: 1;
    text-align: center;
    margin-bottom: 50px;
    padding: 10px 0 30px 0;
    
    .title-wrapper {
        position: relative;
        z-index: 2;
        
        .page-title {
            font-size: 42px;
            font-weight: 700;
            background: linear-gradient(135deg, #1976d2 0%, #42a5f5 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            margin-bottom: 10px;
            display: inline-flex;
            align-items: center;
            gap: 15px;
            
            i {
                font-size: 48px;
                background: linear-gradient(135deg, #1976d2 0%, #42a5f5 100%);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
                animation: rotate 3s linear infinite;
            }
        }
        
        .page-subtitle {
            font-size: 18px;
            color: #666;
            font-weight: 300;
            letter-spacing: 2px;
        }
    }
    
    .header-decoration {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 1;
        pointer-events: none;
        
        .decoration-circle {
            position: absolute;
            border-radius: 50%;
            background: linear-gradient(135deg, rgba(33, 150, 243, 0.15) 0%, rgba(66, 165, 245, 0.15) 100%);
            animation: float 6s ease-in-out infinite;
            
            &.circle-1 {
                width: 120px;
                height: 120px;
                top: -40px;
                left: 10%;
                animation-delay: 0s;
            }
            
            &.circle-2 {
                width: 80px;
                height: 80px;
                top: 20px;
                right: 15%;
                animation-delay: 2s;
            }
            
            &.circle-3 {
                width: 100px;
                height: 100px;
                bottom: -30px;
                left: 50%;
                animation-delay: 4s;
            }
        }
    }
}

// 加载状态
.loading-container {
    text-align: center;
    padding: 80px 20px;
    position: relative;
    z-index: 1;
    
    .loading-spinner {
        i {
            font-size: 48px;
            color: #42a5f5;
            animation: rotate 1s linear infinite;
        }
    }
    
    p {
        margin-top: 20px;
        font-size: 16px;
        color: #666;
    }
}

// 空数据提示
.empty-container {
    position: relative;
    z-index: 1;
    padding: 60px 20px;
    
    .empty-icon {
        font-size: 80px;
        color: #c0c4cc;
    }
}

// 瀑布流容器
.masonry-container {
    position: relative;
    z-index: 1;
    column-count: 3;
    column-gap: 30px;
    padding: 0 10px;
    
    @media (max-width: 1200px) {
        column-count: 2;
    }
    
    @media (max-width: 768px) {
        column-count: 1;
        column-gap: 20px;
    }
}

// 评价卡片
.review-card {
    break-inside: avoid;
    background: #fff;
    border-radius: 20px;
    padding: 0;
    margin-bottom: 30px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    position: relative;
    overflow: hidden;
    animation: fadeInUp 0.6s ease-out both;
    
    &:hover {
        transform: translateY(-8px) scale(1.02);
        box-shadow: 0 20px 40px rgba(66, 165, 245, 0.3);
    }
    
    // 顶部装饰条
    .card-top-bar {
        height: 6px;
        width: 100%;
        background: linear-gradient(90deg, #42a5f5 0%, #2196f3 100%);
        
        &.color-1 {
            background: linear-gradient(90deg, #42a5f5 0%, #2196f3 100%);
        }
        
        &.color-2 {
            background: linear-gradient(90deg, #64b5f6 0%, #42a5f5 100%);
        }
        
        &.color-3 {
            background: linear-gradient(90deg, #90caf9 0%, #64b5f6 100%);
        }
        
        &.color-4 {
            background: linear-gradient(90deg, #1e88e5 0%, #1976d2 100%);
        }
        
        &.color-5 {
            background: linear-gradient(90deg, #5dade2 0%, #3498db 100%);
        }
    }
    
    // 用户信息区域
    .review-header {
        display: flex;
        align-items: flex-start;
        padding: 25px 25px 20px;
        gap: 15px;
        
        .user-avatar-wrapper {
            position: relative;
            flex-shrink: 0;
            
            .avatar-ring {
                position: absolute;
                top: -4px;
                left: -4px;
                width: 64px;
                height: 64px;
                border-radius: 50%;
                background: linear-gradient(135deg, #42a5f5 0%, #2196f3 100%);
                opacity: 0.2;
                animation: pulse 2s ease-in-out infinite;
            }
            
            .user-avatar {
                width: 56px;
                height: 56px;
                border-radius: 50%;
                object-fit: cover;
                border: 3px solid #fff;
                box-shadow: 0 4px 12px rgba(66, 165, 245, 0.2);
                position: relative;
                z-index: 1;
                transition: transform 0.3s;
            }
        }
        
        .user-info {
            flex: 1;
            min-width: 0;
            
            .user-name {
                font-size: 18px;
                font-weight: 600;
                color: #333;
                margin-bottom: 6px;
                display: flex;
                align-items: center;
                gap: 6px;
                
                i {
                    color: #42a5f5;
                    font-size: 16px;
                }
            }
            
            .user-location {
                font-size: 13px;
                color: #909399;
                display: flex;
                align-items: center;
                gap: 4px;
                
                i {
                    font-size: 12px;
                }
            }
        }
        
        .review-rating {
            flex-shrink: 0;
            
            .rating-stars {
                display: flex;
                flex-direction: row;
                align-items: center;
                gap: 5px;
            }
        }
    }
    
    // 评价内容区域
    .review-content {
        padding: 0 25px 25px;
        
        .doctor-info-card {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 15px;
            background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
            border-radius: 12px;
            margin-bottom: 18px;
            transition: all 0.3s;
            
            &:hover {
                background: linear-gradient(135deg, #e8ecf1 0%, #dde3e9 100%);
                transform: translateX(5px);
            }
            
            .doctor-icon {
                width: 40px;
                height: 40px;
                border-radius: 10px;
                background: linear-gradient(135deg, #42a5f5 0%, #2196f3 100%);
                display: flex;
                align-items: center;
                justify-content: center;
                flex-shrink: 0;
                
                i {
                    color: #fff;
                    font-size: 20px;
                }
            }
            
            .doctor-details {
                flex: 1;
                min-width: 0;
                
                .label {
                    display: block;
                    font-size: 12px;
                    color: #909399;
                    margin-bottom: 4px;
                }
                
                .value {
                    display: block;
                    font-size: 15px;
                    color: #333;
                    font-weight: 600;
                }
            }
        }
        
        .patient-impression {
            margin-bottom: 18px;
            
            .impression-label {
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: 13px;
                color: #909399;
                margin-bottom: 10px;
                font-weight: 500;
                
                i {
                    color: #42a5f5;
                }
            }
            
            .impression-tags {
                display: flex;
                flex-wrap: wrap;
                gap: 8px;
                
                .impression-tag {
                    margin: 0;
                    border-radius: 20px;
                    padding: 4px 12px;
                    font-size: 12px;
                    transition: all 0.3s;
                    
                    &:hover {
                        transform: scale(1.1);
                    }
                }
            }
        }
        
        .review-text {
            position: relative;
            padding: 15px;
            background: #fafbfc;
            border-left: 4px solid #42a5f5;
            border-radius: 8px;
            margin-bottom: 15px;
            
            .text-icon {
                position: absolute;
                top: 10px;
                right: 10px;
                color: #c0c4cc;
                font-size: 20px;
            }
            
            p {
                font-size: 15px;
                color: #606266;
                line-height: 1.8;
                margin: 0;
                padding-right: 30px;
            }
        }
        
        .review-time {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            gap: 6px;
            font-size: 12px;
            color: #909399;
            
            i {
                font-size: 14px;
            }
        }
    }
    
    // 悬停时头像动画
    &:hover .user-avatar {
        transform: scale(1.1) rotate(5deg);
    }
}

// 分页
.pagination {
    margin-top: 50px;
    text-align: center;
    position: relative;
    z-index: 1;
    padding: 20px;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 15px;
    backdrop-filter: blur(10px);
}

// 动画定义
@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

@keyframes rotate {
    from {
        transform: rotate(0deg);
    }
    to {
        transform: rotate(360deg);
    }
}

@keyframes float {
    0%, 100% {
        transform: translateY(0) translateX(0);
    }
    50% {
        transform: translateY(-20px) translateX(10px);
    }
}

@keyframes pulse {
    0%, 100% {
        transform: scale(1);
        opacity: 0.2;
    }
    50% {
        transform: scale(1.1);
        opacity: 0.3;
    }
}
</style>

