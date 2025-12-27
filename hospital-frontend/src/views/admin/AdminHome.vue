<!--
 * 管理员首页
 *
-->
<template>
    <div class="admin-home-container">
        <div class="welcome-section">
            <div class="welcome-icon">
                <i class="el-icon-s-home"></i>
            </div>
            <h1 class="welcome-title">欢迎登录医院管理系统</h1>
            <p class="welcome-subtitle">管理员工作台</p>
        </div>
        
        <div class="image-section">
            <div class="image-wrapper">
                <img src="@/assets/homepage-back.jpg" alt="医院图片" class="homepage-image">
                <div class="image-overlay"></div>
            </div>
        </div>
    </div>
</template>
<script>

export default {
    name: "AdminHome",
    data() {
        return {
        };
    },
    methods: {
    },
    created() {
    },
};
</script>

<style scoped lang="scss">
.admin-home-container {
    padding: 40px;
    max-width: 1200px;
    margin: 0 auto;
    animation: fadeIn 0.6s ease;
}

.welcome-section {
    text-align: center;
    margin-bottom: 40px;
    padding: 30px 0;
    
    .welcome-icon {
        width: 80px;
        height: 80px;
        background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
        border-radius: 20px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 20px;
        box-shadow: 0 8px 24px rgba(64, 158, 255, 0.3);
        animation: float 3s ease-in-out infinite;
        
        i {
            font-size: 40px;
            color: #fff;
        }
    }
    
    .welcome-title {
        font-size: 36px;
        font-weight: 700;
        color: #409EFF;
        margin: 0 0 10px 0;
        letter-spacing: 1px;
        text-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
    }
    
    .welcome-subtitle {
        font-size: 18px;
        color: #909399;
        margin: 0;
        font-weight: 300;
    }
}

.image-section {
    display: flex;
    justify-content: center;
    margin-top: 40px;
    
    .image-wrapper {
        position: relative;
        border-radius: 16px;
        overflow: hidden;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
        transition: all 0.4s ease;
        
        &:hover {
            transform: translateY(-8px);
            box-shadow: 0 12px 40px rgba(0, 0, 0, 0.18);
        }
        
        .homepage-image {
            width: 100%;
            max-width: 1000px;
            height: auto;
            display: block;
            transition: transform 0.6s ease;
        }
        
        .image-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(102, 177, 255, 0.05) 100%);
            opacity: 0;
            transition: opacity 0.3s ease;
        }
        
        &:hover .homepage-image {
            transform: scale(1.05);
        }
        
        &:hover .image-overlay {
            opacity: 1;
        }
    }
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

@keyframes float {
    0%, 100% {
        transform: translateY(0);
    }
    50% {
        transform: translateY(-10px);
    }
}

@media (max-width: 768px) {
    .admin-home-container {
        padding: 20px;
    }
    
    .welcome-section {
        .welcome-icon {
            width: 60px;
            height: 60px;
            
            i {
                font-size: 30px;
            }
        }
        
        .welcome-title {
            font-size: 28px;
        }
    }
}
</style>
