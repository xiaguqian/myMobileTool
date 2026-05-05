# 千人千面菜单管理系统 - 移动端API文档

## 服务信息
- **服务地址**: http://localhost:3002
- **API基础路径**: http://localhost:3002/api
- **健康检查**: http://localhost:3002/api/health

## 数据来源说明
移动端后端支持两种数据获取方式：
1. **优先使用发布的JSON文件**（推荐）：通过 `USE_PUBLISHED_DATA=true` 配置，读取管理端发布的静态JSON文件，性能更高
2. **直接查询数据库**：通过 `USE_PUBLISHED_DATA=false` 配置，直接查询数据库，适用于开发调试

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

### 错误响应
```json
{
  "code": 404,
  "success": false,
  "message": "资源不存在",
  "timestamp": 1717584000000
}
```

---

## 1. 根据栏位号获取页面信息

### 获取栏位的页面列表
- **方法**: `GET`
- **路径**: `/api/slots/:slotCode/pages`
- **路径参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | slotCode | string | 是 | 栏位号，如：HOME_TOP, HOME_BOTTOM |

- **响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "page_code": "HOME_PAGE",
      "page_name": "首页",
      "route_path": "/home",
      "description": "应用首页",
      "sort_order": 1,
      "components": [
        {
          "id": 1,
          "component_code": "BANNER_SWIPER",
          "component_name": "轮播图组件",
          "component_type": "banner",
          "default_config": {
            "autoPlay": true,
            "interval": 3000
          },
          "config": {
            "autoPlay": true,
            "interval": 3000,
            "images": [
              "https://example.com/banner1.jpg",
              "https://example.com/banner2.jpg"
            ]
          },
          "sortOrder": 1
        },
        {
          "id": 2,
          "component_code": "GRID_NAV",
          "component_name": "九宫格导航",
          "component_type": "grid",
          "default_config": {
            "columns": 4
          },
          "config": {
            "columns": 4,
            "items": [
              { "name": "商品", "icon": "shopping" },
              { "name": "订单", "icon": "orders" }
            ]
          },
          "sortOrder": 2
        }
      ]
    }
  ],
  "timestamp": 1717584000000
}
```

---

## 2. 根据栏位号获取菜单树

### 获取栏位的菜单树列表
- **方法**: `GET`
- **路径**: `/api/slots/:slotCode/menu-trees`
- **路径参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | slotCode | string | 是 | 栏位号 |

- **响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "tree_code": "MAIN_NAV",
      "tree_name": "主导航菜单树",
      "description": "应用底部主导航",
      "sort_order": 1,
      "structure": [
        {
          "id": 1,
          "nodeCode": "ROOT_MAIN",
          "nodeName": "主导航根节点",
          "parentId": null,
          "menuId": null,
          "nodeIcon": null,
          "sortOrder": 0,
          "nodeType": 1,
          "children": [
            {
              "id": 2,
              "nodeCode": "HOME_NODE",
              "nodeName": "首页",
              "parentId": 1,
              "menuId": 1,
              "nodeIcon": "home",
              "sortOrder": 1,
              "nodeType": 2,
              "menu": {
                "menuCode": "MENU_HOME",
                "menuName": "首页",
                "menuIcon": "home",
                "routeUrl": "/home",
                "pageId": 1,
                "permissionCode": "view:home"
              }
            },
            {
              "id": 3,
              "nodeCode": "PRODUCT_NODE",
              "nodeName": "商品",
              "parentId": 1,
              "menuId": 3,
              "nodeIcon": "shopping",
              "sortOrder": 2,
              "nodeType": 2,
              "menu": {
                "menuCode": "MENU_PRODUCT",
                "menuName": "商品中心",
                "routeUrl": "/product/list",
                "permissionCode": "view:product"
              }
            }
          ]
        }
      ]
    }
  ],
  "timestamp": 1717584000000
}
```

---

## 3. 获取栏位的完整数据（页面+菜单树）

### 获取栏位的完整数据
- **方法**: `GET`
- **路径**: `/api/slots/:slotCode`
- **路径参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | slotCode | string | 是 | 栏位号 |

- **响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "操作成功",
  "data": {
    "slotCode": "HOME_TOP",
    "pages": [ ... ],
    "menuTrees": [ ... ]
  },
  "timestamp": 1717584000000
}
```

---

## 4. 获取有权限的菜单

### 根据权限编码获取菜单
- **方法**: `POST`
- **路径**: `/api/menus/with-permissions`
- **请求体**:
  ```json
  {
    "permissionCodes": ["view:home", "view:product", "view:member"]
  }
  ```

- **响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "menu_code": "MENU_HOME",
      "menu_name": "首页",
      "menu_icon": "home",
      "route_url": "/home",
      "page_id": 1,
      "permission_code": "view:home",
      "description": "访问首页"
    },
    {
      "id": 3,
      "menu_code": "MENU_PRODUCT",
      "menu_name": "商品中心",
      "menu_icon": "shopping",
      "route_url": "/product/list",
      "page_id": 3,
      "permission_code": "view:product",
      "description": "访问商品列表"
    }
  ],
  "timestamp": 1717584000000
}
```

---

## 5. 获取全量菜单

### 获取所有启用的菜单
- **方法**: `GET`
- **路径**: `/api/menus/all`

- **响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "menu_code": "MENU_HOME",
      "menu_name": "首页",
      "menu_icon": "home",
      "route_url": "/home",
      "page_id": 1,
      "permission_code": "view:home",
      "description": "访问首页"
    },
    {
      "id": 2,
      "menu_code": "MENU_MEMBER",
      "menu_name": "会员中心",
      "menu_icon": "user",
      "route_url": "/member",
      "page_id": 2,
      "permission_code": "view:member",
      "description": "访问会员中心"
    }
  ],
  "timestamp": 1717584000000
}
```

---

## 6. 根据路由获取页面详情

### 根据路由路径获取页面
- **方法**: `GET`
- **路径**: `/api/pages/route/:routePath`
- **路径参数**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | routePath | string | 是 | 路由路径，如：/home, /product/list |

- **响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "操作成功",
  "data": {
    "id": 1,
    "page_code": "HOME_PAGE",
    "page_name": "首页",
    "route_path": "/home",
    "description": "应用首页",
    "components": [
      {
        "id": 1,
        "component_code": "BANNER_SWIPER",
        "component_name": "轮播图组件",
        "component_type": "banner",
        "default_config": {
          "autoPlay": true,
          "interval": 3000
        },
        "config": {
          "autoPlay": true,
          "images": [...]
        },
        "sortOrder": 1
      }
    ]
  },
  "timestamp": 1717584000000
}
```

---

## 7. 刷新缓存

### 刷新缓存和重新加载发布数据
- **方法**: `POST`
- **路径**: `/api/cache/refresh`
- **说明**: 当管理端发布了新数据后，可以调用此接口刷新移动端的缓存

- **响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "缓存刷新成功",
  "data": null,
  "timestamp": 1717584000000
}
```

---

## 使用示例

### 1. 移动端启动时加载首页数据
```javascript
// 示例：获取HOME_TOP栏位的页面和菜单树
const slotCode = 'HOME_TOP';

// 获取栏位的完整数据
const response = await fetch(`http://localhost:3002/api/slots/${slotCode}`);
const result = await response.json();

if (result.success) {
  const { pages, menuTrees } = result.data;
  
  // 渲染页面组件
  if (pages.length > 0) {
    const page = pages[0];
    // page.components 包含该页面的所有组件配置
    renderPageComponents(page.components);
  }
  
  // 渲染菜单树
  if (menuTrees.length > 0) {
    renderMenuTree(menuTrees[0].structure);
  }
}
```

### 2. 根据用户权限获取菜单
```javascript
// 示例：根据用户权限获取菜单
const userPermissions = ['view:home', 'view:product'];

const response = await fetch('http://localhost:3002/api/menus/with-permissions', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ permissionCodes: userPermissions })
});

const result = await response.json();
if (result.success) {
  // result.data 包含用户有权限的菜单列表
  const userMenus = result.data;
}
```

### 3. 点击菜单跳转到对应页面
```javascript
// 示例：根据菜单的route_url获取页面详情
const routeUrl = '/product/list';

const response = await fetch(`http://localhost:3002/api/pages/route/${encodeURIComponent(routeUrl)}`);
const result = await response.json();

if (result.success) {
  const pageData = result.data;
  // 动态加载页面组件
  renderPageComponents(pageData.components);
}
```

---

## 缓存策略

移动端后端使用Redis缓存数据，默认缓存时间为300秒（5分钟）。缓存键规则：
- 栏位页面列表: `mobile:slot:pages:{slotCode}`
- 栏位菜单树: `mobile:slot:menuTrees:{slotCode}`
- 全量菜单: `mobile:menus:all`
- 权限菜单: `mobile:menus:permissions:{permissionCodesHash}`

发布新数据后，建议调用 `/api/cache/refresh` 接口刷新缓存。
