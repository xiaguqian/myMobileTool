const mysql = require('mysql2/promise');
const fs = require('fs').promises;
const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '../.env') });

const dbConfig = {
  host: process.env.DB_HOST || 'localhost',
  port: parseInt(process.env.DB_PORT) || 3306,
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  multipleStatements: true
};

const dbName = process.env.DB_NAME || 'menu_manage';
const sqlFile = path.join(__dirname, '../../database/init.sql');

const executeSqlScript = async () => {
  let connection = null;
  
  try {
    console.log('正在连接数据库...');
    connection = await mysql.createConnection(dbConfig);
    console.log('数据库连接成功');

    console.log('正在读取SQL文件...');
    const sqlContent = await fs.readFile(sqlFile, 'utf8');
    console.log('SQL文件读取成功');

    console.log('正在执行SQL脚本...');
    const statements = sqlContent
      .split(';')
      .map(s => s.trim())
      .filter(s => s.length > 0 && !s.startsWith('--'));

    for (const statement of statements) {
      try {
        await connection.query(statement);
      } catch (err) {
        if (!err.message.includes('already exists') && 
            !err.message.includes('Duplicate entry')) {
          console.log('警告:', err.message.substring(0, 100));
        }
      }
    }

    console.log('数据库初始化完成！');
    console.log(`数据库: ${dbName}`);
    console.log('表结构已创建，示例数据已插入');

    await connection.end();
    console.log('数据库连接已关闭');
    
  } catch (error) {
    console.error('数据库初始化失败:', error.message);
    if (connection) {
      try {
        await connection.end();
      } catch (e) {}
    }
    process.exit(1);
  }
};

executeSqlScript();
