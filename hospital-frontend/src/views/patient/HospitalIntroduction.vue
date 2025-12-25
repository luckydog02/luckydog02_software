<!--
 * 医院介绍页面
 *
-->
<template>
    <div class="hospital-intro-container">
        <!-- 主界面卡片 -->
        <div class="main-card">
            <div class="card-left">
                <h3>医院简介</h3>
                <p>{{ hospitalIntro }}</p>
            </div>
            <div class="card-right">
                <div class="user-info-card">
                    <h4>个人信息</h4>
                    <div class="info-item">
                        <span class="label">姓名：</span>
                        <span class="value">{{ patientInfo.pName }}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">手机号：</span>
                        <span class="value">{{ patientInfo.pPhone }}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">身份证号：</span>
                        <span class="value">{{ patientInfo.pCard }}</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- 医院介绍卡片（与主卡片重叠） -->
        <div class="intro-card">
            <div class="intro-left">
                <h2>医院介绍</h2>
                <p class="intro-text">{{ hospitalIntro }}</p>
                <el-button type="text" class="more-btn" @click="showFullIntro = true">
                    查看更多 <i class="el-icon-arrow-right"></i>
                </el-button>
            </div>
            <div class="intro-right">
                <el-carousel :interval="4000" type="card" height="300px">
                    <el-carousel-item v-for="(item, index) in carouselImages" :key="index">
                        <div class="carousel-item">
                            <img :src="item" alt="医院图片" />
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </div>
        </div>

        <!-- 完整介绍Modal -->
        <el-dialog
            title="医院完整介绍"
            :visible.sync="showFullIntro"
            width="70%"
            :before-close="handleClose"
        >
            <div class="full-intro-content">
                <p>{{ fullHospitalIntro }}</p>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="showFullIntro = false">关闭</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import jwtDecode from "jwt-decode";
import { getToken } from "@/utils/storage.js";
import request from "@/utils/request.js";

export default {
    name: "HospitalIntroduction",
    data() {
        return {
            showFullIntro: false,
            hospitalIntro: "华中科技大学同济医学院附属协和医院创建于1866年,系卫生部直属大型综合性医院,内设华中科技大学同济医学院第一临床学院、亚洲紧急救援中心、湖北省急救中心,湖北省远程医学中心。医院先后被评为国家首批三级甲等医院、全国百佳医院、全国精神文明建设先进单位,获全国五一劳动奖状。",
            fullHospitalIntro: "华中科技大学同济医学院附属协和医院创建于1866年,系卫生部直属大型综合性医院,内设华中科技大学同济医学院第一临床学院、亚洲紧急救援中心、湖北省急救中心,湖北省远程医学中心。医院先后被评为国家首批三级甲等医院、全国百佳医院、全国精神文明建设先进单位,获全国五一劳动奖状。医院拥有先进的医疗设备和技术力量,致力于为患者提供优质的医疗服务。医院秉承'患者至上,质量第一'的服务理念,不断推进医疗技术创新和人才培养,为人民群众的健康事业做出积极贡献。",
            carouselImages: [
                require("@/assets/homepage-back.jpg"),
                require("@/assets/homepage-back.jpg"),
                require("@/assets/homepage-back.jpg"),
            ],
            patientInfo: {
                pName: "",
                pPhone: "",
                pCard: "",
            },
        };
    },
    methods: {
        tokenDecode(token) {
            if (token !== null) return jwtDecode(token);
        },
        handleClose(done) {
            done();
        },
        requestPatientInfo() {
            const userId = this.tokenDecode(getToken()).pId;
            request
                .get("doctor/findPatientById", {
                    params: {
                        pId: userId,
                    },
                })
                .then((res) => {
                    if (res.data.status !== 200)
                        return this.$message.error("获取数据失败");
                    this.patientInfo = res.data.data;
                })
                .catch((err) => {
                    console.error(err);
                });
        },
    },
    created() {
        const token = getToken();
        if (token) {
            const decoded = this.tokenDecode(token);
            this.patientInfo.pName = decoded.pName || "";
            this.patientInfo.pPhone = decoded.pPhone || "";
            this.patientInfo.pCard = decoded.pCard || "";
        }
        this.requestPatientInfo();
    },
};
</script>

<style scoped lang="scss">
.hospital-intro-container {
    padding: 30px;
    max-width: 1400px;
    margin: 0 auto;
}

.main-card {
    display: flex;
    gap: 30px;
    margin-bottom: 30px;
    background: #fff;
    border-radius: 8px;
    padding: 30px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    
    .card-left {
        flex: 2;
        
        h3 {
            color: #333;
            margin-bottom: 15px;
            font-size: 20px;
        }
        
        p {
            color: #666;
            line-height: 1.8;
            font-size: 14px;
        }
    }
    
    .card-right {
        flex: 1;
        
        .user-info-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 8px;
            padding: 25px;
            color: #fff;
            
            h4 {
                margin: 0 0 20px 0;
                font-size: 18px;
                border-bottom: 1px solid rgba(255, 255, 255, 0.3);
                padding-bottom: 10px;
            }
            
            .info-item {
                margin-bottom: 15px;
                display: flex;
                
                .label {
                    font-weight: 600;
                    min-width: 80px;
                }
                
                .value {
                    flex: 1;
                }
            }
        }
    }
}

.intro-card {
    display: flex;
    gap: 30px;
    background: #fff;
    border-radius: 8px;
    padding: 30px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    margin-top: -20px; // 与主卡片重叠
    
    .intro-left {
        flex: 1;
        
        h2 {
            color: #333;
            margin-bottom: 20px;
            font-size: 24px;
        }
        
        .intro-text {
            color: #666;
            line-height: 1.8;
            font-size: 14px;
            margin-bottom: 15px;
            display: -webkit-box;
            -webkit-line-clamp: 5;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .more-btn {
            color: #409EFF;
            font-size: 14px;
            padding: 0;
            
            &:hover {
                color: #66b1ff;
            }
        }
    }
    
    .intro-right {
        flex: 1;
        
        .carousel-item {
            width: 100%;
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            
            img {
                width: 100%;
                height: 100%;
                object-fit: cover;
                border-radius: 4px;
            }
        }
    }
}

.full-intro-content {
    padding: 20px 0;
    line-height: 2;
    color: #666;
    font-size: 14px;
}
</style>

