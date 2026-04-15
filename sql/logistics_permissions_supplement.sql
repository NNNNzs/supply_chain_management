-- ----------------------------
-- 物流管理模块权限补充SQL
-- 用于补全前端使用但菜单配置中缺失的权限
-- 创建时间：2026-04-15
-- ----------------------------

-- ----------------------------
-- 说明：
-- 1. 使用 INSERT IGNORE 避免重复键错误
-- 2. 菜单ID从 2400 开始，避免与现有ID冲突
-- 3. 执行后需要重新登录或清除缓存
-- ----------------------------

-- ----------------------------
-- 一、提单管理权限（新增菜单）
-- ----------------------------

-- 提单管理菜单
INSERT IGNORE INTO sys_menu VALUES (2204, '提单管理', 2200, 4, 'bill', 'logistics/bill/index', '', '', 1, 0, 'C', '0', '0', 'logistics:bill:list', 'documentation', 'admin', sysdate(), '', NULL, '提单管理菜单');

-- 提单查询权限
INSERT IGNORE INTO sys_menu VALUES (21220, '提单查询', 2204, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:query', '#', 'admin', sysdate(), '', NULL, '');
-- 提单新增权限
INSERT IGNORE INTO sys_menu VALUES (21221, '提单新增', 2204, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:add', '#', 'admin', sysdate(), '', NULL, '');
-- 提单修改权限
INSERT IGNORE INTO sys_menu VALUES (21222, '提单修改', 2204, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:edit', '#', 'admin', sysdate(), '', NULL, '');
-- 提单删除权限
INSERT IGNORE INTO sys_menu VALUES (21223, '提单删除', 2204, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:remove', '#', 'admin', sysdate(), '', NULL, '');
-- 提单导入权限
INSERT IGNORE INTO sys_menu VALUES (21224, '提单导入', 2204, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:import', '#', 'admin', sysdate(), '', NULL, '');
-- 提单导出权限
INSERT IGNORE INTO sys_menu VALUES (21225, '提单导出', 2204, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:bill:export', '#', 'admin', sysdate(), '', NULL, '');

-- ----------------------------
-- 二、配载管理权限（新增菜单）
-- ----------------------------

-- 配载管理菜单
INSERT IGNORE INTO sys_menu VALUES (2205, '配载管理', 2200, 5, 'allocation', 'logistics/allocation/index', '', '', 1, 0, 'C', '0', '0', 'logistics:allocation:list', 'guide', 'admin', sysdate(), '', NULL, '配载管理菜单');

-- 配载查询权限
INSERT IGNORE INTO sys_menu VALUES (21230, '配载查询', 2205, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:allocation:query', '#', 'admin', sysdate(), '', NULL, '');
-- 配载新增权限
INSERT IGNORE INTO sys_menu VALUES (21231, '配载新增', 2205, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:allocation:add', '#', 'admin', sysdate(), '', NULL, '');
-- 配载修改权限
INSERT IGNORE INTO sys_menu VALUES (21232, '配载修改', 2205, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:allocation:edit', '#', 'admin', sysdate(), '', NULL, '');
-- 配载删除权限
INSERT IGNORE INTO sys_menu VALUES (21233, '配载删除', 2205, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:allocation:remove', '#', 'admin', sysdate(), '', NULL, '');
-- 创建运单权限（配载页面创建运单）
INSERT IGNORE INTO sys_menu VALUES (21234, '创建运单', 2205, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:allocation:create', '#', 'admin', sysdate(), '', NULL, '');

-- ----------------------------
-- 三、回单管理补充权限
-- ----------------------------

-- 回单新增权限
INSERT IGNORE INTO sys_menu VALUES (21215, '回单新增', 2202, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:add', '#', 'admin', sysdate(), '', NULL, '');
-- 回单修改权限
INSERT IGNORE INTO sys_menu VALUES (21216, '回单修改', 2202, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:edit', '#', 'admin', sysdate(), '', NULL, '');
-- 回单导出权限
INSERT IGNORE INTO sys_menu VALUES (21217, '回单导出', 2202, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:export', '#', 'admin', sysdate(), '', NULL, '');

-- ----------------------------
-- 四、发票管理补充权限
-- ----------------------------

-- 发票删除权限
INSERT IGNORE INTO sys_menu VALUES (21218, '发票删除', 2203, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:remove', '#', 'admin', sysdate(), '', NULL, '');

-- ----------------------------
-- 五、司机管理补充权限
-- ----------------------------

-- 司机导出权限
INSERT IGNORE INTO sys_menu VALUES (21120, '司机导出', 2103, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:export', '#', 'admin', sysdate(), '', NULL, '');

-- ----------------------------
-- 六、车辆管理补充权限
-- ----------------------------

-- 车辆导出权限
INSERT IGNORE INTO sys_menu VALUES (21121, '车辆导出', 2104, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:export', '#', 'admin', sysdate(), '', NULL, '');

-- ----------------------------
-- 七、货物管理补充权限
-- ----------------------------

-- 货物导出权限
INSERT IGNORE INTO sys_menu VALUES (21122, '货物导出', 2102, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:export', '#', 'admin', sysdate(), '', NULL, '');

-- ----------------------------
-- 执行说明
-- ----------------------------
-- 1. 使用 INSERT IGNORE 自动跳过已存在的记录
-- 2. 执行后需要重新登录或清除缓存才能看到新增的权限
-- 3. 需要在角色管理中为相应角色分配新增的权限
-- 4. 如果使用 Navicat 等工具，建议逐条执行以便查看详细结果
-- ----------------------------
