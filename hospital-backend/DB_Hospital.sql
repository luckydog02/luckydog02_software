
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
  `a_id` int NOT NULL COMMENT '主键，管理员id（账号）',
  `a_password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '密码',
  `a_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '姓名',
  `a_gender` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '性别',
  `a_card` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '证件号码',
  `a_phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '手机号',
  `a_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`a_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of admin_user
-- ----------------------------
BEGIN;
INSERT INTO `admin_user` VALUES (202501, '123456', 'admin', '男', '34000000000000', '13541111111', '123@qq.com');
COMMIT;

-- ----------------------------
-- Table structure for arrange
-- ----------------------------
DROP TABLE IF EXISTS `arrange`;
CREATE TABLE `arrange` (
  `ar_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci NOT NULL,
  `ar_time` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `d_id` int DEFAULT NULL,
  PRIMARY KEY (`ar_id`) USING BTREE,
  KEY `arTOd` (`d_id`) USING BTREE,
  CONSTRAINT `arTOd` FOREIGN KEY (`d_id`) REFERENCES `doctor_user` (`d_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of arrange
-- ----------------------------
BEGIN;
INSERT INTO `arrange` VALUES ('10002025-11-19', '2025-11-19', 1000);
INSERT INTO `arrange` VALUES ('10002025-11-20', '2025-11-20', 1000);
INSERT INTO `arrange` VALUES ('10012025-11-19', '2025-11-19', 1001);
INSERT INTO `arrange` VALUES ('10012025-11-20', '2025-11-20', 1001);
INSERT INTO `arrange` VALUES ('10022025-11-19', '2025-11-19', 1002);
INSERT INTO `arrange` VALUES ('10022025-11-20', '2025-11-20', 1002);
INSERT INTO `arrange` VALUES ('10032025-11-19', '2025-11-19', 1003);
INSERT INTO `arrange` VALUES ('10042025-11-19', '2025-11-19', 1004);
INSERT INTO `arrange` VALUES ('10042025-11-20', '2025-11-20', 1004);
INSERT INTO `arrange` VALUES ('10042025-11-21', '2025-11-21', 1004);
INSERT INTO `arrange` VALUES ('10042025-11-23', '2025-11-23', 1004);
INSERT INTO `arrange` VALUES ('10072025-11-19', '2025-11-19', 1007);
INSERT INTO `arrange` VALUES ('10072025-11-20', '2025-11-20', 1007);
INSERT INTO `arrange` VALUES ('10072025-11-23', '2025-11-23', 1007);
INSERT INTO `arrange` VALUES ('10082025-11-19', '2025-11-19', 1008);
INSERT INTO `arrange` VALUES ('10082025-11-20', '2025-11-20', 1008);
INSERT INTO `arrange` VALUES ('10092025-11-19', '2025-11-19', 1009);
INSERT INTO `arrange` VALUES ('10092025-11-20', '2025-11-20', 1009);
COMMIT;

-- ----------------------------
-- Table structure for bed
-- ----------------------------
DROP TABLE IF EXISTS `bed`;
CREATE TABLE `bed` (
  `b_id` int NOT NULL,
  `p_id` int DEFAULT NULL,
  `b_state` int DEFAULT NULL,
  `b_start` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `d_id` int DEFAULT NULL,
  `b_reason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `version` int DEFAULT NULL,
  PRIMARY KEY (`b_id`) USING BTREE,
  KEY `bTOp` (`p_id`) USING BTREE,
  KEY `bTOd` (`d_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of bed
-- ----------------------------
BEGIN;
INSERT INTO `bed` VALUES (1, 2000, 1, '2025-11-19', 1000, '病人状况比较糟糕，需要住院3天。照顾下。', NULL);
INSERT INTO `bed` VALUES (2, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (3, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (4, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (5, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (6, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (7, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (8, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (9, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (10, -1, 0, NULL, -1, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for checks
-- ----------------------------
DROP TABLE IF EXISTS `checks`;
CREATE TABLE `checks` (
  `ch_id` int NOT NULL AUTO_INCREMENT,
  `ch_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `ch_price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`ch_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of checks
-- ----------------------------
BEGIN;
INSERT INTO `checks` VALUES (1, 'B超', 201.00);
INSERT INTO `checks` VALUES (2, 'CT', 435.00);
INSERT INTO `checks` VALUES (3, '核磁共振', 653.00);
INSERT INTO `checks` VALUES (4, 'MRI', 534.00);
INSERT INTO `checks` VALUES (5, '心电图', 634.00);
INSERT INTO `checks` VALUES (6, '彩超', 213.00);
INSERT INTO `checks` VALUES (7, '血常规', 434.00);
INSERT INTO `checks` VALUES (8, '肝功能', 543.00);
INSERT INTO `checks` VALUES (9, '血糖', 434.00);
INSERT INTO `checks` VALUES (10, '甲状腺', 434.00);
INSERT INTO `checks` VALUES (12, '听力', 10.00);
COMMIT;

-- ----------------------------
-- Table structure for doctor_user
-- ----------------------------
DROP TABLE IF EXISTS `doctor_user`;
CREATE TABLE `doctor_user` (
  `d_id` int NOT NULL,
  `d_password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `d_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `d_gender` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `d_phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `d_card` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `d_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `d_post` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `d_introduction` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `d_section` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `d_state` int NOT NULL,
  `d_price` decimal(10,2) DEFAULT NULL,
  `d_people` int DEFAULT NULL,
  `d_star` decimal(10,2) DEFAULT NULL,
  `d_avg_star` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`d_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of doctor_user
-- ----------------------------
BEGIN;
INSERT INTO `doctor_user` VALUES (1000, 'e10adc3949ba59abbe56e057f20f883e', '张医生', '女', '18762543671', '352225177380837645', 'zhang@qq.com', '主任医师', '神经内科主任医师', '神经内科', 1, 10.00, 1, 4.00, 4.00);
INSERT INTO `doctor_user` VALUES (1001, 'e10adc3949ba59abbe56e057f20f883e', '王医生', '男', '19872635542', '348882988376153789', 'wang@qq.com', '副主任医师', '神经内科-副主任医师', '神经内科', 0, 5.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1002, 'e10adc3949ba59abbe56e057f20f883e', '李医生', '男', '18627362563', '352224827736281', 'li@gmail.com', '主治医生', '神经内科-主治医生', '神经内科', 1, 20.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1003, 'e10adc3949ba59abbe56e057f20f883e', '赵医生', '男', '18792374621', '348882733628236', 'zhao@163.com', '主任医师', '内分泌科-主任医师', '内分泌科', 1, 10.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1004, 'e10adc3949ba59abbe56e057f20f883e', '马医生', '男', '18562382321', '342223947192347', 'ma@qq.com', '主治医生', '呼吸科-主治医生', '呼吸与危重症医学科', 1, 50.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1005, 'e10adc3949ba59abbe56e057f20f883e', '卞医生', '男', '18676665544', '352229833391837463', 'bian@qq.com', '主任医师', '消化内科-主人医师', '消化内科', 1, 100.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1006, 'e10adc3949ba59abbe56e057f20f883e', '廖医生', '女', '18766623523', '352229378782331', 'liao@qq.com', '副主任医师', '心血管内科医生', '心血管内科', 1, 10.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1007, 'e10adc3949ba59abbe56e057f20f883e', '乌医生', '男', '18723626312', '382227392312132', 'wu@qq.com', '主任医师', '妇科-主任医师', '妇科', 1, 10.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1008, 'e10adc3949ba59abbe56e057f20f883e', '田医生', '女', '19876763231', '348887233210237', 'tian@qq.com', '副主任医师', '手足外科-副主任', '手足外科', 1, 2.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1009, 'e10adc3949ba59abbe56e057f20f883e', '苗医生', '女', '18723776462', '387772372313123', 'miao@163.com', '主治医生', '儿科-主治医生', '儿科', 1, 30.00, 0, 0.00, NULL);
COMMIT;

-- ----------------------------
-- Table structure for drug
-- ----------------------------
DROP TABLE IF EXISTS `drug`;
CREATE TABLE `drug` (
  `dr_id` int NOT NULL AUTO_INCREMENT,
  `dr_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `dr_price` decimal(10,2) DEFAULT NULL,
  `dr_number` int DEFAULT NULL,
  `dr_publisher` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `dr_unit` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`dr_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of drug
-- ----------------------------
BEGIN;
INSERT INTO `drug` VALUES (1, '青霉素', 23.00, 11, '国家医药局', '袋');
INSERT INTO `drug` VALUES (2, '苯唑西林', 11.00, 28, '国家医药局', '盒');
INSERT INTO `drug` VALUES (3, '氨苄西林', 13.00, 51, '国家医药局', '盒');
INSERT INTO `drug` VALUES (4, '哌拉西林', 2.00, 7, '国家医药局', '盒');
INSERT INTO `drug` VALUES (5, '阿莫西林', 13.00, 20, '国家医药局', '盒');
INSERT INTO `drug` VALUES (6, '头孢唑林', 3.00, 32, '国家医药局', '盒');
INSERT INTO `drug` VALUES (7, '头孢氨苄', 4.00, 43, '国家医药局', '盒');
INSERT INTO `drug` VALUES (8, '头孢呋辛', 8.00, 54, '国家医药局', '盒');
INSERT INTO `drug` VALUES (9, '阿米卡星', 5.00, 54, '国家医药局', '袋');
INSERT INTO `drug` VALUES (10, '庆大霉素', 7.00, 63, '国家医药局', '袋');
INSERT INTO `drug` VALUES (11, '红霉素', 6.00, 75, '国家医药局', '袋');
INSERT INTO `drug` VALUES (12, '阿奇霉素', 54.00, 52, '国家医药局', '袋');
INSERT INTO `drug` VALUES (13, '克林霉素', 65.00, 21, '国家医药局', '袋');
INSERT INTO `drug` VALUES (14, '复方磺胺甲噁唑', 76.00, 54, '国家医药局', '袋');
INSERT INTO `drug` VALUES (15, '诺氟沙星', 65.00, 33, '国家医药局', '袋');
INSERT INTO `drug` VALUES (16, '左氧氟沙星', 76.00, 43, '国家医药局', '袋');
COMMIT;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `o_id` int NOT NULL AUTO_INCREMENT,
  `p_id` int DEFAULT NULL,
  `d_id` int DEFAULT NULL,
  `o_record` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `o_start` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `o_end` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `o_state` int DEFAULT NULL,
  `o_drug` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `o_check` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `o_total_price` decimal(10,2) DEFAULT NULL,
  `o_price_state` int DEFAULT NULL,
  `o_advice` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`o_id`) USING BTREE,
  KEY `oTOp` (`p_id`) USING BTREE,
  KEY `0TOd` (`d_id`) USING BTREE,
  CONSTRAINT `0TOd` FOREIGN KEY (`d_id`) REFERENCES `doctor_user` (`d_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `oTOp` FOREIGN KEY (`p_id`) REFERENCES `patient_user` (`p_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=211209 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
BEGIN;
INSERT INTO `orders` VALUES (2137, 2000, 1000, '感冒了，开感冒药。平时多注意休息', '2025-11-19 08:30-09:30', '2025-11-19 19:51:53', 1, '青霉素*23(元)*1 红霉素*6(元)*1 庆大霉素*7(元)*1  药物总价36元 ', 'B超*201(元) CT*435(元) MRI*534(元)  项目总价1170元 ', 0.00, 1, NULL);
INSERT INTO `orders` VALUES (2512, 2000, 1000, NULL, '2025-11-20 09:30-10:30', NULL, 0, NULL, NULL, NULL, 0, NULL);
INSERT INTO `orders` VALUES (5843, 2000, 1008, NULL, '2025-11-20 10:30-11:30', NULL, 0, NULL, NULL, NULL, 0, NULL);
INSERT INTO `orders` VALUES (6051, 2000, 1000, NULL, '2025-11-19 09:30-10:30', NULL, 0, NULL, NULL, 0.00, 1, NULL);
COMMIT;

-- ----------------------------
-- Table structure for review
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `r_id` int NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `p_id` int NOT NULL COMMENT '患者ID',
  `d_id` int NOT NULL COMMENT '医生ID',
  `o_id` int NOT NULL COMMENT '关联挂号单ID',
  `r_star` decimal(10,2) DEFAULT '5.00' COMMENT '评分(1-5)',
  `r_content` text CHARACTER SET utf8mb3 COLLATE utf8_general_ci COMMENT '评价内容',
  `r_impressions` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '患者印象标签(逗号分隔)',
  `r_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  PRIMARY KEY (`r_id`) USING BTREE,
  KEY `rTOp` (`p_id`) USING BTREE,
  KEY `rTOd` (`d_id`) USING BTREE,
  KEY `rTOo` (`o_id`) USING BTREE,
  CONSTRAINT `rTOp` FOREIGN KEY (`p_id`) REFERENCES `patient_user` (`p_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `rTOd` FOREIGN KEY (`d_id`) REFERENCES `doctor_user` (`d_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `rTOo` FOREIGN KEY (`o_id`) REFERENCES `orders` (`o_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of review
-- ----------------------------
BEGIN;
DROP TABLE IF EXISTS `patient_user`;
CREATE TABLE `patient_user` (
  `p_id` int NOT NULL,
  `p_password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `p_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `p_gender` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `p_phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `p_card` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `p_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `p_state` int DEFAULT NULL,
  `p_birthday` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `p_age` int DEFAULT NULL,
  PRIMARY KEY (`p_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of patient_user
-- ----------------------------
BEGIN;
INSERT INTO `patient_user` VALUES (2000, 'e10adc3949ba59abbe56e057f20f883e', '徐先生', '男', '18733223345', '352227655454361762', 'xu@qq.com', 1, '1994-11-27', 29);
INSERT INTO `patient_user` VALUES (2001, 'e10adc3949ba59abbe56e057f20f883e', '曾先生', '男', '18766552374', '356662733625364', 'zeng@qq.com', 1, '2000-11-03', 23);
INSERT INTO `patient_user` VALUES (2002, 'e10adc3949ba59abbe56e057f20f883e', '张女士', '女', '18677662374', '387772633819283', 'zhang@163.com', 1, '1997-11-05', 26);
INSERT INTO `patient_user` VALUES (2003, 'e10adc3949ba59abbe56e057f20f883e', '杨先生', '男', '18766256323', '353329877638231', 'yang@qq.com', 1, '2025-11-26', 0);
INSERT INTO `patient_user` VALUES (2004, 'e10adc3949ba59abbe56e057f20f883e', '马女士', '女', '18766235473', '376662537482735', 'ma@qq.com', 1, '2017-11-06', 6);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;




-- 视图
-- 视图1：医生出诊信息视图（可更新，含CHECK OPTION）
CREATE OR REPLACE VIEW v_doctor_arrange AS
SELECT 
  d.d_id, d.d_name, d.d_section, d.d_price,
  a.ar_id, a.ar_time -- 
FROM doctor_user d
JOIN arrange a ON d.d_id = a.d_id
WHERE d.d_state = 1 -- 仅显示出诊医生
WITH CHECK OPTION; -- 确保修改后仍满足“出诊状态”条件

-- 视图2：患者住院信息视图
CREATE VIEW v_patient_hospital AS
SELECT 
  p.p_id, p.p_name, p.p_gender, p.p_age,
  b.b_id, b.b_start, b.b_reason,
  d.d_name AS doctor_name, d.d_section AS doctor_section
FROM patient_user p
JOIN bed b ON p.p_id = b.p_id
JOIN doctor_user d ON b.d_id = d.d_id
WHERE b.b_state = 1 -- 仅显示住院患者（状态=1）
ORDER BY b.b_start DESC; -- 含ORDER BY，属于不可更新视图




-- 索引
CREATE UNIQUE INDEX idx_d_card ON doctor_user(d_card);
-- 复合索引（订单-患者+就诊时间）
CREATE INDEX idx_o_pid_start ON orders(p_id, o_start);
-- HASH索引（药品名称，仅MEMORY表支持，此处用常规表兼容方案）
CREATE INDEX idx_dr_name_hash ON drug(dr_name) USING HASH;
-- 复合索引（医生-科室+职称）
CREATE INDEX idx_d_section_post ON doctor_user(d_section, d_post);


-- 存储过程
DELIMITER $$
CREATE PROCEDURE calc_doctor_visit_count(IN p_d_id INT, OUT p_count INT)
BEGIN
  SELECT IFNULL(COUNT(o_id), 0) INTO p_count
  FROM orders
  WHERE d_id = p_d_id AND o_state = 1;
END$$
DELIMITER ;

-- 触发器删除医生时同步删除关联床位的主治医生
DELIMITER $$
CREATE TRIGGER trg_doctor_delete_before BEFORE DELETE ON doctor_user
FOR EACH ROW
BEGIN
  UPDATE bed SET d_id = NULL WHERE d_id = OLD.d_id;
END$$
DELIMITER ;




-- 4.1 SQL语句备份（指定字段）
SELECT p_id, p_name, p_age INTO OUTFILE 'D:/MySQL/MySQL Server 8.0/Uploads/backup_patient.txt'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\r\n' FROM patient_user;

-- 备份医生表（doctor_user）到TXT文件
SELECT 
  d_id, d_password, d_name, d_gender, d_phone, 
  d_card, d_email, d_post, d_introduction, d_section, 
  d_state, d_price, d_people, d_star, d_avg_star
INTO OUTFILE 'D:/MySQL/MySQL Server 8.0/Uploads/doctor_user_backup.txt'  -- Windows路径用双反斜杠
FIELDS
  TERMINATED BY '|'                -- 字段用竖线分隔
  OPTIONALLY ENCLOSED BY '\''      -- 字符型字段单引号包裹
  ESCAPED BY '\\'
LINES TERMINATED BY '\r\n'         -- 适配Windows换行符（\r\n）
FROM DB_Hospital.doctor_user;      -- 替换为你的数据库名（DB_Hospital）

-- 4.2 客户端工具备份（终端执行）
-- mysqldump -hlocalhost -uroot -p DB_Hospital patient_user > D:/backup_patient.sql


-- 5.1自主存取控制：创建角色用户并分配权限
-- 创建医生角色（仅能操作诊疗相关数据）
CREATE USER IF NOT EXISTS 'role_doctor'@'localhost' IDENTIFIED BY 'doc123456';
GRANT SELECT ON DB_Hospital.patient_user TO 'role_doctor'@'localhost';
GRANT SELECT, UPDATE ON DB_Hospital.orders TO 'role_doctor'@'localhost';
GRANT SELECT ON DB_Hospital.drug TO 'role_doctor'@'localhost';
GRANT SELECT ON DB_Hospital.checks TO 'role_doctor'@'localhost';

-- 创建患者角色（仅能查询自己的订单）
CREATE USER IF NOT EXISTS 'role_patient'@'localhost' IDENTIFIED BY 'pat123456';
-- 新增患者自查询视图（无需改后端，直接查询视图）
CREATE VIEW v_patient_order AS
SELECT o.o_id, d.d_name, o.o_start, o.o_record, o.o_total_price, o.o_price_state
FROM orders o
JOIN doctor_user d ON o.d_id = d.d_id
WHERE o.p_id = SUBSTRING_INDEX(CURRENT_USER(), '@', 1) + 0; -- 假设用户名=患者p_id
GRANT SELECT ON DB_Hospital.v_patient_order TO 'role_patient'@'localhost';

-- 创建管理员角色（全权限）
CREATE USER IF NOT EXISTS 'role_admin'@'localhost' IDENTIFIED BY 'adm123456';
GRANT ALL PRIVILEGES ON DB_Hospital.* TO 'role_admin'@'localhost' WITH GRANT OPTION; 



-- 并发控制：设置事务隔离级别+行锁存储过程
-- 显式设置全局隔离级别为可重复读（MySQL默认，强化并发控制）
SET GLOBAL TRANSACTION ISOLATION LEVEL REPEATABLE READ;
-- 关闭自动提交（需手动commit）
SET @@autocommit = 0;

-- 并发修改订单状态的存储过程（行锁防止丢失修改）
DELIMITER $$
CREATE PROCEDURE update_order_state_safe(IN p_o_id INT, IN p_new_state INT)
BEGIN
  START TRANSACTION;
  -- 行锁锁定目标订单
  SELECT * FROM orders WHERE o_id = p_o_id FOR UPDATE;
  -- 修改状态
  UPDATE orders SET o_state = p_new_state WHERE o_id = p_o_id;
  COMMIT;
END$$
DELIMITER ;

USE DB_hospital;
