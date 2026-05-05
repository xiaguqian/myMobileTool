const BaseModel = require('./BaseModel');

class Menu extends BaseModel {
  constructor() {
    super('menus', 'id');
  }

  async findByCode(menuCode) {
    return this.findOne({ menu_code: menuCode });
  }

  async findByPermissionCode(permissionCode) {
    return this.findOne({ permission_code: permissionCode });
  }

  async findByPageId(pageId) {
    return this.findOne({ page_id: pageId });
  }

  async getMenusWithPermission(permissionCodes = []) {
    const { pool } = require('../config/database');
    
    if (permissionCodes.length === 0) {
      return [];
    }

    const placeholders = permissionCodes.map(() => '?').join(', ');
    const [rows] = await pool.execute(`
      SELECT * FROM menus 
      WHERE permission_code IN (${placeholders}) 
      AND status = 1 
      AND deleted_at IS NULL
      ORDER BY id ASC
    `, permissionCodes);
    return rows;
  }

  async getAllActiveMenus() {
    return this.findAll({ status: 1 }, 'id ASC');
  }
}

module.exports = new Menu();
