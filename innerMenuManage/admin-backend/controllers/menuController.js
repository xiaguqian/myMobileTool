const asyncHandler = require('express-async-handler');
const Menu = require('../models/Menu');
const response = require('../utils/response');
const { validationResult, body, param, query } = require('express-validator');

const validateMenu = [
  body('menu_code').notEmpty().withMessage('菜单编码不能为空'),
  body('menu_name').notEmpty().withMessage('菜单名称不能为空')
];

const validateId = [
  param('id').isInt({ min: 1 }).withMessage('无效的ID')
];

const getMenus = asyncHandler(async (req, res) => {
  const { page = 1, pageSize = 10, status, keyword } = req.query;
  
  const where = {};
  if (status !== undefined) {
    where.status = parseInt(status);
  }

  if (keyword) {
    const { pool } = require('../config/database');
    const offset = (page - 1) * pageSize;
    
    const [countResult] = await pool.execute(`
      SELECT COUNT(*) as total FROM menus 
      WHERE deleted_at IS NULL 
      AND (menu_code LIKE ? OR menu_name LIKE ? OR permission_code LIKE ?)
    `, [`%${keyword}%`, `%${keyword}%`, `%${keyword}%`]);
    
    const [rows] = await pool.execute(`
      SELECT * FROM menus 
      WHERE deleted_at IS NULL 
      AND (menu_code LIKE ? OR menu_name LIKE ? OR permission_code LIKE ?)
      ORDER BY created_at DESC 
      LIMIT ? OFFSET ?
    `, [`%${keyword}%`, `%${keyword}%`, `%${keyword}%`, parseInt(pageSize), offset]);

    return response.pagination(res, rows, countResult[0].total, parseInt(page), parseInt(pageSize));
  }

  const result = await Menu.findPaginated(parseInt(page), parseInt(pageSize), where, 'created_at DESC');
  response.pagination(res, result.list, result.total, result.page, result.pageSize);
});

const getMenuById = asyncHandler(async (req, res) => {
  const { id } = req.params;
  const menu = await Menu.findById(id);
  
  if (!menu) {
    return response.notFound(res, '菜单不存在');
  }

  response.success(res, menu);
});

const createMenu = asyncHandler(async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return response.badRequest(res, '参数验证失败', errors.array());
  }

  const { 
    menu_code, 
    menu_name, 
    menu_icon, 
    route_url, 
    page_id, 
    permission_code, 
    description, 
    status = 1 
  } = req.body;

  const existing = await Menu.findByCode(menu_code);
  if (existing) {
    return response.badRequest(res, '菜单编码已存在');
  }

  const menu = await Menu.create({
    menu_code,
    menu_name,
    menu_icon,
    route_url,
    page_id,
    permission_code,
    description,
    status
  });

  response.created(res, menu, '菜单创建成功');
});

const updateMenu = asyncHandler(async (req, res) => {
  const { id } = req.params;
  const { 
    menu_code, 
    menu_name, 
    menu_icon, 
    route_url, 
    page_id, 
    permission_code, 
    description, 
    status 
  } = req.body;

  const menu = await Menu.findById(id);
  if (!menu) {
    return response.notFound(res, '菜单不存在');
  }

  if (menu_code && menu_code !== menu.menu_code) {
    const existing = await Menu.findByCode(menu_code);
    if (existing) {
      return response.badRequest(res, '菜单编码已存在');
    }
  }

  const updateData = {};
  if (menu_code !== undefined) updateData.menu_code = menu_code;
  if (menu_name !== undefined) updateData.menu_name = menu_name;
  if (menu_icon !== undefined) updateData.menu_icon = menu_icon;
  if (route_url !== undefined) updateData.route_url = route_url;
  if (page_id !== undefined) updateData.page_id = page_id;
  if (permission_code !== undefined) updateData.permission_code = permission_code;
  if (description !== undefined) updateData.description = description;
  if (status !== undefined) updateData.status = status;

  const updated = await Menu.update(id, updateData);
  response.success(res, updated, '菜单更新成功');
});

const deleteMenu = asyncHandler(async (req, res) => {
  const { id } = req.params;

  const menu = await Menu.findById(id);
  if (!menu) {
    return response.notFound(res, '菜单不存在');
  }

  const { pool } = require('../config/database');
  const [nodes] = await pool.execute(`
    SELECT COUNT(*) as count FROM menu_tree_nodes 
    WHERE menu_id = ? AND deleted_at IS NULL
  `, [id]);

  if (nodes[0].count > 0) {
    return response.badRequest(res, `该菜单已被 ${nodes[0].count} 个菜单树节点引用，无法删除`);
  }

  await Menu.delete(id);
  response.success(res, null, '菜单删除成功');
});

const getAllActiveMenus = asyncHandler(async (req, res) => {
  const menus = await Menu.getAllActiveMenus();
  response.success(res, menus);
});

module.exports = {
  validateMenu,
  validateId,
  getMenus,
  getMenuById,
  createMenu,
  updateMenu,
  deleteMenu,
  getAllActiveMenus
};
