# titanium-clause 条款域 - 模块开发规约

> 版本: V1.0
> 最后更新: 2026-06-23
> 父规约: 见根目录 [CLAUDE.md](../CLAUDE.md)，本文档仅聚焦条款域，不重复全局通用内容

---

## 一、模块概述

**条款域（titanium-clause）** 是 Titanium 保险核心系统的**基础下游域**，负责保险合同的法律文本与规则载体管理。

### 核心业务职责
- **险种条款**：条款的全生命周期管理（创建 → 审批 → 生效 → 修订 → 归档）
- **规则组件**：保险责任（Coverage）、责任免除（Exclusion）、缴费规则（PremiumRule）、理赔规则（ClaimRule）、合同变更规则（ContractChangeRule）
- **告知与签约**：条款告知书（ClauseNotification）、签约模板（ClauseSignTemplate）
- **审批管理**：条款审批记录（ApprovalRecord），状态机驱动的审批流转

### 域定位
- 作为**基础下游域**，被上游业务域通过 Feign 调用（当前：保单域 titanium-policy；规划：产品域 titanium-product）
- 对外暴露 Feign 接口 `ClauseClient`（服务名 `titanium-clause-service`）
- 通过 Kafka 发布条款状态变更事件，供其他域消费

---

## 二、技术栈与端口

| 项 | 值 |
|----|-----|
| JDK | Amazon Corretto 21（`/Users/sunwei/Library/Java/JavaVirtualMachines/corretto-21.0.4/Contents/Home`） |
| Spring Boot | 4.0.1 |
| Axon Framework | 4.10.0（CQRS + 事件溯源） |
| Kafka | 4.0.1（`localhost:9092`，Axon 默认 topic `clause-events`） |
| 数据库 | MySQL，库名 **`titanium_clause`**（`jdbc:mysql://localhost:3306/titanium_clause`） |
| Redis | `localhost:6379` db0 |
| **服务端口** | **8083** |
| context-path | `/titanium-clause` |
| 应用名 | `titanium-clause-service` |

### ⚠️ 端口冲突注意
条款域端口 **8083 与核保域 titanium-underwriting 相同**。两者**无法在同一台机器同时启动**。本地联调时需：
- 错峰启动，或
- 通过 `--server.port=xxxx` 临时覆盖其中一个服务端口

---

## 三、子模块分层结构

```
titanium-clause/
├── titanium-clause-api/              # 远程接口与DTO（被其他域依赖）
│   ├── ClauseClient.java             # @FeignClient 远程接口
│   ├── dto/ClauseDTO.java
│   └── request/                      # 远程调用入参
├── titanium-clause-common/           # 常量与异常
│   ├── constant/ClauseConstants.java # Kafka topic / 状态 / 错误码常量
│   └── exception/                    # 6个业务异常（ClauseNotFound/Duplicate/...）
├── titanium-clause-domain/           # 领域层（核心）
│   ├── aggregate/Clause.java         # 聚合根
│   ├── command/                      # 22个命令（record）
│   ├── event/                        # 19个事件
│   ├── query/                        # 5个查询（record）
│   ├── entity/                       # 聚合内实体（Coverage/Exclusion/PremiumRule...）
│   ├── valueobject/                  # 值对象（ClauseId/ClauseCode/Version/TimeRange...）
│   ├── repository/                   # 仓储接口
│   └── service/                      # 领域服务（ClauseDomainService/ClauseRuleValidationService）
├── titanium-clause-application/      # 应用层
│   ├── service/ClauseApplicationService.java  # 命令编排
│   ├── query/ClauseQueryService.java          # 查询编排（经 QueryGateway）
│   └── mapper/ClauseMapper.java
├── titanium-clause-infrastructure/   # 基础设施层
│   ├── config/                       # AxonConfig / KafkaConfig / TenantContext
│   ├── entity/                       # 10个数据表映射实体（XxxEntity）
│   ├── mapper/                       # 6个 MapStruct Mapper
│   ├── projection/ClauseProjection.java       # 事件投影 + Kafka发布
│   └── repository/                   # 仓储实现 + jpa/ 子包
├── titanium-clause-web/              # Web层
│   ├── controller/ClauseController.java
│   ├── interceptor/TenantInterceptor.java
│   ├── request/ + response/
└── titanium-clause-bootstrap/        # 启动模块
    └── ClauseApplication.java + application.yml
```

### ⚠️ 无独立 query 子模块
本模块**没有** `titanium-clause-query` 子模块，查询逻辑分散在：
- **调度入口**：`titanium-clause-application/query/ClauseQueryService.java`（通过 Axon `QueryGateway` 派发查询）
- **读模型投影**：`titanium-clause-infrastructure/projection/ClauseProjection.java`（事件 → 读模型落库）
- **🔴 缺陷**：当前**没有任何 `@QueryHandler` 实现**，详见第七节「已知缺陷」

---

## 四、核心领域模型

### 4.1 聚合根 Clause
`domain/aggregate/Clause.java`，**充血模型**，是条款域唯一聚合根。
- **20 个 `@CommandHandler`** 命令处理方法（含构造器形式的 CreateClauseCommand）
- **17 个 `@EventSourcingHandler`** 事件溯源方法
- **业务方法** `validateClaim(ClaimEvent)`：校验理赔事件是否命中条款（状态/时间范围/免责/责任触发）
- 内含状态机：`DRAFT → PENDING_APPROVAL → ACTIVE → INACTIVE/EXPIRED/ARCHIVED`（见 `ChangeClauseStatusCommand` 的 switch 流转校验）

### 4.2 命令清单（22 个，均为 record）
| 生命周期类 | 规则组件类 | 审批类 |
|-----------|-----------|--------|
| CreateClauseCommand | AddCoverageCommand | SubmitClauseForApprovalCommand |
| UpdateClauseCommand | RemoveCoverageCommand | ApproveClauseCommand |
| ChangeClauseStatusCommand | UpdateCoverageCommand | RejectClauseCommand |
| ActivateClauseCommand | AddExclusionCommand | |
| InactivateClauseCommand | RemoveExclusionCommand | |
| ReviseClauseCommand | SetPremiumRuleCommand | |
| ArchiveClauseCommand | SetClaimRuleCommand | |
| | SetContractChangeRuleCommand | |
| | AddNotificationCommand | |
| | SetSignTemplateCommand | |
| | CreateLiabilityCommand ⚠️ | |
| | UpdateLiabilityCommand ⚠️ | |

> ⚠️ `CreateLiabilityCommand` / `UpdateLiabilityCommand` 在聚合根中**无对应 `@CommandHandler`**，属未完成功能。

### 4.3 事件清单（19 个）
ClauseCreatedEvent、ClauseUpdatedEvent、ClauseStatusChangedEvent、ClauseSubmittedForApprovalEvent、ClauseApprovedEvent、ClauseRejectedEvent、ClauseRevisedEvent、ClauseArchivedEvent、CoverageAddedEvent、CoverageRemovedEvent、ExclusionAddedEvent、ExclusionRemovedEvent、PremiumRuleSetEvent、ClaimRuleSetEvent、ContractChangeRuleSetEvent、NotificationAddedEvent、SignTemplateSetEvent、LiabilityCreatedEvent ⚠️、LiabilityUpdatedEvent ⚠️

> ⚠️ `LiabilityCreatedEvent` / `LiabilityUpdatedEvent` 无 `@EventSourcingHandler` 与发布点，属未完成功能。

### 4.4 查询清单（5 个，均为 record）
GetClauseByIdQuery、GetClauseByCodeQuery、GetClausesByStatusQuery、GetClausesByTypeQuery、GetClauseAllQuery

---

## 五、编码规约（本模块实践，继承根 CLAUDE.md）

- **命令/查询用 record**：如 `CreateClauseCommand`、`GetClauseByIdQuery` 均为 record，字段以方法名访问（`command.clauseId()`）
- **命令处理用 `@CommandHandler`**：集中在 `Clause` 聚合根；事件用 `@EventSourcingHandler` 重建状态
- **构造器注入**：`@RequiredArgsConstructor`（如 `ClauseApplicationService`、`ClauseProjection`），禁用字段注入
  - ⚠️ 反例：上游 `titanium-policy` 的 `ClauseService` 用了 `@Resource` 字段注入，本模块新增代码不要效仿
- **MapStruct 转换**：infrastructure 层 6 个 Mapper（`ClauseEntityMapper` 等）做 Entity ↔ Domain 转换
  - ⚠️ `ClauseController.toDTO()` 与 `ClauseRepositoryImpl` 中仍存在手写转换，新增代码应优先走 Mapper
- **中文注释**：类/方法注释统一中文，标识符英文
- **SLF4J 占位符**：`logger.info("处理条款创建事件: {}", event.clauseId())`，禁止字符串拼接
- **充血模型**：业务规则（状态流转、理赔校验）内聚在 `Clause` 聚合根，而非 Service
- **多租户**：所有操作贯穿 `tenantId`，由 `TenantInterceptor` + `TenantContext` 获取 `X-Tenant-ID` 请求头

---

## 六、构建与运行

```bash
# JDK 21
export JAVA_HOME=/Users/sunwei/Library/Java/JavaVirtualMachines/corretto-21.0.4/Contents/Home

# 在项目根目录构建条款域（含依赖模块）
cd /Users/sunwei/titanium-project
mvn -pl titanium-clause -am clean install -DskipTests

# 单独启动条款服务（确认 8083 未被 underwriting 占用）
cd titanium-clause/titanium-clause-bootstrap
mvn spring-boot:run

# 访问基址： http://localhost:8083/titanium-clause/api/clauses
```

依赖前置：MySQL（库 `titanium_clause`）、Kafka（9092）、Redis（6379）需先就绪。

---

## 七、已知缺陷与注意事项（基于代码实况）

1. **🔴 读模型查询断链（最严重）**：`ClauseQueryService` 通过 `queryGateway.query(...)` 派发查询，但**全模块无任何 `@QueryHandler` 实现**。所有读接口（getClauseById/byCode/byStatus/byType/all）会因无处理器而超时或失败。`ClauseProjection` 虽把事件写入 JPA 读模型，但查询侧不经仓储、直连 QueryGateway，二者未打通。
   - 修复方向：新增 QueryHandler（建议建 query 包/处理器），从 `ClauseRepository` 读数据并应答各 Query。

2. **🔴 投影更新失效**：`ClauseProjection` 处理状态类事件时调用 `clauseRepository.findById(event.clauseId(), null)`（tenantId 传 null），而 `ClauseRepositoryImpl.findById` 用 `entity.getTenantId().equals(tenantId)` 过滤——传 null 必然返回 false，导致状态变更/审批/归档等读模型**永不更新**。

3. **读模型不完整**：`ClauseRepositoryImpl.toEntity/toDomain` 仅持久化条款主体字段，**未持久化** coverages/exclusions/premiumRule/claimRule 等规则组件；且 `version` 被硬编码为 `"1.0"`，`description` 在回读时被置 null（注释「数据库中没有该字段」）。`clauseType` 被映射进 entity 的 `insuranceType` 字段，命名易混淆。

4. **⚠️ 端口冲突**：8083 与 titanium-underwriting 相同，无法并行启动（见第二节）。

5. **Feign 扫描包错误**：`ClauseApplication` 的 `@EnableFeignClients(basePackages = "com.titanium.clause.infrastructure.client")` 指向**不存在的包**（实际 `ClauseClient` 在 `com.titanium.clause.api`）。条款域作为下游不主动外呼，暂无影响，但属潜在隐患。

6. **未完成功能**：保险责任（Liability）相关的 `CreateLiabilityCommand`/`UpdateLiabilityCommand` 与 `LiabilityCreatedEvent`/`LiabilityUpdatedEvent` 均无处理器，属半成品。

7. **Liquibase 未接入 bootstrap**：SQL 脚本位于模块根 `/liquibase`，但 bootstrap 的 `application.yml` 使用 `jpa.hibernate.ddl-auto: update` 由 Hibernate 自动建表，未配置 `spring.liquibase`。与全局规约「用 Liquibase 管理迁移」不符，需补接入。

---
*本文档为条款域模块级规约，与根 CLAUDE.md 配合使用。*
