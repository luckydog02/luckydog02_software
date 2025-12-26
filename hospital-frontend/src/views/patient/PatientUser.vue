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
          <el-button type="success" class="book-now-btn" @click="menuClick('bookNow')">立即预约</el-button>
          <el-button type="danger" round size="mini" @click="logoutPatient" style="margin-left: 10px;">退出</el-button>
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
        .book-now-btn {
            background-color: #2d8659;
            border-color: #2d8659;
            color: #fff;
            font-size: 16px;
            padding: 12px 30px;
            font-weight: 600;
            
            &:hover {
                background-color: #24704a;
                border-color: #24704a;
            }
        }
    }
}

.content-area {
    width: 100%;
    min-height: calc(100vh - 70px);
}
</style>
