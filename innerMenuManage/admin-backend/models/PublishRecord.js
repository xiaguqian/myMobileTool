const BaseModel = require('./BaseModel');

class PublishRecord extends BaseModel {
  constructor() {
    super('publish_records', 'id');
  }

  async createRecord(data) {
    const { pool } = require('../config/database');
    const { version, publishUser, fileName, filePath, description, status = 1 } = data;
    
    const [result] = await pool.execute(`
      INSERT INTO publish_records 
      (version, publish_user, file_name, file_path, description, status)
      VALUES (?, ?, ?, ?, ?, ?)
    `, [version, publishUser, fileName, filePath, description, status]);
    
    return result.insertId;
  }

  async getLatest() {
    const { pool } = require('../config/database');
    const [rows] = await pool.execute(`
      SELECT * FROM publish_records 
      WHERE status = 1 
      ORDER BY publish_time DESC 
      LIMIT 1
    `);
    return rows[0] || null;
  }

  async getHistory(page = 1, pageSize = 20) {
    const { pool } = require('../config/database');
    const offset = (page - 1) * pageSize;
    
    const [countResult] = await pool.execute(`
      SELECT COUNT(*) as total FROM publish_records
    `);
    
    const [rows] = await pool.execute(`
      SELECT * FROM publish_records 
      ORDER BY publish_time DESC 
      LIMIT ? OFFSET ?
    `, [pageSize, offset]);
    
    return {
      list: rows,
      total: countResult[0].total,
      page,
      pageSize
    };
  }

  async getByVersion(version) {
    const { pool } = require('../config/database');
    const [rows] = await pool.execute(`
      SELECT * FROM publish_records WHERE version = ?
    `, [version]);
    return rows[0] || null;
  }
}

module.exports = new PublishRecord();
