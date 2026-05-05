const asyncHandler = require('express-async-handler');
const Component = require('../models/Component');
const response = require('../utils/response');
const { validationResult, body, param, query } = require('express-validator');

const validateComponent = [
  body('component_code').notEmpty().withMessage('组件编码不能为空'),
  body('component_name').notEmpty().withMessage('组件名称不能为空'),
  body('component_type').notEmpty().withMessage('组件类型不能为空')
];

const validateId = [
  param('id').isInt({ min: 1 }).withMessage('无效的ID')
];

const getComponents = asyncHandler(async (req, res) => {
  const { page = 1, pageSize = 10, status, keyword, componentType } = req.query;
  
  const where = {};
  if (status !== undefined) {
    where.status = parseInt(status);
  }
  if (componentType) {
    where.component_type = componentType;
  }

  if (keyword) {
    const { pool } = require('../config/database');
    const offset = (page - 1) * pageSize;
    
    let countSql = `SELECT COUNT(*) as total FROM components WHERE deleted_at IS NULL AND (component_code LIKE ? OR component_name LIKE ?)`;
    let listSql = `SELECT * FROM components WHERE deleted_at IS NULL AND (component_code LIKE ? OR component_name LIKE ?)`;
    const values = [`%${keyword}%`, `%${keyword}%`];

    if (componentType) {
      countSql += ` AND component_type = ?`;
      listSql += ` AND component_type = ?`;
      values.push(componentType);
    }
    if (status !== undefined) {
      countSql += ` AND status = ?`;
      listSql += ` AND status = ?`;
      values.push(parseInt(status));
    }

    listSql += ` ORDER BY created_at DESC LIMIT ? OFFSET ?`;
    values.push(parseInt(pageSize), offset);

    const [countResult] = await pool.execute(countSql, values.slice(0, -2));
    const [rows] = await pool.execute(listSql, values);

    return response.pagination(res, rows, countResult[0].total, parseInt(page), parseInt(pageSize));
  }

  const result = await Component.findPaginated(parseInt(page), parseInt(pageSize), where, 'created_at DESC');
  response.pagination(res, result.list, result.total, result.page, result.pageSize);
});

const getComponentById = asyncHandler(async (req, res) => {
  const { id } = req.params;
  const component = await Component.findById(id);
  
  if (!component) {
    return response.notFound(res, '组件不存在');
  }

  response.success(res, component);
});

const getComponentTypes = asyncHandler(async (req, res) => {
  const types = await Component.getAllTypes();
  response.success(res, types);
});

const createComponent = asyncHandler(async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return response.badRequest(res, '参数验证失败', errors.array());
  }

  const { component_code, component_name, component_type, default_config, description, status = 1 } = req.body;

  const existing = await Component.findByCode(component_code);
  if (existing) {
    return response.badRequest(res, '组件编码已存在');
  }

  const component = await Component.create({
    component_code,
    component_name,
    component_type,
    default_config: default_config ? JSON.stringify(default_config) : null,
    description,
    status
  });

  response.created(res, component, '组件创建成功');
});

const updateComponent = asyncHandler(async (req, res) => {
  const { id } = req.params;
  const { component_code, component_name, component_type, default_config, description, status } = req.body;

  const component = await Component.findById(id);
  if (!component) {
    return response.notFound(res, '组件不存在');
  }

  if (component_code && component_code !== component.component_code) {
    const existing = await Component.findByCode(component_code);
    if (existing) {
      return response.badRequest(res, '组件编码已存在');
    }
  }

  const updateData = {};
  if (component_code !== undefined) updateData.component_code = component_code;
  if (component_name !== undefined) updateData.component_name = component_name;
  if (component_type !== undefined) updateData.component_type = component_type;
  if (default_config !== undefined) updateData.default_config = default_config ? JSON.stringify(default_config) : null;
  if (description !== undefined) updateData.description = description;
  if (status !== undefined) updateData.status = status;

  const updated = await Component.update(id, updateData);
  response.success(res, updated, '组件更新成功');
});

const deleteComponent = asyncHandler(async (req, res) => {
  const { id } = req.params;

  const component = await Component.findById(id);
  if (!component) {
    return response.notFound(res, '组件不存在');
  }

  const usingPages = await Component.getPagesUsingComponent(id);
  if (usingPages.length > 0) {
    return response.badRequest(res, `该组件已被 ${usingPages.length} 个页面使用，无法删除`);
  }

  await Component.delete(id);
  response.success(res, null, '组件删除成功');
});

const getPagesUsingComponent = asyncHandler(async (req, res) => {
  const { id } = req.params;

  const component = await Component.findById(id);
  if (!component) {
    return response.notFound(res, '组件不存在');
  }

  const pages = await Component.getPagesUsingComponent(id);
  response.success(res, pages);
});

module.exports = {
  validateComponent,
  validateId,
  getComponents,
  getComponentById,
  getComponentTypes,
  createComponent,
  updateComponent,
  deleteComponent,
  getPagesUsingComponent
};
