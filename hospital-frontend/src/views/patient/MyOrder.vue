<!--
 * 我的挂号
 *
-->
<template>
    <div>
        <el-card>
            <el-tabs v-model="activeTab" @tab-click="handleTabClick">
                <!-- 待就诊 -->
                <el-tab-pane label="待就诊" name="pending">
                    <el-table :data="pendingOrders" size="small" stripe style="width: 100%" border>
                        <el-table-column prop="oId" label="挂号单号" width="75px"/>
                        <el-table-column prop="pId" label="本人id" width="75px"/>
                        <el-table-column prop="pName" label="姓名" width="75px"/>
                        <el-table-column prop="dId" label="医生id" width="75px"/>
                        <el-table-column prop="dName" label="医生姓名" width="100px"/>
                        <el-table-column prop="oStart" label="挂号时间" width="195px"/>
                        <el-table-column prop="oTotalPrice" label="需交费用/元" width="100px"/>
                        <el-table-column prop="oPriceState" label="缴费状态" width="120px">
                            <template slot-scope="scope">
                                <el-tag type="info" v-if="scope.row.oPriceState === 0">未缴费</el-tag>
                                <el-tag type="success" v-if="scope.row.oPriceState === 1">已缴费</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="150px">
                            <template slot-scope="scope">
                                <el-button
                                    type="danger"
                                    size="mini"
                                    style="font-size: 14px"
                                    @click="cancelOrder(scope.row)"
                                    v-if="canCancelOrder(scope.row)"
                                >
                                    <i class="iconfont icon-delete-button" style="font-size: 12px;"></i>取消预约
                                </el-button>
                                <el-tag type="warning" v-else>已过取消时间</el-tag>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
                
                <!-- 已就诊 -->
                <el-tab-pane label="已就诊" name="completed">
                    <el-table :data="completedOrders" size="small" stripe style="width: 100%" border>
                        <el-table-column prop="oId" label="挂号单号" width="75px"/>
                        <el-table-column prop="pId" label="本人id" width="75px"/>
                        <el-table-column prop="pName" label="姓名" width="75px"/>
                        <el-table-column prop="dId" label="医生id" width="75px"/>
                        <el-table-column prop="dName" label="医生姓名" width="100px"/>
                        <el-table-column prop="oStart" label="挂号时间" width="195px"/>
                        <el-table-column prop="oEnd" label="结束时间" width="185px"/>
                        <el-table-column prop="oTotalPrice" label="费用/元" width="100px"/>
                        <el-table-column prop="oPriceState" label="缴费状态" width="120px">
                            <template slot-scope="scope">
                                <el-tag type="warning" v-if="scope.row.oPriceState === 0">未缴费</el-tag>
                                <el-tag type="success" v-if="scope.row.oPriceState === 1">已缴费</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="250px">
                            <template slot-scope="scope">
                                <el-button
                                    type="primary"
                                    size="mini"
                                    style="font-size: 14px; margin-right: 5px;"
                                    @click="showRatingDialog(scope.row)"
                                    v-if="scope.row.oPriceState === 1"
                                >
                                    <i class="iconfont icon-edit-button" style="font-size: 12px;"></i>我要评价
                                </el-button>
                                <el-button
                                    type="success"
                                    size="mini"
                                    style="font-size: 14px"
                                    @click="seeReport(scope.row.oId)"
                                    v-if="scope.row.oPriceState === 1"
                                >
                                    <i class="iconfont icon-export-button" style="font-size: 12px;"></i>导出报告单
                                </el-button>
                                <el-button
                                    size="mini"
                                    type="warning"
                                    style="font-size: 14px"
                                    v-if="scope.row.oPriceState === 0 && scope.row.oState === 1"
                                    @click="priceClick(scope.row.oId, scope.row.dId)"
                                >
                                    <i class="iconfont icon-edit-button" style="font-size: 12px;"></i>点击缴费</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
            </el-tabs>
        </el-card>
        <!-- 评价对话框 -->
        <el-dialog title="用户评价" :visible.sync="starVisible">
            <div>
                <h3>
                    请对工号：{{ dId }}&nbsp;医生：{{ dName }}&nbsp;进行评价
                </h3>
            </div>
            <div>
                <el-rate v-model="star" show-text> </el-rate>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="starVisible = false" style="font-size: 18px;"><i class="iconfont icon-r-left" style="font-size: 20px;"></i> 取 消</el-button>
                <el-button type="primary" @click="starClick"style="font-size: 18px;"><i class="iconfont icon-r-yes" style="font-size: 20px;"></i> 确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
    name: "MyOrder",
    data() {
        return {
            userId: 1,
            orderData: [],
            star: 5,
            starVisible: false,
            dId: 1,
            dName: "",
            activeTab: "pending", // 当前选中的标签页
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
        // 显示评价对话框
        showRatingDialog(order) {
            this.dId = order.dId;
            this.dName = order.dName;
            this.star = 5; // 重置评分
            this.starVisible = true;
        },
        //评价点击确认
        starClick() {
            request
                .get("doctor/updateStar", {
                    params: {
                        dId: this.dId,
                        dStar: this.star,
                    },
                })
                .then((res) => {
                    if (res.data.status !== 200)
                        return this.$message.error("评价失败");
                    this.$message.success("谢谢您的评价");
                    this.starVisible = false;
                    this.requestOrder(); // 刷新数据
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
                        });
                    this.starVisible = true;
                    this.requestOrder();
                });
        },
        //请求挂号信息
        requestOrder() {
            request
                .get("patient/findOrderByPid", {
                    params: {
                        pId: this.userId,
                    },
                })
                .then((res) => {
                    if (res.data.status !== 200)
                        this.$message.error("请求数据失败");
                    this.orderData = res.data.data;
                });
        },
        //token解码
        tokenDecode(token) {
            if (token !== null) return jwtDecode(token);
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
    },
    created() {
        //解码token
        this.userId = this.tokenDecode(getToken()).pId;
        this.requestOrder();
    },
};
</script>
<style lang="scss" scoped>
.el-dialog div {
    text-align: center;
    margin-bottom: 8px;
}
</style>
