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
                                @click="openClick(scope.row.dId, scope.row.dName)"
                                class="book-btn"
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
                            :key="time"
                            :label="time"
                            :value="time"
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
                        // 从后端获取医生数据，确保包含所有字段
                        this.sectionData = res.data.data.map((item) => {
                            const doctor = item.doctor || {};
                            return {
                                dId: doctor.dId,
                                dName: doctor.dName,
                                dGender: doctor.dGender || doctor.dSex || '',
                                dPost: doctor.dPost || '',
                                dSection: doctor.dSection || '',
                                dIntroduction: doctor.dIntroduction || '',
                                dPrice: doctor.dPrice || 0,
                                dAvgStar: doctor.dAvgStar || 0
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
            // 使用选择的日期作为预约日期
            if (this.selectedDate) {
                this.orderForm.orderDate = this.selectedDate;
            }
            this.orderFormVisible = true;
            this.requestTime(id);
        },
    },
    created() {
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

