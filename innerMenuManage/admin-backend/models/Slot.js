const BaseModel = require('./BaseModel');

class Slot extends BaseModel {
  constructor() {
    super('slots', 'id');
  }

  async findByCode(slotCode) {
    return this.findOne({ slot_code: slotCode });
  }

  async getAssociatedPages(slotId) {
    const { pool } = require('../config/database');
    const [rows] = await pool.execute(`
      SELECT p.*, sp.sort_order, sp.status as relation_status
      FROM pages p
      JOIN slot_pages sp ON p.id = sp.page_id
      WHERE sp.slot_id = ? AND p.deleted_at IS NULL AND sp.deleted_at IS NULL
      ORDER BY sp.sort_order ASC
    `, [slotId]);
    return rows;
  }

  async getAssociatedMenuTrees(slotId) {
    const { pool } = require('../config/database');
    const [rows] = await pool.execute(`
      SELECT mt.*, smt.sort_order, smt.status as relation_status
      FROM menu_trees mt
      JOIN slot_menu_trees smt ON mt.id = smt.tree_id
      WHERE smt.slot_id = ? AND mt.deleted_at IS NULL AND smt.deleted_at IS NULL
      ORDER BY smt.sort_order ASC
    `, [slotId]);
    return rows;
  }

  async associatePage(slotId, pageId, sortOrder = 0) {
    const { pool } = require('../config/database');
    await pool.execute(`
      INSERT INTO slot_pages (slot_id, page_id, sort_order, status)
      VALUES (?, ?, ?, 1)
      ON DUPLICATE KEY UPDATE
        sort_order = VALUES(sort_order),
        status = 1,
        deleted_at = NULL
    `, [slotId, pageId, sortOrder]);
    return true;
  }

  async disassociatePage(slotId, pageId) {
    const { pool } = require('../config/database');
    const [result] = await pool.execute(`
      UPDATE slot_pages SET deleted_at = NOW() 
      WHERE slot_id = ? AND page_id = ?
    `, [slotId, pageId]);
    return result.affectedRows > 0;
  }

  async associateMenuTree(slotId, treeId, sortOrder = 0) {
    const { pool } = require('../config/database');
    await pool.execute(`
      INSERT INTO slot_menu_trees (slot_id, tree_id, sort_order, status)
      VALUES (?, ?, ?, 1)
      ON DUPLICATE KEY UPDATE
        sort_order = VALUES(sort_order),
        status = 1,
        deleted_at = NULL
    `, [slotId, treeId, sortOrder]);
    return true;
  }

  async disassociateMenuTree(slotId, treeId) {
    const { pool } = require('../config/database');
    const [result] = await pool.execute(`
      UPDATE slot_menu_trees SET deleted_at = NOW() 
      WHERE slot_id = ? AND tree_id = ?
    `, [slotId, treeId]);
    return result.affectedRows > 0;
  }
}

module.exports = new Slot();
