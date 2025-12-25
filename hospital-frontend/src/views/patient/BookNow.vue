<!--
 * 立即预约页面
 *
-->
<template>
    <div class="book-now-container">
        <h2 class="page-title">立即预约</h2>
        
        <!-- 搜索栏 -->
        <el-card class="search-card">
            <div class="search-section">
                <div class="search-item">
                    <label>科目搜索：</label>
                    <el-select 
                        v-model="selectedSection" 
                        placeholder="请选择科室"
                        @change="onSectionChange"
                        style="width: 200px;"
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
                    <label>日期搜索：</label>
                    <el-date-picker
                        v-model="selectedDate"
                        type="date"
                        placeholder="选择日期"
                        format="yyyy-MM-dd"
                        value-format="yyyy-MM-dd"
                        @change="onDateChange"
                        style="width: 200px;"
                    >
                    </el-date-picker>
                </div>
            </div>
        </el-card>

        <!-- 医生列表 -->
        <el-card class="doctor-list-card">
            <div class="list-header">
                <span>医生列表</span>
                <span v-if="selectedSection && selectedDate" class="search-info">
                    （科室：<span class="highlight">{{ selectedSection }}</span> 
                    挂号时间：<span class="highlight">{{ selectedDate }}</span>）
                </span>
            </div>

            <el-divider></el-divider>

            <!-- 选择挂号时间 -->
            <div v-if="selectedDate" class="date-selector">
                <span class="date-label">请选择你要挂号的日期：</span>
                <div class="date-buttons">
                    <el-button
                        v-for="monthDay in monthDays"
                        :key="monthDay"
                        size="mini"
                        type="primary"
                        plain
                        @click="dateClick(monthDay)"
                    >
                        {{ monthDay }}
                    </el-button>
                </div>
            </div>

            <br/>
            
            <!-- 表格 -->
            <el-table :data="sectionData" size="small" stripe style="width: 100%" border>
                <el-table-column type="index" label="序号" width="60"/>
                <el-table-column prop="dId" label="工号" width="80"/>
                <el-table-column prop="dName" label="姓名" width="80"/>
                <el-table-column prop="dGender" label="性别" width="60"/>
                <el-table-column prop="dPost" label="职位" width="100"/>
                <el-table-column prop="dSection" label="科室" width="100"/>
                <el-table-column prop="dIntroduction" label="简介" />
                <el-table-column prop="dPrice" label="挂号费用/元" width="100"/>
                <el-table-column prop="dAvgStar" label="评价/5分" width="80"/>
                <el-table-column label="操作" width="140" v-if="clickTag">
                    <template slot-scope="scope">
                        <el-button
                            class="iconfont icon-register-button"
                            style="font-size: 14px"
                            size="mini"
                            type="primary"
                            @click="openClick(scope.row.dId, scope.row.dName)"
                        >
                            挂号
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 挂号对话框 -->
        <el-dialog title="填写挂号信息" :visible.sync="orderFormVisible">
            <el-form :model="orderForm" ref="orderForm" :rules="orderRules">
                <el-form-item
                    label="挂号时间段"
                    label-width="100px"
                    prop="oTime"
                >
                    <el-select
                        v-model="orderForm.oTime"
                        placeholder="请选择"
                        no-data-text="请尝试预约明日"
                    >
                        <el-option
                            v-for="time in times"
                            :key="time"
                            :label="time"
                            :value="time"
                        >
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="挂号日期" label-width="100px">
                    <el-input
                        v-model="orderForm.orderDate"
                        autocomplete="off"
                        disabled
                    ></el-input>
                </el-form-item>
                <el-form-item label="医生工号" label-width="100px">
                    <el-input
                        v-model="orderForm.dId"
                        autocomplete="off"
                        disabled
                    ></el-input>
                </el-form-item>
                <el-form-item label="医生姓名" label-width="100px">
                    <el-input
                        v-model="orderForm.dName"
                        autocomplete="off"
                        disabled
                    ></el-input>
                </el-form-item>
                <el-form-item label="患者姓名" label-width="100px">
                    <el-input
                        v-model="orderForm.pName"
                        autocomplete="off"
                        disabled
                    ></el-input>
                </el-form-item>
                <el-form-item label="患者身份证号" label-width="100px">
                    <el-input
                        v-model="orderForm.pCard"
                        autocomplete="off"
                        disabled
                    ></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="orderFormVisible = false" style="font-size: 18px;">
                    <i class="iconfont icon-cancel-button" style="font-size: 20px;"></i> 取 消
                </el-button>
                <el-button type="primary" @click="orderSuccess('orderForm')" style="font-size: 18px;">
                    <i class="iconfont icon-sure-button" style="font-size: 20px;"></i> 确 定
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
            monthDays: [],
            clickTag: false,
            orderFormVisible: false,
            orderForm: { orderDate: "" },
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
            // 更新日期按钮
            this.updateMonthDays();
        },
        updateMonthDays() {
            this.monthDays = [];
            for (let i = 0; i < 7; i++) {
                this.nowDay(i);
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
                    this.sectionData = res.data.data.map((item) => item.doctor);
                    this.clickTag = true;
                })
                .catch((err) => {
                    console.error(err);
                    this.$message.error("获取医生列表失败");
                });
        },
        dateClick(date) {
            const nowDate = new Date();
            let year = nowDate.getFullYear();
            this.orderForm.orderDate = year + "-" + date;
            let dateYear = year + "-" + date;
            this.orderDate = dateYear;
            this.selectedDate = dateYear;
            this.loadDoctors();
        },
        nowDay(num) {
            let nowDate = new Date();
            let currentHour = nowDate.getHours();
            let currentMinute = nowDate.getMinutes();

            if (
                currentHour > 17 ||
                (currentHour === 17 && currentMinute > 30)
            ) {
                num++;
            }

            nowDate.setDate(nowDate.getDate() + num);
            let month = nowDate.getMonth() + 1;
            let date = nowDate.getDate();
            if (date < 10) {
                date = "0" + date;
            }
            if (month < 10) {
                month = "0" + month;
            }
            let time = month + "-" + date;
            this.monthDays.push(time);
        },
        requestTime(id) {
            this.idTime = id + this.orderDate;
            request
                .get("order/findOrderTime", {
                    params: {
                        arId: this.idTime,
                    },
                })
                .then((res) => {
                    const date = new Date(this.orderDate);
                    const today = new Date();
                    const isToday =
                        date.getFullYear() === today.getFullYear() &&
                        date.getMonth() === today.getMonth() &&
                        date.getDate() === today.getDate();
                    let array = [];
                    if (!this.isTimeAfterTarget("09:30") || !isToday) {
                        array.push("08:30-09:30  " + "   余号 " + "1");
                    }
                    if (!this.isTimeAfterTarget("10:30") || !isToday) {
                        array.push("09:30-10:30  " + "   余号 " + "1");
                    }
                    if (!this.isTimeAfterTarget("11:30") || !isToday) {
                        array.push("10:30-11:30  " + "   余号 " + "1");
                    }
                    if (!this.isTimeAfterTarget("15:30") || !isToday) {
                        array.push("14:30-15:30  " + "   余号 " + "1");
                    }
                    if (!this.isTimeAfterTarget("16:30") || !isToday) {
                        array.push("15:30-16:30  " + "   余号 " + "1");
                    }
                    if (!this.isTimeAfterTarget("17:30") || !isToday) {
                        array.push("16:30-17:30  " + "   余号 " + "1");
                    }
                    this.times = array;
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
                            if (res.data.status != 200)
                                return this.$message.error(res.data.msg);
                            this.orderFormVisible = false;
                            this.$message.success("挂号成功！");
                            this.orderForm.oTime = '';
                            this.loadDoctors();
                        });
                } else {
                    return false;
                }
            });
        },
        tokenDecode(token) {
            if (token !== null) return jwtDecode(token);
        },
        openClick(id, name) {
            this.orderForm.dId = id;
            this.orderForm.dName = name;
            this.orderFormVisible = true;
            this.requestTime(id);
        },
    },
    created() {
        this.updateMonthDays();
        const token = getToken();
        if (token) {
            const decoded = this.tokenDecode(token);
            this.orderForm.pName = decoded.pName;
            this.orderForm.pCard = decoded.pCard;
            this.orderForm.pId = decoded.pId;
        }
    },
};
</script>

<style scoped lang="scss">
.book-now-container {
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

.search-card {
    margin-bottom: 20px;
    
    .search-section {
        display: flex;
        gap: 30px;
        align-items: center;
        
        .search-item {
            display: flex;
            align-items: center;
            gap: 10px;
            
            label {
                font-size: 14px;
                color: #606266;
                white-space: nowrap;
            }
        }
    }
}

.doctor-list-card {
    .list-header {
        font-size: 16px;
        color: #333;
        margin-bottom: 10px;
        
        .search-info {
            color: #606266;
            margin-left: 10px;
            
            .highlight {
                color: #e75c09;
            }
        }
    }
    
    .date-selector {
        margin: 15px 0;
        
        .date-label {
            font-size: 14px;
            color: #606266;
            margin-right: 10px;
        }
        
        .date-buttons {
            display: inline-block;
            
            .el-button {
                margin-right: 5px;
                margin-bottom: 5px;
            }
        }
    }
}
</style>

