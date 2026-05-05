const BaseModel = require('./BaseModel');

class Page extends BaseModel {
  constructor() {
    super('pages', 'id');
  }

  async findByCode(pageCode) {
    return this.findOne({ page_code: pageCode });
  }

  async findByRoute(routePath) {
    return this.findOne({ route_path: routePath });
  }

  async getComponents(pageId) {
    const { pool } = require('../config/database');
    const [rows] = await pool.execute(`
      SELECT 
        c.*, 
        pc.id as relation_id,
        pc.component_config,
        pc.sort_order,
        pc.status as relation_status
      FROM components c
      JOIN page_components pc ON c.id = pc.component_id
      WHERE pc.page_id = ? AND c.deleted_at IS NULL AND pc.deleted_at IS NULL
      ORDER BY pc.sort_order ASC
    `, [pageId]);
    return rows;
  }

  async addComponent(pageId, componentId, componentConfig = null, sortOrder = 0) {
    const { pool } = require('../config/database');
    await pool.execute(`
      INSERT INTO page_components (page_id, component_id, component_config, sort_order, status)
      VALUES (?, ?, ?, ?, 1)
      ON DUPLICATE KEY UPDATE
        component_config = VALUES(component_config),
        sort_order = VALUES(sort_order),
        status = 1,
        deleted_at = NULL
    `, [pageId, componentId, componentConfig ? JSON.stringify(componentConfig) : null, sortOrder]);
    return true;
  }

  async removeComponent(pageId, componentId) {
    const { pool } = require('../config/database');
    const [result] = await pool.execute(`
      UPDATE page_components SET deleted_at = NOW() 
      WHERE page_id = ? AND component_id = ?
    `, [pageId, componentId]);
    return result.affectedRows > 0;
  }

  async updateComponentConfig(pageId, componentId, componentConfig) {
    const { pool } = require('../config/database');
    const [result] = await pool.execute(`
      UPDATE page_components SET component_config = ?
      WHERE page_id = ? AND component_id = ? AND deleted_at IS NULL
    `, [JSON.stringify(componentConfig), pageId, componentId]);
    return result.affectedRows > 0;
  }

  async getAssociatedSlots(pageId) {
    const { pool } = require('../config/database');
    const [rows] = await pool.execute(`
      SELECT s.*, sp.sort_order, sp.status as relation_status
      FROM slots s
      JOIN slot_pages sp ON s.id = sp.slot_id
      WHERE sp.page_id = ? AND s.deleted_at IS NULL AND sp.deleted_at IS NULL
      ORDER BY sp.sort_order ASC
    `, [pageId]);
    return rows;
  }
}

module.exports = new Page();
