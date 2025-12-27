<!--
 * 患者管理列表
 *
-->
<template>
   <el-card>
        <!-- 搜索栏 -->
        <el-row type="flex">
            <el-col :span="8">
                <el-input v-model="query" placeholder="请输入患者姓名查询">
                    <el-button
                        slot="append"
                        icon="iconfont icon-search-button"
                        @click="requestPatients"
                        round
                    ></el-button>
                </el-input>
            </el-col>
        </el-row>

        <!-- 表格 -->
        <el-table 
          :data="patientData" 
          size="small" 
          stripe 
          border 
          style="width: 100%"
          class="patient-table"
          :row-class-name="tableRowClassName"
        >
            <el-table-column prop="pId" label="账号" width="120" align="center"/>
            <el-table-column prop="pName" label="姓名" width="120" align="center"/>
            <el-table-column prop="pGender" label="性别" width="80" align="center"/>
            <el-table-column prop="pAge" label="年龄/岁" width="100" align="center"/>
            <el-table-column prop="pCard" label="证件号" width="150" align="center"/>
            <el-table-column prop="pPhone" label="手机号" width="150" align="center"/>
            <el-table-column prop="pEmail" label="邮箱" width="150" align="center"/>
            <el-table-column prop="pState" label="状态" width="80" align="center">
                <template slot-scope="scope">
                    <el-tag type="success" v-if="scope.row.pState === 1">
                      正常
                    </el-tag>
                    <el-tag type="danger" v-else>已删除</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right" align="center">
                <template slot-scope="scope">
                    <el-button
                        type="danger"
                        size="mini"
                        @click="deleteDialog(scope.row.pId)"
                        class="action-btn delete-btn"
                    >
                      <i class="iconfont icon-delete-button"></i>
                    </el-button>
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

</template>
<script>
import request from "@/utils/request.js";
export default {
    name: "PatientList",
    data() {
        return {
            pageNumber: 1,
            size: 8,
            query: "",
            patientData: [],
            total: 3,
        };
    },
    methods: {
        //删除病人操作
        deletePatient(id) {
            request
                .get("admin/deletePatient", {
                    params: {
                        pId: id,
                    },
                })
                .then((res) => {
                    this.requestPatients();
                });
        },
        //删除对话框
        deleteDialog(id) {
            this.$confirm("此操作将删除该患者信息, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            })
                .then(() => {
                    this.deletePatient(id);
                    this.$message({
                        type: "success",
                        message: "删除成功!",
                    });
                })
                .catch(() => {
                    this.$message({
                        type: "info",
                        message: "已取消删除",
                    });
                });
        },
        //页面大小改变时触发
        handleSizeChange(size) {
            this.size = size;
            this.requestPatients();
        },
        //点击分页触发
        handleCurrentChange(num) {
            this.pageNumber = num;
            this.requestPatients();
        },
        //请求病患列表
        requestPatients() {
            request
                .get("admin/findAllPatients", {
                    params: {
                        pageNumber: this.pageNumber,
                        size: this.size,
                        query: this.query,
                    },
                })
                .then((res) => {
                    this.patientData = res.data.data.patients;
                    this.total = res.data.data.total;
                });
        },
        // 表格行类名
        tableRowClassName({ row, rowIndex }) {
            if (rowIndex % 2 === 0) {
                return 'even-row';
            } else {
                return 'odd-row';
            }
        },
    },
    created() {
      this.requestPatients();
    },
};
</script>
<style scoped lang="scss">
// 表格样式美化
.patient-table {
    margin-top: 20px;
    margin-bottom: 20px;
    border-radius: 8px;
    overflow: hidden;
    animation: fadeIn 0.6s ease;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    
    ::v-deep .el-table__header {
        background: linear-gradient(135deg, #f5f7fa 0%, #ecf5ff 100%);
        
        th {
            background: transparent !important;
            color: #303133;
            font-weight: 600;
            font-size: 14px;
            padding: 12px 0;
            border-bottom: 2px solid #e4e7ed;
            text-align: center;
            
            .cell {
                text-align: center;
            }
        }
    }
    
    ::v-deep .el-table__body {
        tr {
            &.even-row {
                background-color: #ffffff;
            }
            
            &.odd-row {
                background-color: #fafafa;
            }
            
            td {
                padding: 12px 0;
                font-size: 13px;
                color: #606266;
                border-bottom: 1px solid #f0f2f5;
                text-align: center;
                
                .cell {
                    text-align: center;
                }
            }
        }
    }
    
    ::v-deep .el-table__border {
        border: 1px solid #e4e7ed;
    }
}

// 操作按钮样式
.action-btn {
    font-size: 14px;
    padding: 6px 12px;
    border-radius: 6px;
    transition: all 0.3s ease;
    margin-right: 8px;
    
    i {
        font-size: 12px;
    }
    
    &:last-child {
        margin-right: 0;
    }
    
    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
    }
    
    &:active {
        transform: translateY(0);
    }
}

.delete-btn {
    background: #606266;
    border-color: #606266;
    color: #ffffff;
    
    &:hover {
        background: #909399;
        border-color: #909399;
        box-shadow: 0 4px 12px rgba(96, 98, 102, 0.3);
    }
}

// 动画效果
@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.el-form {
    margin-top: 0;
}

// 卡片样式
::v-deep .el-card {
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    
    &:hover {
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
    }
}

// 分页样式
::v-deep .el-pagination {
    margin-top: 20px;
    text-align: center;
    
    .el-pager li {
        border-radius: 4px;
        transition: all 0.3s ease;
        
        &:hover {
            color: #409EFF;
            transform: scale(1.1);
        }
        
        &.active {
            background: #409EFF;
            color: #fff;
        }
    }
    
    .btn-prev,
    .btn-next {
        border-radius: 4px;
        transition: all 0.3s ease;
        
        &:hover {
            color: #409EFF;
            transform: scale(1.1);
        }
    }
}
</style>
