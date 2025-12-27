<!--
 * 立即预约页面
 *
-->
<template>
    <div class="book-now-container">
        <!-- 页面标题 -->
        <div class="page-header">
            <div class="header-icon">
                <i class="el-icon-calendar"></i>
            </div>
            <div class="header-text">
                <h2 class="page-title">立即预约</h2>
                <p class="page-subtitle">选择科室和日期，快速预约医生</p>
            </div>
        </div>
        
        <!-- 搜索栏 -->
        <el-card class="search-card" shadow="hover">
            <div slot="header" class="card-header">
                <i class="el-icon-search"></i>
                <span>筛选条件</span>
            </div>
            <div class="search-section">
                <div class="search-item">
                    <div class="search-label">
                        <i class="el-icon-office-building"></i>
                        <span>科室</span>
                    </div>
                    <el-select 
                        v-model="selectedSection" 
                        placeholder="请选择科室"
                        @change="onSectionChange"
                        class="search-select"
                        clearable
                    >
                        <el-option
                            v-for="section in allSections"
                            :key="section"
                            :label="section"
                            :value="section"
                        >
                        </el-option>
                    </el-select>
                </div>
                <div class="search-item">
                    <div class="search-label">
                        <i class="el-icon-date"></i>
                        <span>日期</span>
                    </div>
                    <el-date-picker
                        v-model="selectedDate"
                        type="date"
                        placeholder="选择日期"
                        format="yyyy-MM-dd"
                        value-format="yyyy-MM-dd"
                        @change="onDateChange"
                        class="search-select"
                        clearable
                    >
                    </el-date-picker>
                </div>
            </div>
        </el-card>

        <!-- 医生列表 -->
        <el-card class="doctor-list-card" shadow="hover">
            <div slot="header" class="card-header">
                <i class="el-icon-user-solid"></i>
                <span>医生列表</span>
                <span v-if="selectedSection && selectedDate" class="search-info">
                    <span class="info-tag">
                        <i class="el-icon-office-building"></i>
                        {{ selectedSection }}
                    </span>
                    <span class="info-tag">
                        <i class="el-icon-date"></i>
                        {{ selectedDate }}
                    </span>
                </span>
            </div>

            <!-- 表格 -->
            <div v-if="sectionData.length > 0" class="table-wrapper">
                <el-table 
                    :data="sectionData" 
                    size="medium" 
                    stripe 
                    style="width: 100%" 
                    :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: '600' }"
                >
                    <el-table-column type="index" label="序号" width="70" align="center"/>
                    <el-table-column prop="dId" label="工号" width="90" align="center"/>
                    <el-table-column prop="dName" label="姓名" width="100" align="center">
                        <template slot-scope="scope">
                            <span class="doctor-name">{{ scope.row.dName }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="dGender" label="性别" width="80" align="center">
                        <template slot-scope="scope">
                            <span v-if="scope.row.dGender">{{ scope.row.dGender }}</span>
                            <span v-else class="empty-text">-</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="dPost" label="职位" width="120" align="center">
                        <template slot-scope="scope">
                            <el-tag v-if="scope.row.dPost" size="small" type="info">{{ scope.row.dPost }}</el-tag>
                            <span v-else class="empty-text">-</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="dSection" label="科室" width="120" align="center"/>
                    <el-table-column prop="dIntroduction" label="简介" min-width="200" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <span v-if="scope.row.dIntroduction">{{ scope.row.dIntroduction }}</span>
                            <span v-else class="empty-text">暂无简介</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="dPrice" label="挂号费用" width="110" align="center">
                        <template slot-scope="scope">
                            <span class="price-text">¥{{ scope.row.dPrice }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="dAvgStar" label="评分" width="140" align="center">
                        <template slot-scope="scope">
                            <el-rate
                                :value="scope.row.dAvgStar || 0"
                                disabled
                                show-score
                                text-color="#ff9900"
                                score-template="{value}分"
                            ></el-rate>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="120" align="center" fixed="right" v-if="clickTag">
                        <template slot-scope="scope">
                            <el-button
                                size="small"
                                type="primary"
                                icon="el-icon-check"
                                @click="handleBookClick(scope.row)"
                                class="book-btn"
                                :disabled="!scope.row.arId"
                            >
                                挂号
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            
            <!-- 空状态 -->
            <el-empty 
                v-if="!selectedSection || !selectedDate" 
                description="请先选择科室和日期"
                :image-size="120"
            >
            </el-empty>
            <el-empty 
                v-else-if="sectionData.length === 0" 
                description="该日期暂无医生排班"
                :image-size="120"
            >
            </el-empty>
        </el-card>

        <!-- 挂号对话框 -->
        <el-dialog 
            title="填写挂号信息" 
            :visible.sync="orderFormVisible"
            width="600px"
            class="order-dialog"
        >
            <div class="dialog-header-info">
                <div class="info-item">
                    <i class="el-icon-user-solid"></i>
                    <span>医生：{{ orderForm.dName }}</span>
                </div>
                <div class="info-item">
                    <i class="el-icon-date"></i>
                    <span>日期：{{ orderForm.orderDate }}</span>
                </div>
            </div>
            
            <el-form :model="orderForm" ref="orderForm" :rules="orderRules" class="order-form">
                <el-form-item
                    label="挂号时间段"
                    label-width="120px"
                    prop="oTime"
                >
                    <el-select
                        v-model="orderForm.oTime"
                        placeholder="请选择时间段"
                        no-data-text="请尝试预约明日"
                        style="width: 100%"
                    >
                        <el-option
                            v-for="time in times"
                            :key="time.value"
                            :label="time.label"
                            :value="time.value"
                        >
                        </el-option>
                    </el-select>
                </el-form-item>
                
                <el-divider></el-divider>
                
                <div class="form-info-section">
                    <h4 class="section-title">
                        <i class="el-icon-info"></i>
                        预约信息
                    </h4>
                    <el-form-item label="挂号日期" label-width="120px">
                        <el-input
                            v-model="orderForm.orderDate"
                            autocomplete="off"
                            disabled
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="医生工号" label-width="120px">
                        <el-input
                            v-model="orderForm.dId"
                            autocomplete="off"
                            disabled
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="医生姓名" label-width="120px">
                        <el-input
                            v-model="orderForm.dName"
                            autocomplete="off"
                            disabled
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="患者姓名" label-width="120px">
                        <el-input
                            v-model="orderForm.pName"
                            autocomplete="off"
                            disabled
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="患者身份证号" label-width="120px">
                        <el-input
                            v-model="orderForm.pCard"
                            autocomplete="off"
                            disabled
                        ></el-input>
                    </el-form-item>
                </div>
            </el-form>
            
            <div slot="footer" class="dialog-footer">
                <el-button @click="orderFormVisible = false" size="medium">
                    <i class="el-icon-close"></i> 取 消
                </el-button>
                <el-button type="primary" @click="orderSuccess('orderForm')" size="medium">
                    <i class="el-icon-check"></i> 确认预约
                </el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import request from "@/utils/request";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage";

export default {
    name: "BookNow",
    data() {
        return {
            selectedSection: "",
            selectedDate: "",
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
                "功能科",
            ],
            sectionData: [],
            clickTag: false,
            orderFormVisible: false,
            orderForm: { 
                orderDate: "",
                oTime: ""
            },
            times: [],
            orderRules: {
                oTime: [
                    { required: true, message: "选择时间段", trigger: "blur" },
                ],
            },
            orderDate: "",
            idTime: "",
        };
    },
    methods: {
        onSectionChange() {
            if (this.selectedSection && this.selectedDate) {
                this.loadDoctors();
            }
        },
        onDateChange() {
            if (this.selectedSection && this.selectedDate) {
                this.loadDoctors();
            }
        },
        loadDoctors() {
            if (!this.selectedSection || !this.selectedDate) {
                return;
            }
            
            request
                .get("/arrange/findByTime", {
                    params: {
                        arTime: this.selectedDate,
                        dSection: this.selectedSection,
                    },
                })
                .then((res) => {
                    if (res.data.status === 200 && res.data.data) {
                        // 从后端获取医生数据，确保包含所有字段和排班ID
                        this.sectionData = res.data.data.map((item) => {
                            const doctor = item.doctor || {};
                            // 确保 arId 存在，如果不存在则使用 item 的 arId 或 item.arId
                            const arId = item.arId || item.ar_id || null;
                            console.log("排班数据项:", item, "arId:", arId);
                            return {
                                dId: doctor.dId,
                                dName: doctor.dName,
                                dGender: doctor.dGender || doctor.dSex || '',
                                dPost: doctor.dPost || '',
                                dSection: doctor.dSection || '',
                                dIntroduction: doctor.dIntroduction || '',
                                dPrice: doctor.dPrice || 0,
                                dAvgStar: doctor.dAvgStar || 0,
                                arId: arId // 保存排班ID
                            };
                        });
                        this.clickTag = true;
                    } else {
                        this.sectionData = [];
                        this.$message.warning(res.data.msg || '暂无医生数据');
                    }
                })
                .catch((err) => {
                    console.error(err);
                    this.$message.error("获取医生列表失败");
                });
        },
        requestTime(arId) {
            // 修复：直接使用排班ID，而不是拼接
            console.log("requestTime 调用，arId:", arId, "类型:", typeof arId);
            if (!arId) {
                this.$message.warning("排班信息不存在");
                this.times = [];
                return;
            }
            // 确保 arId 是字符串类型
            const arIdStr = String(arId).trim();
            if (!arIdStr) {
                this.$message.warning("排班ID无效");
                this.times = [];
                return;
            }
            // 获取日期用于时间段判断
            const dateToUse = this.orderDate || this.orderForm.orderDate || this.selectedDate;
            if (!dateToUse) {
                this.$message.warning("请先选择预约日期");
                this.times = [];
                return;
            }
            this.idTime = arIdStr;
            console.log("========== 开始请求时间段 ==========");
            console.log("发送请求，arId:", arIdStr);
            console.log("请求URL: order/findOrderTime");
            console.log("请求参数:", { arId: arIdStr });
            request
                .get("order/findOrderTime", {
                    params: {
                        arId: arIdStr, // 直接使用排班ID，确保是字符串
                    },
                })
                .then((res) => {
                    console.log("========== 收到响应 ==========");
                    console.log("响应状态码:", res.status);
                    console.log("响应数据:", res.data);
                    console.log("findOrderTime 完整响应:", JSON.stringify(res.data, null, 2));
                    if (res.data && res.data.status === 200 && res.data.data) {
                        const orderData = res.data.data;
                        console.log("orderData:", orderData);
                        console.log("timeSlots:", orderData.timeSlots);
                        const dateStr = dateToUse; // 格式: yyyy-MM-dd
                        const [year, month, day] = dateStr.split('-');
                        const date = new Date(parseInt(year), parseInt(month) - 1, parseInt(day));
                        const today = new Date();
                        today.setHours(0, 0, 0, 0);
                        const selectedDateOnly = new Date(date);
                        selectedDateOnly.setHours(0, 0, 0, 0);
                        const isToday = selectedDateOnly.getTime() === today.getTime();
                        
                        // 使用后端返回的时间段数据
                        let array = [];
                        if (orderData.timeSlots && Array.isArray(orderData.timeSlots) && orderData.timeSlots.length > 0) {
                            console.log("处理时间段数据，共", orderData.timeSlots.length, "个时间段");
                            orderData.timeSlots.forEach((slot, index) => {
                                console.log(`时间段 ${index + 1}:`, slot);
                                const timeSlot = slot.timeSlot; // 如：08:30-09:30
                                const remainingCount = slot.remainingCount !== undefined ? slot.remainingCount : 0;
                                
                                console.log(`时间段: ${timeSlot}, 剩余号数: ${remainingCount}, 是否今天: ${isToday}`);
                                
                                // 提取时间段的开始时间（如：08:30）
                                const startTime = timeSlot.split('-')[0];
                                
                                // 如果是今天，只显示当前时间之后的时间段
                                if (isToday) {
                                    const isTimePassed = this.isTimeAfterTarget(startTime);
                                    console.log(`时间段 ${timeSlot} 开始时间 ${startTime} 是否已过: ${isTimePassed}`);
                                    // 检查当前时间是否已超过该时间段的开始时间
                                    if (!isTimePassed) {
                                        // 当前时间未超过该时间段，可以显示
                                        if (remainingCount > 0) {
                                            array.push({
                                                label: `${timeSlot}    余号 ${remainingCount}`,
                                                value: timeSlot
                                            });
                                            console.log(`添加时间段: ${timeSlot}, 剩余号数: ${remainingCount}`);
                                        } else {
                                            console.log(`跳过时间段: ${timeSlot}, 剩余号数为0`);
                                        }
                                    } else {
                                        console.log(`跳过时间段: ${timeSlot}, 时间已过`);
                                    }
                                    // 如果当前时间已超过，不显示该时间段
                                } else {
                                    // 不是今天，显示所有有剩余号数的时间段
                                    if (remainingCount > 0) {
                                        array.push({
                                            label: `${timeSlot}    余号 ${remainingCount}`,
                                            value: timeSlot
                                        });
                                        console.log(`添加时间段: ${timeSlot}, 剩余号数: ${remainingCount}`);
                                    } else {
                                        console.log(`跳过时间段: ${timeSlot}, 剩余号数为0`);
                                    }
                                }
                            });
                        } else {
                            // 如果后端没有返回时间段数据，提示错误
                            console.error("后端未返回时间段数据");
                            this.$message.error("获取时间段数据失败，请刷新重试");
                            this.times = [];
                        }
                        this.times = array;
                        console.log("时间段数组:", array, "是否今天:", isToday, "选择日期:", dateToUse);
                    } else {
                        console.error("findOrderTime 返回错误:", res.data);
                        // 即使后端返回错误，也尝试显示时间段（降级处理）
                        const msg = res.data?.msg || "获取时间段失败";
                        console.warn(msg + "，将使用默认时间段");
                        // 继续执行时间段生成逻辑
                        const dateStr = dateToUse;
                        const [year, month, day] = dateStr.split('-');
                        const date = new Date(parseInt(year), parseInt(month) - 1, parseInt(day));
                        const today = new Date();
                        today.setHours(0, 0, 0, 0);
                        const selectedDateOnly = new Date(date);
                        selectedDateOnly.setHours(0, 0, 0, 0);
                        const isToday = selectedDateOnly.getTime() === today.getTime();
                        
                        // 错误处理：不显示降级时间段，提示用户
                        this.times = [];
                        this.$message.error("获取时间段数据失败，请刷新重试");
                    }
                })
                .catch((err) => {
                    console.error("========== 请求失败 ==========");
                    console.error("错误对象:", err);
                    console.error("错误信息:", err.message);
                    console.error("错误响应:", err.response);
                    console.error("请求参数 arId:", arId);
                    // 即使请求失败，也尝试显示时间段（可能是网络问题）
                    const dateStr = dateToUse;
                    const [year, month, day] = dateStr.split('-');
                    const date = new Date(parseInt(year), parseInt(month) - 1, parseInt(day));
                    const today = new Date();
                    today.setHours(0, 0, 0, 0);
                    const selectedDateOnly = new Date(date);
                    selectedDateOnly.setHours(0, 0, 0, 0);
                    const isToday = selectedDateOnly.getTime() === today.getTime();
                    
                    // 请求失败时，不显示时间段，提示用户
                    this.times = [];
                    this.$message.error("获取时间段失败，请检查网络连接后重试");
                });
        },
        isTimeAfterTarget(timeString) {
            const currentTime = new Date();
            const [targetHour, targetMinute] = timeString.split(":");
            const targetTime = new Date();
            targetTime.setHours(targetHour);
            targetTime.setMinutes(targetMinute);
            targetTime.setSeconds(0);
            return currentTime > targetTime;
        },
        orderSuccess(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    request
                        .get("patient/addOrder", {
                            params: {
                                pId: this.orderForm.pId,
                                dId: this.orderForm.dId,
                                oStart:
                                    this.orderForm.orderDate +
                                    " " +
                                    this.orderForm.oTime,
                                arId: this.idTime,
                            },
                        })
                        .then((res) => {
                            if (res.data.status != 200) {
                                this.$message.error(res.data.msg || "挂号失败");
                                return;
                            }
                            this.orderFormVisible = false;
                            this.$message.success("挂号成功！");
                            this.orderForm.oTime = '';
                            // 关闭对话框
                            this.orderFormVisible = false;
                            // 重新加载医生列表（会更新剩余号数）
                            this.loadDoctors();
                        })
                        .catch((err) => {
                            console.error("预约失败:", err);
                            this.$message.error(err.response?.data?.msg || err.message || "预约失败，请重试");
                        });
                } else {
                    return false;
                }
            });
        },
        tokenDecode(token) {
            if (token !== null) return jwtDecode(token);
        },
        handleBookClick(row) {
            console.log("========== 点击挂号按钮 ==========");
            console.log("行数据:", row);
            const id = row.dId;
            const name = row.dName;
            const arId = row.arId;
            console.log("提取的参数:", { id, name, arId });
            this.openClick(id, name, arId);
        },
        openClick(id, name, arId) {
            console.log("========== openClick 方法被调用 ==========");
            console.log("openClick 参数:", { id, name, arId });
            console.log("当前选择的日期:", this.selectedDate);
            this.orderForm.dId = id;
            this.orderForm.dName = name;
            // 使用选择的日期作为预约日期
            if (this.selectedDate) {
                this.orderForm.orderDate = this.selectedDate;
                this.orderDate = this.selectedDate; // 修复：同时设置 orderDate，供 requestTime 使用
                console.log("设置预约日期:", this.selectedDate);
            }
            // 检查 arId 是否存在
            if (!arId) {
                console.error("arId 为空，无法预约");
                this.$message.error("排班信息不存在，无法预约");
                return;
            }
            // 清空之前的时间段，避免显示旧数据
            this.times = [];
            this.orderForm.oTime = '';
            this.orderFormVisible = true;
            console.log("对话框已打开，准备请求时间段");
            // 延迟一下再请求，确保对话框已打开
            this.$nextTick(() => {
                console.log("$nextTick 执行，开始调用 requestTime");
                this.requestTime(arId); // 传递排班ID而不是医生ID
            });
        },
    },
    created() {
        console.log("========== BookNow 组件已创建 ==========");
        console.log("页面加载时间:", new Date().toLocaleString());
        const token = getToken();
        if (token) {
            const decoded = this.tokenDecode(token);
            this.orderForm.pName = decoded.pName;
            this.orderForm.pCard = decoded.pCard;
            this.orderForm.pId = decoded.pId;
            console.log("患者信息已加载:", { pName: decoded.pName, pId: decoded.pId });
        } else {
            console.warn("未找到 token");
        }
    },
    mounted() {
        console.log("========== BookNow 组件已挂载 ==========");
        console.log("如果看到这条消息，说明控制台工作正常");
    },
};
</script>

<style scoped lang="scss">
.book-now-container {
    padding: 30px;
    max-width: 1400px;
    margin: 0 auto;
    background: #f5f7fa;
    min-height: calc(100vh - 70px);
}

// 页面标题
.page-header {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-bottom: 30px;
    padding: 20px 0;
    
    .header-icon {
        width: 60px;
        height: 60px;
        background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
        border-radius: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 16px rgba(64, 158, 255, 0.3);
        
        i {
            font-size: 32px;
            color: #fff;
        }
    }
    
    .header-text {
        flex: 1;
        
        .page-title {
            font-size: 32px;
            font-weight: 700;
            color: #303133;
            margin: 0 0 8px 0;
            letter-spacing: 0.5px;
        }
        
        .page-subtitle {
            font-size: 15px;
            color: #909399;
            margin: 0;
        }
    }
}

// 搜索卡片
.search-card {
    margin-bottom: 24px;
    border-radius: 12px;
    border: 1px solid #e4e7ed;
    transition: all 0.3s ease;
    
    &:hover {
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    }
    
    ::v-deep .el-card__header {
        background: linear-gradient(135deg, #f5f7fa 0%, #ecf5ff 100%);
        border-bottom: 1px solid #e4e7ed;
        padding: 16px 20px;
        
        .card-header {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 16px;
            font-weight: 600;
            color: #303133;
            
            i {
                color: #409EFF;
                font-size: 18px;
            }
        }
    }
    
    .search-section {
        display: flex;
        gap: 40px;
        align-items: flex-end;
        padding: 10px 0;
        
        .search-item {
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 12px;
            
            .search-label {
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: 14px;
                font-weight: 500;
                color: #606266;
                
                i {
                    color: #409EFF;
                    font-size: 16px;
                }
            }
            
            .search-select {
                width: 100%;
            }
        }
    }
}

// 医生列表卡片
.doctor-list-card {
    border-radius: 12px;
    border: 1px solid #e4e7ed;
    transition: all 0.3s ease;
    
    &:hover {
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    }
    
    ::v-deep .el-card__header {
        background: linear-gradient(135deg, #f5f7fa 0%, #ecf5ff 100%);
        border-bottom: 1px solid #e4e7ed;
        padding: 16px 20px;
        
        .card-header {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 16px;
            font-weight: 600;
            color: #303133;
            
            i {
                color: #409EFF;
                font-size: 18px;
            }
            
            .search-info {
                margin-left: auto;
                display: flex;
                gap: 12px;
                
                .info-tag {
                    display: inline-flex;
                    align-items: center;
                    gap: 4px;
                    padding: 4px 12px;
                    background: #fff;
                    border: 1px solid #409EFF;
                    border-radius: 16px;
                    font-size: 13px;
                    color: #409EFF;
                    
                    i {
                        font-size: 14px;
                    }
                }
            }
        }
    }
    
    .table-wrapper {
        margin-top: 20px;
        
        ::v-deep .el-table {
            border-radius: 8px;
            overflow: hidden;
            
            .doctor-name {
                font-weight: 600;
                color: #303133;
            }
            
            .price-text {
                color: #f56c6c;
                font-weight: 600;
                font-size: 15px;
            }
            
            .book-btn {
                border-radius: 6px;
                transition: all 0.3s ease;
                
                &:hover {
                    transform: translateY(-1px);
                    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
                }
            }
            
            .empty-text {
                color: #c0c4cc;
                font-size: 13px;
            }
        }
    }
}

// 对话框样式
::v-deep .order-dialog {
    .el-dialog {
        border-radius: 16px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
        
        .el-dialog__header {
            padding: 24px 24px 16px;
            border-bottom: 1px solid #f0f2f5;
            
            .el-dialog__title {
                font-size: 20px;
                font-weight: 600;
                color: #303133;
            }
        }
        
        .el-dialog__body {
            padding: 24px;
        }
    }
    
    .dialog-header-info {
        display: flex;
        gap: 20px;
        padding: 16px;
        background: linear-gradient(135deg, #f5f7fa 0%, #ecf5ff 100%);
        border-radius: 8px;
        margin-bottom: 20px;
        
        .info-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            color: #606266;
            
            i {
                color: #409EFF;
                font-size: 16px;
            }
        }
    }
    
    .order-form {
        .form-info-section {
            .section-title {
                display: flex;
                align-items: center;
                gap: 8px;
                font-size: 15px;
                font-weight: 600;
                color: #303133;
                margin: 0 0 16px 0;
                
                i {
                    color: #409EFF;
                }
            }
        }
    }
    
    .dialog-footer {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
        padding-top: 16px;
        border-top: 1px solid #f0f2f5;
        
        .el-button {
            border-radius: 8px;
            padding: 10px 24px;
            
            i {
                margin-right: 4px;
            }
        }
    }
}

// 响应式设计
@media (max-width: 768px) {
    .book-now-container {
        padding: 20px 15px;
    }
    
    .page-header {
        flex-direction: column;
        text-align: center;
        
        .header-icon {
            width: 50px;
            height: 50px;
            
            i {
                font-size: 26px;
            }
        }
        
        .header-text .page-title {
            font-size: 26px;
        }
    }
    
    .search-card .search-section {
        flex-direction: column;
        gap: 20px;
    }
    
    .doctor-list-card .date-selector .date-buttons {
        .date-btn {
            flex: 1;
            min-width: calc(50% - 5px);
        }
    }
}
</style>

