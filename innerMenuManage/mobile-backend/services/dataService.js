const fs = require('fs').promises;
const path = require('path');
const { getCache, setCache } = require('../config/redis');
require('dotenv').config();

const USE_PUBLISHED_DATA = process.env.USE_PUBLISHED_DATA === 'true';
const PUBLISHED_DATA_DIR = process.env.PUBLISHED_DATA_DIR || '../admin-backend/output';
const CACHE_TTL = parseInt(process.env.CACHE_TTL) || 300;

let publishedDataCache = null;

const getPublishedData = async () => {
  if (publishedDataCache) {
    return publishedDataCache;
  }

  try {
    const dataDir = path.resolve(__dirname, PUBLISHED_DATA_DIR);
    const files = await fs.readdir(dataDir);
    const jsonFiles = files
      .filter(f => f.endsWith('.json') && f.startsWith('menu_data_'))
      .sort()
      .reverse();

    if (jsonFiles.length === 0) {
      console.warn('没有找到发布的数据文件');
      return null;
    }

    const latestFile = jsonFiles[0];
    const content = await fs.readFile(path.join(dataDir, latestFile), 'utf8');
    publishedDataCache = JSON.parse(content);
    
    console.log(`已加载发布数据: ${latestFile}`);
    return publishedDataCache;
  } catch (err) {
    console.warn('读取发布数据失败:', err.message);
    return null;
  }
};

const getDataFromDB = async (queryType, params) => {
  const { pool } = require('../config/database');
  
  switch (queryType) {
    case 'slotPages': {
      const { slotCode } = params;
      const [rows] = await pool.execute(`
        SELECT 
          p.id, p.page_code, p.page_name, p.route_path, p.description,
          sp.sort_order
        FROM pages p
        JOIN slot_pages sp ON p.id = sp.page_id
        JOIN slots s ON s.id = sp.slot_id
        WHERE s.slot_code = ? 
          AND p.status = 1 
          AND sp.status = 1
          AND p.deleted_at IS NULL
          AND sp.deleted_at IS NULL
          AND s.deleted_at IS NULL
        ORDER BY sp.sort_order ASC
      `, [slotCode]);

      for (const page of rows) {
        const [components] = await pool.execute(`
          SELECT 
            c.id, c.component_code, c.component_name, c.component_type, c.default_config,
            pc.component_config as config, pc.sort_order
          FROM components c
          JOIN page_components pc ON c.id = pc.component_id
          WHERE pc.page_id = ? 
            AND c.status = 1 
            AND pc.status = 1
            AND c.deleted_at IS NULL
            AND pc.deleted_at IS NULL
          ORDER BY pc.sort_order ASC
        `, [page.id]);
        page.components = components;
      }
      return rows;
    }

    case 'slotMenuTrees': {
      const { slotCode } = params;
      const [rows] = await pool.execute(`
        SELECT 
          mt.id, mt.tree_code, mt.tree_name, mt.description,
          smt.sort_order
        FROM menu_trees mt
        JOIN slot_menu_trees smt ON mt.id = smt.tree_id
        JOIN slots s ON s.id = smt.slot_id
        WHERE s.slot_code = ? 
          AND mt.status = 1 
          AND smt.status = 1
          AND mt.deleted_at IS NULL
          AND smt.deleted_at IS NULL
          AND s.deleted_at IS NULL
        ORDER BY smt.sort_order ASC
      `, [slotCode]);

      const buildTree = async (nodes, parentId = null) => {
        const result = [];
        for (const node of nodes) {
          const isParentNull = parentId === null && node.parent_id === null;
          const isParentMatch = parentId !== null && node.parent_id === parentId;
          
          if (isParentNull || isParentMatch) {
            const children = await buildTree(nodes, node.id);
            const treeNode = {
              id: node.id,
              nodeCode: node.node_code,
              nodeName: node.node_name,
              parentId: node.parent_id,
              menuId: node.menu_id,
              nodeIcon: node.node_icon,
              sortOrder: node.sort_order,
              nodeType: node.node_type,
              children: children.length > 0 ? children : undefined
            };

            if (node.menu_id) {
              treeNode.menu = {
                menuCode: node.menu_code,
                menuName: node.menu_name_ref,
                menuIcon: node.menu_icon_ref,
                routeUrl: node.route_url,
                pageId: node.page_id,
                permissionCode: node.permission_code
              };
            }
            result.push(treeNode);
          }
        }
        return result;
      };

      for (const tree of rows) {
        const [nodes] = await pool.execute(`
          SELECT 
            mtn.*,
            m.menu_code,
            m.menu_name as menu_name_ref,
            m.menu_icon as menu_icon_ref,
            m.route_url,
            m.page_id,
            m.permission_code
          FROM menu_tree_nodes mtn
          LEFT JOIN menus m ON mtn.menu_id = m.id
          WHERE mtn.tree_id = ? AND mtn.deleted_at IS NULL
          ORDER BY mtn.parent_id ASC, mtn.sort_order ASC
        `, [tree.id]);
        tree.structure = await buildTree(nodes);
      }
      return rows;
    }

    case 'menusWithPermissions': {
      const { permissionCodes } = params;
      if (!permissionCodes || permissionCodes.length === 0) {
        return [];
      }

      const placeholders = permissionCodes.map(() => '?').join(', ');
      const [rows] = await pool.execute(`
        SELECT 
          id, menu_code, menu_name, menu_icon, route_url, 
          page_id, permission_code, description
        FROM menus 
        WHERE permission_code IN (${placeholders}) 
          AND status = 1 
          AND deleted_at IS NULL
        ORDER BY id ASC
      `, permissionCodes);
      return rows;
    }

    case 'allMenus': {
      const [rows] = await pool.execute(`
        SELECT 
          id, menu_code, menu_name, menu_icon, route_url, 
          page_id, permission_code, description
        FROM menus 
        WHERE status = 1 AND deleted_at IS NULL
        ORDER BY id ASC
      `);
      return rows;
    }

    case 'pageByRoute': {
      const { routePath } = params;
      const [rows] = await pool.execute(`
        SELECT id, page_code, page_name, route_path, description
        FROM pages 
        WHERE route_path = ? AND status = 1 AND deleted_at IS NULL
        LIMIT 1
      `, [routePath]);
      
      if (rows.length === 0) return null;

      const page = rows[0];
      const [components] = await pool.execute(`
        SELECT 
          c.id, c.component_code, c.component_name, c.component_type, c.default_config,
          pc.component_config as config, pc.sort_order
        FROM components c
        JOIN page_components pc ON c.id = pc.component_id
        WHERE pc.page_id = ? 
          AND c.status = 1 
          AND pc.status = 1
          AND c.deleted_at IS NULL
          AND pc.deleted_at IS NULL
        ORDER BY pc.sort_order ASC
      `, [page.id]);
      page.components = components;
      return page;
    }

    default:
      return null;
  }
};

const getPagesBySlotCode = async (slotCode) => {
  const cacheKey = `mobile:slot:pages:${slotCode}`;
  const cached = await getCache(cacheKey);
  if (cached) return cached;

  let data = null;

  if (USE_PUBLISHED_DATA) {
    const publishedData = await getPublishedData();
    if (publishedData && publishedData.slots) {
      const slot = publishedData.slots.find(s => s.slotCode === slotCode);
      if (slot) {
        data = slot.pages || [];
      }
    }
  }

  if (!data) {
    data = await getDataFromDB('slotPages', { slotCode });
  }

  if (data) {
    await setCache(cacheKey, data, CACHE_TTL);
  }
  return data || [];
};

const getMenuTreesBySlotCode = async (slotCode) => {
  const cacheKey = `mobile:slot:menuTrees:${slotCode}`;
  const cached = await getCache(cacheKey);
  if (cached) return cached;

  let data = null;

  if (USE_PUBLISHED_DATA) {
    const publishedData = await getPublishedData();
    if (publishedData && publishedData.slots) {
      const slot = publishedData.slots.find(s => s.slotCode === slotCode);
      if (slot) {
        data = slot.menuTrees || [];
      }
    }
  }

  if (!data) {
    data = await getDataFromDB('slotMenuTrees', { slotCode });
  }

  if (data) {
    await setCache(cacheKey, data, CACHE_TTL);
  }
  return data || [];
};

const getMenusWithPermissions = async (permissionCodes) => {
  const cacheKey = `mobile:menus:permissions:${JSON.stringify(permissionCodes).slice(0, 100)}`;
  const cached = await getCache(cacheKey);
  if (cached) return cached;

  let data = null;

  if (USE_PUBLISHED_DATA) {
    const publishedData = await getPublishedData();
    if (publishedData && publishedData.menus) {
      data = publishedData.menus.filter(m => 
        permissionCodes.includes(m.permissionCode)
      );
    }
  }

  if (!data) {
    data = await getDataFromDB('menusWithPermissions', { permissionCodes });
  }

  if (data) {
    await setCache(cacheKey, data, CACHE_TTL);
  }
  return data || [];
};

const getAllMenus = async () => {
  const cacheKey = 'mobile:menus:all';
  const cached = await getCache(cacheKey);
  if (cached) return cached;

  let data = null;

  if (USE_PUBLISHED_DATA) {
    const publishedData = await getPublishedData();
    if (publishedData && publishedData.menus) {
      data = publishedData.menus;
    }
  }

  if (!data) {
    data = await getDataFromDB('allMenus', {});
  }

  if (data) {
    await setCache(cacheKey, data, CACHE_TTL);
  }
  return data || [];
};

const getPageByRoute = async (routePath) => {
  const cacheKey = `mobile:page:route:${routePath}`;
  const cached = await getCache(cacheKey);
  if (cached) return cached;

  let data = null;

  if (USE_PUBLISHED_DATA) {
    const publishedData = await getPublishedData();
    if (publishedData && publishedData.pages) {
      data = publishedData.pages.find(p => p.routePath === routePath);
    }
  }

  if (!data) {
    data = await getDataFromDB('pageByRoute', { routePath });
  }

  if (data) {
    await setCache(cacheKey, data, CACHE_TTL);
  }
  return data;
};

const refreshPublishedDataCache = async () => {
  publishedDataCache = null;
  return getPublishedData();
};

module.exports = {
  getPagesBySlotCode,
  getMenuTreesBySlotCode,
  getMenusWithPermissions,
  getAllMenus,
  getPageByRoute,
  refreshPublishedDataCache
};
