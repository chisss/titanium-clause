# titanium-clause 条款域 - 多 Agent 协作指南

> 版本: V1.0
> 最后更新: 2026-06-23
> 配合根目录 [AGENTS.md](../AGENTS.md) 与本模块 [CLAUDE.md](./CLAUDE.md) 使用，聚焦条款域协作边界

---

## 一、模块定位与边界

条款域是 **DDD + Axon CQRS + 事件驱动**的**基础下游域**，向上游提供条款数据与规则校验能力。

### 谁调用本模块
| 调用方 | 方式 | 入口 | 说明 |
|--------|------|------|------|
| 保单域 titanium-policy | Feign | `ClauseService` → `ClauseClient` | 已实现，调 `getClauseById` / `getClauses` |
| 产品域 titanium-product | Feign（规划） | `ClauseClient` | 规约定位的下游消费者，尚未接入 |

### 对外契约（修改需谨慎，影响上游）
- **Feign 接口**：`titanium-clause-api/.../ClauseClient.java`，服务名 `titanium-clause-service`，路径前缀 `/api/clauses`
- **DTO**：`titanium-clause-api/.../dto/ClauseDTO.java`
- ⚠️ 改 `ClauseClient` 方法签名或 `ClauseDTO` 字段，必须同步通知/检查 titanium-policy 的 `ClauseService`

---

## 二、与其他域的交互点

### 2.1 入站（被调用）
具体 Feign 方法：`createClause` / `updateClause` / `getClauseById` / `getClauses` / `activateClause` / `inactivateClause`。
- 上游真实调用类：`titanium-policy` 的 `com.titanium.policy.application.service.ClauseService`

### 2.2 出站事件（Kafka 发布）
`ClauseProjection` 通过 `KafkaTemplate` 发布事件，topic 常量定义于 `ClauseConstants`：
| 常量 | topic | 发布时机 | 当前消费方 |
|------|-------|---------|-----------|
| `TOPIC_CLAUSE_STATUS_CHANGED` | `clause-status-changed` | 状态变更 / 审批通过 | 暂无外部消费者 |
| `TOPIC_CLAUSE_CREATED` | `clause-created` | （常量已定义，未实际发布） | — |
| `TOPIC_CLAUSE_UPDATED` | `clause-updated` | （常量已定义，未实际发布） | — |
| `TOPIC_CLAUSE_DELETED` | `clause-deleted` | （常量已定义，未实际发布） | — |

> Axon 自身事件总线 topic 为 `clause-events`（见 application.yml `axon.kafka.default-topic`）。

### 2.3 入站事件
本模块**无 `@KafkaListener`**，当前不消费任何外部域事件。

---

## 三、文件锁定建议（高频冲突区）

条款域命令/事件众多，并行开发时以下文件为**高冲突热点**，单写者锁定，避免多 Agent 同时编辑：

| 锁定级别 | 文件 | 原因 |
|---------|------|------|
| 🔴 独占锁 | `titanium-clause-domain/.../aggregate/Clause.java` | 唯一聚合根，20 命令处理器 + 17 事件处理器集中于此，改动牵一发动全身 |
| 🟠 协调锁 | `titanium-clause-domain/.../command/*.java` | 22 个命令，新增/改签名需联动聚合根与应用服务 |
| 🟠 协调锁 | `titanium-clause-domain/.../event/*.java` | 19 个事件，改字段需联动聚合根 EventSourcingHandler 与投影 |
| 🟠 协调锁 | `titanium-clause-infrastructure/.../projection/ClauseProjection.java` | 读模型投影 + Kafka 发布，事件变更必同步 |
| 🟡 注意 | `titanium-clause-application/.../service/ClauseApplicationService.java` | 命令编排集中点 |
| 🟡 对外契约 | `titanium-clause-api/.../ClauseClient.java`、`dto/ClauseDTO.java` | 改动影响上游 policy |

**铁律**：同一时刻 `Clause.java` 只能有一个写者。新增命令/事件时，按「命令 → 聚合根处理器 → 事件 → 事件溯源处理器 → 投影」链路串行推进。

---

## 四、Agent 任务分工建议

| 角色 | 职责范围 | 锁定文件 |
|------|---------|---------|
| Lead | 聚合根 `Clause.java`、状态机与业务规则 | aggregate 独占 |
| Worker-A | 命令与应用服务编排（command + application/service） | command 包 + ClauseApplicationService |
| Worker-B | 事件与读模型投影（event + projection + repository impl） | event 包 + projection + infrastructure/repository |
| Worker-C | **🔴 补全 QueryHandler 与读模型查询链**（见 CLAUDE.md 缺陷1/2） | 新建 query 处理器 + application/query |
| Scout | Web 层、Feign 契约、DTO 一致性核查（含上游 policy 联动） | web + api |

> 建议优先安排 Worker-C：当前查询侧无 `@QueryHandler`、投影 `findById(..., null)` 必失效，是阻断性缺陷。

---

## 五、协作检查清单

### 改聚合根 Clause.java 时，需同步检查/修改：
- [ ] 新增命令 → 在 `command/` 建 record，并在 `Clause` 加 `@CommandHandler`
- [ ] 新增事件 → 在 `event/` 建 record，并在 `Clause` 加 `@EventSourcingHandler`
- [ ] 事件影响读模型 → 同步 `ClauseProjection` 的 `@EventHandler` 与落库逻辑
- [ ] 状态流转变化 → 同步 `ChangeClauseStatusCommand` 的 switch 校验
- [ ] 应用层编排 → 同步 `ClauseApplicationService` 对应方法
- [ ] 暴露给前端/外域 → 同步 Controller、`ClauseClient`、`ClauseDTO`，并核查上游 policy
- [ ] 数据表字段变化 → 同步 `infrastructure/entity/*Entity`、对应 Mapper、`/liquibase` 脚本
- [ ] 测试 → 新增/更新 application、domain、infrastructure 层单元测试

### 改 Kafka 事件 / topic 时：
- [ ] 同步 `ClauseConstants` topic 常量
- [ ] 确认下游消费者（当前无外部消费者，未来 policy/product 接入时需通知）
- [ ] 校验 `spring.json.trusted.packages`（当前 `com.titanium.clause.*`）覆盖事件包路径

### 改对外 Feign 契约时：
- [ ] 同步 `ClauseClient` 与 `ClauseController` 路径/签名一致
- [ ] 核查 titanium-policy `ClauseService` 是否受影响
- [ ] 保持 `ClauseDTO` 字段向后兼容

---

## 六、协作风险提示

- **端口 8083 与 titanium-underwriting 冲突**：多 Agent 联调启动服务前先确认端口占用。
- **读模型与查询侧割裂**：投影写库走 `ClauseRepository`，查询走 `QueryGateway`（且无 handler）。改任一侧务必评估另一侧，避免「写了查不到」。
- **Feign 扫描包配置错误**：`ClauseApplication` 的 `@EnableFeignClients` basePackages 指向不存在的包，新增出站调用前需先修正。

---
*本文档为条款域多 Agent 协作指南，与根 AGENTS.md 配合使用。*
