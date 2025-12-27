<!--
 * 医生今日挂号列表
 *
-->
<template>
    <el-card class="order-today-card">
        <div slot="header" class="card-header">
            <span class="card-title">
                <i class="el-icon-date"></i> 今日挂号列表
            </span>
        </div>
        <el-table 
            :data="orderData" 
            size="small" 
            stripe 
            border
            class="order-table"
            :header-cell-style="{ background: '#f5f7fa', color: '#606266', textAlign: 'center', fontWeight: 'bold' }"
        >
            <el-table-column label="序号" type="index" width="70" align="center"></el-table-column>
            <el-table-column label="挂号单号" prop="oId" width="100" align="center"></el-table-column>
            <el-table-column label="患者ID" prop="pId" width="90" align="center"></el-table-column>
            <el-table-column label="患者姓名" prop="pName" width="120" align="center"></el-table-column>
            <el-table-column label="医生姓名" prop="dName" width="120" align="center"></el-table-column>
            <el-table-column label="预约时间段" width="220" align="center">
                <template slot-scope="scope">
                    <span class="time-slot">{{ formatTimeSlot(scope.row.oStart) }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center" fixed="right">
                <template slot-scope="scope">
                    <div class="action-buttons">
                        <el-button 
                            size="mini" 
                            type="warning" 
                            icon="el-icon-edit"
                            @click="dealClick(scope.row.oId, scope.row.pId)"
                            :disabled="!canProcessOrder(scope.row)"
                            :title="getProcessButtonTitle(scope.row)"
                        >
                            处理
                        </el-button>
                        <el-tag 
                            v-if="scope.row.oState === 1" 
                            type="success" 
                            size="small"
                            style="margin-left: 8px;"
                        >
                            已处理
                        </el-tag>
                    </div>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
</template>
<script>
import jwtDecode from "jwt-decode";
import { getToken} from "@/utils/storage.js";
import request from "@/utils/request.js";
export default {
    name: "orderToday",
    data() {
        return {
            userId: 1,
            userName: "dada",
            today: "",

            orderData:[],

        }
    },
    methods: {
        //判断是否可以处理订单（只有在预约时间段内才能处理）
        canProcessOrder(order) {
            // 如果订单已完成，不能处理
            if (order.oState === 1) {
                return false;
            }
            
            // 检查预约时间
            if (!order.oStart) {
                return false;
            }
            
            try {
                // 解析预约时间，格式：yyyy-MM-dd HH:mm-HH:mm
                // 例如：2025-01-20 08:30-09:30
                let startTimeStr = order.oStart;
                let endTimeStr = order.oStart;
                
                if (order.oStart.length > 11) {
                    const datePart = order.oStart.substring(0, 10); // yyyy-MM-dd
                    const timePart = order.oStart.substring(11); // HH:mm-HH:mm
                    
                    if (timePart.includes("-")) {
                        const times = timePart.split("-");
                        if (times.length >= 2) {
                            const startTime = times[0]; // 08:30
                            const endTime = times[1]; // 09:30
                            startTimeStr = datePart + " " + startTime + ":00"; // 2025-01-20 08:30:00
                            endTimeStr = datePart + " " + endTime + ":00"; // 2025-01-20 09:30:00
                        }
                    }
                }
                
                // 解析预约开始时间和结束时间
                const appointmentStartTime = new Date(startTimeStr.replace(/-/g, "/"));
                const appointmentEndTime = new Date(endTimeStr.replace(/-/g, "/"));
                const now = new Date();
                
                // 检查当前时间是否在预约时间段内
                return now >= appointmentStartTime && now <= appointmentEndTime;
            } catch (e) {
                console.error("解析预约时间失败:", e);
                return false;
            }
        },
        //获取处理按钮的提示信息
        getProcessButtonTitle(order) {
            if (order.oState === 1) {
                return "该订单已完成处理";
            }
            
            if (!this.canProcessOrder(order)) {
                if (!order.oStart) {
                    return "预约时间无效";
                }
                
                try {
                    const datePart = order.oStart.substring(0, 10);
                    const timePart = order.oStart.substring(11);
                    if (timePart && timePart.includes("-")) {
                        const times = timePart.split("-");
                        if (times.length >= 2) {
                            return `请在 ${times[0]}-${times[1]} 时间段内处理`;
                        }
                    }
                } catch (e) {
                    // 忽略错误
                }
                
                return "未到预约时间段，无法处理";
            }
            
            return "点击处理该预约";
        },
        //挂号处理//页面跳转传值
        dealClick(oId, pId){
            // 再次检查是否可以处理
            const order = this.orderData.find(o => o.oId === oId);
            if (order && !this.canProcessOrder(order)) {
                this.$message.warning("未到预约时间段，无法处理该预约");
                return;
            }
            
            this.$router.push(
                {
                    path: "/dealOrder",
                    query: {
                        oId: oId,
                        pId: pId
                    }
                }
            );

        },
        //获取挂号信息
        requestOrder(){
            request.get("doctor/findOrderByNull", {
                params: {
                    dId: this.userId,
                    oStart: this.today
                }
            })
            .then(res => {
                if(res.data.status != 200)
                return this.$message.error("获取数据失败");
               this.orderData = res.data.data;
            })
        },
    //token解码
    tokenDecode(token){
      if (token !== null)
      return jwtDecode(token);
    },
        //获取当天日期
        nowDay(){
            const nowDate = new Date();
            let date = {
              year: nowDate.getFullYear(),
              month: nowDate.getMonth() + 1,
              date: nowDate.getDate(),
            };
            if(date.date < 10){
              date.date = "0"+ date.date
            }
            if(date.month < 10){
              date.month = "0"+date.month
            }
            this.today = date.year+"-"+date.month+"-"+date.date;

          },
        //格式化预约时间段
        formatTimeSlot(oStart) {
            if (!oStart) return "-";
            
            try {
                // 如果格式是 yyyy-MM-dd HH:mm-HH:mm，直接格式化显示
                if (oStart.length > 11 && oStart.includes("-") && oStart.substring(11).includes("-")) {
                    const datePart = oStart.substring(0, 10); // yyyy-MM-dd
                    const timePart = oStart.substring(11); // HH:mm-HH:mm
                    
                    // 格式化日期部分（例如：2025-12-28）
                    const [year, month, day] = datePart.split("-");
                    const formattedDate = `${year}-${month}-${day}`;
                    
                    // 返回：2025-12-28 08:30-09:30
                    return `${formattedDate} ${timePart}`;
                }
                
                return oStart;
            } catch (e) {
                console.error("格式化预约时间段失败:", e);
                return oStart;
            }
        },
    },
    created(){
        //解码token信息
        this.userId = this.tokenDecode(getToken()).dId;
        this.userName = this.tokenDecode(getToken()).dName;
        //获取当天日期
        this.nowDay();
        //获取订单信息
        this.requestOrder();
    },
}
</script>
<style scoped lang="scss">
.order-today-card {
    margin: 20px;
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
    }
}

.order-table {
    width: 100%;
    
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
    
    // 操作按钮区域
    .action-buttons {
        display: flex;
        justify-content: center;
        align-items: center;
    }
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
