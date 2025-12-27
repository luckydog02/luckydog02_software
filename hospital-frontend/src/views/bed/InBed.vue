<!--
 * 患者住院列表
 *
-->
<template>
    <div class="inbed-container">
        <el-card class="inbed-card">
            <div slot="header" class="card-header">
                <span class="card-title">
                    <i class="el-icon-s-home"></i> 住院申请管理
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
            >
                <el-table-column label="挂号单号" prop="oId" width="110" align="center"></el-table-column>
                <el-table-column label="患者ID" prop="pId" width="100" align="center"></el-table-column>
                <el-table-column label="医生ID" prop="dId" width="100" align="center"></el-table-column>
                <el-table-column label="挂号时间" prop="oStart" width="200" align="center"></el-table-column>
                <el-table-column label="结束时间" prop="oEnd" width="180" align="center">
                    <template slot-scope="scope">
                        <span v-if="scope.row.oEnd">{{ formatTime(scope.row.oEnd) }}</span>
                        <span v-else>-</span>
                    </template>
                </el-table-column>
                <el-table-column label="挂号状态" prop="oState" width="120" align="center">
                    <template slot-scope="scope">
                        <el-tag 
                            v-if="scope.row.oState === 1" 
                            type="success"
                            size="small"
                            class="status-tag"
                        >已完成</el-tag>
                        <el-tag 
                            v-if="scope.row.oState === 0" 
                            type="danger"
                            size="small"
                            class="status-tag"
                        >未完成</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right" align="center">
                    <template slot-scope="scope">
                        <el-button
                            size="mini"
                            type="warning"
                            icon="el-icon-s-home"
                            @click="BedDiag(scope.row.pId, scope.row.dId)"
                        >申请住院</el-button>
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

        <!-- 住院对话框 -->
        <el-dialog 
            title="申请住院" 
            :visible.sync="BedFormVisible"
            width="500px"
            class="bed-dialog"
        >
            <el-form class="bed-form" :model="bedForm" label-width="100px">
                <el-form-item label="患者账号" prop="pId">
                    <el-input v-model="bedForm.pId" disabled></el-input>
                </el-form-item>
                <el-form-item label="医生账号">
                    <el-input v-model="bedForm.dId" disabled></el-input>
                </el-form-item>
                <el-form-item label="申请原因">
                    <el-input
                        v-model="bedForm.bReason"
                        type="textarea"
                        :rows="4"
                        placeholder="请输入申请住院的原因"
                    ></el-input>
                </el-form-item>
                <el-form-item label="病床号" prop="bId">
                    <el-select v-model="bedForm.bId" placeholder="请选择病床号" style="width: 100%;">
                        <el-option
                            v-for="item in nullBed"
                            :key="item.bId"
                            :label="`床位 ${item.bId}`"
                            :value="item.bId"
                        >
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="BedFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="bedClick">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
import request from "@/utils/request.js";
export default {
    name: "InBed",
    data() {
        return {
            userId: 1,
            userName: "",
            pageNumber: 1,
            size: 4,
            query: "",
            total: 3,
            orderData: [],
            //申请住院对话框
            BedFormVisible: false,
            bedForm: {},
            nullBed: [],
        };
    },
    methods: {
        //点击申请床位确认按钮
        bedClick() {
            request
                .get("bed/updateBed", {
                    params: {
                        bId: this.bedForm.bId,
                        dId: this.bedForm.dId,
                        pId: this.bedForm.pId,
                        bReason: this.bedForm.bReason,
                    },
                })
                .then((res) => {
                    if (res.data.status !== 200)
                        return this.$message.error("来晚了...该床位已被占用");
                    this.BedFormVisible = false;
                    this.$message.success("申请住院成功！");
                    this.requestOrders();
                });
        },

        //请求所有空床位
        requestBeds() {
            request
                .get("bed/findNullBed")
                .then((res) => {
                    if (res.data.status !== 200)
                        return this.$message.error("数据请求失败");
                    this.nullBed = res.data.data;
                })
                .catch((err) => {
                });
        },
        //打开申请住院对话框
        BedDiag(pId, dId) {
            this.bedForm.pId = pId;
            this.bedForm.dId = dId;
            this.BedFormVisible = true;
            this.requestBeds();
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
        //获取已完成的订单信息
        requestOrders() {
            request
                .get("order/findOrderFinish", {
                    params: {
                        dId: this.userId,
                        pageNumber: this.pageNumber,
                        size: this.size,
                        query: this.query,
                    },
                })
                .then((res) => {
                    if (res.data.status !== 200)
                        return this.$message.error("数据请求失败");
                    this.orderData = res.data.data.orders;
                    this.total = res.data.data.total;
                });
        },
        //token解码
        tokenDecode(token) {
            if (token !== null) return jwtDecode(token);
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
        //解码token信息
        this.userId = this.tokenDecode(getToken()).dId;
        this.userName = this.tokenDecode(getToken()).dName;
        //获取订单信息
        this.requestOrders();
    },
};
</script>
<style lang="scss" scoped>
.inbed-container {
    padding: 20px;
}

.inbed-card {
    
    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .card-title {
            font-size: 18px;
            font-weight: 600;
            
            i {
                margin-right: 8px;
            }
        }
        
        .search-box {
            display: flex;
            align-items: center;
        }
    }
}

// 表格样式
.order-table {
    margin-top: 20px;
    margin-bottom: 20px;
}


.bed-form {
    .el-form-item {
        margin-bottom: 20px;
        
        .el-form-item__label {
            font-weight: 500;
            color: #606266;
        }
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
