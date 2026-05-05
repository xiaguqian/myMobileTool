const BaseModel = require('./BaseModel');

class Component extends BaseModel {
  constructor() {
    super('components', 'id');
  }

  async findByCode(componentCode) {
    return this.findOne({ component_code: componentCode });
  }

  async findByType(componentType) {
    return this.findAll({ component_type: componentType }, 'sort_order ASC');
  }

  async getAllTypes() {
    const { pool } = require('../config/database');
    const [rows] = await pool.execute(`
      SELECT DISTINCT component_type 
      FROM components 
      WHERE deleted_at IS NULL
      ORDER BY component_type ASC
    `);
    return rows.map(row => row.component_type);
  }

  async getPagesUsingComponent(componentId) {
    const { pool } = require('../config/database');
    const [rows] = await pool.execute(`
      SELECT 
        p.*, 
        pc.id as relation_id,
        pc.component_config,
        pc.sort_order
      FROM pages p
      JOIN page_components pc ON p.id = pc.page_id
      WHERE pc.component_id = ? AND p.deleted_at IS NULL AND pc.deleted_at IS NULL
      ORDER BY pc.sort_order ASC
    `, [componentId]);
    return rows;
  }
}

module.exports = new Component();
