<!--
 * 医生用户主题布局
 *
-->
<template>
  <el-container class="doctor-container">
    <!-- 头部 -->
    <el-header class="doctor-header">
      <div class="header-left">
        <div class="logo-wrapper" @click="menuClick('doctorLayout')">
          <img src="../../assets/hospital.png" class="hospital-logo" alt="医院图标"/>
        </div>
      </div>

      <div class="header-right">
        <div class="user-info">
          <i class="el-icon-user-solid user-icon"></i>
          <span class="welcome-text">欢迎您，<b>{{ userName }}</b>&nbsp;医生</span>
        </div>
        <el-button class="logout-btn" @click="logoutDoctor">
          <i class="el-icon-switch-button"></i>
          退出
        </el-button>
      </div>
    </el-header>

    <el-container style="flex: 1; overflow: hidden;">
      <!-- 侧边栏 -->
      <el-aside width="240px" class="doctor-aside">
        <!-- 导航菜单 -->
        <el-menu
          :default-active="activePath"
          class="doctor-menu"
          background-color="#fff"
          text-color="#606266"
          active-text-color="#409EFF"
        >
          <el-menu-item index="doctorLayout" @click="menuClick('doctorLayout')">
            <i class="el-icon-s-home"></i>
            <span slot="title">首页</span>
          </el-menu-item>
          <el-menu-item index="orderToday" @click="menuClick('orderToday')">
            <i class="el-icon-date"></i>
            <span slot="title">今日挂号列表</span>
          </el-menu-item>
          <el-menu-item index="doctorOrder" @click="menuClick('doctorOrder')">
            <i class="el-icon-tickets"></i>
            <span slot="title">历史挂号列表</span>
          </el-menu-item>
          <el-menu-item index="inBed" @click="menuClick('inBed')">
            <i class="el-icon-suitcase"></i>
            <span slot="title">住院申请管理</span>
          </el-menu-item>
          <el-menu-item index="doctorCard" @click="menuClick('doctorCard')">
            <i class="el-icon-user-solid"></i>
            <span slot="title">个人信息查询</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="doctor-main">
        <div class="main-content-wrapper">
          <transition name="fade-transform" mode="out-in">
            <router-view></router-view>
          </transition>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>
<script>
import jwtDecode from "jwt-decode";
import { getToken, clearToken, getActivePath, setActivePath} from "@/utils/storage.js";
export default {
  name: "Doctor",
  data() {
    return {
      userName: "",
      activePath:"",
    };
  },
  methods: {
    //token解码
    tokenDecode(token){
      if (token !== null)
      return jwtDecode(token);
    },
    //导航栏点击事件
    menuClick(path){
      this.activePath=path;
            setActivePath(path);
            if(this.$route.path !== "/"+path) this.$router.push(path);
    },
    //退出登录
    logoutDoctor() {
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
     // 获取激活路径
     this.activePath=getActivePath();
     // 解码token
     this.userName = this.tokenDecode(getToken()).dName;
    }
};
</script>
<style scoped lang="scss">
.doctor-container {
    height: 100vh;
    display: flex;
    flex-direction: column;
}

// 头部样式
.doctor-header {
    background: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 30px;
    height: 70px !important;
    border-bottom: 1px solid #e4e7ed;
    position: relative;
    z-index: 1000;
    
    .header-left {
        display: flex;
        align-items: center;
        
        .logo-wrapper {
            cursor: pointer;
            transition: transform 0.3s ease;
            
            &:hover {
                transform: scale(1.05);
            }
            
            .hospital-logo {
                width: 250px;
                height: 30px;
            }
        }
    }
    
    .header-right {
        display: flex;
        align-items: center;
        gap: 20px;
        
        .user-info {
            display: flex;
            align-items: center;
            gap: 8px;
            
            .user-icon {
                font-size: 18px;
                color: #409EFF;
            }
            
            .welcome-text {
                font-size: 15px;
                color: #606266;
                
                b {
                    color: #303133;
                    font-weight: 600;
                }
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

// 侧边栏样式
.doctor-aside {
    background: #fff;
    border-right: 1px solid #e4e7ed;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
    overflow-y: auto;
    
    &::-webkit-scrollbar {
        width: 6px;
    }
    
    &::-webkit-scrollbar-track {
        background: #f5f7fa;
    }
    
    &::-webkit-scrollbar-thumb {
        background: rgba(64, 158, 255, 0.3);
        border-radius: 3px;
        
        &:hover {
            background: rgba(64, 158, 255, 0.5);
        }
    }
}

// 菜单样式
.doctor-menu {
    border: none;
    padding: 10px 0;
    
    ::v-deep .el-menu-item {
        height: 50px;
        line-height: 50px;
        margin: 4px 10px;
        border-radius: 8px;
        transition: all 0.3s ease;
        
        i {
            margin-right: 8px;
            font-size: 18px;
            color: #606266;
        }
        
        &:hover {
            background-color: #ecf5ff !important;
            color: #409EFF !important;
            
            i {
                color: #409EFF;
            }
        }
        
        &.is-active {
            background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%) !important;
            color: #fff !important;
            font-weight: 600;
            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
            
            i {
                color: #fff;
            }
        }
    }
}

// 主内容区
.doctor-main {
    background: #f5f7fa;
    padding: 0 !important;
    overflow-y: auto !important;
    overflow-x: hidden !important;
    position: relative;
    
    &::-webkit-scrollbar {
        width: 8px;
    }
    
    &::-webkit-scrollbar-track {
        background: rgba(0, 0, 0, 0.02);
        border-radius: 4px;
    }
    
    &::-webkit-scrollbar-thumb {
        background: rgba(64, 158, 255, 0.3);
        border-radius: 4px;
        
        &:hover {
            background: rgba(64, 158, 255, 0.5);
        }
    }
    
    .main-content-wrapper {
        padding: 20px;
        box-sizing: border-box;
    }
}

// Element UI 容器样式覆盖
.doctor-container {
    ::v-deep .el-container {
        height: calc(100vh - 70px);
        min-height: 0;
        display: flex;
        flex-direction: row;
    }
    
    ::v-deep .el-header {
        flex-shrink: 0;
        height: 70px !important;
    }
    
    ::v-deep .el-aside {
        flex-shrink: 0;
        height: 100%;
        overflow-y: auto;
    }
    
    ::v-deep .el-main {
        padding: 0 !important;
        overflow-y: auto !important;
        overflow-x: hidden !important;
        height: 100% !important;
        min-height: 0;
        flex: 1;
    }
}

// 页面切换动画
.fade-transform-enter-active,
.fade-transform-leave-active {
    transition: all 0.3s ease;
}

.fade-transform-enter {
    opacity: 0;
    transform: translateX(20px);
}

.fade-transform-leave-to {
    opacity: 0;
    transform: translateX(-20px);
}
</style>
