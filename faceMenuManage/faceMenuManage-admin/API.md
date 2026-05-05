# 专业管理系统 - 管理端接口文档

## 项目概述

管理端采用 Spring Boot + Thymeleaf 前后端不分离架构，用于配置和管理栏位、菜单、页面、组件等元素。

### 技术栈
- Spring Boot 2.7.18
- Thymeleaf
- Spring Data JPA
- Redis
- MySQL 8.0.33
- Lombok
- FastJSON

### 配置信息
- **端口**: 8080
- **数据库**: localhost:3306/face_menu_manage
- **数据库密码**: 871502794
- **Redis**: localhost:6379 (无密码)

---

## 页面路由

| 路径 | 说明 |
|------|------|
| `/` | 首页 |
| `/slot` | 栏位列表 |
| `/slot/add` | 添加栏位 |
| `/slot/edit/{id}` | 编辑栏位 |
| `/menu-tree` | 菜单树列表 |
| `/menu-tree/add` | 添加菜单树 |
| `/menu-tree/edit/{id}` | 编辑菜单树 |
| `/menu` | 菜单列表 |
| `/menu/add` | 添加菜单 |
| `/menu/edit/{id}` | 编辑菜单 |
| `/page` | 页面列表 |
| `/page/add` | 添加页面 |
| `/page/edit/{id}` | 编辑页面 |
| `/component` | 组件列表 |
| `/component/add` | 添加组件 |
| `/component/edit/{id}` | 编辑组件 |
| `/slot-config/menu-tree` | 栏位配置菜单树 |
| `/slot-config/page` | 栏位配置页面 |
| `/page-config/component/{pageId}` | 页面配置组件 |
| `/publish` | 发布页面 |

---

## API 接口

### 1. 栏位管理

#### 1.1 获取栏位列表
- **URL**: `/slot`
- **Method**: GET
- **说明**: 显示所有栏位列表页面

#### 1.2 添加栏位
- **URL**: `/slot/add`
- **Method**: GET/POST
- **说明**: 
  - GET: 显示添加栏位表单页面
  - POST: 提交添加栏位表单
- **Request Body (POST)**:
  ```
  slotCode: 栏位编码（唯一）
  slotName: 栏位名称
  description: 栏位描述
  positions: 位置列表（JSON数组）
  ```
- **示例**:
  ```json
  {
    "slotCode": "header",
    "slotName": "顶部栏位",
    "description": "页面顶部导航栏位",
    "positions": ["top-left", "top-center", "top-right"]
  }
  ```

#### 1.3 编辑栏位
- **URL**: `/slot/edit/{id}`
- **Method**: GET/POST
- **说明**:
  - GET: 显示编辑栏位表单页面
  - POST: 提交编辑栏位表单

#### 1.4 删除栏位
- **URL**: `/slot/delete/{id}`
- **Method**: GET
- **说明**: 删除指定栏位

---

### 2. 菜单树管理

#### 2.1 获取菜单树列表
- **URL**: `/menu-tree`
- **Method**: GET
- **说明**: 显示所有菜单树列表页面

#### 2.2 添加菜单树
- **URL**: `/menu-tree/add`
- **Method**: GET/POST
- **说明**:
  - GET: 显示添加菜单树表单页面
  - POST: 提交添加菜单树表单
- **Request Body (POST)**:
  ```
  treeCode: 菜单树编码（唯一）
  treeName: 菜单树名称
  description: 菜单树描述
  ```

#### 2.3 编辑菜单树
- **URL**: `/menu-tree/edit/{id}`
- **Method**: GET/POST
- **说明**:
  - GET: 显示编辑菜单树表单页面
  - POST: 提交编辑菜单树表单

#### 2.4 删除菜单树
- **URL**: `/menu-tree/delete/{id}`
- **Method**: GET
- **说明**: 删除指定菜单树

---

### 3. 菜单管理

#### 3.1 获取菜单列表
- **URL**: `/menu`
- **Method**: GET
- **说明**: 显示所有菜单列表页面

#### 3.2 添加菜单
- **URL**: `/menu/add`
- **Method**: GET/POST
- **说明**:
  - GET: 显示添加菜单表单页面
  - POST: 提交添加菜单表单
- **Request Body (POST)**:
  ```
  menuCode: 菜单编码（唯一）
  menuName: 菜单名称
  menuUrl: 菜单URL（路由路径）
  menuType: 菜单类型（menu/directory）
  parentId: 父菜单ID（可选）
  menuTreeId: 所属菜单树ID
  sort: 排序号
  icon: 菜单图标
  status: 状态（1启用 0禁用）
  ```

#### 3.3 编辑菜单
- **URL**: `/menu/edit/{id}`
- **Method**: GET/POST
- **说明**:
  - GET: 显示编辑菜单表单页面
  - POST: 提交编辑菜单表单

#### 3.4 删除菜单
- **URL**: `/menu/delete/{id}`
- **Method**: GET
- **说明**: 删除指定菜单

---

### 4. 页面管理

#### 4.1 获取页面列表
- **URL**: `/page`
- **Method**: GET
- **说明**: 显示所有页面列表页面

#### 4.2 添加页面
- **URL**: `/page/add`
- **Method**: GET/POST
- **说明**:
  - GET: 显示添加页面表单页面
  - POST: 提交添加页面表单
- **Request Body (POST)**:
  ```
  pageCode: 页面编码（唯一）
  pageName: 页面名称
  pagePath: 页面路径
  description: 页面描述
  status: 状态（1启用 0禁用）
  ```

#### 4.3 编辑页面
- **URL**: `/page/edit/{id}`
- **Method**: GET/POST
- **说明**:
  - GET: 显示编辑页面表单页面
  - POST: 提交编辑页面表单

#### 4.4 删除页面
- **URL**: `/page/delete/{id}`
- **Method**: GET
- **说明**: 删除指定页面

---

### 5. 组件管理

#### 5.1 获取组件列表
- **URL**: `/component`
- **Method**: GET
- **说明**: 显示所有组件列表页面

#### 5.2 添加组件
- **URL**: `/component/add`
- **Method**: GET/POST
- **说明**:
  - GET: 显示添加组件表单页面
  - POST: 提交添加组件表单
- **Request Body (POST)**:
  ```
  componentCode: 组件编码（唯一）
  componentName: 组件名称
  componentType: 组件类型（Header/Footer/Banner/List/Form等）
  configJson: 组件配置JSON
  description: 组件描述
  status: 状态（1启用 0禁用）
  ```
- **组件类型说明**:
  - **Header**: 头部组件
  - **Footer**: 底部组件
  - **Banner**: 轮播图组件
  - **List**: 列表组件
  - **Form**: 表单组件

#### 5.3 编辑组件
- **URL**: `/component/edit/{id}`
- **Method**: GET/POST
- **说明**:
  - GET: 显示编辑组件表单页面
  - POST: 提交编辑组件表单

#### 5.4 删除组件
- **URL**: `/component/delete/{id}`
- **Method**: GET
- **说明**: 删除指定组件

---

### 6. 栏位配置管理

#### 6.1 配置栏位-菜单树
- **URL**: `/slot-config/menu-tree`
- **Method**: GET/POST
- **说明**:
  - GET: 显示栏位配置菜单树页面
  - POST: 提交栏位-菜单树关联配置
- **Request Body (POST)**:
  ```
  slotId: 栏位ID
  menuTreeIds: 菜单树ID列表（逗号分隔）
  ```

#### 6.2 配置栏位-页面
- **URL**: `/slot-config/page`
- **Method**: GET/POST
- **说明**:
  - GET: 显示栏位配置页面
  - POST: 提交栏位-页面关联配置
- **Request Body (POST)**:
  ```
  slotId: 栏位ID
  pageIds: 页面ID列表（逗号分隔）
  ```

---

### 7. 页面配置管理

#### 7.1 配置页面组件
- **URL**: `/page-config/component/{pageId}`
- **Method**: GET/POST
- **说明**:
  - GET: 显示页面配置组件页面
  - POST: 提交页面-组件关联配置
- **Request Body (POST)**:
  ```
  componentIds: 组件ID列表（按顺序，逗号分隔）
  ```

---

### 8. 发布功能

#### 8.1 发布页面
- **URL**: `/publish`
- **Method**: GET
- **说明**: 显示发布页面，可生成和下载JSON数据文件

#### 8.2 发布生成JSON
- **URL**: `/publish/generate`
- **Method**: POST
- **说明**: 生成发布数据JSON文件

#### 8.3 下载JSON文件
- **URL**: `/publish/download`
- **Method**: GET
- **说明**: 下载生成的JSON文件

#### 8.4 下载ZIP包
- **URL**: `/publish/download-zip`
- **Method**: GET
- **说明**: 下载包含所有数据的ZIP压缩包

---

## 数据发布说明

### 发布后生成的文件结构

```
publish/
├── full_data.json          # 完整数据（所有栏位、菜单树、菜单、页面、组件）
├── full_data.zip           # 完整数据ZIP包
└── slots/
    ├── {slotCode}_data.json     # 单个栏位数据
    └── ...
```

### full_data.json 数据结构

```json
{
  "generateTime": "2024-01-01 00:00:00",
  "version": "1.0",
  "slots": [
    {
      "id": 1,
      "slotCode": "header",
      "slotName": "顶部栏位",
      "description": "页面顶部导航栏位",
      "positions": ["top-left", "top-center"],
      "menuTrees": [
        {
          "id": 1,
          "treeCode": "main-nav",
          "treeName": "主导航",
          "menus": [
            {
              "id": 1,
              "menuCode": "home",
              "menuName": "首页",
              "menuUrl": "/home",
              "children": [...]
            }
          ]
        }
      ],
      "pages": [
        {
          "id": 1,
          "pageCode": "home-page",
          "pageName": "首页",
          "pagePath": "/home",
          "components": [
            {
              "id": 1,
              "componentCode": "header-banner",
              "componentName": "头部横幅",
              "componentType": "Banner",
              "config": {
                "items": [...]
              }
            }
          ]
        }
      ]
    }
  ],
  "allMenus": [...]
}
```

---

## 实体关系图

```
栏位 (Slot)
  ├── 关联菜单树 (SlotMenuTree)
  │       └── 菜单树 (MenuTree)
  │               └── 菜单 (Menu) [树形结构]
  │
  └── 关联页面 (SlotPage)
          └── 页面 (Page)
                  └── 关联组件 (PageComponent)
                          └── 组件 (Component)
```

---

## 启动说明

### 前置条件
1. 已安装 JDK 1.8+
2. 已安装 Maven
3. MySQL 服务已启动
4. Redis 服务已启动

### 数据库初始化
项目使用 JPA 自动建表，首次启动时会自动创建所需的数据库表。

需要提前创建数据库：
```sql
CREATE DATABASE face_menu_manage DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 启动命令
```bash
cd faceMenuManage-admin
mvn spring-boot:run
```

### 访问地址
- 管理端首页: http://localhost:8080/