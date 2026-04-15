-- ----------------------------
-- 物流管理模块缺失权限补充SQL
-- 核对 sys_menu.txt 发现缺失的按钮权限
-- 创建时间：2026-04-15
-- ----------------------------

-- ----------------------------
-- 一、客户管理按钮权限（缺失）
-- ----------------------------
INSERT IGNORE INTO sys_menu VALUES (21101, '客户查询', 2101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21102, '客户新增', 2101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21103, '客户修改', 2101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21104, '客户删除', 2101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21105, '客户导出', 2101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:customer:export', '#', 'admin', sysdate(), '', NULL, '');

-- ----------------------------
-- 二、货物管理按钮权限（缺失）
-- ----------------------------
INSERT IGNORE INTO sys_menu VALUES (21106, '货物查询', 2102, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21107, '货物新增', 2102, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21108, '货物修改', 2102, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21109, '货物删除', 2102, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:goods:remove', '#', 'admin', sysdate(), '', NULL, '');
-- 货物导出已存在 (21122)

-- ----------------------------
-- 三、司机管理按钮权限（缺失）
-- ----------------------------
INSERT IGNORE INTO sys_menu VALUES (21110, '司机查询', 2103, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21111, '司机新增', 2103, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21112, '司机修改', 2103, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21113, '司机删除', 2103, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:driver:remove', '#', 'admin', sysdate(), '', NULL, '');
-- 司机导出已存在 (21120)

-- ----------------------------
-- 四、车辆管理按钮权限（缺失）
-- ----------------------------
INSERT IGNORE INTO sys_menu VALUES (21114, '车辆查询', 2104, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21115, '车辆新增', 2104, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21116, '车辆修改', 2104, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21117, '车辆删除', 2104, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:vehicle:remove', '#', 'admin', sysdate(), '', NULL, '');
-- 车辆导出已存在 (21121)

-- ----------------------------
-- 五、订单管理按钮权限（可能缺失）
-- ----------------------------
INSERT IGNORE INTO sys_menu VALUES (21201, '订单查询', 2201, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21202, '订单新增', 2201, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21203, '订单修改', 2201, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21204, '订单删除', 2201, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21205, '订单导出', 2201, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:export', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21206, '订单导入', 2201, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:order:import', '#', 'admin', sysdate(), '', NULL, '');

-- ----------------------------
-- 六、回单管理按钮权限（部分缺失）
-- ----------------------------
INSERT IGNORE INTO sys_menu VALUES (21207, '回单查询', 2202, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21208, '回单上传', 2202, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:upload', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21209, '回单确认', 2202, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:confirm', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21210, '回单删除', 2202, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:receipt:remove', '#', 'admin', sysdate(), '', NULL, '');
-- 回单新增、修改、导出已存在 (21215-21217)

-- ----------------------------
-- 七、发票管理按钮权限（部分缺失）
-- ----------------------------
INSERT IGNORE INTO sys_menu VALUES (21211, '发票批次查询', 2203, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21212, '合并开票', 2203, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:merge', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21213, '发票开具', 2203, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:issue', '#', 'admin', sysdate(), '', NULL, '');
INSERT IGNORE INTO sys_menu VALUES (21214, '发票作废', 2203, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'logistics:invoice:cancel', '#', 'admin', sysdate(), '', NULL, '');
-- 发票删除已存在 (21218)

-- ----------------------------
-- 执行说明
-- ----------------------------
-- 1. 使用 INSERT IGNORE 自动跳过已存在的记录
-- 2. 执行后需要重新登录或清除缓存才能看到新增的权限
-- 3. 需要在角色管理中为相应角色分配新增的权限
-- ----------------------------
