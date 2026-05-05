const { pool } = require('../config/database');

class BaseModel {
  constructor(tableName, primaryKey = 'id') {
    this.tableName = tableName;
    this.primaryKey = primaryKey;
  }

  async findById(id) {
    const [rows] = await pool.execute(
      `SELECT * FROM ${this.tableName} WHERE ${this.primaryKey} = ? AND deleted_at IS NULL`,
      [id]
    );
    return rows[0] || null;
  }

  async findOne(where = {}) {
    const conditions = Object.keys(where)
      .map(key => `${key} = ?`)
      .join(' AND ');
    
    const values = Object.values(where);
    
    const [rows] = await pool.execute(
      `SELECT * FROM ${this.tableName} WHERE ${conditions} AND deleted_at IS NULL LIMIT 1`,
      values
    );
    return rows[0] || null;
  }

  async findAll(where = {}, orderBy = null) {
    let sql = `SELECT * FROM ${this.tableName} WHERE deleted_at IS NULL`;
    const values = [];

    if (Object.keys(where).length > 0) {
      const conditions = Object.keys(where)
        .map(key => `${key} = ?`)
        .join(' AND ');
      sql += ` AND ${conditions}`;
      values.push(...Object.values(where));
    }

    if (orderBy) {
      sql += ` ORDER BY ${orderBy}`;
    }

    const [rows] = await pool.execute(sql, values);
    return rows;
  }

  async findPaginated(page = 1, pageSize = 10, where = {}, orderBy = 'created_at DESC') {
    const offset = (page - 1) * pageSize;
    
    let countSql = `SELECT COUNT(*) as total FROM ${this.tableName} WHERE deleted_at IS NULL`;
    let listSql = `SELECT * FROM ${this.tableName} WHERE deleted_at IS NULL`;
    const values = [];

    if (Object.keys(where).length > 0) {
      const conditions = Object.keys(where)
        .map(key => `${key} = ?`)
        .join(' AND ');
      countSql += ` AND ${conditions}`;
      listSql += ` AND ${conditions}`;
      values.push(...Object.values(where));
    }

    listSql += ` ORDER BY ${orderBy} LIMIT ? OFFSET ?`;

    const [countResult] = await pool.execute(countSql, values);
    const [rows] = await pool.execute(listSql, [...values, pageSize, offset]);

    return {
      list: rows,
      total: countResult[0].total,
      page,
      pageSize
    };
  }

  async create(data) {
    const keys = Object.keys(data);
    const placeholders = keys.map(() => '?').join(', ');
    const values = Object.values(data);

    const [result] = await pool.execute(
      `INSERT INTO ${this.tableName} (${keys.join(', ')}) VALUES (${placeholders})`,
      values
    );

    return this.findById(result.insertId);
  }

  async update(id, data) {
    const keys = Object.keys(data);
    const setClause = keys.map(key => `${key} = ?`).join(', ');
    const values = [...Object.values(data), id];

    await pool.execute(
      `UPDATE ${this.tableName} SET ${setClause} WHERE ${this.primaryKey} = ?`,
      values
    );

    return this.findById(id);
  }

  async delete(id) {
    const [result] = await pool.execute(
      `UPDATE ${this.tableName} SET deleted_at = NOW() WHERE ${this.primaryKey} = ?`,
      [id]
    );
    return result.affectedRows > 0;
  }

  async hardDelete(id) {
    const [result] = await pool.execute(
      `DELETE FROM ${this.tableName} WHERE ${this.primaryKey} = ?`,
      [id]
    );
    return result.affectedRows > 0;
  }

  async count(where = {}) {
    let sql = `SELECT COUNT(*) as total FROM ${this.tableName} WHERE deleted_at IS NULL`;
    const values = [];

    if (Object.keys(where).length > 0) {
      const conditions = Object.keys(where)
        .map(key => `${key} = ?`)
        .join(' AND ');
      sql += ` AND ${conditions}`;
      values.push(...Object.values(where));
    }

    const [result] = await pool.execute(sql, values);
    return result[0].total;
  }

  async exists(where = {}) {
    const count = await this.count(where);
    return count > 0;
  }
}

module.exports = BaseModel;
