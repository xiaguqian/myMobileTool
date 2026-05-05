# 专业管理系统 - 用户端API接口文档

## 项目概述

用户端API采用 Spring Boot 前后端分离架构，提供RESTful接口供前端调用。

### 技术栈
- Spring Boot 2.7.18
- Spring Data JPA
- Redis
- MySQL 8.0.33
- Lombok
- FastJSON
- SpringDoc OpenAPI (Swagger)

### 配置信息
- **端口**: 8081
- **数据库**: localhost:3306/face_menu_manage
- **数据库密码**: 871502794
- **Redis**: localhost:6379 (无密码)
- **Swagger地址**: http://localhost:8081/swagger-ui.html

---

## 统一响应格式

所有接口返回统一的 `ApiResponse` 格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 响应码说明
| 响应码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## API 接口列表

### 1. 根据栏位号获取页面信息

#### 接口信息
- **URL**: `/api/slot/{slotCode}/pages`
- **Method**: GET
- **Content-Type**: application/json
- **标签**: 栏位接口

#### 路径参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| slotCode | String | 是 | 栏位编码 |

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "pageCode": "home-page",
      "pageName": "首页",
      "pagePath": "/home",
      "description": "系统首页",
      "status": 1,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00",
      "components": [
        {
          "id": 1,
          "componentCode": "header-banner",
          "componentName": "头部横幅",
          "componentType": "Banner",
          "config": {
            "items": [
              {
                "title": "欢迎使用系统",
                "description": "专业管理系统",
                "color": "#409eff"
              }
            ]
          }
        }
      ]
    }
  ]
}
```

---

### 2. 根据栏位号获取菜单树

#### 接口信息
- **URL**: `/api/slot/{slotCode}/menu-trees`
- **Method**: GET
- **Content-Type**: application/json
- **标签**: 栏位接口

#### 路径参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| slotCode | String | 是 | 栏位编码 |

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "treeCode": "main-nav",
      "treeName": "主导航",
      "description": "系统主导航菜单树",
      "status": 1,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00",
      "menus": [
        {
          "id": 1,
          "menuCode": "home",
          "menuName": "首页",
          "menuUrl": "/home",
          "menuType": "menu",
          "sort": 1,
          "icon": "HomeFilled",
          "status": 1,
          "children": [
            {
              "id": 2,
              "menuCode": "home-child",
              "menuName": "子菜单",
              "menuUrl": "/home/child",
              "menuType": "menu",
              "sort": 1,
              "icon": "Document",
              "status": 1,
              "children": []
            }
          ]
        }
      ]
    }
  ]
}
```

---

### 3. 根据栏位号获取完整信息

#### 接口信息
- **URL**: `/api/slot/{slotCode}`
- **Method**: GET
- **Content-Type**: application/json
- **标签**: 栏位接口

#### 路径参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| slotCode | String | 是 | 栏位编码 |

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "slotCode": "header",
    "pages": [
      {
        "id": 1,
        "pageCode": "home-page",
        "pageName": "首页",
        "pagePath": "/home",
        "description": "系统首页",
        "status": 1,
        "components": [...]
      }
    ],
    "menuTrees": [
      {
        "id": 1,
        "treeCode": "main-nav",
        "treeName": "主导航",
        "description": "系统主导航菜单树",
        "status": 1,
        "menus": [...]
      }
    ]
  }
}
```

---

### 4. 获取全量菜单

#### 接口信息
- **URL**: `/api/slot/menus/all`
- **Method**: GET
- **Content-Type**: application/json
- **标签**: 栏位接口

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "menuCode": "home",
      "menuName": "首页",
      "menuUrl": "/home",
      "menuType": "menu",
      "parentId": null,
      "menuTreeId": 1,
      "sort": 1,
      "icon": "HomeFilled",
      "status": 1,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00"
    },
    {
      "id": 2,
      "menuCode": "about",
      "menuName": "关于",
      "menuUrl": "/about",
      "menuType": "menu",
      "parentId": null,
      "menuTreeId": 1,
      "sort": 2,
      "icon": "InfoFilled",
      "status": 1,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00"
    }
  ]
}
```

---

## 数据模型说明

### Menu（菜单）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 主键ID |
| menuCode | String | 菜单编码（唯一） |
| menuName | String | 菜单名称 |
| menuUrl | String | 菜单URL（路由路径） |
| menuType | String | 菜单类型（menu/directory） |
| parentId | Long | 父菜单ID |
| menuTreeId | Long | 所属菜单树ID |
| sort | Integer | 排序号 |
| icon | String | 菜单图标 |
| status | Integer | 状态（1启用 0禁用） |
| children | List\<Menu\> | 子菜单列表 |

### MenuTree（菜单树）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 主键ID |
| treeCode | String | 菜单树编码（唯一） |
| treeName | String | 菜单树名称 |
| description | String | 描述 |
| status | Integer | 状态（1启用 0禁用） |
| menus | List\<Menu\> | 菜单列表（树形结构） |

### Page（页面）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 主键ID |
| pageCode | String | 页面编码（唯一） |
| pageName | String | 页面名称 |
| pagePath | String | 页面路径 |
| description | String | 描述 |
| status | Integer | 状态（1启用 0禁用） |
| components | List\<Component\> | 组件列表 |

### Component（组件）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 主键ID |
| componentCode | String | 组件编码（唯一） |
| componentName | String | 组件名称 |
| componentType | String | 组件类型 |
| config | Map | 组件配置（JSON解析） |
| description | String | 描述 |
| status | Integer | 状态（1启用 0禁用） |

### Slot（栏位）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 主键ID |
| slotCode | String | 栏位编码（唯一） |
| slotName | String | 栏位名称 |
| description | String | 描述 |
| positions | List\<String\> | 位置列表 |
| status | Integer | 状态（1启用 0禁用） |

---

## 组件类型及配置说明

### 1. Header（头部组件）

```json
{
  "title": "系统标题",
  "description": "系统描述信息"
}
```

### 2. Footer（底部组件）

```json
{
  "copyright": "© 2024 专业管理系统"
}
```

### 3. Banner（轮播图组件）

```json
{
  "items": [
    {
      "title": "欢迎使用系统",
      "description": "专业管理系统",
      "color": "#409eff"
    },
    {
      "title": "新功能上线",
      "description": "全新体验",
      "color": "#67c23a"
    }
  ]
}
```

### 4. List（列表组件）

```json
{
  "title": "数据列表",
  "columns": [
    { "prop": "name", "label": "名称", "width": 150 },
    { "prop": "value", "label": "值", "width": 200 }
  ],
  "items": [
    { "name": "项目1", "value": "值1" },
    { "name": "项目2", "value": "值2" }
  ]
}
```

### 5. Form（表单组件）

```json
{
  "title": "信息表单",
  "fields": [
    {
      "prop": "name",
      "label": "姓名",
      "type": "input",
      "placeholder": "请输入姓名",
      "defaultValue": ""
    },
    {
      "prop": "gender",
      "label": "性别",
      "type": "select",
      "placeholder": "请选择性别",
      "options": [
        { "label": "男", "value": "male" },
        { "label": "女", "value": "female" }
      ],
      "defaultValue": ""
    },
    {
      "prop": "birthday",
      "label": "生日",
      "type": "date",
      "placeholder": "请选择日期",
      "defaultValue": ""
    },
    {
      "prop": "remark",
      "label": "备注",
      "type": "textarea",
      "placeholder": "请输入备注",
      "defaultValue": ""
    }
  ]
}
```

---

## 跨域配置

API已配置CORS跨域支持，允许以下来源访问：
- `http://localhost:3000` (Vue开发服务器)
- `http://localhost:8080` (管理端)

如需添加其他来源，请修改 `WebConfig.java` 中的 `addCorsMappings` 方法。

---

## Swagger 在线文档

启动服务后访问：
- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/v3/api-docs

---

## 启动说明

### 前置条件
1. 已安装 JDK 1.8+
2. 已安装 Maven
3. MySQL 服务已启动
4. Redis 服务已启动

### 启动命令
```bash
cd faceMenuManage-user-api
mvn spring-boot:run
```

### 访问地址
- API基础地址: http://localhost:8081/api
- Swagger文档: http://localhost:8081/swagger-ui.html

---

## 与管理端关系

1. **共享数据库**: 用户端API与管理端使用同一个数据库 `face_menu_manage`
2. **独立运行**: 两个系统可以独立启动、独立部署，互不影响
3. **数据同步**: 管理端配置的数据，用户端API可以直接读取
4. **发布机制**: 管理端发布后生成的JSON文件，用户端可以选择从静态资源服务器加载或直接从数据库读取

---

## 注意事项

1. 确保MySQL和Redis服务已启动
2. 首次运行前需创建数据库 `face_menu_manage`
3. 管理端和用户端可以同时运行在不同端口（8080和8081）
4. 前端Vue应用通过代理访问API，避免跨域问题