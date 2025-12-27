<!--
 * 医院介绍页面
 *
-->
<template>
    <div class="hospital-intro-container">
        <!-- 医院信息顶部 -->
        <div class="index-hospital-info-top posr">
            <div class="info">
                <div class="w1200 pt30 clr">
                    <a href="javascript:;" class="fll hospital-logo mr20 ovh">
                        <img class="all-img img-cover" alt="武汉理工大学医院" src="@/assets/logo.jpg">
                    </a>
                    <div class="fll">
                        <a href="javascript:;" class="f28 db lh38 fcf mb10">武汉理工大学医院</a>
                        <div class="flag mb15 green lh22">
                            <span class="br4 dib bw pl10 pr10">综合医院</span>
                            <span class="br4 dib bw pl10 pr10">公立</span>
                            <span class="br4 dib bw pl10 pr10">医保</span>
                            <span class="br4 dib bw pl10 pr10">三甲</span>
                        </div>
                        <div class="f16 fcf mb10 opc9 hospital-composition">组成：南湖校医院、西院校医院、东院校医院、余家头校医院</div>
                    </div>
                    <div class="flr link-tel pl20 pr20 mr20 f16 pb20 pt20 br6">
                        <p class="lh28 top-icon-email tac">纠正信息请发送需求至</p>
                        <p class="lh28 tac">
                            <a href="javascript:;" class="green">whut_xyy@qq.com</a>
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <div class="w1200 view posr">
            <!-- 医院介绍 -->
            <div class="index-hospital-intr br12 bw mb20 pr20">
                <h2 class="f28 lh36 pt25 pb5 mb3">
                    <span class="hos-index-title pl20">医院介绍</span>
                </h2>
                <div class="clr pl20 hospital-intro-content">
                    <div class="fll left-txt">
                        <div class="f16 lh28 wbwr hid4 taj fc6 mb10">
                            {{ hospitalIntro }}
                        </div>
                        <div class="mb20">
                            <a href="javascript:;" class="green f14 lh22 icon-right-0" @click="showFullIntro = true">查看更多</a>
                        </div>
                        <div class="pl20 pr20 pt10 pb10 br10 links fc6 lh34 f14">
                            <div class="ellipsis">门诊时间：早上8：00—晚上17：30</div>
                            <div class="ellipsis">导航地址：湖北省武汉市解放大道1277号</div>
                            <div class="ellipsis">官网链接：http://hp.whut.edu.cn/yygk/yyjj/</div>
                        </div>
                    </div>
                    <div class="flr right-banner posr ovh br10">
                        <el-carousel :interval="2000" height="300px" :indicator-position="false" :arrow="arrow">
                            <el-carousel-item v-for="(item, index) in carouselImages" :key="index">
                                <div class="banner-item">
                                    <img :src="item" alt="医院图片" class="all-img img-cover banner-img">
                                </div>
                            </el-carousel-item>
                        </el-carousel>
                    </div>
                </div>
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
import request from "@/utils/request.js";

export default {
    name: "HospitalIntroduction",
    data() {
        return {
            showFullIntro: false,
            arrow: "hover",
            hospitalIntro: "武汉理工大学医院（以下简称医院）是经卫生行政部门批准开办的医院，隶属武汉理工大学，其马房山校区医院、余家头校区分院分别获批洪山区珞南街理工大社区卫生服务中心、武昌区杨园街理工大社区卫生服务中心，其南湖、三层楼校区分设医疗点。",
            fullHospitalIntro: "武汉理工大学医院（以下简称医院）是经卫生行政部门批准开办的医院，隶属武汉理工大学，其马房山校区医院、余家头校区分院分别获批洪山区珞南街理工大社区卫生服务中心、武昌区杨园街理工大社区卫生服务中心，其南湖、三层楼校区分设医疗点。医院现有医护人员及职工168人，其中高级职称人员30人、中级职称人员86人；开设内科、外科、妇科、眼科、口腔科、耳鼻喉科、中医科、理疗科、皮肤科、精神科、肛肠科、检验科、放射科、超声室、心电图室、胃镜室、住院部、心理咨询、公共卫生科、计生办等科室；提供心血管、内分泌、肝病、消化、神经、肛肠、眼科、骨科、皮肤、心理咨询等专科服务，定期聘请上级医院专家坐诊；拥有自动生化分析仪、数字化摄影机（DR）、彩超机、12导联心电图仪、长程心电图仪、长程血压仪、电子胃镜、全自动电解质分析仪、全自动血球分析仪、血凝仪、CT等大型医疗设备。医院坚决贯彻国家卫生政策，充分发挥人员、设备、组织优势与特色，全心服务学校\"立德树人\"和\"双一流\"发展战略，围绕学校师生和社区居民的卫生与健康需求，积极提供常见病、多发病诊治以及院前急救等基本医疗服务，扎实开展预防、保健、康复、健康体检、健康管理与促进、突发公共卫生事件处理等基本公共卫生工作，贯彻落实计划生育、学校医疗费管理改革等基本工作职责，都进行了积极探索并取得优良成绩。医院以习近平新时代中国特色社会主义思想为指导，努力践行\"以人为本、服务师生、保障健康、促进和谐\"的办院理念，扎实开展医德医风与机关作风建设工作，积极组织服务形象设计比赛、\"祖国好、理工美、医护亲\"职工摄影展等特色文化活动，赢得学校教职工形象风采展示赛、\"唱响新时代\"歌咏赛等一、二等奖，荣获洪山区与武昌区计划生育工作红旗单位、全国基层卫生岗位练兵和技能竞赛优胜单位、社区医疗机构医疗质量管理先进单位、武汉理工大学先进党总支等荣誉，为把武汉理工大学建设成\"让人民满意、让世人仰慕\"的一流大学做出了积极贡献！",
            carouselImages: [
                require("@/assets/homepage-back.jpg"),
                require("@/assets/homepage-back.jpg"),
                require("@/assets/homepage-back.jpg"),
            ]
        };
    },
    methods: {
        handleClose(done) {
            done();
        }
    },
    created() {
        // 页面初始化
    },
};
</script>

<style scoped lang="scss">
.hospital-intro-container {
    background: #f5f7fa;
    min-height: calc(100vh - 70px);
    overflow-y: auto;
    position: relative;
}

// 医院信息顶部
.index-hospital-info-top {
    background: #1a4d7a;
    padding: 30px 0 50px 0;
    margin-bottom: -30px;
    position: relative;
    overflow: hidden;
    
    
    .info {
        position: relative;
        z-index: 1;
        
        .w1200 {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }
        
        .hospital-logo {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            overflow: hidden;
            background: #fff;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
            transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
            border: 2px solid rgba(255, 255, 255, 0.3);
            
            &:hover {
                transform: scale(1.08) translateY(-5px);
                box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
                border-color: rgba(255, 255, 255, 0.5);
            }
            
            img {
                width: 100%;
                height: 100%;
                object-fit: cover;
                transition: transform 0.3s ease;
            }
            
            &:hover img {
                transform: scale(1.05);
            }
        }
        
        .f28 {
            font-size: 32px;
            font-weight: 700;
            letter-spacing: 0.5px;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        
        a.f28 {
            text-decoration: none !important;
            color: #ffffff !important;
            transition: all 0.3s ease;
            display: inline-block;
            
            &:hover {
                text-decoration: none !important;
                color: #ffffff !important;
                transform: scale(1.05);
                text-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                letter-spacing: 1px;
            }
            
            &:focus,
            &:active,
            &:visited {
                text-decoration: none !important;
                color: #ffffff !important;
            }
        }
        
        .fcf {
            color: #fff;
        }
        
        .flag {
            margin-top: 8px;
            
            .br4 {
                border-radius: 20px;
                background: rgba(255, 255, 255, 0.25);
                backdrop-filter: blur(10px);
                color: #fff;
                padding: 5px 12px;
                margin-right: 10px;
                margin-bottom: 6px;
                transition: all 0.3s ease;
                display: inline-block;
                font-size: 11px;
                font-weight: 500;
                border: 1px solid rgba(255, 255, 255, 0.3);
                
                &:hover {
                    background: rgba(255, 255, 255, 0.35);
                    transform: translateY(-3px) scale(1.05);
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
                }
            }
        }
        
        .link-tel {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 12px;
            width: 180px;
            height: 120px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
            transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
            border: 1px solid rgba(255, 255, 255, 0.5);
            align-self: flex-start;
            
            &:hover {
                transform: translateY(-8px) scale(1.05);
                box-shadow: 0 8px 30px rgba(0, 0, 0, 0.18);
                background: #fff;
            }
            
            .top-icon-email {
                background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path fill="%231a4d7a" d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/></svg>') no-repeat center top;
                padding-top: 32px;
                margin-bottom: 10px;
                background-size: 24px 24px;
            }
            
            p {
                margin: 0;
                padding: 0;
                font-size: 13px;
                color: #303133;
                
                a {
                    color: #1a4d7a;
                    font-weight: 500;
                    transition: color 0.3s ease;
                    
                    &:hover {
                        color: #1a4d7a;
                    }
                }
            }
        }
    }
}

// 通用样式类
.w1200 {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
}

.view {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px 20px 30px;
    margin-top: -50px;
    overflow-y: auto;
    max-height: calc(100vh - 150px);
    position: relative;
    z-index: 1;
    
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
        transition: background 0.3s ease;
        
        &:hover {
            background: rgba(64, 158, 255, 0.5);
        }
    }
}

.clr {
    &::after {
        content: '';
        display: table;
        clear: both;
    }
}

.fll {
    float: left;
}

.flr {
    float: right;
}

.f28 {
    font-size: 28px;
}

.f24 {
    font-size: 24px;
}

.f18 {
    font-size: 18px;
}

.f16 {
    font-size: 16px;
}

.f14 {
    font-size: 14px;
}

.lh36 {
    line-height: 36px;
}

.lh30 {
    line-height: 30px;
}

.lh28 {
    line-height: 28px;
}

.lh26 {
    line-height: 26px;
}

.lh24 {
    line-height: 24px;
}

.lh22 {
    line-height: 22px;
}

.lh38 {
    line-height: 38px;
}

.lh34 {
    line-height: 34px;
}

.lh56 {
    line-height: 56px;
}

.lh40 {
    line-height: 40px;
}

.lh60 {
    line-height: 60px;
}

.pt30 {
    padding-top: 30px;
}

.pt25 {
    padding-top: 25px;
}

.pb30 {
    padding-bottom: 30px;
}

.pb25 {
    padding-bottom: 25px;
}

.pb20 {
    padding-bottom: 20px;
}

.pb15 {
    padding-bottom: 15px;
}

.pb10 {
    padding-bottom: 10px;
}

.pb5 {
    padding-bottom: 5px;
}

.pt10 {
    padding-top: 10px;
}

.pt5 {
    padding-top: 5px;
}

.pl20 {
    padding-left: 20px;
}

.pr20 {
    padding-right: 20px;
}

.mr20 {
    margin-right: 20px;
}

.mr30 {
    margin-right: 30px;
}

.mr40 {
    margin-right: 40px;
}

.ml20 {
    margin-left: 20px;
}

.ml15 {
    margin-left: 15px;
}

.mr15 {
    margin-right: 15px;
}

.mb30 {
    margin-bottom: 30px;
}

.mb20 {
    margin-bottom: 20px;
}

.mb15 {
    margin-bottom: 15px;
}

.mb10 {
    margin-bottom: 10px;
}

.mb5 {
    margin-bottom: 5px;
}

.mb3 {
    margin-bottom: 3px;
}

.mt15 {
    margin-top: 15px;
}

.br12 {
    border-radius: 12px;
}

.br10 {
    border-radius: 10px;
}

.br8 {
    border-radius: 8px;
}

.br6 {
    border-radius: 6px;
}

.br4 {
    border-radius: 4px;
}

.br-50 {
    border-radius: 50%;
}

.bw {
    background: #fff;
}

.posr {
    position: relative;
}

.ovh {
    overflow: hidden;
}

.db {
    display: block;
}

.dib {
    display: inline-block;
}

.tac {
    text-align: center;
}

.taj {
    text-align: justify;
}

.fc6 {
    color: #666;
}

.fc9 {
    color: #999;
}

.fc3 {
    color: #333;
}

.fcf {
    color: #fff;
}

.green {
    color: #67c23a;
}

.yellow {
    color: #ff9900;
}

.fn {
    font-weight: normal;
}

.ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.wbwr {
    word-break: break-word;
}

.hid2 {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.hid4 {
    display: -webkit-box;
    -webkit-line-clamp: 4;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.bsbb {
    box-sizing: border-box;
}

.all-img {
    width: 100%;
    height: 100%;
}

.img-cover {
    object-fit: cover;
}

// 医院介绍
.index-hospital-intr {
    background: #fff;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 2px 16px rgba(0, 0, 0, 0.08);
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    border: 1px solid rgba(0, 0, 0, 0.04);
    
    &:hover {
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
        transform: translateY(-2px);
    }
    
    h2 {
        margin-bottom: 16px;
        padding-bottom: 12px;
        border-bottom: 2px solid #f0f2f5;
        
        .hos-index-title {
            position: relative;
            padding-left: 12px;
            
            &::before {
                content: '';
                position: absolute;
                left: 0;
                top: 50%;
                transform: translateY(-50%);
                width: 4px;
                height: 24px;
                background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
                border-radius: 2px;
            }
        }
    }
    
    .hospital-intro-content {
        display: flex;
        align-items: flex-end;
        gap: 30px;
    }
    
    .left-txt {
        width: calc(50% - 15px);
        display: flex;
        flex-direction: column;
        
        .f16 {
            line-height: 1.8;
            color: #606266;
            margin-bottom: 16px;
        }
        
        .links {
            background: linear-gradient(135deg, #f5f7fa 0%, #ecf5ff 100%);
            border-radius: 12px;
            border: 1px solid #e4e7ed;
            transition: all 0.3s ease;
            padding: 16px 20px;
            
            &:hover {
                background: linear-gradient(135deg, #ecf5ff 0%, #d4e8ff 100%);
                transform: translateX(8px);
                box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
                border-color: #b3d8ff;
            }
            
            .ellipsis {
                margin-bottom: 8px;
                display: flex;
                align-items: center;
                gap: 8px;
                
                &:last-child {
                    margin-bottom: 0;
                }
                
                &::before {
                    content: '•';
                    color: #409EFF;
                    font-size: 18px;
                    font-weight: bold;
                }
            }
        }
        
        .green {
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            font-weight: 500;
            
            &:hover {
                color: #409EFF;
                transform: translateX(5px);
            }
        }
    }
    
    .right-banner {
        width: calc(50% - 15px);
        height: 300px;
        border-radius: 16px;
        overflow: hidden;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
        transition: all 0.4s ease;
        border: 1px solid rgba(0, 0, 0, 0.06);
        align-self: flex-end;
        display: flex;
        align-items: flex-end;
        
        &:hover {
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
            transform: translateY(-3px);
        }
        
        // Element UI 轮播图样式
        ::v-deep .el-carousel {
            width: 100%;
            height: 100%;
            
            .el-carousel__container {
                height: 100% !important;
            }
        }
        
        ::v-deep .el-carousel-item {
            height: 100% !important;
        }
        
        .banner-item {
            width: 100%;
            height: 100%;
            display: block;
            position: relative;
            
            .banner-img {
                width: 100%;
                height: 100%;
                object-fit: cover;
                object-position: center;
                display: block;
                transition: transform 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
            }
        }
        
        // 轮播图悬停效果
        &:hover .banner-img {
            transform: scale(1.08);
        }
        
        // Element UI 轮播图样式优化
        ::v-deep .el-carousel__indicators {
            display: none !important;
        }
        
        ::v-deep .el-carousel__button {
            background-color: rgba(255, 255, 255, 0.6);
            width: 12px;
            height: 12px;
            border-radius: 50%;
            transition: all 0.3s ease;
            border: 2px solid rgba(255, 255, 255, 0.8);
        }
        
        ::v-deep .el-carousel__indicator.is-active .el-carousel__button {
            background-color: #409EFF;
            width: 24px;
            border-radius: 6px;
            border-color: #409EFF;
        }
        
        ::v-deep .el-carousel__arrow {
            background-color: rgba(64, 158, 255, 0.7);
            backdrop-filter: blur(10px);
            color: #fff;
            width: 40px;
            height: 40px;
            border-radius: 50%;
            transition: all 0.3s ease;
            
            &:hover {
                background-color: rgba(64, 158, 255, 0.9);
                transform: scale(1.1);
            }
        }
    }
}

// 医院环境
.index-hospital-imgs {
    .hos-img {
        img {
            width: calc(33.333% - 14px);
            height: 200px;
            margin-right: 20px;
            margin-bottom: 20px;
            
            &:nth-child(3n) {
                margin-right: 0;
            }
        }
    }
}

// 科室导航
.index-ks-map {
    .list {
        ul {
            list-style: none;
            margin: 0;
            padding: 0;
            
            li {
                width: calc(16.666% - 17px);
                margin-right: 20px;
                
                &:nth-child(6n) {
                    margin-right: 0;
                }
                
                .a {
                    background: #f5f7fa;
                    transition: all 0.3s ease;
                    
                    &:hover {
                        background: #409EFF;
                        color: #fff;
                    }
                }
            }
        }
    }
}

// 医生团队
.index-doctors {
    .top {
        .cell {
            width: calc(50% - 10px);
            
            .img {
                width: 100%;
                height: 200px;
                margin-bottom: 15px;
            }
        }
    }
    
    .bottom {
        ul {
            list-style: none;
            margin: 0;
            padding: 0;
            
            li {
                width: calc(25% - 15px);
                margin-right: 20px;
                
                &:nth-child(4n) {
                    margin-right: 0;
                }
                
                .cell {
                    background: #fff;
                    border: 1px solid #e4e7ed;
                    padding: 15px;
                    
                    .img-name {
                        .img {
                            width: 80px;
                            height: 80px;
                            margin: 0 auto 10px;
                        }
                    }
                    
                    .bb1 {
                        border-bottom: 1px solid #e4e7ed;
                        padding-bottom: 15px;
                    }
                    
                    .bt1 {
                        border-top: 1px solid #e4e7ed;
                    }
                    
                    .index-doctors-bottom-btns2 {
                        a {
                            color: #fff;
                            text-decoration: none;
                            
                            &.icon-zx {
                                background: #409EFF;
                            }
                            
                            &.icon-yy {
                                background: #67c23a;
                            }
                            
                            &:hover {
                                opacity: 0.8;
                            }
                        }
                    }
                }
            }
        }
    }
    
    .index-doctors-bottom-btns {
        a {
            padding: 8px 20px;
            border-radius: 4px;
            text-decoration: none;
            color: #fff;
            
            &.a {
                background: #409EFF;
            }
            
            &.a-green {
                background: #67c23a;
            }
            
            &:hover {
                opacity: 0.8;
            }
        }
    }
}

// 诊疗评价
.index-zlpj {
    ul {
        list-style: none;
        margin: 0;
        padding: 0;
        
        li {
            width: calc(50% - 10px);
            margin-right: 20px;
            
            &:nth-child(2n) {
                margin-right: 0;
            }
            
            .cell {
                display: flex;
                background: #fff;
                border: 1px solid #e4e7ed;
                border-radius: 8px;
                padding: 20px;
                
                .img {
                    width: 60px;
                    height: 60px;
                    margin-right: 15px;
                    flex-shrink: 0;
                }
                
                .info {
                    flex: 1;
                    
                    .star {
                        display: inline-block;
                        width: 80px;
                        height: 16px;
                        background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"><path fill="%23ffd700" d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>') repeat-x;
                        background-size: 16px 16px;
                        
                        .span {
                            height: 100%;
                            background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"><path fill="%23ffd700" d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>') repeat-x;
                            background-size: 16px 16px;
                        }
                    }
                }
            }
        }
    }
}

.hos-index-title {
    font-weight: bold;
    color: #333;
}

.icon-right-0::after {
    content: '→';
    margin-left: 5px;
}

.icon-right-more::after {
    content: '→';
    margin-left: 5px;
}

.a-hover:hover {
    color: #409EFF;
}

.opc9 {
    opacity: 0.9;
}

.hospital-composition {
    transition: all 0.3s ease;
    cursor: default;
    
    &:hover {
        opacity: 1;
        transform: translateX(5px);
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
}

.pl10 {
    padding-left: 10px;
}

.pr10 {
    padding-right: 10px;
}

.pl32 {
    padding-left: 32px;
}

.full-intro-content {
    padding: 20px 0;
    line-height: 2.2;
    color: #606266;
    font-size: 15px;
    text-align: justify;
    
    p {
        margin-bottom: 16px;
        text-indent: 2em;
        
        &:last-child {
            margin-bottom: 0;
        }
    }
}

// 对话框样式优化
::v-deep .el-dialog {
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
    
    .el-dialog__header {
        padding: 24px 24px 16px;
        border-bottom: 1px solid #f0f2f5;
        
        .el-dialog__title {
            font-size: 20px;
            font-weight: 600;
            color: #303133;
        }
    }
    
    .el-dialog__body {
        padding: 24px;
    }
    
    .el-dialog__footer {
        padding: 16px 24px;
        border-top: 1px solid #f0f2f5;
    }
}
</style>

