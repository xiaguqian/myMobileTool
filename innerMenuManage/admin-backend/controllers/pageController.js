const asyncHandler = require('express-async-handler');
const Page = require('../models/Page');
const response = require('../utils/response');
const { validationResult, body, param, query } = require('express-validator');

const validatePage = [
  body('page_code').notEmpty().withMessage('页面编码不能为空'),
  body('page_name').notEmpty().withMessage('页面名称不能为空'),
  body('route_path').notEmpty().withMessage('路由路径不能为空')
];

const validateId = [
  param('id').isInt({ min: 1 }).withMessage('无效的ID')
];

const getPages = asyncHandler(async (req, res) => {
  const { page = 1, pageSize = 10, status, keyword } = req.query;
  
  const where = {};
  if (status !== undefined) {
    where.status = parseInt(status);
  }

  if (keyword) {
    const { pool } = require('../config/database');
    const offset = (page - 1) * pageSize;
    
    const [countResult] = await pool.execute(`
      SELECT COUNT(*) as total FROM pages 
      WHERE deleted_at IS NULL 
      AND (page_code LIKE ? OR page_name LIKE ? OR route_path LIKE ?)
    `, [`%${keyword}%`, `%${keyword}%`, `%${keyword}%`]);
    
    const [rows] = await pool.execute(`
      SELECT * FROM pages 
      WHERE deleted_at IS NULL 
      AND (page_code LIKE ? OR page_name LIKE ? OR route_path LIKE ?)
      ORDER BY created_at DESC 
      LIMIT ? OFFSET ?
    `, [`%${keyword}%`, `%${keyword}%`, `%${keyword}%`, parseInt(pageSize), offset]);

    return response.pagination(res, rows, countResult[0].total, parseInt(page), parseInt(pageSize));
  }

  const result = await Page.findPaginated(parseInt(page), parseInt(pageSize), where, 'created_at DESC');
  response.pagination(res, result.list, result.total, result.page, result.pageSize);
});

const getPageById = asyncHandler(async (req, res) => {
  const { id } = req.params;
  const page = await Page.findById(id);
  
  if (!page) {
    return response.notFound(res, '页面不存在');
  }

  const components = await Page.getComponents(id);
  const slots = await Page.getAssociatedSlots(id);

  response.success(res, {
    ...page,
    components,
    slots
  });
});

const createPage = asyncHandler(async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return response.badRequest(res, '参数验证失败', errors.array());
  }

  const { page_code, page_name, route_path, description, status = 1 } = req.body;

  const existing = await Page.findByCode(page_code);
  if (existing) {
    return response.badRequest(res, '页面编码已存在');
  }

  const page = await Page.create({
    page_code,
    page_name,
    route_path,
    description,
    status
  });

  response.created(res, page, '页面创建成功');
});

const updatePage = asyncHandler(async (req, res) => {
  const { id } = req.params;
  const { page_code, page_name, route_path, description, status } = req.body;

  const page = await Page.findById(id);
  if (!page) {
    return response.notFound(res, '页面不存在');
  }

  if (page_code && page_code !== page.page_code) {
    const existing = await Page.findByCode(page_code);
    if (existing) {
      return response.badRequest(res, '页面编码已存在');
    }
  }

  const updateData = {};
  if (page_code !== undefined) updateData.page_code = page_code;
  if (page_name !== undefined) updateData.page_name = page_name;
  if (route_path !== undefined) updateData.route_path = route_path;
  if (description !== undefined) updateData.description = description;
  if (status !== undefined) updateData.status = status;

  const updated = await Page.update(id, updateData);
  response.success(res, updated, '页面更新成功');
});

const deletePage = asyncHandler(async (req, res) => {
  const { id } = req.params;

  const page = await Page.findById(id);
  if (!page) {
    return response.notFound(res, '页面不存在');
  }

  await Page.delete(id);
  response.success(res, null, '页面删除成功');
});

const getPageComponents = asyncHandler(async (req, res) => {
  const { id } = req.params;

  const page = await Page.findById(id);
  if (!page) {
    return response.notFound(res, '页面不存在');
  }

  const components = await Page.getComponents(id);
  response.success(res, components);
});

const addPageComponent = asyncHandler(async (req, res) => {
  const { pageId, componentId, componentConfig, sortOrder = 0 } = req.body;

  const page = await Page.findById(pageId);
  if (!page) {
    return response.notFound(res, '页面不存在');
  }

  const Component = require('../models/Component');
  const component = await Component.findById(componentId);
  if (!component) {
    return response.notFound(res, '组件不存在');
  }

  await Page.addComponent(pageId, componentId, componentConfig, sortOrder);
  response.success(res, null, '组件添加成功');
});

const removePageComponent = asyncHandler(async (req, res) => {
  const { pageId, componentId } = req.body;

  await Page.removeComponent(pageId, componentId);
  response.success(res, null, '组件移除成功');
});

const updateComponentConfig = asyncHandler(async (req, res) => {
  const { pageId, componentId, componentConfig } = req.body;

  const page = await Page.findById(pageId);
  if (!page) {
    return response.notFound(res, '页面不存在');
  }

  await Page.updateComponentConfig(pageId, componentId, componentConfig);
  response.success(res, null, '组件配置更新成功');
});

const batchConfigureComponents = asyncHandler(async (req, res) => {
  const { pageId, components } = req.body;

  const page = await Page.findById(pageId);
  if (!page) {
    return response.notFound(res, '页面不存在');
  }

  for (const item of components) {
    await Page.addComponent(pageId, item.componentId, item.componentConfig, item.sortOrder);
  }

  response.success(res, null, '批量配置成功');
});

module.exports = {
  validatePage,
  validateId,
  getPages,
  getPageById,
  createPage,
  updatePage,
  deletePage,
  getPageComponents,
  addPageComponent,
  removePageComponent,
  updateComponentConfig,
  batchConfigureComponents
};
