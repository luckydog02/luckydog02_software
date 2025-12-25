<!--
 * 诊疗评价页面
 *
-->
<template>
    <div class="treatment-reviews-container">
        <h2 class="page-title">诊疗评价</h2>
        
        <!-- 加载状态 -->
        <div v-if="loading" style="text-align: center; padding: 40px;">
            <i class="el-icon-loading" style="font-size: 24px;"></i>
            <p>加载中...</p>
        </div>

        <!-- 空数据提示 -->
        <el-empty v-if="!loading && reviews.length === 0" description="暂无评价数据"></el-empty>

        <!-- 评价列表 -->
        <div v-if="!loading && reviews.length > 0" class="reviews-list">
            <div 
                v-for="review in reviews" 
                :key="review.id"
                class="review-card"
            >
                <div class="review-header">
                    <div class="user-avatar">
                        <img :src="review.avatar || defaultAvatar" alt="用户头像" />
                    </div>
                    <div class="user-info">
                        <div class="user-name">{{ review.userName }}</div>
                        <div class="user-location">{{ review.location }}</div>
                    </div>
                    <div class="review-rating">
                        <el-rate
                            v-model="review.rating"
                            disabled
                            show-score
                            text-color="#ff9900"
                            score-template="{value}"
                        >
                        </el-rate>
                    </div>
                </div>
                
                <div class="review-content">
                    <div class="doctor-info">
                        <span class="label">就诊医生：</span>
                        <span class="value">{{ review.doctorName }} {{ review.doctorTitle }}</span>
                    </div>
                    <div class="patient-impression">
                        <span class="label">患者印象：</span>
                        <el-tag 
                            v-for="impression in review.impressions" 
                            :key="impression"
                            size="small"
                            type="info"
                            class="impression-tag"
                        >
                            {{ impression }}
                        </el-tag>
                    </div>
                    <div class="review-text">
                        {{ review.content }}
                    </div>
                    <div class="review-time">
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

export default {
    name: "TreatmentReviews",
    data() {
        return {
            reviews: [],
            pageNumber: 1,
            size: 10,
            total: 0,
            loading: false,
            defaultAvatar: require("@/assets/logo.png")
        };
    },
    methods: {
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
                    avatar: "",
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

.reviews-list {
    .review-card {
        background: #fff;
        border: 1px solid #e4e7ed;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 20px;
        transition: all 0.3s;
        
        &:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
        
        .review-header {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
            
            .user-avatar {
                width: 50px;
                height: 50px;
                margin-right: 15px;
                flex-shrink: 0;
                
                img {
                    width: 100%;
                    height: 100%;
                    border-radius: 50%;
                    object-fit: cover;
                }
            }
            
            .user-info {
                flex: 1;
                
                .user-name {
                    font-size: 16px;
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 5px;
                }
                
                .user-location {
                    font-size: 12px;
                    color: #909399;
                }
            }
            
            .review-rating {
                flex-shrink: 0;
            }
        }
        
        .review-content {
            .doctor-info {
                margin-bottom: 10px;
                font-size: 14px;
                
                .label {
                    color: #909399;
                }
                
                .value {
                    color: #333;
                    font-weight: 500;
                }
            }
            
            .patient-impression {
                margin-bottom: 15px;
                display: flex;
                align-items: center;
                flex-wrap: wrap;
                gap: 8px;
                
                .label {
                    font-size: 14px;
                    color: #909399;
                }
                
                .impression-tag {
                    margin: 0;
                }
            }
            
            .review-text {
                font-size: 14px;
                color: #606266;
                line-height: 1.8;
                margin-bottom: 10px;
            }
            
            .review-time {
                font-size: 12px;
                color: #909399;
                text-align: right;
            }
        }
    }
}

.pagination {
    margin-top: 30px;
    text-align: center;
}
</style>

