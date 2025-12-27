<!--
 * 病床管理列表
 *
-->
<template>
    <div>
        <el-card>
            <!-- 搜索栏 -->
            <el-row type="flex">
                <el-col :span="8">
                    <el-input v-model="query" placeholder="请输入患者id查询">
                        <el-button
                            round
                            slot="append"
                            icon="iconfont icon-search-button"
                            @click="requestBeds"
                        ></el-button>
                    </el-input>
                </el-col>
                <el-col :span="1"></el-col>
            </el-row>

          <!--添加病床按钮-->
          <el-row type="flex">
                <el-col :span="6">
                    <br/>
                    <el-button
                        size="small"
                        type="primary"
                        @click="addFormVisible = true"
                        class="add-bed-btn"
                    >
                      <i class="iconfont icon-add-button"></i>
                      增加床位
                    </el-button>
                </el-col>
            </el-row>

            <!-- 表格 -->
            <el-table 
              :data="bedData" 
              size="small" 
              stripe 
              style="width: 100%" 
              border
              class="bed-table"
              :row-class-name="tableRowClassName"
            >
                <el-table-column label="床号" prop="bId" align="center"/>
                <el-table-column label="患者id" prop="pId" align="center"/>
                <el-table-column label="医生id" prop="dId" align="center"/>
                <el-table-column label="开始时间" prop="bStart" align="center"/>
                <el-table-column label="申请理由" prop="bReason" align="center"/>
                <el-table-column label="状态" prop="bState" align="center">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.bState === 1" type="danger">
                          已占用
                        </el-tag>
                        <el-tag v-if="scope.row.bState === 0" type="primary">
                          空
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200" fixed="right" align="center">
                    <template slot-scope="scope">
                        <el-button
                            size="mini"
                            type="danger"
                            @click="emptyDialog(scope.row.bId)"
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

        <!-- 增加床位对话框 -->
        <el-dialog title="增加床位" :visible.sync="addFormVisible">
            <el-form :model="addForm" :rules="rules" ref="ruleForm">
                <el-form-item label="床号" prop="bId" label-width="80px">
                    <el-input v-model.number="addForm.bId"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="addFormVisible = false" style="font-size: 18px;"><i class="iconfont icon-r-left" style="font-size: 20px;"></i> 取 消</el-button>
                <el-button type="primary" @click="addBed('ruleForm')"
                    style="font-size: 18px;"><i class="iconfont icon-r-yes" style="font-size: 20px;"></i> 确 定</el-button
                >
            </div>
        </el-dialog>
    </div>
</template>
<script>
import request from "@/utils/request.js";
export default {
    name: "CheckList",
    data() {
        return {
            pageNumber: 1,
            size: 8,
            query: "",
            bedData: [],
            total: 3,
            addFormVisible: false,
            addForm: {},
            rules: {
                bId: [
                    { required: true, message: "请输入床号", trigger: "blur" },
                    {
                        type: "number",
                        message: "床号必须数字类型",
                        trigger: "blur",
                    },
                ],
            },
        };
    },
    methods: {
        //清空床位操作
        emptyBed(id) {
            request
                .get("bed/emptyBed", {
                    params: {
                        bId: id,
                    },
                })
                .then((res) => {
                    this.requestBeds();
                });
        },
        //清空对话框
        emptyDialog(id) {
            this.$confirm("此操作将清空该床位, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            })
                .then(() => {
                    this.emptyBed(id);
                    this.$message({
                        type: "success",
                        message: "清空成功!",
                    });
                })
                .catch(() => {
                    this.$message({
                        type: "info",
                        message: "已取消清空",
                    });
                });
        },

        //删除床位操作
        deleteBed(id) {
            request
                .get("bed/deleteBed", {
                    params: {
                        bId: id,
                    },
                })
                .then((res) => {
                    this.requestBeds();
                });
        },
        //删除对话框
        deleteDialog(id) {
            this.$confirm("此操作将删除该床位, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            })
                .then(() => {
                    this.deleteBed(id);
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
        //点击增加确认按钮
        addBed(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    request
                        .get("bed/addBed", {
                            params: {
                                bId: this.addForm.bId,
                                pId: -1,
                                dId: -1,
                            },
                        })
                        .then((res) => {
                            if (res.data.status !== 200)
                                return this.$message.error("床号或已被占用！");
                            this.addFormVisible = false;
                            this.requestBeds();
                            this.$message.success("增加床位成功！");
                        });
                } else {
                    return false;
                }
            });
        },
        //页面大小改变时触发
        handleSizeChange(size) {
            this.size = size;
            this.requestBeds();
        },
        //   页码改变时触发
        handleCurrentChange(num) {
            this.pageNumber = num;
            this.requestBeds();
        },
        // 加载检查列表
        requestBeds() {
            request
                .get("bed/findAllBeds", {
                    params: {
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
                    this.bedData = res.data.data?.beds || [];
                    this.total = res.data.data?.total || 0;
                })
                .catch((err) => {
                    console.error("获取床位列表失败:", err);
                    this.$message.error("获取床位列表失败，请重试");
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
        this.requestBeds();
    },
};
</script>
<style scoped lang="scss">
// 增加床位按钮样式
.add-bed-btn {
    font-size: 15px;
    font-weight: 600;
    padding: 10px 20px;
    border-radius: 8px;
    background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
    border: none;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    transition: all 0.3s ease;
    animation: fadeInUp 0.5s ease;
    
    i {
        font-size: 15px;
        margin-right: 4px;
    }
    
    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
        background: linear-gradient(135deg, #66b1ff 0%, #409EFF 100%);
    }
    
    &:active {
        transform: translateY(0);
    }
}

// 表格样式美化
.bed-table {
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

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(20px);
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
