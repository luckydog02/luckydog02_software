<!--
 * 患者管理主布局
 *
-->
<template>
    <div class="patient-container">
      <!-- 固定顶部导航栏 -->
      <div class="fixed-navbar">
        <div class="navbar-left">
          <img src="../../assets/hospital.png" class="hospital-logo" alt="医院图标"/>
        </div>
        <div class="navbar-center">
          <div 
            v-for="tab in tabs" 
            :key="tab.path"
            :class="['nav-tab', { 'active': activePath === tab.path }]"
            @click="menuClick(tab.path)"
          >
            {{ tab.label }}
          </div>
        </div>
        <div class="navbar-right">
          <div class="book-now-link" @click="menuClick('bookNow')">
            <i class="el-icon-calendar"></i>
            <span>立即预约</span>
          </div>
          <el-button class="logout-btn" @click="logoutPatient">
            <i class="el-icon-switch-button"></i>
            退出
          </el-button>
        </div>
      </div>
      
      <!-- 内容区域 -->
      <div class="content-area">
        <router-view></router-view>
      </div>
    </div>
</template>
<script>
import jwtDecode from "jwt-decode";
import {
    getToken,
    clearToken,
    getActivePath,
    setActivePath,
} from "@/utils/storage.js";
export default {
    name: "Patient",
    data() {
        return {
            userName: "",
            activePath: "hospitalIntro",
            tabs: [
                { label: "医院介绍", path: "hospitalIntro" },
                { label: "科室导航", path: "departmentNav" },
                { label: "医生团队", path: "doctorTeam" },
                { label: "智能助手", path: "aiChat" },
                { label: "诊疗评价", path: "treatmentReviews" },
                { label: "我的预约", path: "myAppointments" },
            ],
        };
    },
    methods: {
        //token解码
        tokenDecode(token) {
            if (token !== null) return jwtDecode(token);
        },
        //导航栏点击事件
        menuClick(path) {
            this.activePath = path;
            setActivePath(path);
            if (this.$route.path !== "/" + path) this.$router.push("/" + path);
        },
        //退出登录
        logoutPatient() {
            this.$confirm("此操作将退出登录, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            })
            .then(() => {
                clearToken();
                this.$message({
                    type: "success",
                    message: "退出登录成功!",
                });
                this.$router.push("login");
            })
            .catch(() => {
                this.$message({
                    type: "info",
                    message: "已取消",
                });
            });
        },
    },
    created() {
        //解码token
        this.userName = this.tokenDecode(getToken()).pName;
        // 根据当前路由设置激活路径
        const currentPath = this.$route.path;
        if (currentPath.includes("hospitalIntro")) {
            this.activePath = "hospitalIntro";
        } else if (currentPath.includes("departmentNav")) {
            this.activePath = "departmentNav";
        } else if (currentPath.includes("doctorTeam")) {
            this.activePath = "doctorTeam";
        } else if (currentPath.includes("treatmentReviews")) {
            this.activePath = "treatmentReviews";
        } else if (currentPath.includes("myAppointments")) {
            this.activePath = "myAppointments";
        } else if (currentPath.includes("bookNow")) {
            this.activePath = "bookNow";
        } else {
            // 默认跳转到医院介绍页面
            this.activePath = "hospitalIntro";
            if (currentPath === "/patient" || currentPath.includes("patientLayout")) {
                this.$router.push("/hospitalIntro");
            }
        }
    },
    watch: {
        $route(to) {
            // 路由变化时更新激活路径
            if (to.path.includes("hospitalIntro")) {
                this.activePath = "hospitalIntro";
            } else if (to.path.includes("departmentNav")) {
                this.activePath = "departmentNav";
            } else if (to.path.includes("doctorTeam")) {
                this.activePath = "doctorTeam";
            } else if (to.path.includes("aiChat")) {
                this.activePath = "aiChat";
            } else if (to.path.includes("treatmentReviews")) {
                this.activePath = "treatmentReviews";
            } else if (to.path.includes("myAppointments")) {
                this.activePath = "myAppointments";
            } else if (to.path.includes("bookNow")) {
                this.activePath = "bookNow";
            }
        }
    },
};
</script>
<style scoped lang="scss">
.patient-container {
    width: 100%;
    min-height: 100vh;
    padding-top: 70px; // 为固定导航栏留出空间
}

.fixed-navbar {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 70px;
    background-color: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 30px;
    z-index: 1000;
    
    .navbar-left {
        display: flex;
        align-items: center;
        
        .hospital-logo {
            width: 250px;
            height: 30px;
        }
    }
    
    .navbar-center {
        display: flex;
        align-items: center;
        gap: 40px;
        
        .nav-tab {
            cursor: pointer;
            font-size: 16px;
            color: #333;
            padding: 10px 15px;
            transition: all 0.3s;
            border-bottom: 2px solid transparent;
            
            &:hover {
                color: #409EFF;
            }
            
            &.active {
                color: #409EFF;
                border-bottom-color: #409EFF;
                font-weight: 600;
            }
        }
    }
    
    .navbar-right {
        display: flex;
        align-items: center;
        gap: 20px;
        
        .book-now-link {
            display: flex;
            align-items: center;
            gap: 6px;
            cursor: pointer;
            font-size: 17px;
            color: #ffffff;
            padding: 10px 20px;
            font-weight: 600;
            border-radius: 25px;
            transition: all 0.3s ease;
            background: #1a4d7a;
            border: none;
            box-shadow: 0 4px 12px rgba(26, 77, 122, 0.4);
            position: relative;
            overflow: hidden;
            
            &::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
                transition: left 0.5s;
            }
            
            i {
                font-size: 20px;
                color: #ffffff;
            }
            
            span {
                position: relative;
                z-index: 1;
            }
            
            &:hover {
                background: #2d5f8a;
                color: #ffffff;
                transform: translateY(-2px);
                box-shadow: 0 6px 16px rgba(26, 77, 122, 0.5);
                
                &::before {
                    left: 100%;
                }
                
                i {
                    color: #ffffff;
                }
            }
            
            &:active {
                transform: translateY(0);
                box-shadow: 0 2px 8px rgba(255, 107, 53, 0.3);
            }
        }
        
        .logout-btn {
            background-color: #fff;
            border: 1px solid #dcdfe6;
            color: #606266;
            font-size: 14px;
            padding: 8px 18px;
            font-weight: 500;
            border-radius: 6px;
            transition: all 0.3s ease;
            
            i {
                margin-right: 4px;
                font-size: 14px;
            }
            
            &:hover {
                background-color: #f5f7fa;
                border-color: #c0c4cc;
                color: #409EFF;
            }
            
            &:active {
                background-color: #ecf5ff;
                border-color: #409EFF;
            }
        }
    }
}

.content-area {
    width: 100%;
    min-height: calc(100vh - 70px);
}
</style>
