const asyncHandler = require('express-async-handler');
const MenuTree = require('../models/MenuTree');
const response = require('../utils/response');
const { validationResult, body, param, query } = require('express-validator');

const validateMenuTree = [
  body('tree_code').notEmpty().withMessage('菜单树编码不能为空'),
  body('tree_name').notEmpty().withMessage('菜单树名称不能为空')
];

const validateId = [
  param('id').isInt({ min: 1 }).withMessage('无效的ID')
];

const getMenuTrees = asyncHandler(async (req, res) => {
  const { page = 1, pageSize = 10, status, keyword } = req.query;
  
  const where = {};
  if (status !== undefined) {
    where.status = parseInt(status);
  }

  if (keyword) {
    const { pool } = require('../config/database');
    const offset = (page - 1) * pageSize;
    
    const [countResult] = await pool.execute(`
      SELECT COUNT(*) as total FROM menu_trees 
      WHERE deleted_at IS NULL 
      AND (tree_code LIKE ? OR tree_name LIKE ?)
    `, [`%${keyword}%`, `%${keyword}%`]);
    
    const [rows] = await pool.execute(`
      SELECT * FROM menu_trees 
      WHERE deleted_at IS NULL 
      AND (tree_code LIKE ? OR tree_name LIKE ?)
      ORDER BY created_at DESC 
      LIMIT ? OFFSET ?
    `, [`%${keyword}%`, `%${keyword}%`, parseInt(pageSize), offset]);

    return response.pagination(res, rows, countResult[0].total, parseInt(page), parseInt(pageSize));
  }

  const result = await MenuTree.findPaginated(parseInt(page), parseInt(pageSize), where, 'created_at DESC');
  response.pagination(res, result.list, result.total, result.page, result.pageSize);
});

const getMenuTreeById = asyncHandler(async (req, res) => {
  const { id } = req.params;
  const menuTree = await MenuTree.findById(id);
  
  if (!menuTree) {
    return response.notFound(res, '菜单树不存在');
  }

  const slots = await MenuTree.getAssociatedSlots(id);

  response.success(res, {
    ...menuTree,
    slots
  });
});

const getMenuTreeStructure = asyncHandler(async (req, res) => {
  const { id } = req.params;
  const menuTree = await MenuTree.findById(id);
  
  if (!menuTree) {
    return response.notFound(res, '菜单树不存在');
  }

  const treeStructure = await MenuTree.buildTreeStructure(id);
  response.success(res, treeStructure);
});

const createMenuTree = asyncHandler(async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return response.badRequest(res, '参数验证失败', errors.array());
  }

  const { tree_code, tree_name, description, status = 1 } = req.body;

  const existing = await MenuTree.findByCode(tree_code);
  if (existing) {
    return response.badRequest(res, '菜单树编码已存在');
  }

  const menuTree = await MenuTree.create({
    tree_code,
    tree_name,
    description,
    status
  });

  response.created(res, menuTree, '菜单树创建成功');
});

const updateMenuTree = asyncHandler(async (req, res) => {
  const { id } = req.params;
  const { tree_code, tree_name, description, status } = req.body;

  const menuTree = await MenuTree.findById(id);
  if (!menuTree) {
    return response.notFound(res, '菜单树不存在');
  }

  if (tree_code && tree_code !== menuTree.tree_code) {
    const existing = await MenuTree.findByCode(tree_code);
    if (existing) {
      return response.badRequest(res, '菜单树编码已存在');
    }
  }

  const updateData = {};
  if (tree_code !== undefined) updateData.tree_code = tree_code;
  if (tree_name !== undefined) updateData.tree_name = tree_name;
  if (description !== undefined) updateData.description = description;
  if (status !== undefined) updateData.status = status;

  const updated = await MenuTree.update(id, updateData);
  response.success(res, updated, '菜单树更新成功');
});

const deleteMenuTree = asyncHandler(async (req, res) => {
  const { id } = req.params;

  const menuTree = await MenuTree.findById(id);
  if (!menuTree) {
    return response.notFound(res, '菜单树不存在');
  }

  const { pool } = require('../config/database');
  const [relations] = await pool.execute(`
    SELECT COUNT(*) as count FROM slot_menu_trees 
    WHERE tree_id = ? AND deleted_at IS NULL
  `, [id]);

  if (relations[0].count > 0) {
    return response.badRequest(res, `该菜单树已被 ${relations[0].count} 个栏位关联，无法删除`);
  }

  await pool.execute(`UPDATE menu_tree_nodes SET deleted_at = NOW() WHERE tree_id = ?`, [id]);
  await MenuTree.delete(id);
  response.success(res, null, '菜单树删除成功');
});

const addTreeNode = asyncHandler(async (req, res) => {
  const { treeId, nodeCode, nodeName, parentId, menuId, nodeIcon, sortOrder = 0, nodeType = 1 } = req.body;

  const menuTree = await MenuTree.findById(treeId);
  if (!menuTree) {
    return response.notFound(res, '菜单树不存在');
  }

  if (parentId) {
    const parentNode = await MenuTree.getNodeById(parentId);
    if (!parentNode) {
      return response.notFound(res, '父节点不存在');
    }
  }

  if (menuId) {
    const Menu = require('../models/Menu');
    const menu = await Menu.findById(menuId);
    if (!menu) {
      return response.notFound(res, '菜单不存在');
    }
  }

  const nodeId = await MenuTree.addNode(treeId, {
    nodeCode,
    nodeName,
    parentId: parentId || null,
    menuId: menuId || null,
    nodeIcon,
    sortOrder,
    nodeType
  });

  const node = await MenuTree.getNodeById(nodeId);
  response.created(res, node, '节点添加成功');
});

const updateTreeNode = asyncHandler(async (req, res) => {
  const { nodeId, nodeCode, nodeName, parentId, menuId, nodeIcon, sortOrder, nodeType } = req.body;

  const node = await MenuTree.getNodeById(nodeId);
  if (!node) {
    return response.notFound(res, '节点不存在');
  }

  const updated = await MenuTree.updateNode(nodeId, {
    nodeCode,
    nodeName,
    parentId,
    menuId,
    nodeIcon,
    sortOrder,
    nodeType
  });

  if (!updated) {
    return response.error(res, '更新失败');
  }

  const updatedNode = await MenuTree.getNodeById(nodeId);
  response.success(res, updatedNode, '节点更新成功');
});

const deleteTreeNode = asyncHandler(async (req, res) => {
  const { id } = req.params;

  const node = await MenuTree.getNodeById(id);
  if (!node) {
    return response.notFound(res, '节点不存在');
  }

  await MenuTree.deleteNode(id);
  response.success(res, null, '节点删除成功');
});

const getTreeNodeById = asyncHandler(async (req, res) => {
  const { id } = req.params;
  const node = await MenuTree.getNodeById(id);
  
  if (!node) {
    return response.notFound(res, '节点不存在');
  }

  response.success(res, node);
});

const batchUpdateTreeNodes = asyncHandler(async (req, res) => {
  const { treeId, nodes } = req.body;

  const menuTree = await MenuTree.findById(treeId);
  if (!menuTree) {
    return response.notFound(res, '菜单树不存在');
  }

  for (const node of nodes) {
    if (node.id) {
      await MenuTree.updateNode(node.id, {
        nodeCode: node.nodeCode,
        nodeName: node.nodeName,
        parentId: node.parentId,
        menuId: node.menuId,
        nodeIcon: node.nodeIcon,
        sortOrder: node.sortOrder,
        nodeType: node.nodeType
      });
    } else {
      await MenuTree.addNode(treeId, {
        nodeCode: node.nodeCode,
        nodeName: node.nodeName,
        parentId: node.parentId,
        menuId: node.menuId,
        nodeIcon: node.nodeIcon,
        sortOrder: node.sortOrder,
        nodeType: node.nodeType
      });
    }
  }

  response.success(res, null, '批量更新成功');
});

module.exports = {
  validateMenuTree,
  validateId,
  getMenuTrees,
  getMenuTreeById,
  getMenuTreeStructure,
  createMenuTree,
  updateMenuTree,
  deleteMenuTree,
  addTreeNode,
  updateTreeNode,
  deleteTreeNode,
  getTreeNodeById,
  batchUpdateTreeNodes
};
