# Titanium Clause Domain

Clause 是保险条款域，负责条款版本、保险责任、费率规则和条款与产品的可引用定义。

## 核心职责

- 维护条款生命周期和版本快照。
- 维护保险责任、责任参数及条款级费率规则。
- 为 Product 和 Policy 提供租户隔离的条款查询接口。

## 边界与依赖

Clause 定义合同责任和费率基础数据；Product 负责产品组合和定价计划；Policy 只保存出单时的条款/责任快照。

## 快速使用

```bash
cd /Users/sunwei/titanium-project/titanium-clause
mvn -q -DskipITs verify
```

本地服务端口为 `8083`，上下文路径为 `/titanium-clause`。
