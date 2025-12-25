<!--
 * 我的预约页面
 *
-->
<template>
    <div class="my-appointments-container">
        <h2 class="page-title">我的预约</h2>
        
        <el-card>
            <el-table :data="orderData" size="small" stripe style="width: 100%" border>
                <el-table-column prop="oId" label="挂号单号" width="100px"/>
                <el-table-column prop="pName" label="姓名" width="100px"/>
                <el-table-column prop="dName" label="医生姓名" width="100px"/>
                <el-table-column prop="oStart" label="挂号时间" width="180px"/>
                <el-table-column prop="oEnd" label="结束时间" width="180px"/>
                <el-table-column prop="oTotalPrice" label="需交费用/元" width="120px"/>
                <el-table-column prop="oPriceState" label="缴费状态" width="150px">
                    <template slot-scope="scope">
                        <el-tag
                            type="success"
                            v-if="scope.row.oPriceState === 1"
                        >已缴费</el-tag>
                        <el-button
                            size="mini"
                            type="warning"
                            style="font-size: 14px"
                            v-if="
                                scope.row.oPriceState === 0 &&
                                scope.row.oState === 1
                            "
                            @click="priceClick(scope.row.oId, scope.row.dId)"
                        >
                            <i class="iconfont icon-edit-button" style="font-size: 12px;"></i>点击缴费</el-button>
                    </template>
                </el-table-column>
                <el-table-column prop="oState" label="挂号状态" width="120px">
                    <template slot-scope="scope">
                        <el-tag
                            size="mini"
                            type="primary"
                            v-if="
                                scope.row.oState === 1 &&
                                scope.row.oPriceState === 1
                            "
                        >已完成</el-tag>
                        <el-tag
                            type="danger"
                            v-if="
                                scope.row.oState === 0 && scope.row.oState === 0
                            "
                        >未完成</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="报告单" width="120px">
                    <template slot-scope="scope">
                        <el-button
                            type="success"
                            size="mini"
                            style="font-size: 14px"
                            @click="seeReport(scope.row.oId)"
                            v-if="
                                scope.row.oState === 1 &&
                                scope.row.oPriceState === 1
                            "
                        >
                            <i class="iconfont icon-export-button" style="font-size: 12px;"></i>导出
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
        
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
            star: 5,
            starVisible: false,
            dId: 1,
            dName: "",
            oId: null,  // 当前评价的订单ID
            reviewContent: "",  // 评价内容
            impressions: []  // 患者印象标签
        };
    },
    methods: {
        //评价点击确认
        starClick() {
            // 先更新医生评分
            request
                .get("doctor/updateStar", {
                    params: {
                        dId: this.dId,
                        dStar: this.star,
                    },
                })
                .then((res) => {
                    if (res.data.status !== 200) {
                        this.$message.error("评价失败");
                        return;
                    }
                    // 提交评价到review表
                    const reviewData = {
                        pId: this.userId,
                        dId: this.dId,
                        oId: this.oId,
                        rStar: this.star,
                        rContent: this.reviewContent || "暂无评价内容",
                        rImpressions: this.impressions.join(",")
                    };
                    return request.post("review/add", reviewData);
                })
                .then((res) => {
                    if (res && res.data.status !== 200) {
                        this.$message.error("评价提交失败");
                        return;
                    }
                    this.$message.success("谢谢您的评价");
                    this.starVisible = false;
                    // 清空表单
                    this.reviewContent = "";
                    this.impressions = [];
                    this.star = 5;
                })
                .catch((err) => {
                    console.error(err);
                    this.$message.error("评价提交失败，请稍后重试");
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
    background: #fff;
    min-height: calc(100vh - 70px);
}

.page-title {
    font-size: 24px;
    font-weight: 600;
    color: #333;
    margin-bottom: 30px;
}

.el-dialog div {
    text-align: center;
    margin-bottom: 8px;
}
</style>

