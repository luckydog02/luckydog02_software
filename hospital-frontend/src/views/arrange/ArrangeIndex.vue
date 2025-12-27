<!--
 * 医生排班页面
 *
-->
<template>
    <div>
        <!--排班日期选择-->
        <el-card class="date-select-card">
            <div class="date-select-wrapper">
                <div class="date-select-header">
                    <i class="el-icon-calendar"></i>
                    <span class="header-title">选择排班日期</span>
                </div>
                <div class="date-buttons">
                    <el-button
                        v-for="monthDay in monthDays"
                        :key="monthDay"
                        :type="arrangeTime && arrangeTime.includes(monthDay) ? 'primary' : ''"
                        :class="['date-btn', { 'active': arrangeTime && arrangeTime.includes(monthDay) }]"
                        @click="dateClick(monthDay)"
                    >
                        {{ monthDay }}
                    </el-button>
                </div>
            </div>
        </el-card>

       <!--科室选择-->
        <el-card class="section-select-card">
            <div class="section-select-wrapper">
                <div class="section-select-header">
                    <i class="el-icon-office-building"></i>
                    <span class="header-title">选择科室</span>
                </div>
                <div class="section-select-content">
                    <el-select
                        v-model="selectedMainSection"
                        placeholder="请选择主科室"
                        class="main-section-select"
                        @change="onMainSectionChange"
                    >
                        <el-option
                            v-for="mainSection in mainSections"
                            :key="mainSection.value"
                            :label="mainSection.label"
                            :value="mainSection.value"
                        >
                        </el-option>
                    </el-select>
                    <el-select
                        v-model="section"
                        placeholder="请选择子科室"
                        class="sub-section-select"
                        filterable
                        @change="sectionClick(section)"
                    >
                        <el-option
                            v-for="subSection in currentSubSections"
                            :key="subSection"
                            :label="subSection"
                            :value="subSection"
                        >
                        </el-option>
                    </el-select>
                </div>
            </div>
        </el-card>

        <!--科室医生列表-->
        <el-card class="doctor-list-card">
          <div class="doctor-list-wrapper">
            <!--排班信息-->
            <div class="arrange-info">
              <div class="info-item">
                <i class="el-icon-date"></i>
                <span class="info-label">排班日期：</span>
                <span class="info-value">{{ arrangeTime || '未选择' }}</span>
              </div>
              <div class="info-item">
                <i class="el-icon-office-building"></i>
                <span class="info-label">科室：</span>
                <span class="info-value">{{ section || '未选择' }}</span>
              </div>
            </div>

            <!--搜索栏-->
            <div class="search-wrapper">
              <el-input 
                v-model="query"
                placeholder="请输入医生姓名查询"
                class="doctor-input"
              >
                <el-button
                    slot="append"
                    round
                    icon="iconfont icon-search-button"
                    @click="requestDoctors"
                ></el-button>
              </el-input>
            </div>

            <!--表格-->
            <el-table 
              size="small" 
              :data="doctorData" 
              border
              class="arrange-table"
              :row-class-name="tableRowClassName"
            >
              <el-table-column label="账号" prop="dId" align="center"/>
              <el-table-column label="姓名" prop="dName" align="center"/>
              <el-table-column label="性别" prop="dGender" align="center"/>
              <el-table-column label="职位" prop="dPost" align="center"/>
              <el-table-column label="部门" prop="dSection" align="center"/>
              <el-table-column label="操作" align="center">
                <template slot-scope="scope">
                  <el-button
                      v-if="scope.row.arrangeId == null"
                      size="mini"
                      type="primary"
                      class="action-btn arrange-btn"
                      @click="arrangeClick(scope.row.dId)"
                  >
                    <i class="iconfont icon-edit-button"></i>
                    排班
                  </el-button>
                  <el-button
                      v-if="scope.row.arrangeId != null"
                      size="mini"
                      type="danger"
                      class="action-btn cancel-btn"
                      @click="deleteArrange(scope.row.arrangeId)"
                  >
                    <i class="iconfont icon-delete-button"></i>
                    取消
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
                class="pagination"
            >
            </el-pagination>
          </div>
        </el-card>

    </div>
</template>
<script>
import request from "@/utils/request";
const ARRANGEDATE = "arrangeDate";
export default {
    name: "ArrangeIndex",
    data() {
        return {
            //日期数据
            monthDays: [],
            monthDay: "",
            activePath: "",
            arrangeTime:"",
            //科室数据
            inters: [
              "神经内科",
              "呼吸与危重症医学科",
              "内分泌科",
              "消化内科",
              "心血管内科",
              "肾内科",
              "发热门诊",
            ],
            outs: [
              "手足外科",
              "普通外科",
              "肛肠外科",
              "神经外科",
              "泌尿外科",
              "骨科",
              "烧伤整形外科",
            ],
            women: ["妇科", "产科"],
            kids: ["儿科", "儿童保健科"],
            fives: ["耳鼻咽喉科", "眼科", "口腔科"],
            chinese: ["中医科"],
            others: ["康复医学科", "急诊科", "皮肤性病科", "功能科"],
            //主科室和子科室映射
            mainSections: [
                { label: "内科", value: "internal" },
                { label: "外科", value: "surgery" },
                { label: "妇产科", value: "women" },
                { label: "儿科", value: "kids" },
                { label: "五官科", value: "fives" },
                { label: "中医科", value: "chinese" },
                { label: "其他", value: "others" },
            ],
            selectedMainSection: "",
            //科室医生表格数据
            section: "",
            doctorData: [],
            total: 3,
            pageNumber: 1,
            size: 8,
            query: "",
        };
    },
    computed: {
        currentSubSections() {
            switch(this.selectedMainSection) {
                case "internal":
                    return this.inters;
                case "surgery":
                    return this.outs;
                case "women":
                    return this.women;
                case "kids":
                    return this.kids;
                case "fives":
                    return this.fives;
                case "chinese":
                    return this.chinese;
                case "others":
                    return this.others;
                default:
                    return [];
            }
        },
    },

    methods: {
        //日历点击
        dateClick(monthDay) {
            const nowDate = new Date();
            let year = nowDate.getFullYear();
            let dateTime = year + "-" + monthDay;
            this.arrangeTime = dateTime;
            sessionStorage.setItem(ARRANGEDATE, dateTime);
            this.requestDoctors();
        },
        //主科室变化
        onMainSectionChange() {
            this.section = "";
            this.doctorData = [];
        },
        //获取当天及后7天的日期星期
        nowDay(num) {
            let nowDate = new Date();
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
        //科室点击
        sectionClick(section){
          this.section = section;
          this.requestDoctors();
        },
        //排班点击
        arrangeClick(dId) {
          request
              .get("arrange/addArrange", {
                params: {
                  arId: dId + sessionStorage.getItem("arrangeDate"),
                  arTime: sessionStorage.getItem("arrangeDate"),
                  dId: dId,
                },
              })
              .then((res) => {
                if (res.data.status !== 200)
                  return this.$message.error("已排班");
                this.$message.success("排班成功！");
                this.requestDoctors();
              });
        },
        deleteArrange(arrangeId) {
          request
              .get("arrange/deleteArrange", {
                params: {
                  arId: arrangeId,
                },
              })
              .then((res) => {
                if (res.data.status !== 200)
                  return this.$message.error("排班信息不存在");
                this.$message.success("删除排班信息成功！");
                this.requestDoctors();
              });
        },
        //页面大小改变时触发
        handleSizeChange(size) {
          this.size = size;
          this.requestDoctors();
        },
        //页码改变时触发
        handleCurrentChange(num) {
          this.pageNumber = num;
          this.requestDoctors();
        },
        //根据部门请求医生信息
        requestDoctors() {
          if (!this.section || !this.arrangeTime) {
            if (!this.section) {
              this.$message.warning("请先选择科室");
            }
            if (!this.arrangeTime) {
              this.$message.warning("请先选择排班日期");
            }
            return;
          }
          request
              .get("doctor/findDoctorBySectionPage", {
                params: {
                  pageNumber: this.pageNumber,
                  size: this.size,
                  query: this.query,
                  dSection: this.section,
                  arrangeDate: sessionStorage.getItem("arrangeDate"),
                },
              })
              .then((res) => {
                if (res.data.status !== 200) {
                  this.$message.error("数据请求失败");
                  return;
                }
                this.doctorData = res.data.data?.doctors || [];
                this.total = res.data.data?.total || 0;
              })
              .catch((err) => {
                console.error("获取医生列表失败:", err);
                this.$message.error("获取医生列表失败，请重试");
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
        //获取当天的后7天
        for (let i = 0; i < 25; i++) {
            this.nowDay(i);
        }
    },
};
</script>
<style scoped lang="scss">
// 日期选择卡片
.date-select-card {
    margin-bottom: 20px;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    animation: fadeIn 0.6s ease;
    
    .date-select-wrapper {
        padding: 10px;
        
        .date-select-header {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #f0f2f5;
            
            i {
                font-size: 24px;
                color: #409EFF;
                margin-right: 10px;
            }
            
            .header-title {
                font-size: 18px;
                font-weight: 600;
                color: #303133;
            }
        }
        
        .date-buttons {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            
            .date-btn {
                min-width: 80px;
                padding: 10px 15px;
                border-radius: 8px;
                transition: all 0.3s ease;
                font-weight: 500;
                
                &:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 4px 8px rgba(64, 158, 255, 0.3);
                }
                
                &.active {
                    background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
                    border-color: #409EFF;
                    color: #fff;
                    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
                }
            }
        }
    }
}

// 科室选择卡片
.section-select-card {
    margin-bottom: 20px;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    animation: fadeIn 0.6s ease;
    
    .section-select-wrapper {
        padding: 10px;
        
        .section-select-header {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #f0f2f5;
            
            i {
                font-size: 24px;
                color: #67C23A;
                margin-right: 10px;
            }
            
            .header-title {
                font-size: 18px;
                font-weight: 600;
                color: #303133;
            }
        }
        
        .section-select-content {
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
            
            .main-section-select,
            .sub-section-select {
                flex: 1;
                min-width: 200px;
            }
        }
    }
}

// 医生列表卡片
.doctor-list-card {
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    animation: fadeIn 0.6s ease;
    
    .doctor-list-wrapper {
        padding: 10px;
        
        .arrange-info {
            display: flex;
            gap: 30px;
            margin-bottom: 20px;
            padding: 15px;
            background: linear-gradient(135deg, #f5f7fa 0%, #ecf5ff 100%);
            border-radius: 8px;
            
            .info-item {
                display: flex;
                align-items: center;
                gap: 8px;
                
                i {
                    font-size: 18px;
                    color: #409EFF;
                }
                
                .info-label {
                    font-weight: 600;
                    color: #606266;
                }
                
                .info-value {
                    font-weight: 600;
                    color: #409EFF;
                    font-size: 16px;
                }
            }
        }
        
        .search-wrapper {
            margin-bottom: 20px;
            
            .doctor-input {
                max-width: 400px;
            }
        }
    }
}

// 表格样式美化
.arrange-table {
    margin-top: 20px;
    margin-bottom: 20px;
    border-radius: 8px;
    overflow: hidden;
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
        margin-right: 4px;
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

.arrange-btn {
    background: #409EFF;
    border-color: #409EFF;
    
    &:hover {
        background: #66b1ff;
        border-color: #66b1ff;
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    }
}

.cancel-btn {
    background: #606266;
    border-color: #606266;
    color: #ffffff;
    
    &:hover {
        background: #909399;
        border-color: #909399;
        box-shadow: 0 4px 12px rgba(96, 98, 102, 0.3);
    }
}

// 分页样式
.pagination {
    margin-top: 20px;
    text-align: center;
    
    ::v-deep .el-pager li {
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
    
    ::v-deep .btn-prev,
    ::v-deep .btn-next {
        border-radius: 4px;
        transition: all 0.3s ease;
        
        &:hover {
            color: #409EFF;
            transform: scale(1.1);
        }
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

// 卡片悬停效果
::v-deep .el-card {
    transition: all 0.3s ease;
    
    &:hover {
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
    }
}
</style>
