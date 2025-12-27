<!--
 * 我的预约页面
 *
-->
<template>
    <div class="my-appointments-container">
        <h2 class="page-title">我的预约</h2>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
            <i class="el-icon-loading" style="font-size: 32px; color: #409EFF;"></i>
            <p>加载中...</p>
        </div>

        <!-- 分类标签页 -->
        <el-tabs v-model="activeTab" @tab-click="handleTabClick" v-if="!loading">
            <!-- 待就诊 -->
            <el-tab-pane label="待就诊" name="pending">
                <el-empty v-if="pendingOrders.length === 0" description="暂无待就诊记录"></el-empty>
                <div v-if="pendingOrders.length > 0" class="appointments-list">
            <div 
                v-for="order in pendingOrders" 
                :key="order.oId"
                class="appointment-card br12 bw"
            >
                <div class="card-header">
                    <div class="order-info">
                        <div class="order-number">
                            <i class="el-icon-document"></i>
                            <span class="label">挂号单号：</span>
                            <span class="value">{{ order.oId }}</span>
                        </div>
                        <div class="order-status">
                            <el-tag
                                size="small"
                                type="info"
                            >
                                <i class="el-icon-time"></i> 待就诊
                            </el-tag>
                        </div>
                    </div>
                </div>

                <div class="card-body">
                    <div class="info-row">
                        <div class="info-item">
                            <i class="el-icon-user"></i>
                            <span class="label">患者姓名：</span>
                            <span class="value">{{ order.pName }}</span>
                        </div>
                        <div class="info-item">
                            <i class="el-icon-user-solid"></i>
                            <span class="label">医生姓名：</span>
                            <span class="value">{{ order.dName }}</span>
                        </div>
                    </div>

                    <div class="info-row">
                        <div class="info-item">
                            <i class="el-icon-time"></i>
                            <span class="label">预约时间段：</span>
                            <span class="value">{{ formatTimeSlot(order.oStart) }}</span>
                        </div>
                    </div>
                </div>

                <div class="card-footer">
                    <!-- 待就诊：取消预约按钮 -->
                    <el-button
                        type="danger"
                        size="small"
                        @click="cancelOrder(order)"
                        v-if="canCancelOrder(order)"
                        class="action-btn"
                    >
                        <i class="el-icon-close"></i> 取消预约
                    </el-button>
                    <el-tag
                        type="warning"
                        size="small"
                        v-else-if="order.oState === 0"
                        class="action-tag"
                    >
                        已过取消时间
                    </el-tag>
                </div>
            </div>
                </div>
            </el-tab-pane>
            
            <!-- 已就诊 -->
            <el-tab-pane label="已就诊" name="completed">
                <el-empty v-if="completedOrders.length === 0" description="暂无已就诊记录"></el-empty>
                <div v-if="completedOrders.length > 0" class="appointments-list">
            <div 
                v-for="order in completedOrders" 
                :key="order.oId"
                class="appointment-card br12 bw"
            >
                <div class="card-header">
                    <div class="order-info">
                        <div class="order-number">
                            <i class="el-icon-document"></i>
                            <span class="label">挂号单号：</span>
                            <span class="value">{{ order.oId }}</span>
                        </div>
                        <div class="order-status">
                            <el-tag
                                size="small"
                                type="success"
                                v-if="order.oState === 1 && order.oPriceState === 1"
                            >
                                <i class="el-icon-check"></i> 已完成
                            </el-tag>
                            <el-tag
                                size="small"
                                type="warning"
                                v-else-if="order.oState === 1 && order.oPriceState === 0"
                            >
                                <i class="el-icon-time"></i> 待缴费
                            </el-tag>
                        </div>
                    </div>
                </div>

                <div class="card-body">
                    <div class="info-row">
                        <div class="info-item">
                            <i class="el-icon-user"></i>
                            <span class="label">患者姓名：</span>
                            <span class="value">{{ order.pName }}</span>
                        </div>
                        <div class="info-item">
                            <i class="el-icon-user-solid"></i>
                            <span class="label">医生姓名：</span>
                            <span class="value">{{ order.dName }}</span>
                        </div>
                    </div>

                    <div class="info-row">
                        <div class="info-item">
                            <i class="el-icon-time"></i>
                            <span class="label">挂号时间：</span>
                            <span class="value">{{ formatTime(order.oStart) }}</span>
                        </div>
                        <div class="info-item">
                            <i class="el-icon-timer"></i>
                            <span class="label">结束时间：</span>
                            <span class="value">{{ formatTime(order.oEnd) }}</span>
                        </div>
                    </div>

                    <div class="info-row">
                        <div class="info-item price-item">
                            <i class="el-icon-coin"></i>
                            <span class="label">需交费用：</span>
                            <span class="value price">¥{{ order.oTotalPrice || '0.00' }}</span>
                        </div>
                        <div class="info-item">
                            <i class="el-icon-wallet"></i>
                            <span class="label">缴费状态：</span>
                            <el-tag
                                type="success"
                                size="small"
                                v-if="order.oPriceState === 1"
                            >
                                <i class="el-icon-check"></i> 已缴费
                            </el-tag>
                            <el-tag
                                type="warning"
                                size="small"
                                v-else
                            >
                                <i class="el-icon-warning"></i> 未缴费
                            </el-tag>
                        </div>
                    </div>
                </div>

                <div class="card-footer">
                    <!-- 已就诊：缴费、评价、导出按钮 -->
                    <el-button
                        type="warning"
                        size="small"
                        v-if="order.oPriceState === 0 && order.oState === 1"
                        @click="priceClick(order.oId, order.dId)"
                        class="action-btn"
                    >
                        <i class="el-icon-wallet"></i> 点击缴费
                    </el-button>
                    <el-button
                        type="primary"
                        size="small"
                        @click="openReviewDialog(order.oId, order.dId)"
                        v-if="order.oState === 1 && order.oPriceState === 1"
                        :disabled="order.hasReviewed === true"
                        :class="['action-btn', { 'disabled-btn': order.hasReviewed === true }]"
                    >
                        <i class="el-icon-edit"></i> {{ order.hasReviewed === true ? '已评价' : '我要评价' }}
                    </el-button>
                    <el-button
                        type="success"
                        size="small"
                        @click="seeReport(order.oId)"
                        v-if="order.oState === 1 && order.oPriceState === 1"
                        class="action-btn"
                    >
                        <i class="el-icon-download"></i> 导出报告单
                    </el-button>
                </div>
            </div>
                </div>
            </el-tab-pane>
        </el-tabs>
        
        <!-- 评价对话框 -->
        <el-dialog title="用户评价" :visible.sync="starVisible" width="600px">
            <div style="text-align: center; margin-bottom: 20px;">
                <h3>
                    请对工号：{{ dId }}&nbsp;医生：{{ dName }}&nbsp;进行评价
                </h3>
            </div>
            <div style="text-align: center; margin-bottom: 20px;">
                <el-rate v-model="star" show-text> </el-rate>
            </div>
            <div style="margin-bottom: 20px;">
                <el-input
                    type="textarea"
                    :rows="4"
                    placeholder="请输入您的评价内容..."
                    v-model="reviewContent"
                    maxlength="500"
                    show-word-limit
                >
                </el-input>
            </div>
            <div style="margin-bottom: 20px;">
                <span style="margin-right: 10px;">患者印象：</span>
                <el-checkbox-group v-model="impressions">
                    <el-checkbox label="医德高尚">医德高尚</el-checkbox>
                    <el-checkbox label="医术高明">医术高明</el-checkbox>
                    <el-checkbox label="热情待人">热情待人</el-checkbox>
                    <el-checkbox label="诊断认真">诊断认真</el-checkbox>
                    <el-checkbox label="态度好">态度好</el-checkbox>
                    <el-checkbox label="认真负责">认真负责</el-checkbox>
                    <el-checkbox label="细致周到">细致周到</el-checkbox>
                </el-checkbox-group>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="starVisible = false" style="font-size: 18px;">
                    <i class="iconfont icon-r-left" style="font-size: 20px;"></i> 取 消
                </el-button>
                <el-button type="primary" @click="starClick" style="font-size: 18px;">
                    <i class="iconfont icon-r-yes" style="font-size: 20px;"></i> 确 定
                </el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";

export default {
    name: "MyAppointments",
    data() {
        return {
            userId: 1,
            orderData: [],
            loading: false,
            star: 5,
            starVisible: false,
            dId: 1,
            dName: "",
            oId: null,  // 当前评价的订单ID
            reviewContent: "",  // 评价内容
            impressions: [],  // 患者印象标签
            activeTab: "pending"  // 当前选中的标签页
        };
    },
    computed: {
        // 待就诊订单（未完成，oState === 0）
        pendingOrders() {
            return this.orderData.filter(order => order.oState === 0);
        },
        // 已就诊订单（已完成，oState === 1）
        completedOrders() {
            return this.orderData.filter(order => order.oState === 1);
        },
    },
    methods: {
        // 标签页切换
        handleTabClick(tab) {
            this.activeTab = tab.name;
        },
        //判断是否可以取消预约
        canCancelOrder(order) {
            // 只有未完成的订单才能取消
            if (order.oState !== 0) {
                return false;
            }
            
            // 检查预约时间是否已过
            if (!order.oStart) {
                return false;
            }
            
            try {
                // 解析预约时间，格式：yyyy-MM-dd HH:mm-HH:mm
                // 提取开始时间部分（第一个时间段）
                let startTimeStr = order.oStart;
                if (order.oStart.length > 11) {
                    const datePart = order.oStart.substring(0, 10); // yyyy-MM-dd
                    const timePart = order.oStart.substring(11); // HH:mm-HH:mm
                    if (timePart.includes("-")) {
                        const startTime = timePart.split("-")[0]; // 第一个时间段，例如：08:30
                        startTimeStr = datePart + " " + startTime + ":00"; // 格式：2025-11-19 08:30:00
                    }
                }
                
                // 解析预约开始时间
                const appointmentTime = new Date(startTimeStr.replace(/-/g, "/"));
                const now = new Date();
                
                // 检查是否已过就诊时间（需要在就诊时间前才能取消）
                return now < appointmentTime;
            } catch (e) {
                console.error("解析预约时间失败:", e);
                return false;
            }
        },
        //取消预约
        cancelOrder(order) {
            this.$confirm("确定要取消该预约吗？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            })
                .then(() => {
                    request
                        .get("patient/cancelOrder", {
                            params: {
                                oId: order.oId,
                                pId: this.userId,
                            },
                        })
                        .then((res) => {
                            if (res.data.status !== 200) {
                                this.$message.error(res.data.msg || "取消预约失败");
                                return;
                            }
                            this.$message.success("取消预约成功");
                            this.requestOrder();
                        })
                        .catch((err) => {
                            console.error("取消预约失败:", err);
                            this.$message.error("取消预约失败，请重试");
                        });
                })
                .catch(() => {
                    this.$message.info("已取消操作");
                });
        },
        //评价点击确认
        starClick() {
            // 验证必要参数
            if (!this.oId) {
                this.$message.error("订单ID不能为空");
                return;
            }
            if (!this.dId) {
                this.$message.error("医生ID不能为空");
                return;
            }
            if (!this.userId) {
                this.$message.error("患者ID不能为空");
                return;
            }
            
            // 提交评价到review表
            const reviewData = {
                pId: this.userId,
                dId: this.dId,
                oId: this.oId,
                rStar: Number(this.star),  // 确保是数字类型
                rContent: this.reviewContent || "暂无评价内容",
                rImpressions: this.impressions.join(",") || ""
            };
            
            console.log("提交评价数据：", reviewData);
            
            request.post("review/add", reviewData)
                .then((res) => {
                    console.log("评价提交响应：", res);
                    if (res.data.status !== 200) {
                        const errorMsg = res.data.msg || "评价提交失败";
                        this.$message.error(errorMsg);
                        return;
                    }
                    // 评价提交成功后，更新医生评分
                    return request.get("doctor/updateStar", {
                        params: {
                            dId: this.dId,
                            dStar: this.star,
                        },
                    });
                })
                .then((res) => {
                    if (res && res.data.status !== 200) {
                        console.warn("更新医生评分失败，但评价已提交");
                    }
                    this.$message.success("谢谢您的评价");
                    this.starVisible = false;
                    // 清空表单
                    this.reviewContent = "";
                    this.impressions = [];
                    this.star = 5;
                    // 重新加载订单列表，更新评价状态
                    this.requestOrder();
                })
                .catch((err) => {
                    console.error("评价提交错误：", err);
                    const errorMsg = err.response?.data?.msg || err.message || "评价提交失败，请稍后重试";
                    this.$message.error(errorMsg);
                });
        },
        //查看报告单
        seeReport(id) {
            window.location.href =
                "http://localhost:9281/patient/pdf?oId=" + id;
        },
        //点击缴费按钮
        priceClick(oId, dId) {
            request
                .get("order/updatePrice", {
                    params: {
                        oId: oId,
                    },
                })
                .then((res) => {
                    if (res.data.status !== 200) {
                        this.$message.error("请求数据失败");
                        return;
                    }
                    this.$message.success("单号 " + oId + " 缴费成功！");
                    request
                        .get("admin/findDoctor", {
                            params: {
                                dId: dId,
                            },
                        })
                        .then((res) => {
                            if (res.data.status !== 200)
                                return this.$message.error("请求数据失败");
                            this.dId = res.data.data.dId;
                            this.dName = res.data.data.dName;
                            this.oId = oId;  // 保存订单ID
                            this.starVisible = true;  // 显示评价对话框
                        });
                    this.requestOrder();
                });
        },
        //请求挂号信息
        requestOrder() {
            this.loading = true;
            request
                .get("patient/findOrderByPid", {
                    params: {
                        pId: this.userId,
                    },
                })
                .then((res) => {
                    if (res.data.status !== 200) {
                        this.$message.error("请求数据失败");
                        return;
                    }
                    const orders = res.data.data || [];
                    // 检查每个订单是否已评价
                    this.checkReviewsForOrders(orders);
                })
                .catch((err) => {
                    console.error(err);
                    this.$message.error("获取预约信息失败，请稍后重试");
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        // 检查订单是否已评价
        checkReviewsForOrders(orders) {
            // 先设置订单数据，默认未评价
            this.orderData = orders.map(order => ({
                ...order,
                hasReviewed: false
            }));
            
            // 获取所有已完成的订单ID
            const completedOrderIds = orders
                .filter(order => order.oState === 1 && order.oPriceState === 1)
                .map(order => order.oId);
            
            if (completedOrderIds.length === 0) {
                return;
            }
            
            // 批量查询评价状态
            const reviewPromises = completedOrderIds.map(oId => 
                request.get("review/checkByOId", {
                    params: { oId: oId }
                }).then(res => ({
                    oId: oId,
                    hasReviewed: res.data && res.data.status === 200 && res.data.data === true
                })).catch(() => ({
                    oId: oId,
                    hasReviewed: false
                }))
            );
            
            Promise.all(reviewPromises).then(reviewResults => {
                // 创建评价状态映射
                const reviewMap = {};
                reviewResults.forEach(result => {
                    reviewMap[result.oId] = result.hasReviewed;
                });
                
                // 更新订单的评价状态
                this.orderData = this.orderData.map(order => ({
                    ...order,
                    hasReviewed: reviewMap[order.oId] || false
                }));
            }).catch((err) => {
                console.error("检查评价状态失败：", err);
            });
        },
        // 打开评价对话框（从"我要评价"按钮触发）
        openReviewDialog(oId, dId) {
            request
                .get("admin/findDoctor", {
                    params: {
                        dId: dId,
                    },
                })
                .then((res) => {
                    if (res.data.status !== 200) {
                        return this.$message.error("请求数据失败");
                    }
                    this.dId = res.data.data.dId;
                    this.dName = res.data.data.dName;
                    this.oId = oId;  // 保存订单ID
                    this.starVisible = true;  // 显示评价对话框
                })
                .catch((err) => {
                    console.error(err);
                    this.$message.error("获取医生信息失败");
                });
        },
        //格式化时间
        formatTime(time) {
            if (!time) return "-";
            const date = new Date(time);
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, "0");
            const day = String(date.getDate()).padStart(2, "0");
            const hour = String(date.getHours()).padStart(2, "0");
            const minute = String(date.getMinutes()).padStart(2, "0");
            return `${year}-${month}-${day} ${hour}:${minute}`;
        },
        //格式化预约时间段（格式：yyyy-MM-dd HH:mm-HH:mm）
        formatTimeSlot(oStart) {
            if (!oStart) return "-";
            
            try {
                // 如果格式是 yyyy-MM-dd HH:mm-HH:mm，直接格式化显示
                if (oStart.length > 11 && oStart.includes("-") && oStart.substring(11).includes("-")) {
                    const datePart = oStart.substring(0, 10); // yyyy-MM-dd
                    const timePart = oStart.substring(11); // HH:mm-HH:mm
                    
                    // 格式化日期部分（例如：2025-01-20）
                    const [year, month, day] = datePart.split("-");
                    const formattedDate = `${year}年${month}月${day}日`;
                    
                    // 返回：2025年01月20日 08:30-09:30
                    return `${formattedDate} ${timePart}`;
                }
                
                // 如果不是标准格式，尝试解析
                const date = new Date(oStart);
                if (!isNaN(date.getTime())) {
                    const year = date.getFullYear();
                    const month = String(date.getMonth() + 1).padStart(2, "0");
                    const day = String(date.getDate()).padStart(2, "0");
                    const hour = String(date.getHours()).padStart(2, "0");
                    const minute = String(date.getMinutes()).padStart(2, "0");
                    return `${year}年${month}月${day}日 ${hour}:${minute}`;
                }
                
                return oStart;
            } catch (e) {
                console.error("格式化预约时间段失败:", e);
                return oStart;
            }
        },
        //token解码
        tokenDecode(token) {
            if (token !== null) return jwtDecode(token);
        },
    },
    created() {
        //解码token
        this.userId = this.tokenDecode(getToken()).pId;
        this.requestOrder();
    },
};
</script>

<style scoped lang="scss">
.my-appointments-container {
    padding: 30px;
    max-width: 1400px;
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

.appointments-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.appointment-card {
    background: #fff;
    border-radius: 12px;
    padding: 25px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    
    &:hover {
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
        transform: translateY(-2px);
    }
}

.br12 {
    border-radius: 12px;
}

.bw {
    background: #fff;
}

.card-header {
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 2px solid #f0f2f5;
    
    .order-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .order-number {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 16px;
            
            i {
                color: #409EFF;
                font-size: 20px;
            }
            
            .label {
                color: #909399;
                font-weight: 500;
            }
            
            .value {
                color: #303133;
                font-weight: 600;
            }
        }
    }
}

.card-body {
    margin-bottom: 20px;
    
    .info-row {
        display: flex;
        gap: 40px;
        margin-bottom: 15px;
        
        &:last-child {
            margin-bottom: 0;
        }
        
        .info-item {
            display: flex;
            align-items: center;
            gap: 8px;
            flex: 1;
            font-size: 14px;
            
            i {
                color: #409EFF;
                font-size: 16px;
            }
            
            .label {
                color: #909399;
            }
            
            .value {
                color: #303133;
                font-weight: 500;
            }
            
            &.price-item {
                .value.price {
                    color: #f56c6c;
                    font-size: 18px;
                    font-weight: 600;
                }
            }
        }
    }
}

.card-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    padding-top: 15px;
    border-top: 1px solid #f0f2f5;
    
    .action-btn {
        padding: 10px 20px;
        font-size: 14px;
        
        i {
            margin-right: 5px;
        }
        
        &.disabled-btn {
            background-color: #c0c4cc;
            border-color: #c0c4cc;
            color: #fff;
            cursor: not-allowed;
            
            &:hover {
                background-color: #c0c4cc;
                border-color: #c0c4cc;
                color: #fff;
            }
        }
    }
}

// 评价对话框样式
.el-dialog {
    .dialog-footer {
        text-align: right;
    }
}

// 响应式设计
@media (max-width: 768px) {
    .my-appointments-container {
        padding: 20px 15px;
    }
    
    .appointment-card {
        padding: 20px;
    }
    
    .card-body {
        .info-row {
            flex-direction: column;
            gap: 15px;
        }
    }
    
    .card-footer {
        flex-direction: column;
        
        .action-btn {
            width: 100%;
        }
    }
}
</style>

