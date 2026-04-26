# Data Model Tool API 接口文档

## 基础信息

- 服务端口: 8080
- 基础路径: http://localhost:8080
- 认证方式: Shiro Session (登录后获取token, 后续请求需携带Cookie)

## 公共响应格式

```json
{
    "code": 200,
    "message": "success",
    "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码, 200表示成功, 其他表示失败 |
| message | String | 消息 |
| data | Object | 返回数据 |

---

## 一、认证接口 (无需登录)

### 1.1 用户登录

**接口路径**: `POST /api/auth/login`

**功能说明**: 用户登录, 登录成功后返回token

**请求参数**:

```json
{
    "username": "admin",
    "password": "admin123"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**响应示例**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "session-id",
        "username": "admin"
    }
}
```

---

### 1.2 用户登出

**接口路径**: `POST /api/auth/logout`

**功能说明**: 用户登出

**请求参数**: 无

**响应示例**:

```json
{
    "code": 200,
    "message": "success",
    "data": null
}
```

---

### 1.3 用户注册

**接口路径**: `POST /api/auth/register`

**功能说明**: 新用户注册

**请求参数**:

```json
{
    "username": "newuser",
    "password": "123456",
    "nickname": "新用户",
    "email": "user@example.com"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| nickname | String | 否 | 昵称 |
| email | String | 否 | 邮箱 |

**响应示例**:

```json
{
    "code": 200,
    "message": "success",
    "data": null
}
```

---

### 1.4 修改密码

**接口路径**: `POST /api/auth/changePassword`

**功能说明**: 修改用户密码

**请求参数**:

```json
{
    "username": "admin",
    "oldPassword": "admin123",
    "newPassword": "newpassword"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| oldPassword | String | 是 | 原密码 |
| newPassword | String | 是 | 新密码 |

**响应示例**:

```json
{
    "code": 200,
    "message": "success",
    "data": null
}
```

---

## 二、数据源管理接口 (需要登录)

### 2.1 查询数据源列表

**接口路径**: `GET /api/datasource/list`

**功能说明**: 查询所有数据源配置(不包括主数据源)

**请求参数**: 无

**响应示例**:

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "name": "test_db",
            "driverClassName": "com.mysql.cj.jdbc.Driver",
            "jdbcUrl": "jdbc:mysql://localhost:3306/test_db",
            "username": "root",
            "password": null,
            "description": "测试数据库",
            "status": 1
        }
    ]
}
```

---

### 2.2 查询单个数据源

**接口路径**: `GET /api/datasource/{id}`

**功能说明**: 根据ID查询数据源详情

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 数据源ID (路径参数) |

**响应示例**: 同2.1的单个对象

---

### 2.3 创建数据源

**接口路径**: `POST /api/datasource/create`

**功能说明**: 创建新的数据源配置

**请求参数**:

```json
{
    "name": "test_db",
    "driverClassName": "com.mysql.cj.jdbc.Driver",
    "jdbcUrl": "jdbc:mysql://localhost:3306/test_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai",
    "username": "root",
    "password": "",
    "description": "测试数据库",
    "status": 1
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 数据源名称(唯一标识) |
| driverClassName | String | 否 | 驱动类名, 默认com.mysql.cj.jdbc.Driver |
| jdbcUrl | String | 是 | JDBC连接URL |
| username | String | 是 | 数据库用户名 |
| password | String | 否 | 数据库密码 |
| description | String | 否 | 描述 |
| status | Integer | 否 | 状态: 1-启用, 0-禁用, 默认1 |

---

### 2.4 更新数据源

**接口路径**: `POST /api/datasource/update`

**功能说明**: 更新数据源配置

**请求参数**: 同创建接口, 需包含id字段

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 数据源ID |

---

### 2.5 删除数据源

**接口路径**: `POST /api/datasource/delete/{id}`

**功能说明**: 删除数据源配置

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 数据源ID (路径参数) |

---

### 2.6 刷新数据源

**接口路径**: `POST /api/datasource/refresh`

**功能说明**: 刷新所有启用的动态数据源, 使配置变更生效

**请求参数**: 无

---

## 三、模型管理接口 (需要登录)

### 3.1 创建数据表

**接口路径**: `POST /api/model/create`

**功能说明**: 在指定数据源中创建数据表

**请求参数**:

```json
{
    "dataSourceName": "test_db",
    "tableName": "user_info",
    "tableComment": "用户信息表",
    "fields": [
        {
            "fieldName": "id",
            "fieldType": "BIGINT",
            "nullable": false,
            "primaryKey": true,
            "autoIncrement": true,
            "comment": "主键ID"
        },
        {
            "fieldName": "name",
            "fieldType": "VARCHAR",
            "length": 50,
            "nullable": false,
            "comment": "姓名"
        },
        {
            "fieldName": "age",
            "fieldType": "INT",
            "nullable": true,
            "comment": "年龄"
        },
        {
            "fieldName": "salary",
            "fieldType": "DECIMAL",
            "precision": 10,
            "length": 2,
            "nullable": true,
            "comment": "工资"
        },
        {
            "fieldName": "create_time",
            "fieldType": "DATETIME",
            "defaultValue": "CURRENT_TIMESTAMP",
            "comment": "创建时间"
        }
    ]
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dataSourceName | String | 是 | 数据源名称 |
| tableName | String | 是 | 表名 |
| tableComment | String | 否 | 表注释 |
| fields | Array | 是 | 字段列表 |

**字段配置说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| fieldName | String | 是 | 字段名 |
| fieldType | String | 是 | 字段类型(VARCHAR, INT, BIGINT, DECIMAL, DATETIME, TEXT等) |
| length | Integer | 否 | 长度(VARCHAR类型必填, DECIMAL时表示小数位数) |
| precision | Integer | 否 | 精度(DECIMAL类型时表示整数位数) |
| nullable | Boolean | 否 | 是否允许为空, 默认false |
| primaryKey | Boolean | 否 | 是否为主键 |
| autoIncrement | Boolean | 否 | 是否自增 |
| defaultValue | String | 否 | 默认值(CURRENT_TIMESTAMP, NULL, 或具体值) |
| comment | String | 否 | 字段注释 |

---

## 四、数据操作接口 (需要登录)

### 4.1 插入/更新数据

**接口路径**: `POST /api/data/upsert`

**功能说明**: 根据主键判断是插入还是更新数据

**请求参数**:

```json
{
    "dataSourceName": "test_db",
    "tableName": "user_info",
    "primaryKeys": ["id"],
    "dataList": [
        {
            "id": 1,
            "name": "张三",
            "age": 25,
            "salary": 15000.00
        },
        {
            "id": 2,
            "name": "李四",
            "age": 30,
            "salary": 20000.00
        }
    ]
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dataSourceName | String | 是 | 数据源名称 |
| tableName | String | 是 | 表名 |
| primaryKeys | Array | 否 | 主键字段列表, 不传则读取表结构 |
| dataList | Array | 是 | 数据列表 |

---

### 4.2 删除数据

**接口路径**: `POST /api/data/delete`

**功能说明**: 根据主键删除数据

**请求参数**:

```json
{
    "dataSourceName": "test_db",
    "tableName": "user_info",
    "primaryKeys": ["id"],
    "dataList": [
        { "id": 1 },
        { "id": 2 }
    ]
}
```

---

### 4.3 清空表

**接口路径**: `POST /api/data/truncate`

**功能说明**: 清空指定数据表(TRUNCATE)

**请求参数**:

```json
{
    "dataSourceName": "test_db",
    "tableName": "user_info"
}
```

---

## 五、数据规则配置接口 (需要登录)

### 5.1 查询规则列表

**接口路径**: `GET /api/rule/list?dataSourceName={数据源名}&tableName={表名}`

**功能说明**: 查询指定表的所有字段规则配置

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dataSourceName | String | 是 | 数据源名称 |
| tableName | String | 是 | 表名 |

---

### 5.2 查询单个规则

**接口路径**: `GET /api/rule/{id}`

**功能说明**: 根据ID查询规则详情

---

### 5.3 创建规则

**接口路径**: `POST /api/rule/create`

**功能说明**: 为指定字段创建数据生成规则

**请求参数**:

```json
{
    "dataSourceName": "test_db",
    "tableName": "user_info",
    "fieldName": "name",
    "fieldType": "VARCHAR",
    "ruleType": "NAME",
    "ruleConfig": "{}",
    "description": "姓名生成规则"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dataSourceName | String | 是 | 数据源名称 |
| tableName | String | 是 | 表名 |
| fieldName | String | 是 | 字段名 |
| fieldType | String | 否 | 字段类型 |
| ruleType | String | 是 | 规则类型(见下方规则类型说明) |
| ruleConfig | String | 否 | 规则配置(JSON格式) |
| description | String | 否 | 描述 |

---

### 5.4 更新规则

**接口路径**: `POST /api/rule/update`

**功能说明**: 更新规则配置

---

### 5.5 删除规则

**接口路径**: `POST /api/rule/delete/{id}`

**功能说明**: 删除规则

---

### 5.6 批量保存规则

**接口路径**: `POST /api/rule/batchSave`

**功能说明**: 批量保存规则配置, 会先删除该表所有旧规则

---

## 六、规则类型说明

| 规则类型 | 说明 | ruleConfig配置示例 |
|---------|------|---------------------|
| NAME | 生成随机姓名 | `{}` |
| ADDRESS | 生成随机地址 | `{}` |
| PHONE | 生成随机手机号 | `{}` |
| EMAIL | 生成随机邮箱 | `{}` |
| RANDOM_NUMBER | 生成随机数字 | `{"min": 1, "max": 100, "isDecimal": false, "scale": 2}` |
| RANDOM_STRING | 生成随机字符串 | `{"length": 8, "includeUppercase": true, "includeLowercase": true, "includeDigits": true}` |
| SEQUENCE | 生成顺序序号 | `{"start": 1, "step": 1, "prefix": "USER", "padding": 6}` |
| DATE | 生成随机日期 | `{"format": "yyyy-MM-dd", "startDate": "2020-01-01", "endDate": "2025-12-31"}` |
| DATETIME | 生成随机日期时间 | `{"format": "yyyy-MM-dd HH:mm:ss"}` |
| UUID | 生成UUID | `{"removeDash": false, "toUpperCase": false}` |
| FIXED_VALUE | 固定值 | `{"value": "固定内容"}` |
| ENUM_VALUES | 枚举值随机选择 | `{"values": ["男", "女", "未知"]}` |

---

## 七、模拟数据生成接口 (需要登录)

### 7.1 生成模拟数据

**接口路径**: `POST /api/mock/generate`

**功能说明**: 根据配置的规则生成模拟数据并插入数据库

**请求参数**:

```json
{
    "dataSourceName": "test_db",
    "tableName": "user_info",
    "count": 1000,
    "batchSize": 1000
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dataSourceName | String | 是 | 数据源名称 |
| tableName | String | 是 | 表名 |
| count | Integer | 否 | 生成条数, 默认100 |
| batchSize | Integer | 否 | 批量插入大小, 默认1000 |

**响应示例**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "count": 1000,
        "dataSourceName": "test_db",
        "tableName": "user_info"
    }
}
```

---

## 八、默认账号

- 用户名: `admin`
- 密码: `admin123`

---

## 九、使用流程示例

1. **执行initData.sql** 创建主数据库和表
2. **修改application.yml** 中的数据库密码
3. **启动应用**
4. **登录获取token**: POST /api/auth/login
5. **添加数据源**: POST /api/datasource/create
6. **刷新数据源**: POST /api/datasource/refresh
7. **创建表**: POST /api/model/create
8. **配置数据规则**: POST /api/rule/batchSave
9. **生成模拟数据**: POST /api/mock/generate
10. **手动增删改数据**: 使用 /api/data/* 接口
