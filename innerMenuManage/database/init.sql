-- ============================================
-- 千人千面菜单管理系统 - 数据库初始化脚本
-- 数据库: menu_manage
-- 创建时间: 2026-05-05
-- ============================================

CREATE DATABASE IF NOT EXISTS menu_manage DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE menu_manage;

-- ============================================
-- 1. 栏位表 (slots)
-- 栏位是组件中的固定位置，每个栏位号对应一组菜单树
-- ============================================
CREATE TABLE IF NOT EXISTS slots (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    slot_code VARCHAR(100) NOT NULL UNIQUE COMMENT '栏位号，唯一标识',
    slot_name VARCHAR(200) NOT NULL COMMENT '栏位名称',
    description TEXT COMMENT '栏位描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME DEFAULT NULL COMMENT '软删除时间',
    INDEX idx_slot_code (slot_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='栏位表';

-- ============================================
-- 2. 页面表 (pages)
-- 页面由多个组件按顺序组成
-- ============================================
CREATE TABLE IF NOT EXISTS pages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    page_code VARCHAR(100) NOT NULL UNIQUE COMMENT '页面编码，唯一标识',
    page_name VARCHAR(200) NOT NULL COMMENT '页面名称',
    route_path VARCHAR(200) NOT NULL COMMENT '路由路径',
    description TEXT COMMENT '页面描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME DEFAULT NULL COMMENT '软删除时间',
    INDEX idx_page_code (page_code),
    INDEX idx_route_path (route_path)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面表';

-- ============================================
-- 3. 组件表 (components)
-- 组件是页面的重要组成元素
-- ============================================
CREATE TABLE IF NOT EXISTS components (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    component_code VARCHAR(100) NOT NULL UNIQUE COMMENT '组件编码，唯一标识',
    component_name VARCHAR(200) NOT NULL COMMENT '组件名称',
    component_type VARCHAR(100) NOT NULL COMMENT '组件类型（如：banner、list、form等）',
    default_config JSON COMMENT '组件默认JSON配置',
    description TEXT COMMENT '组件描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME DEFAULT NULL COMMENT '软删除时间',
    INDEX idx_component_code (component_code),
    INDEX idx_component_type (component_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组件表';

-- ============================================
-- 4. 菜单表 (menus)
-- 菜单是操作元素，点击跳转指定页面
-- ============================================
CREATE TABLE IF NOT EXISTS menus (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    menu_code VARCHAR(100) NOT NULL UNIQUE COMMENT '菜单编码，唯一标识',
    menu_name VARCHAR(200) NOT NULL COMMENT '菜单名称',
    menu_icon VARCHAR(200) COMMENT '菜单图标',
    route_url VARCHAR(200) COMMENT '路由URL（跳转路径）',
    page_id BIGINT DEFAULT NULL COMMENT '关联的页面ID（外键）',
    permission_code VARCHAR(200) COMMENT '权限编码',
    description TEXT COMMENT '菜单描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME DEFAULT NULL COMMENT '软删除时间',
    INDEX idx_menu_code (menu_code),
    INDEX idx_page_id (page_id),
    INDEX idx_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ============================================
-- 5. 菜单树表 (menu_trees)
-- 菜单树以树形结构存储菜单信息
-- ============================================
CREATE TABLE IF NOT EXISTS menu_trees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tree_code VARCHAR(100) NOT NULL UNIQUE COMMENT '菜单树编码，唯一标识',
    tree_name VARCHAR(200) NOT NULL COMMENT '菜单树名称',
    description TEXT COMMENT '菜单树描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME DEFAULT NULL COMMENT '软删除时间',
    INDEX idx_tree_code (tree_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单树表';

-- ============================================
-- 6. 菜单树节点表 (menu_tree_nodes)
-- 存储菜单树的节点结构，实现树形关系
-- ============================================
CREATE TABLE IF NOT EXISTS menu_tree_nodes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tree_id BIGINT NOT NULL COMMENT '菜单树ID',
    node_code VARCHAR(100) NOT NULL COMMENT '节点编码',
    node_name VARCHAR(200) NOT NULL COMMENT '节点名称',
    parent_id BIGINT DEFAULT NULL COMMENT '父节点ID，NULL表示根节点',
    menu_id BIGINT DEFAULT NULL COMMENT '关联的菜单ID（叶子节点关联菜单）',
    node_icon VARCHAR(200) COMMENT '节点图标',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    node_type TINYINT DEFAULT 1 COMMENT '节点类型：1-目录，2-菜单',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME DEFAULT NULL COMMENT '软删除时间',
    INDEX idx_tree_id (tree_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_menu_id (menu_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单树节点表';

-- ============================================
-- 7. 页面组件关联表 (page_components)
-- 页面与组件的关联关系，配置页面包含哪些组件及顺序
-- ============================================
CREATE TABLE IF NOT EXISTS page_components (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    page_id BIGINT NOT NULL COMMENT '页面ID',
    component_id BIGINT NOT NULL COMMENT '组件ID',
    component_config JSON COMMENT '该组件在页面中的个性化配置',
    sort_order INT DEFAULT 0 COMMENT '组件在页面中的排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME DEFAULT NULL COMMENT '软删除时间',
    INDEX idx_page_id (page_id),
    INDEX idx_component_id (component_id),
    UNIQUE KEY uk_page_component (page_id, component_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面组件关联表';

-- ============================================
-- 8. 栏位页面关联表 (slot_pages)
-- 栏位与页面的关联关系
-- ============================================
CREATE TABLE IF NOT EXISTS slot_pages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    slot_id BIGINT NOT NULL COMMENT '栏位ID',
    page_id BIGINT NOT NULL COMMENT '页面ID',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME DEFAULT NULL COMMENT '软删除时间',
    INDEX idx_slot_id (slot_id),
    INDEX idx_page_id (page_id),
    UNIQUE KEY uk_slot_page (slot_id, page_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='栏位页面关联表';

-- ============================================
-- 9. 栏位菜单树关联表 (slot_menu_trees)
-- 栏位与菜单树的关联关系，一个栏位可关联多个菜单树
-- ============================================
CREATE TABLE IF NOT EXISTS slot_menu_trees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    slot_id BIGINT NOT NULL COMMENT '栏位ID',
    tree_id BIGINT NOT NULL COMMENT '菜单树ID',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME DEFAULT NULL COMMENT '软删除时间',
    INDEX idx_slot_id (slot_id),
    INDEX idx_tree_id (tree_id),
    UNIQUE KEY uk_slot_tree (slot_id, tree_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='栏位菜单树关联表';

-- ============================================
-- 10. 发布记录表 (publish_records)
-- 记录每次发布操作，生成JSON文件
-- ============================================
CREATE TABLE IF NOT EXISTS publish_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    version VARCHAR(50) NOT NULL COMMENT '发布版本号',
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    publish_user VARCHAR(100) COMMENT '发布人',
    file_name VARCHAR(200) COMMENT '生成的JSON文件名',
    file_path VARCHAR(500) COMMENT '文件路径',
    description TEXT COMMENT '发布描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_version (version),
    INDEX idx_publish_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发布记录表';

-- ============================================
-- 初始化示例数据
-- ============================================

-- 插入示例栏位
INSERT INTO slots (slot_code, slot_name, description, status) VALUES
('HOME_TOP', '首页顶部栏位', '首页顶部展示区域', 1),
('HOME_BOTTOM', '首页底部栏位', '首页底部导航区域', 1),
('MEMBER_CENTER', '会员中心栏位', '会员中心侧边栏', 1);

-- 插入示例页面
INSERT INTO pages (page_code, page_name, route_path, description, status) VALUES
('HOME_PAGE', '首页', '/home', '应用首页', 1),
('MEMBER_PAGE', '会员中心', '/member', '个人中心页面', 1),
('PRODUCT_LIST', '商品列表', '/product/list', '商品列表页', 1),
('ORDER_LIST', '订单列表', '/order/list', '订单列表页', 1);

-- 插入示例组件
INSERT INTO components (component_code, component_name, component_type, default_config, description, status) VALUES
('BANNER_SWIPER', '轮播图组件', 'banner', '{"autoPlay": true, "interval": 3000, "images": []}', '首页轮播图', 1),
('GRID_NAV', '九宫格导航', 'grid', '{"columns": 4, "items": []}', '九宫格导航菜单', 1),
('PRODUCT_LIST_V2', '商品列表组件', 'list', '{"pageSize": 10, "showPrice": true}', '商品列表展示', 1),
('USER_INFO', '用户信息卡片', 'card', '{"showAvatar": true, "showPoints": true}', '用户信息展示', 1);

-- 插入示例菜单
INSERT INTO menus (menu_code, menu_name, menu_icon, route_url, page_id, permission_code, description, status) VALUES
('MENU_HOME', '首页', 'home', '/home', 1, 'view:home', '访问首页', 1),
('MENU_MEMBER', '会员中心', 'user', '/member', 2, 'view:member', '访问会员中心', 1),
('MENU_PRODUCT', '商品中心', 'shopping', '/product/list', 3, 'view:product', '访问商品列表', 1),
('MENU_ORDER', '订单中心', 'orders', '/order/list', 4, 'view:order', '访问订单列表', 1);

-- 插入示例菜单树
INSERT INTO menu_trees (tree_code, tree_name, description, status) VALUES
('MAIN_NAV', '主导航菜单树', '应用底部主导航', 1),
('MEMBER_SIDEBAR', '会员中心侧边栏', '会员中心侧边导航', 1);

-- 插入示例菜单树节点（主导航树）
INSERT INTO menu_tree_nodes (tree_id, node_code, node_name, parent_id, menu_id, node_icon, sort_order, node_type) VALUES
(1, 'ROOT_MAIN', '主导航根节点', NULL, NULL, NULL, 0, 1),
(1, 'HOME_NODE', '首页', 1, 1, 'home', 1, 2),
(1, 'PRODUCT_NODE', '商品', 1, 3, 'shopping', 2, 2),
(1, 'MEMBER_NODE', '我的', 1, 2, 'user', 3, 2);

-- 插入示例菜单树节点（会员中心侧边栏）
INSERT INTO menu_tree_nodes (tree_id, node_code, node_name, parent_id, menu_id, node_icon, sort_order, node_type) VALUES
(2, 'ROOT_MEMBER', '会员中心根节点', NULL, NULL, NULL, 0, 1),
(2, 'ORDER_GROUP', '订单管理', 3, NULL, 'orders', 1, 1),
(2, 'MY_ORDER', '我的订单', 4, 4, 'order', 1, 2),
(2, 'ACCOUNT_GROUP', '账户管理', 3, NULL, 'user', 2, 1),
(2, 'USER_INFO', '个人信息', 6, 2, 'profile', 1, 2);

-- 关联栏位与菜单树
INSERT INTO slot_menu_trees (slot_id, tree_id, sort_order, status) VALUES
(1, 1, 1, 1),
(3, 2, 1, 1);

-- 配置页面组件（首页）
INSERT INTO page_components (page_id, component_id, component_config, sort_order, status) VALUES
(1, 1, '{"autoPlay": true, "interval": 3000, "images": ["https://example.com/banner1.jpg", "https://example.com/banner2.jpg"]}', 1, 1),
(1, 2, '{"columns": 4, "items": [{"name": "商品", "icon": "shopping"}, {"name": "订单", "icon": "orders"}, {"name": "会员", "icon": "user"}, {"name": "设置", "icon": "setting"}]}', 2, 1);

-- 配置页面组件（会员中心）
INSERT INTO page_components (page_id, component_id, component_config, sort_order, status) VALUES
(2, 4, '{"showAvatar": true, "showPoints": true}', 1, 1);

-- 关联栏位与页面
INSERT INTO slot_pages (slot_id, page_id, sort_order, status) VALUES
(1, 1, 1, 1),
(2, 2, 1, 1);
