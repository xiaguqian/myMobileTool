const BaseModel = require('./BaseModel');

class MenuTree extends BaseModel {
  constructor() {
    super('menu_trees', 'id');
  }

  async findByCode(treeCode) {
    return this.findOne({ tree_code: treeCode });
  }

  async getAssociatedSlots(treeId) {
    const { pool } = require('../config/database');
    const [rows] = await pool.execute(`
      SELECT s.*, smt.sort_order, smt.status as relation_status
      FROM slots s
      JOIN slot_menu_trees smt ON s.id = smt.slot_id
      WHERE smt.tree_id = ? AND s.deleted_at IS NULL AND smt.deleted_at IS NULL
      ORDER BY smt.sort_order ASC
    `, [treeId]);
    return rows;
  }

  async getTreeNodes(treeId, parentId = null) {
    const { pool } = require('../config/database');
    
    const [rows] = await pool.execute(`
      SELECT 
        mtn.*,
        m.menu_code,
        m.menu_name,
        m.route_url,
        m.page_id,
        m.permission_code
      FROM menu_tree_nodes mtn
      LEFT JOIN menus m ON mtn.menu_id = m.id
      WHERE mtn.tree_id = ? 
        AND mtn.deleted_at IS NULL
        AND (mtn.parent_id = ? OR (? IS NULL AND mtn.parent_id IS NULL))
      ORDER BY mtn.sort_order ASC
    `, [treeId, parentId, parentId]);
    
    return rows;
  }

  async buildTreeStructure(treeId) {
    const { pool } = require('../config/database');
    
    const [rows] = await pool.execute(`
      SELECT 
        mtn.*,
        m.menu_code,
        m.menu_name as menu_name_ref,
        m.menu_icon as menu_icon_ref,
        m.route_url,
        m.page_id,
        m.permission_code,
        m.status as menu_status
      FROM menu_tree_nodes mtn
      LEFT JOIN menus m ON mtn.menu_id = m.id
      WHERE mtn.tree_id = ? AND mtn.deleted_at IS NULL
      ORDER BY mtn.parent_id ASC, mtn.sort_order ASC
    `, [treeId]);

    const buildTree = (nodes, parentId = null) => {
      const result = [];
      
      for (const node of nodes) {
        const isParentNull = parentId === null && node.parent_id === null;
        const isParentMatch = parentId !== null && node.parent_id === parentId;
        
        if (isParentNull || isParentMatch) {
          const children = buildTree(nodes, node.id);
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
              permissionCode: node.permission_code,
              status: node.menu_status
            };
          }

          result.push(treeNode);
        }
      }
      
      return result;
    };

    return buildTree(rows);
  }

  async addNode(treeId, nodeData) {
    const { pool } = require('../config/database');
    const { nodeCode, nodeName, parentId, menuId, nodeIcon, sortOrder = 0, nodeType = 1 } = nodeData;
    
    const [result] = await pool.execute(`
      INSERT INTO menu_tree_nodes 
      (tree_id, node_code, node_name, parent_id, menu_id, node_icon, sort_order, node_type)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    `, [treeId, nodeCode, nodeName, parentId, menuId, nodeIcon, sortOrder, nodeType]);

    return result.insertId;
  }

  async updateNode(nodeId, nodeData) {
    const { pool } = require('../config/database');
    const { nodeCode, nodeName, parentId, menuId, nodeIcon, sortOrder, nodeType } = nodeData;
    
    const updates = [];
    const values = [];

    if (nodeCode !== undefined) {
      updates.push('node_code = ?');
      values.push(nodeCode);
    }
    if (nodeName !== undefined) {
      updates.push('node_name = ?');
      values.push(nodeName);
    }
    if (parentId !== undefined) {
      updates.push('parent_id = ?');
      values.push(parentId);
    }
    if (menuId !== undefined) {
      updates.push('menu_id = ?');
      values.push(menuId);
    }
    if (nodeIcon !== undefined) {
      updates.push('node_icon = ?');
      values.push(nodeIcon);
    }
    if (sortOrder !== undefined) {
      updates.push('sort_order = ?');
      values.push(sortOrder);
    }
    if (nodeType !== undefined) {
      updates.push('node_type = ?');
      values.push(nodeType);
    }

    if (updates.length === 0) return false;

    values.push(nodeId);

    const [result] = await pool.execute(`
      UPDATE menu_tree_nodes 
      SET ${updates.join(', ')} 
      WHERE id = ? AND deleted_at IS NULL
    `, values);

    return result.affectedRows > 0;
  }

  async deleteNode(nodeId) {
    const { pool } = require('../config/database');
    
    const [result] = await pool.execute(`
      UPDATE menu_tree_nodes 
      SET deleted_at = NOW() 
      WHERE id = ? OR parent_id = ?
    `, [nodeId, nodeId]);

    return result.affectedRows > 0;
  }

  async getNodeById(nodeId) {
    const { pool } = require('../config/database');
    const [rows] = await pool.execute(`
      SELECT 
        mtn.*,
        m.menu_code,
        m.menu_name as menu_name_ref,
        m.route_url,
        m.page_id,
        m.permission_code
      FROM menu_tree_nodes mtn
      LEFT JOIN menus m ON mtn.menu_id = m.id
      WHERE mtn.id = ? AND mtn.deleted_at IS NULL
    `, [nodeId]);
    return rows[0] || null;
  }
}

module.exports = new MenuTree();
