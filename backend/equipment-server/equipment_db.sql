/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3309
 Source Server Type    : MySQL
 Source Server Version : 80046
 Source Host           : localhost:3309
 Source Schema         : equipment_db

 Target Server Type    : MySQL
 Target Server Version : 80046
 File Encoding         : 65001

 Date: 08/09/2026 19:50:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_analysis
-- ----------------------------
DROP TABLE IF EXISTS `ai_analysis`;
CREATE TABLE `ai_analysis`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'AI分析ID',
  `equipment_id` bigint NULL DEFAULT NULL COMMENT '关联设备ID',
  `work_order_id` bigint NULL DEFAULT NULL COMMENT '关联工单ID',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '故障描述',
 `analysis_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'AI分析结果',
 `sources` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'RAG引用资料',
 `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI设备故障分析记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_analysis
-- ----------------------------
INSERT INTO `ai_analysis`
(`id`, `equipment_id`, `work_order_id`, `description`, `analysis_result`, `create_time`)
VALUES (1, NULL, NULL, '设备运行时温度明显升高，并伴随异响', '一、可能原因\n1、设备内部润滑不良或润滑剂变质，导致运动部件摩擦加剧，产生异常高温和金属摩擦声。\n2、轴承或齿轮等传动部件出现磨损、点蚀或间隙过大，运行中产生周期性撞击异响及发热。\n3、冷却系统故障，如风扇损坏、散热器堵塞或冷却液不足，使热量无法正常散发。\n4、电机或电气部件过载或绕组局部短路，导致电流增大、线圈发热并伴随电磁噪音。\n\n二、检查建议\n1、立即停机并切断电源，待设备冷却后，仔细靠近异响部位，判断响声来自电机、减速箱还是传动轴，同时用测温枪测量各部件表面温度分布，寻找温度最高点。\n2、检查润滑系统油位和油质，确认润滑剂是否充足、变色或含有金属屑，必要时更换润滑脂或润滑油。\n3、拆开防护罩，检查轴承座与转子间隙，手动盘车感受是否有卡滞或异常振动，检查联轴器、皮带或齿轮啮合面是否有偏磨痕迹。\n4、使用万用表测量电机三相电流是否平衡且处于额定值内，同时检测散热风扇运转是否正常，清理散热翅片和进风口积尘。\n\n三、注意事项\n1、切勿在设备高温及异响状态下持续运转，以免扩大机械损伤甚至引发安全事故，应立即停机处理。\n2、在检查高温部件时，先确认设备已充分冷却并挂牌上锁，防止突然启动造成人身伤害。\n3、拆卸电气部分时，须由持证电工执行并验电，避免带电作业引发触电风险。\n4、分析结果仅供辅助参考，最终应以专业人员现场检查结果为准。', '2026-09-03 21:54:42');
INSERT INTO `ai_analysis`
(`id`, `equipment_id`, `work_order_id`, `description`, `analysis_result`, `create_time`)
VALUES (2, 1, NULL, '半小时后温度明显升高', '一、可能原因\n1、设备散热系统故障，如风扇转速下降、散热片堵塞或风道不畅。\n2、设备负载过大或运行参数异常，导致产热量超过散热能力。\n3、润滑不良或机械部件摩擦加剧，使局部温度快速上升。\n4、环境温度过高或设备安装位置通风不良，影响自然散热。\n5、冷却介质不足或循环系统堵塞，如水冷或油冷设备流量下降。\n\n二、检查建议\n1、检查散热风扇是否正常运转，清理散热片及进风口滤网上的灰尘。\n2、查看设备运行电流或负载表，确认是否超负荷运转，并核对运行参数设定。\n3、检查轴承、齿轮等运动部件的润滑状态，按规定补充或更换润滑剂。\n4、测量环境温度，确保设备周围有足够通风空间，避免靠近热源。\n5、若为水冷或油冷设备，检查冷却液液位、管路是否堵塞或泄漏，确认循环泵工作正常。\n\n三、注意事项\n1、不要在设备高温状态下直接触摸发热部件，防止烫伤。\n2、若设备温度持续上升并伴有异味或异响，应立即停机断电，避免进一步损坏。\n3、不要随意拆开非专业人员不应触及的防护罩或高压部件，需先切断电源并等待冷却。\n4、分析结果仅供辅助参考，最终应以专业人员现场检查结果为准。', '2026-09-03 22:22:04');
INSERT INTO `ai_analysis`
(`id`, `equipment_id`, `work_order_id`, `description`, `analysis_result`, `create_time`)
VALUES(3, 1, NULL, '半小时后温度明显升高', '一、可能原因\n1、设备运行半小时后温度升高，可能是散热系统故障，例如风扇不转、散热片堵塞或风道不畅。\n2、可能是润滑不良导致机械摩擦加剧，例如轴承缺油或磨损严重，产生过多热量。\n3、可能是负载过大，设备长时间超负荷运行，电流升高引起发热。\n4、可能是电气部分问题，例如绕组绝缘老化、接触电阻过大或电压异常。\n5、可能是环境温度过高或设备附近热源影响，导致散热效果下降。\n\n二、检查建议\n1、检查设备散热风扇是否正常运转，清理散热片和通风口灰尘。\n2、检查轴承和传动部件润滑情况，必要时补充或更换润滑油。\n3、测量设备运行电流和电压，确认是否在额定范围内。\n4、检查设备负载情况，排除过载或卡阻现象。\n5、用手背触摸设备外壳感受温度分布，判断是否有局部过热点，同时检查温度传感器是否准确。\n\n三、注意事项\n1、发现温度升高后应立即停机，等待设备冷却再进行检查，避免烫伤。\n2、不要随意拆卸带电部件，电气检查应由专业电工操作。\n3、若设备带有自动保护功能，不要强行复位，应先查明过热原因。\n4、分析结果仅供辅助参考，最终应以专业人员现场检查结果为准。', '2026-09-03 22:24:13');
INSERT INTO `ai_analysis`
(`id`, `equipment_id`, `work_order_id`, `description`, `analysis_result`, `create_time`)
VALUES (4, 1, 3, '半小时后温度明显升高', '一、可能原因\n1、设备散热系统故障，例如风扇停转、散热片堵塞或通风口受阻。\n2、设备长时间高负载运行，超出额定工作能力，导致产热过快。\n3、润滑不良或机械摩擦加剧，例如轴承缺油、部件磨损或装配过紧。\n4、冷却介质不足或循环不畅，如冷却液缺失、管路堵塞或水泵失效。\n5、环境温度过高或设备附近有热源，影响自然散热效果。\n\n二、检查建议\n1、首先检查散热风扇是否正常转动，清理散热片和通风口上的灰尘与杂物。\n2、查看设备运行负载是否过高，确认是否超过额定工况，必要时降低负荷或暂停运行。\n3、检查润滑部位，补充或更换合适规格的润滑油，并观察有无异常磨损或异响。\n4、若设备带有冷却系统，检查冷却液液位、管路是否通畅，以及循环泵或风扇工作状态。\n5、使用测温仪器测量设备外壳、轴承、电机等关键部位的温度分布，判断热量集中来源。\n\n三、注意事项\n1、在检查和操作前务必切断电源或按设备安全规程进行，防止触电或机械伤害。\n2、不要随意拆卸重要部件或调整参数，避免造成二次损坏。\n3、若温度持续快速升高并出现异味、烟雾或异常声响，应立即停机并远离设备。\n4、分析结果仅供辅助参考，最终应以专业人员现场检查结果为准。', '2026-09-03 22:28:21');
INSERT INTO `ai_analysis`
(`id`, `equipment_id`, `work_order_id`, `description`, `analysis_result`, `create_time`)
VALUES (5, 1, NULL, '半小时温度明显升高', '一、可能原因\n1、散热不良：设备风道堵塞、散热风扇故障或散热片积尘，导致热量无法及时排出。\n2、过载运行：设备负载超过额定能力，电流增大，线圈或传动部件产生过多热量。\n3、润滑异常：润滑油不足、变质或润滑脂加注过多，造成摩擦阻力增加并发热。\n4、机械部件故障：轴承磨损、皮带过紧、转子扫膛或齿轮卡阻等，产生异常摩擦热。\n\n二、检查建议\n1、检查散热系统：确认风扇运转正常，清理风道和散热片上的灰尘异物。\n2、测量运行电流：使用钳形电流表测量实际电流，与设备铭牌额定值比较。\n3、检查润滑状态：观察油位、油质，确认润滑脂是否适量，必要时更换指定牌号油品。\n4、检查转动部件：停机断电后手动盘车，感受是否有卡滞、异响，并检查轴承及密封部位。\n5、测量温度分布：使用红外测温仪或热电偶测量外壳、轴承座和散热出风口温度，判断发热集中位置。\n\n三、注意事项\n1、设备高温时切勿直接用皮肤接触外壳，应先停机降温并使用测温工具确认安全。\n2、若温度在短时间内继续快速上升或有异味、异响，应立即切断电源并停机处理。\n3、分析结果仅供辅助参考，最终应以专业人员现场检查结果为准。', '2026-09-03 22:46:54');

-- ----------------------------
-- Table structure for equipment
-- ----------------------------
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `equipment_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备编号',
  `equipment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备名称',
  `equipment_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备类型',
  `manufacturer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生产厂家',
  `purchase_date` date NULL DEFAULT NULL COMMENT '购置日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '正常' COMMENT '设备状态',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '存放位置',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `equipment_no`(`equipment_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of equipment
-- ----------------------------
INSERT INTO `equipment` VALUES (3, 'EQ001', '生产设备A', '生产设备', '测试厂商', '2026-01-01', '正常', '一号车间', 'RBAC测试设备', '2026-09-07 17:37:53', '2026-09-07 17:37:53');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'ADMIN', '管理员', '2026-09-07 00:55:01');
INSERT INTO `sys_role` VALUES (2, 'WORKER', '维修人员', '2026-09-07 00:55:01');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '正常' COMMENT '状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$GmbTQ18R.gZnoTzD4HWgdea87acY6cOP6PcU4hpDR5LetoAQ8sUYm', '系统管理员', '正常', '2026-09-05 21:46:29');
INSERT INTO `sys_user` VALUES (2, 'worker', '$2a$10$G99hMyt6qfpBUcobSDjrf.SzwAlwNoDb4HKfvFn1nDTS3GHQ/MCC6', '维修人员', '正常', '2026-09-07 02:47:18');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2, 2);

-- ----------------------------
-- Table structure for work_order
-- ----------------------------
DROP TABLE IF EXISTS `work_order`;
CREATE TABLE `work_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工单ID',
  `equipment_id` bigint NOT NULL COMMENT '设备ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '工单标题',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '故障描述',
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '待处理' COMMENT '工单状态',
  `assignee` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处理人',
  `result` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处理结果',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备维修工单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of work_order
-- ----------------------------
INSERT INTO `work_order` VALUES (1, 1, '设备运行异响', '设备运行过程中出现异常响声，需要检查', '已完成', '张三', '已检查并处理设备异响，运行恢复正常', '2026-08-28 23:36:57', '2026-08-28 23:36:57');
INSERT INTO `work_order` VALUES (2, 1, '设备温度异常', '设备运行一段时间后温度偏高', '已完成', '李四', '已检查散热系统，设备恢复正常', '2026-08-29 22:42:17', '2026-08-29 22:42:17');
INSERT INTO `work_order` VALUES (3, 1, 'AI辅助分析维修工单', '半小时后温度明显升高', '待处理', NULL, NULL, '2026-09-03 22:28:54', '2026-09-03 22:28:54');

SET FOREIGN_KEY_CHECKS = 1;
