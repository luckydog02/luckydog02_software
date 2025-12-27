<!--
 * 医生历史挂号列表
 *
-->
<template>
    <div class="doctor-order-container">
        <el-card class="order-card">
            <div slot="header" class="card-header">
                <span class="card-title">
                    <i class="el-icon-document"></i> 历史挂号列表
                </span>
                <!-- 搜索栏 -->
                <div class="search-box">
                    <el-input 
                        v-model="query" 
                        placeholder="请输入患者ID查询"
                        size="small"
                        style="width: 250px;"
                        clearable
                    >
                        <el-button
                            slot="append"
                            icon="el-icon-search"
                            @click="requestOrders"
                        ></el-button>
                    </el-input>
                </div>
            </div>
            <el-table 
                size="small" 
                :data="orderData" 
                stripe 
                border
                class="order-table"
                :header-cell-style="{ background: '#f5f7fa', color: '#606266', textAlign: 'center', fontWeight: 'bold' }"
            >
                <el-table-column prop="oId" label="挂号单号" width="100" align="center"></el-table-column>
                <el-table-column prop="dId" label="本人ID" width="90" align="center"></el-table-column>
                <el-table-column prop="pId" label="患者ID" width="90" align="center"></el-table-column>
                <el-table-column label="预约时间段" width="220" align="center">
                    <template slot-scope="scope">
                        <span class="time-slot">{{ formatTimeSlot(scope.row.oStart) }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="结束时间" width="180" align="center">
                    <template slot-scope="scope">
                        <span v-if="scope.row.oEnd">{{ formatTime(scope.row.oEnd) }}</span>
                        <span v-else class="no-data">-</span>
                    </template>
                </el-table-column>
                <el-table-column prop="oRecord" label="病因" width="200" align="center" show-overflow-tooltip>
                    <template slot-scope="scope">
                        <span v-if="scope.row.oRecord">{{ scope.row.oRecord }}</span>
                        <span v-else class="no-data">-</span>
                    </template>
                </el-table-column>
                <el-table-column prop="oDrug" label="药物" width="180" align="center" show-overflow-tooltip>
                    <template slot-scope="scope">
                        <span v-if="scope.row.oDrug">{{ scope.row.oDrug }}</span>
                        <span v-else class="no-data">-</span>
                    </template>
                </el-table-column>
                <el-table-column prop="oCheck" label="检查项目" width="180" align="center" show-overflow-tooltip>
                    <template slot-scope="scope">
                        <span v-if="scope.row.oCheck">{{ scope.row.oCheck }}</span>
                        <span v-else class="no-data">-</span>
                    </template>
                </el-table-column>
                <el-table-column label="需交费用/元" width="120" align="center">
                    <template slot-scope="scope">
                        <span class="price-text" v-if="scope.row.oTotalPrice">¥{{ scope.row.oTotalPrice.toFixed(2) }}</span>
                        <span v-else class="no-data">-</span>
                    </template>
                </el-table-column>
                <el-table-column label="缴费状态" width="110" align="center">
                    <template slot-scope="scope">
                        <el-tag
                            type="success"
                            size="small"
                            v-if="scope.row.oPriceState === 1"
                        >已缴费</el-tag>
                        <el-tag
                            type="danger"
                            size="small"
                            v-else
                        >未缴费</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="挂号状态" width="130" align="center">
                    <template slot-scope="scope">
                        <el-tag
                            type="primary"
                            size="small"
                            v-if="scope.row.oState === 1 && scope.row.oPriceState === 1"
                        >已完成</el-tag>
                        <el-tag
                            type="warning"
                            size="small"
                            v-else-if="scope.row.oState === 1 && scope.row.oPriceState === 0"
                        >已诊断未缴费</el-tag>
                        <el-tag
                            type="danger"
                            size="small"
                            v-else
                        >未完成</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120" align="center" fixed="right">
                    <template slot-scope="scope">
                        <el-button
                            size="mini"
                            type="warning"
                            icon="el-icon-edit"
                            @click="dealClick(scope.row.oId, scope.row.pId)"
                            v-if="scope.row.oState === 1 && scope.row.oPriceState === 1"
                        >追诊</el-button>
                        <span v-else class="no-action">-</span>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                background
                layout="total, sizes, prev, pager, next, jumper"
                :current-page="pageNumber"
                :page-size="size"
                :page-sizes="[1, 2, 4, 8, 16]"
                :total="total"
            >
            </el-pagination>
        </el-card>
    </div>
</template>
<script>
import request from "@/utils/request.js";
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
export default {
    name: "DoctorOrder",
    data() {
        return {
            userId: 1,
            orderData: [],
            pageNumber: 1,
            size: 8,
            query: "",
            total: 3,
        };
    },
    methods: {
        //点击追诊按钮
        dealClick(oId, pId) {
            this.$router.push({
                path: "/dealOrderAgain",
                query: {
                    oId: oId,
                    pId: pId,
                },
            });
        },
        //页面大小改变时触发
        handleSizeChange(size) {
            this.size = size;
            this.requestOrders();
        },
        //页码改变时触发
        handleCurrentChange(num) {
            this.pageNumber = num;
            this.requestOrders();
        },

        //请求挂号信息
        requestOrders() {
            request
                .get("order/findOrderByDid", {
                    params: {
                        dId: this.userId,
                        pageNumber: this.pageNumber,
                        size: this.size,
                        query: this.query,
                    },
                })
                .then((res) => {
                    if (res.data.status !== 200) {
                        this.$message.error("请求数据失败");
                        return;
                    }
                    this.orderData = res.data.data?.orders || [];
                    this.total = res.data.data?.total || 0;
                })
                .catch((err) => {
                    console.error("获取挂号列表失败:", err);
                    this.$message.error("获取挂号列表失败，请重试");
                });
        },
        //token解码
        tokenDecode(token) {
            if (token !== null) return jwtDecode(token);
        },
        //格式化预约时间段
        formatTimeSlot(oStart) {
            if (!oStart) return "-";
            
            try {
                // 如果格式是 yyyy-MM-dd HH:mm-HH:mm，直接格式化显示
                if (oStart.length > 11 && oStart.includes("-") && oStart.substring(11).includes("-")) {
                    const datePart = oStart.substring(0, 10); // yyyy-MM-dd
                    const timePart = oStart.substring(11); // HH:mm-HH:mm
                    
                    // 返回：2025-12-28 08:30-09:30
                    return `${datePart} ${timePart}`;
                }
                
                return oStart;
            } catch (e) {
                console.error("格式化预约时间段失败:", e);
                return oStart;
            }
        },
        //格式化时间
        formatTime(time) {
            if (!time) return "-";
            try {
                const date = new Date(time);
                if (isNaN(date.getTime())) return time;
                
                const year = date.getFullYear();
                const month = String(date.getMonth() + 1).padStart(2, "0");
                const day = String(date.getDate()).padStart(2, "0");
                const hour = String(date.getHours()).padStart(2, "0");
                const minute = String(date.getMinutes()).padStart(2, "0");
                return `${year}-${month}-${day} ${hour}:${minute}`;
            } catch (e) {
                return time;
            }
        },
    },
    created() {
        // 解码token
        this.userId = this.tokenDecode(getToken()).dId;
        this.requestOrders();
    },
};
</script>
<style lang="scss" scoped>
.doctor-order-container {
    padding: 20px;
}

.order-card {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    
    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .card-title {
            font-size: 18px;
            font-weight: 600;
            color: #303133;
            
            i {
                margin-right: 8px;
                color: #409EFF;
            }
        }
        
        .search-box {
            display: flex;
            align-items: center;
        }
    }
}

.order-table {
    margin-top: 20px;
    margin-bottom: 20px;
    
    // 表格单元格内容居中
    ::v-deep .el-table td,
    ::v-deep .el-table th {
        text-align: center;
    }
    
    // 表格行悬停效果
    ::v-deep .el-table--striped .el-table__body tr.el-table__row--striped:hover > td {
        background-color: #f0f9ff;
    }
    
    // 表格行悬停效果（非斑马纹行）
    ::v-deep .el-table__body tr:hover > td {
        background-color: #f0f9ff;
    }
    
    // 时间显示样式
    .time-slot {
        font-family: 'Courier New', monospace;
        color: #409EFF;
        font-weight: 500;
        font-size: 13px;
    }
    
    // 价格显示样式
    .price-text {
        color: #f56c6c;
        font-weight: 600;
        font-size: 14px;
    }
    
    // 无数据显示
    .no-data {
        color: #c0c4cc;
        font-style: italic;
    }
    
    // 无操作显示
    .no-action {
        color: #c0c4cc;
    }
}

// 分页样式
::v-deep .el-pagination {
    margin-top: 20px;
    text-align: right;
    padding: 10px 0;
}

// 空数据提示
::v-deep .el-table__empty-block {
    padding: 40px 0;
    
    .el-table__empty-text {
        color: #909399;
        font-size: 14px;
    }
}
</style>
