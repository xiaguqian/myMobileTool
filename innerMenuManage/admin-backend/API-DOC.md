# 千人千面菜单管理系统 - 管理端API文档

## 服务信息
- **服务地址**: http://localhost:3001
- **API基础路径**: http://localhost:3001/api
- **健康检查**: http://localhost:3001/api/health

---

## 通用响应格式

### 成功响应
```json
{
  "code": 200,
  "success": true,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1717584000000
}
```

### 分页响应
```json
{
  "code": 200,
  "success": true,
  "message": "查询成功",
  "data": {
    "list": [ ... ],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 100,
      "totalPages": 10
    }
  },
  "timestamp": 1717584000000
}
```

### 错误响应
```json
{
  "code": 400,
  "success": false,
  "message": "请求参数错误",
  "errors": [ ... ],
  "timestamp": 1717584000000
}
```

---

## 1. 栏位管理 API

### 1.1 获取栏位列表
- **方法**: `GET`
- **路径**: `/api/slots`
- **查询参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | page | integer | 否 | 页码，默认1 |
  | pageSize | integer | 否 | 每页数量，默认10 |
  | status | integer | 否 | 状态筛选：0-禁用，1-启用 |
  | keyword | string | 否 | 关键词搜索（栏位号/名称） |

### 1.2 获取栏位详情
- **方法**: `GET`
- **路径**: `/api/slots/:id`
- **路径参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | id | integer | 是 | 栏位ID |

### 1.3 创建栏位
- **方法**: `POST`
- **路径**: `/api/slots`
- **请求体**:
  ```json
  {
    "slot_code": "HOME_TOP",
    "slot_name": "首页顶部栏位",
    "description": "首页顶部展示区域",
    "status": 1
  }
  ```

### 1.4 更新栏位
- **方法**: `PUT`
- **路径**: `/api/slots/:id`
- **请求体**: 同创建栏位

### 1.5 删除栏位
- **方法**: `DELETE`
- **路径**: `/api/slots/:id`

### 1.6 获取栏位关联的页面
- **方法**: `GET`
- **路径**: `/api/slots/:id/pages`

### 1.7 关联栏位与页面
- **方法**: `POST`
- **路径**: `/api/slots/associate-page`
- **请求体**:
  ```json
  {
    "slotId": 1,
    "pageId": 1,
    "sortOrder": 1
  }
  ```

### 1.8 取消栏位与页面关联
- **方法**: `POST`
- **路径**: `/api/slots/disassociate-page`
- **请求体**:
  ```json
  {
    "slotId": 1,
    "pageId": 1
  }
  ```

### 1.9 获取栏位关联的菜单树
- **方法**: `GET`
- **路径**: `/api/slots/:id/menu-trees`

### 1.10 关联栏位与菜单树
- **方法**: `POST`
- **路径**: `/api/slots/associate-menu-tree`
- **请求体**:
  ```json
  {
    "slotId": 1,
    "treeId": 1,
    "sortOrder": 1
  }
  ```

### 1.11 取消栏位与菜单树关联
- **方法**: `POST`
- **路径**: `/api/slots/disassociate-menu-tree`
- **请求体**:
  ```json
  {
    "slotId": 1,
    "treeId": 1
  }
  ```

---

## 2. 页面管理 API

### 2.1 获取页面列表
- **方法**: `GET`
- **路径**: `/api/pages`
- **查询参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | page | integer | 否 | 页码，默认1 |
  | pageSize | integer | 否 | 每页数量，默认10 |
  | status | integer | 否 | 状态筛选 |
  | keyword | string | 否 | 关键词搜索 |

### 2.2 获取页面详情
- **方法**: `GET`
- **路径**: `/api/pages/:id`

### 2.3 创建页面
- **方法**: `POST`
- **路径**: `/api/pages`
- **请求体**:
  ```json
  {
    "page_code": "HOME_PAGE",
    "page_name": "首页",
    "route_path": "/home",
    "description": "应用首页",
    "status": 1
  }
  ```

### 2.4 更新页面
- **方法**: `PUT`
- **路径**: `/api/pages/:id`

### 2.5 删除页面
- **方法**: `DELETE`
- **路径**: `/api/pages/:id`

### 2.6 获取页面组件
- **方法**: `GET`
- **路径**: `/api/pages/:id/components`

### 2.7 为页面添加组件
- **方法**: `POST`
- **路径**: `/api/pages/add-component`
- **请求体**:
  ```json
  {
    "pageId": 1,
    "componentId": 1,
    "componentConfig": { "autoPlay": true },
    "sortOrder": 1
  }
  ```

### 2.8 移除页面组件
- **方法**: `POST`
- **路径**: `/api/pages/remove-component`
- **请求体**:
  ```json
  {
    "pageId": 1,
    "componentId": 1
  }
  ```

### 2.9 更新组件配置
- **方法**: `PUT`
- **路径**: `/api/pages/update-component-config`
- **请求体**:
  ```json
  {
    "pageId": 1,
    "componentId": 1,
    "componentConfig": { "autoPlay": false }
  }
  ```

### 2.10 批量配置页面组件
- **方法**: `POST`
- **路径**: `/api/pages/batch-configure-components`
- **请求体**:
  ```json
  {
    "pageId": 1,
    "components": [
      {
        "componentId": 1,
        "componentConfig": { ... },
        "sortOrder": 1
      },
      {
        "componentId": 2,
        "componentConfig": { ... },
        "sortOrder": 2
      }
    ]
  }
  ```

---

## 3. 组件管理 API

### 3.1 获取组件类型列表
- **方法**: `GET`
- **路径**: `/api/components/types`

### 3.2 获取组件列表
- **方法**: `GET`
- **路径**: `/api/components`
- **查询参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | page | integer | 否 | 页码 |
  | pageSize | integer | 否 | 每页数量 |
  | status | integer | 否 | 状态筛选 |
  | componentType | string | 否 | 组件类型筛选 |
  | keyword | string | 否 | 关键词搜索 |

### 3.3 获取组件详情
- **方法**: `GET`
- **路径**: `/api/components/:id`

### 3.4 创建组件
- **方法**: `POST`
- **路径**: `/api/components`
- **请求体**:
  ```json
  {
    "component_code": "BANNER_SWIPER",
    "component_name": "轮播图组件",
    "component_type": "banner",
    "default_config": { "autoPlay": true },
    "description": "首页轮播图",
    "status": 1
  }
  ```

### 3.5 更新组件
- **方法**: `PUT`
- **路径**: `/api/components/:id`

### 3.6 删除组件
- **方法**: `DELETE`
- **路径**: `/api/components/:id`

### 3.7 获取使用该组件的页面
- **方法**: `GET`
- **路径**: `/api/components/:id/using-pages`

---

## 4. 菜单管理 API

### 4.1 获取所有启用的菜单
- **方法**: `GET`
- **路径**: `/api/menus/active`

### 4.2 获取菜单列表
- **方法**: `GET`
- **路径**: `/api/menus`
- **查询参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | page | integer | 否 | 页码 |
  | pageSize | integer | 否 | 每页数量 |
  | status | integer | 否 | 状态筛选 |
  | keyword | string | 否 | 关键词搜索 |

### 4.3 获取菜单详情
- **方法**: `GET`
- **路径**: `/api/menus/:id`

### 4.4 创建菜单
- **方法**: `POST`
- **路径**: `/api/menus`
- **请求体**:
  ```json
  {
    "menu_code": "MENU_HOME",
    "menu_name": "首页",
    "menu_icon": "home",
    "route_url": "/home",
    "page_id": 1,
    "permission_code": "view:home",
    "description": "访问首页",
    "status": 1
  }
  ```

### 4.5 更新菜单
- **方法**: `PUT`
- **路径**: `/api/menus/:id`

### 4.6 删除菜单
- **方法**: `DELETE`
- **路径**: `/api/menus/:id`

---

## 5. 菜单树管理 API

### 5.1 获取菜单树列表
- **方法**: `GET`
- **路径**: `/api/menu-trees`
- **查询参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | page | integer | 否 | 页码 |
  | pageSize | integer | 否 | 每页数量 |
  | status | integer | 否 | 状态筛选 |
  | keyword | string | 否 | 关键词搜索 |

### 5.2 获取菜单树详情
- **方法**: `GET`
- **路径**: `/api/menu-trees/:id`

### 5.3 获取菜单树结构
- **方法**: `GET`
- **路径**: `/api/menu-trees/:id/structure`
- **响应示例**:
  ```json
  {
    "code": 200,
    "success": true,
    "data": [
      {
        "id": 1,
        "nodeCode": "ROOT",
        "nodeName": "根节点",
        "nodeType": 1,
        "children": [
          {
            "id": 2,
            "nodeCode": "HOME",
            "nodeName": "首页",
            "nodeType": 2,
            "menuId": 1,
            "menu": {
              "menuCode": "MENU_HOME",
              "menuName": "首页",
              "routeUrl": "/home"
            }
          }
        ]
      }
    ]
  }
  ```

### 5.4 创建菜单树
- **方法**: `POST`
- **路径**: `/api/menu-trees`
- **请求体**:
  ```json
  {
    "tree_code": "MAIN_NAV",
    "tree_name": "主导航菜单树",
    "description": "应用底部主导航",
    "status": 1
  }
  ```

### 5.5 更新菜单树
- **方法**: `PUT`
- **路径**: `/api/menu-trees/:id`

### 5.6 删除菜单树
- **方法**: `DELETE`
- **路径**: `/api/menu-trees/:id`

### 5.7 添加菜单树节点
- **方法**: `POST`
- **路径**: `/api/menu-trees/node`
- **请求体**:
  ```json
  {
    "treeId": 1,
    "nodeCode": "HOME_NODE",
    "nodeName": "首页",
    "parentId": null,
    "menuId": 1,
    "nodeIcon": "home",
    "sortOrder": 1,
    "nodeType": 2
  }
  ```

### 5.8 更新菜单树节点
- **方法**: `PUT`
- **路径**: `/api/menu-trees/node`
- **请求体**:
  ```json
  {
    "nodeId": 1,
    "nodeName": "新名称",
    "sortOrder": 2
  }
  ```

### 5.9 删除菜单树节点
- **方法**: `DELETE`
- **路径**: `/api/menu-trees/node/:id`

### 5.10 获取菜单树节点详情
- **方法**: `GET`
- **路径**: `/api/menu-trees/node/:id`

### 5.11 批量更新菜单树节点
- **方法**: `POST`
- **路径**: `/api/menu-trees/batch-update`
- **请求体**:
  ```json
  {
    "treeId": 1,
    "nodes": [
      { "id": 1, "nodeName": "首页", "sortOrder": 1 },
      { "nodeCode": "NEW_NODE", "nodeName": "新节点", "parentId": 1 }
    ]
  }
  ```

---

## 6. 发布管理 API

### 6.1 发布数据
- **方法**: `POST`
- **路径**: `/api/publish`
- **请求体**:
  ```json
  {
    "description": "首次发布",
    "publishUser": "admin"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 200,
    "success": true,
    "message": "发布成功",
    "data": {
      "version": "v20260505_1717584000000",
      "fileName": "menu_data_v20260505_1717584000000.json",
      "publishTime": "2026-05-05T12:00:00.000Z",
      "downloadUrl": "/api/publish/download/menu_data_v20260505_1717584000000.json"
    }
  }
  ```

### 6.2 获取最新发布记录
- **方法**: `GET`
- **路径**: `/api/publish/latest`

### 6.3 获取发布历史
- **方法**: `GET`
- **路径**: `/api/publish/history`
- **查询参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | page | integer | 否 | 页码 |
  | pageSize | integer | 否 | 每页数量 |

### 6.4 获取所有发布数据
- **方法**: `GET`
- **路径**: `/api/publish/data`

### 6.5 下载发布文件
- **方法**: `GET`
- **路径**: `/api/publish/download/:fileName`

### 6.6 根据版本获取发布记录
- **方法**: `GET`
- **路径**: `/api/publish/version/:version`

---

## 发布数据JSON格式说明

发布后生成的JSON文件包含以下结构：

```json
{
  "version": "v20260505_1717584000000",
  "publishTime": "2026-05-05T12:00:00.000Z",
  "slots": [
    {
      "id": 1,
      "slotCode": "HOME_TOP",
      "slotName": "首页顶部栏位",
      "pages": [
        {
          "id": 1,
          "pageCode": "HOME_PAGE",
          "routePath": "/home",
          "components": [
            {
              "componentCode": "BANNER_SWIPER",
              "componentType": "banner",
              "config": { "autoPlay": true },
              "sortOrder": 1
            }
          ]
        }
      ],
      "menuTrees": [
        {
          "id": 1,
          "treeCode": "MAIN_NAV",
          "structure": [ ... ]
        }
      ]
    }
  ],
  "pages": [ ... ],
  "menus": [ ... ],
  "menuTrees": [ ... ]
}
```
