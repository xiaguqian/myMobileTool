const asyncHandler = require('express-async-handler');
const Slot = require('../models/Slot');
const response = require('../utils/response');
const { validationResult, body, param, query } = require('express-validator');

const validateSlot = [
  body('slot_code').notEmpty().withMessage('栏位号不能为空'),
  body('slot_name').notEmpty().withMessage('栏位名称不能为空')
];

const validateId = [
  param('id').isInt({ min: 1 }).withMessage('无效的ID')
];

const getSlots = asyncHandler(async (req, res) => {
  const { page = 1, pageSize = 10, status, keyword } = req.query;
  
  const where = {};
  if (status !== undefined) {
    where.status = parseInt(status);
  }

  if (keyword) {
    const { pool } = require('../config/database');
    const offset = (page - 1) * pageSize;
    
    const [countResult] = await pool.execute(`
      SELECT COUNT(*) as total FROM slots 
      WHERE deleted_at IS NULL 
      AND (slot_code LIKE ? OR slot_name LIKE ?)
    `, [`%${keyword}%`, `%${keyword}%`]);
    
    const [rows] = await pool.execute(`
      SELECT * FROM slots 
      WHERE deleted_at IS NULL 
      AND (slot_code LIKE ? OR slot_name LIKE ?)
      ORDER BY created_at DESC 
      LIMIT ? OFFSET ?
    `, [`%${keyword}%`, `%${keyword}%`, parseInt(pageSize), offset]);

    return response.pagination(res, rows, countResult[0].total, parseInt(page), parseInt(pageSize));
  }

  const result = await Slot.findPaginated(parseInt(page), parseInt(pageSize), where, 'created_at DESC');
  response.pagination(res, result.list, result.total, result.page, result.pageSize);
});

const getSlotById = asyncHandler(async (req, res) => {
  const { id } = req.params;
  const slot = await Slot.findById(id);
  
  if (!slot) {
    return response.notFound(res, '栏位不存在');
  }

  const pages = await Slot.getAssociatedPages(id);
  const menuTrees = await Slot.getAssociatedMenuTrees(id);

  response.success(res, {
    ...slot,
    pages,
    menuTrees
  });
});

const createSlot = asyncHandler(async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return response.badRequest(res, '参数验证失败', errors.array());
  }

  const { slot_code, slot_name, description, status = 1 } = req.body;

  const existing = await Slot.findByCode(slot_code);
  if (existing) {
    return response.badRequest(res, '栏位号已存在');
  }

  const slot = await Slot.create({
    slot_code,
    slot_name,
    description,
    status
  });

  response.created(res, slot, '栏位创建成功');
});

const updateSlot = asyncHandler(async (req, res) => {
  const { id } = req.params;
  const { slot_code, slot_name, description, status } = req.body;

  const slot = await Slot.findById(id);
  if (!slot) {
    return response.notFound(res, '栏位不存在');
  }

  if (slot_code && slot_code !== slot.slot_code) {
    const existing = await Slot.findByCode(slot_code);
    if (existing) {
      return response.badRequest(res, '栏位号已存在');
    }
  }

  const updateData = {};
  if (slot_code !== undefined) updateData.slot_code = slot_code;
  if (slot_name !== undefined) updateData.slot_name = slot_name;
  if (description !== undefined) updateData.description = description;
  if (status !== undefined) updateData.status = status;

  const updated = await Slot.update(id, updateData);
  response.success(res, updated, '栏位更新成功');
});

const deleteSlot = asyncHandler(async (req, res) => {
  const { id } = req.params;

  const slot = await Slot.findById(id);
  if (!slot) {
    return response.notFound(res, '栏位不存在');
  }

  await Slot.delete(id);
  response.success(res, null, '栏位删除成功');
});

const getAssociatedPages = asyncHandler(async (req, res) => {
  const { id } = req.params;

  const slot = await Slot.findById(id);
  if (!slot) {
    return response.notFound(res, '栏位不存在');
  }

  const pages = await Slot.getAssociatedPages(id);
  response.success(res, pages);
});

const associatePage = asyncHandler(async (req, res) => {
  const { slotId, pageId, sortOrder = 0 } = req.body;

  const slot = await Slot.findById(slotId);
  if (!slot) {
    return response.notFound(res, '栏位不存在');
  }

  const Page = require('../models/Page');
  const page = await Page.findById(pageId);
  if (!page) {
    return response.notFound(res, '页面不存在');
  }

  await Slot.associatePage(slotId, pageId, sortOrder);
  response.success(res, null, '关联成功');
});

const disassociatePage = asyncHandler(async (req, res) => {
  const { slotId, pageId } = req.body;

  await Slot.disassociatePage(slotId, pageId);
  response.success(res, null, '取消关联成功');
});

const getAssociatedMenuTrees = asyncHandler(async (req, res) => {
  const { id } = req.params;

  const slot = await Slot.findById(id);
  if (!slot) {
    return response.notFound(res, '栏位不存在');
  }

  const menuTrees = await Slot.getAssociatedMenuTrees(id);
  response.success(res, menuTrees);
});

const associateMenuTree = asyncHandler(async (req, res) => {
  const { slotId, treeId, sortOrder = 0 } = req.body;

  const slot = await Slot.findById(slotId);
  if (!slot) {
    return response.notFound(res, '栏位不存在');
  }

  const MenuTree = require('../models/MenuTree');
  const menuTree = await MenuTree.findById(treeId);
  if (!menuTree) {
    return response.notFound(res, '菜单树不存在');
  }

  await Slot.associateMenuTree(slotId, treeId, sortOrder);
  response.success(res, null, '关联成功');
});

const disassociateMenuTree = asyncHandler(async (req, res) => {
  const { slotId, treeId } = req.body;

  await Slot.disassociateMenuTree(slotId, treeId);
  response.success(res, null, '取消关联成功');
});

module.exports = {
  validateSlot,
  validateId,
  getSlots,
  getSlotById,
  createSlot,
  updateSlot,
  deleteSlot,
  getAssociatedPages,
  associatePage,
  disassociatePage,
  getAssociatedMenuTrees,
  associateMenuTree,
  disassociateMenuTree
};
