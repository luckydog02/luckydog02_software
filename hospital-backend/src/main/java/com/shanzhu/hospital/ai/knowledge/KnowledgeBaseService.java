package com.shanzhu.hospital.ai.knowledge;

import com.shanzhu.hospital.ai.embedding.EmbeddingService;
import com.shanzhu.hospital.ai.vector.VectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 知识库服务
 * 初始化科室介绍和就诊须知数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseService {
    
    private final VectorStore vectorStore;
    private final EmbeddingService embeddingService;
    
    /**
     * 初始化知识库
     */
    @PostConstruct
    public void init() {
        log.info("开始初始化知识库...");
        
        try {
            // 读取科室介绍文件
            loadDepartmentKnowledge();
            
            log.info("知识库初始化完成，共加载 {} 个文档", vectorStore.getAllDocuments().size());
            
        } catch (Exception e) {
            log.error("知识库初始化失败", e);
        }
    }
    
    /**
     * 加载科室知识
     */
    private void loadDepartmentKnowledge() {
        // 科室介绍数据（从文件读取或硬编码）
        String[] departments = {
            "神经内科：专注于中枢神经系统（脑、脊髓）、周围神经系统及肌肉疾病的诊断与治疗，如头痛、头晕、癫痫、中风、帕金森病、神经炎等。主治症状：头晕、头痛、失眠、中风后遗症、癫痫、帕金森病、手足震颤。",
            "呼吸与危重症医学科：诊治呼吸系统疾病，包括肺炎、哮喘、慢性阻塞性肺疾病（COPD）、肺栓塞、呼吸衰竭等。主治症状：感冒、咳嗽、哮喘、支气管炎、肺炎、咯血、胸闷、呼吸衰竭。",
            "内分泌科：专注于糖尿病、甲状腺疾病（甲亢、甲减）、肾上腺疾病、垂体疾病、骨质疏松等代谢及内分泌腺体疾病的诊疗。主治症状：糖尿病、甲亢/甲减、骨质疏松、肥胖症、痛风、肾上腺疾病。",
            "消化内科：诊治食管、胃、肠、肝、胆、胰等消化系统疾病，如胃炎、胃溃疡、肝炎、胆囊炎、结肠炎等。主治症状：胃痛、胃胀、腹泻、便秘、胃炎、胃溃疡、肝硬化、胆囊炎、消化道出血。",
            "心血管内科：擅长冠心病、高血压、心力衰竭、心律失常、心肌病等心血管疾病的诊治。主治症状：高血压、冠心病、心悸、胸痛、心律失常、心力衰竭。",
            "肾内科：诊治肾炎、肾病综合征、肾功能衰竭、尿路感染等肾脏疾病。主治症状：肾炎、尿毒症、水肿、蛋白尿、血尿、尿路感染。",
            "发热门诊：专门接诊发热（体温≥37.3℃）患者，排查感染性疾病。主治症状：体温≥37.3℃的各类发热患者，及具有呼吸道症状的急症。",
            "手足外科：诊治手足外伤（骨折、肌腱损伤）、先天性手足畸形、甲沟炎、糖尿病足等。主治症状：手脚外伤、断指再植、足畸形纠正。",
            "普通外科：涵盖腹部外科（阑尾炎、疝气、胆囊疾病）、乳腺疾病、甲状腺疾病等。主治症状：甲状腺结节、乳腺肿块、阑尾炎、腹股沟疝（疝气）、胆结石、痔疮。",
            "肛肠外科：诊治痔疮、肛裂、肛瘘、肛周脓肿、结直肠息肉等肛肠疾病。主治症状：复杂痔疮、肛瘘、肛周脓肿、便血。",
            "神经外科：处理脑外伤、脑肿瘤、脑出血、脊柱脊髓疾病等。主治症状：脑出血（需手术）、脑肿瘤、脑外伤、垂体瘤。",
            "泌尿外科：诊治泌尿系统疾病（肾结石、前列腺增生、膀胱炎、尿道狭窄等）。主治症状：肾结石、输尿管结石、前列腺疾病、尿频尿急、肾囊肿。",
            "骨科：涵盖骨折、关节炎、腰椎间盘突出、骨质疏松、股骨头坏死等疾病。主治症状：骨折、脱臼、颈椎病、腰间盘突出、关节炎、运动损伤。",
            "烧伤整形外科：处理烧伤、烫伤、冻伤及瘢痕修复，开展美容整形手术。主治症状：各类烫伤、烧伤修复、体表肿物切除、疤痕整形。",
            "妇科：诊治女性生殖系统疾病，如阴道炎、子宫肌瘤、卵巢囊肿、宫颈癌筛查等。主治症状：阴道炎、盆腔炎、子宫肌瘤、月经不调、痛经、更年期综合征。",
            "产科：提供孕期保健、产前检查、自然分娩及剖宫产服务。主治症状：孕期检查、产前咨询、顺产/剖宫产、产后恢复。",
            "儿科：诊治0-14岁儿童常见病（如感冒、肺炎、腹泻）、多发病及疑难病。主治症状：14周岁以下儿童出现的各类内科疾病（发热、咳嗽、腹泻等）。",
            "儿童保健科：专注于儿童生长发育监测（身高、体重、智力）、营养指导、心理行为评估。主治症状：儿童生长发育评估、疫苗接种咨询、多动症、营养指导。",
            "耳鼻咽喉科：诊治中耳炎、鼻窦炎、咽炎、喉炎、耳鸣、耳聋、扁桃体炎等。主治症状：鼻炎、中耳炎、咽炎、打呼噜（鼾症）、听力下降、声嘶。",
            "眼科：诊治近视、远视、白内障、青光眼、结膜炎、视网膜病变等。主治症状：近视、白内障、青光眼、结膜炎（红眼病）、视力模糊。",
            "口腔科：涵盖补牙、拔牙、洗牙、正畸（牙齿矫正）、种植牙、口腔颌面外科疾病等。主治症状：牙痛、拔牙、补牙、牙龈出血、口腔溃疡、牙齿矫正。",
            "中医科：运用中医理论诊治内科、妇科、儿科等疾病，如失眠、脾胃虚弱、月经不调、颈肩腰腿痛等。主治症状：各类慢性病及亚健康状态。",
            "康复医学科：针对中风后遗症、骨折术后、关节炎、脑瘫等患者提供康复治疗。主治症状：偏瘫恢复、肌肉萎缩、功能障碍、术后康复训练。",
            "急诊科：24小时接诊急危重症患者，如心脏骤停、严重外伤、急性胸痛、脑卒中等。主治症状：各类急性外伤、中毒、昏迷等危及生命的紧急情况。",
            "皮肤性病科：诊治皮炎、湿疹、荨麻疹、银屑病（牛皮癣）、痤疮等皮肤病，及常见性传播疾病。主治症状：湿疹、荨麻疹、皮炎、痤疮、脱发、各类性传播疾病。",
            "功能科：提供影像学及功能检查服务，包括超声（B超、彩超）、心电图、脑电图、肺功能检测等。"
        };
        
        // 就诊须知
        String[] policies = {
            "预约挂号说明：可通过本系统网页端、移动端或自助机进行预约。系统支持提前7天预约，每日上午8:00开放新号源。挂号必须提供患者本人的有效身份证件（身份证/医保卡）。",
            "就诊流程：1. 取号：请于就诊时段前20分钟到达医院，凭预约码或身份证在自助机取号。2. 候诊：前往对应科室候诊区，观察诊区显示屏叫号。3. 就诊：进入诊室由医生面诊、开具检查单或处方。4. 缴费：通过本系统\"我的订单\"或自助机完成线上缴费。5. 检查/取药：完成检查或凭处方前往药房取药。",
            "注意事项：需进行抽血检查、肝胆B超、胃肠镜检查的患者，请在就诊前一天22:00后禁食、禁水。如无法按时就诊，请至少提前一天在系统中取消预约，否则将计入黑名单。本系统预约仅针对普通门诊和专家门诊，危重症患者请直接前往急诊科。"
        };
        
        // 添加科室介绍
        for (String dept : departments) {
            String id = UUID.randomUUID().toString();
            vectorStore.addDocument(id, dept, "科室介绍");
            double[] embedding = embeddingService.generateEmbedding(dept);
            vectorStore.setEmbedding(id, embedding);
        }
        
        // 添加就诊须知
        for (String policy : policies) {
            String id = UUID.randomUUID().toString();
            vectorStore.addDocument(id, policy, "就诊须知");
            double[] embedding = embeddingService.generateEmbedding(policy);
            vectorStore.setEmbedding(id, embedding);
        }
    }
}

