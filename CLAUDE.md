# CLAUDE.md

这是 Claude (AI 编程助手) 的项目索引文档，帮助快速了解项目结构和相关文档。

## 项目概述

供应链管理系统，基于若依框架开发。

- **后端**: Spring Boot 4.0.3 + MySQL + Redis
- **前端**: Vue 3 + Element Plus + Vite
- **部署**: GitHub Actions + Docker Compose

## 项目结构

```
supply_chain_management/
├── ruoyi-admin/          # 后端主模块
├── ruoyi-framework/      # 框架核心
├── ruoyi-system/         # 系统模块
├── ruoyi-logistics/      # 物流管理模块
├── ruoyi-generator/      # 代码生成
├── ruoyi-ui/            # 前端项目
├── docs/                # 项目文档
├── .github/             # GitHub Actions 配置
└── docker-compose.yml   # Docker 部署配置
```

## 相关文档

### 项目文档
| 文档 | 说明 |
|------|------|
| [开发规范](docs/development-specs.md) | **开发规范文档（含文档管理、代码规范、Git 工作流、数据库版本管理）** |
| [部署文档](docs/deployment.md) | GitHub Actions 自动化部署、Docker 配置、Nginx 配置 |
| [README.md](README.md) | 项目介绍和本地开发指南 |
| [数据库脚本管理](sql/README.md) | **数据库脚本管理规范（生产环境增量迁移）** |

### 物流管理模块文档
| 文档 | 说明 |
|------|------|
| [需求文档](docs/logistics/01-requirements.md) | **物流管理系统需求规格说明书** |
| [设计文档](docs/logistics/02-design.md) | 物流管理系统技术设计与实现方案 |
| [进度文档](docs/logistics/03-progress.md) | 物流管理系统实现进度跟踪 |
| [物流实现总结](docs/logistics-implementation-summary.md) | 物流管理模块完整实现说明 |
| [数据库脚本指南](docs/logistics-database-guide.md) | 物流数据库表和菜单 SQL 执行指南 |
| [后端代码清单](docs/logistics-backend-checklist.md) | 使用代码生成器生成后端代码的详细步骤 |
| [发票管理流程](docs/logistics/invoice-workflow.md) | 发票管理业务流程图和接口说明 |
| [工作台账](docs/工作台账.md) | 物流运输工作台账原始数据 |

## 开发规范

### 文档管理规范 ⚠️ 重要
- **每次业务发生变化，必须同步更新需求文档**（`docs/logistics/01-requirements.md`）
- 需求文档是业务逻辑的唯一真实来源，必须保持与实际业务一致
- 业务变更包括：新增功能、修改现有功能、删除功能、业务规则调整等
- 更新需求文档时，记录变更日期、版本号、变更内容和变更人

### 后端开发
- 基于 Spring Boot 4.0.3
- 使用 MyBatis Plus
- 遵循 RESTful API 设计

### 前端开发
- Vue 3 Composition API
- Element Plus 组件库
- Pinia 状态管理

### Git 工作流
- `main` 分支用于生产环境
- 推送到 main 分支触发自动部署
- 前后端独立部署

## 部署相关

### 自动部署触发条件
- **前端**: 修改 `ruoyi-ui/` 目录下的文件
- **后端**: 修改 `ruoyi-*/` 或 `pom.xml` 文件

### 服务器环境
- 前端: `/www/wwwroot/zzj.nnnnzs.cn/dist`
- 后端: `/www/wwwroot/zzj.nnnnzs.cn/backend`
- Nginx: 反向代理后端 API 到 `/api/` 路径

详细配置请参考 [部署文档](docs/deployment.md)。

## 物流管理模块

物流管理模块是基于若依框架开发的完整物流运输管理系统，实现了从工作台账到线上系统的全功能。

### 模块结构

```
ruoyi-logistics/
└── src/main/
    ├── java/com/scm/logistics/
    │   ├── domain/        # 实体类（客户、货物、司机、车辆、订单等）
    │   ├── mapper/        # MyBatis Mapper 接口
    │   └── service/       # 服务层
    └── resources/mapper/logistics/  # MyBatis XML 映射文件
```

### 前端结构

```
ruoyi-ui/src/
├── api/logistics/        # 物流模块 API
│   ├── customer.js
│   ├── goods.js
│   ├── driver.js
│   ├── vehicle.js
│   ├── bill.js            # 提单管理
│   ├── allocation.js      # 配载管理
│   ├── order.js
│   ├── orderLog.js        # 订单操作日志（v3.2新增）
│   ├── receipt.js
│   └── invoice.js
└── views/logistics/
    ├── customer/index.vue    # 客户管理
    ├── goods/index.vue       # 货物管理
    ├── driver/index.vue      # 司机管理
    ├── vehicle/index.vue     # 车辆管理
    ├── bill/index.vue        # 提单管理（多货物明细）
    ├── allocation/index.vue  # 配载管理（三栏布局）
    ├── order/index.vue       # 运单管理（列表页）
    ├── order/form.vue        # 运单管理（独立表单页）
    ├── order/detail.vue      # 运单详情页（v3.2新增）
    ├── receipt/index.vue     # 回单管理
    ├── invoice/index.vue     # 发票管理
    └── settlement/           # 财务结算管理
        ├── receivable/       # 应收结算
        ├── payable/          # 应付结算
        └── report/           # 财务报表
```

### 核心功能

- **客户管理**：客户信息维护、信用额度、结算方式
- **货物管理**：货物信息、分类、参考价格
- **司机管理**：司机信息、银行卡信息、常用车辆关联
- **车辆管理**：车辆信息、载重、默认司机关联
- **提单管理**：多货物明细支持、提单号自动生成、货物明细级联操作、状态自动更新
- **配载管理**：智能推荐货物、货物明细级别分配、一键创建运单并派车、载重利用率计算
- **运单管理**：运单创建（独立表单页或配载生成）、运单号自动生成、司机车辆联动、Excel 导入导出
- **订单详情**：独立详情页、完整操作日志时间线展示（v3.2新增）
- **操作日志**：字段级变更追踪、回单状态联动（v3.2新增）
- **回单管理**：回单上传、确认、图片预览、回单编号自动生成、确认回单自动完成订单
- **发票管理**：订单管理合并开票、发票管理新增、发票开具/作废、删除/取消合并自动恢复订单状态
- **财务结算**：应收/应付结算、结算单生成

### 实现进度

| 模块 | 状态 | 完成度 |
|------|------|--------|
| 客户管理 | ✅ 已完成 | 100% |
| 货物管理 | ✅ 已完成 | 100% |
| 司机管理 | ✅ 已完成 | 100% |
| 车辆管理 | ✅ 已完成 | 100% |
| 车队管理 | ✅ 已完成 | 100% |
| 提单管理 | ⚠️ 已废弃 | - |
| 配载管理 | ⚠️ 已废弃 | - |
| 运单管理 | ✅ 已完成 | 100% |
| 回单管理 | ✅ 已完成 | 100% |
| 发票管理 | ✅ 已完成 | 100% |
| 应收结算 | ⚠️ 待实现 | 33% |
| 应付结算 | ⚠️ 待实现 | 33% |
| 财务报表 | ⚠️ 待实现 | 33% |
| **总体进度** | | **78%** |

### 订单号生成规则

**格式**：客户编码 + 连字符 + 日期 + 连字符 + 当日序号(3位)

- 格式：`{客户编码}-{yyyyMMdd}-{序号}`
- 示例：`RMWJ-20260416-001`

### 回单号生成规则

**格式**：HD + 年月日 + 流水号(4位)

- 示例：`HD202604140001`

### 发票批次号生成规则

**格式**：FP + 年月日 + 流水号(4位)

- 示例：`FP202604210001`

### 数据库表

**基础数据表**：
- logistics_customer - 客户信息表
- logistics_goods - 货物信息表
- logistics_fleet - 车队信息表（v3.1新增）
- logistics_driver - 司机信息表（支持个人/车队类型）
- logistics_vehicle - 车辆信息表

**运输业务表**：
- logistics_bill - 提单表（已废弃，保留结构）
- logistics_bill_item - 提单货物明细表（已废弃）
- logistics_bill_order_detail - 提单运单明细表（已废弃）
- logistics_order - 运单表（货运单/派车单）
- logistics_order_goods - 订单货物明细表（v3.0新增）
- logistics_order_log - 订单操作日志表（v3.2新增）
- logistics_receipt - 回单信息表

**财务结算表**：
- logistics_invoice_batch - 发票批次表
- logistics_invoice_detail - 发票批次明细表
- logistics_settlement - 财务结算表
- logistics_settlement_detail - 结算明细表

**SQL 脚本**：
- `sql/logistics.sql` - 完整初始化脚本（仅用于新环境）
- `sql/migrations/` - 增量迁移脚本目录（生产环境使用）
- `sql/migrations/v3.1.0_init_version_tracker.sql` - 版本追踪表初始化（已执行）
- `sql/migrations/v3.2.0_add_order_operation_log.sql` - 订单操作日志表（新增）

**⚠️ 生产环境数据库变更规范**：
- 已创建 `db_version` 表记录所有迁移历史
- 以后所有变更必须使用增量迁移脚本
- 详细规范请参考：[数据库脚本管理](sql/README.md)

**当前数据库版本**：v3.2.0（订单操作日志、回单状态联动）
