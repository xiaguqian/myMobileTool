require('dotenv').config();
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const morgan = require('morgan');
const helmet = require('helmet');
const compression = require('compression');

const { testConnection } = require('./config/database');
const { connectRedis, closeRedis } = require('./config/redis');
const { errorHandler, notFoundHandler } = require('./middleware/errorHandler');

const slotsRoutes = require('./routes/slots');
const pagesRoutes = require('./routes/pages');
const componentsRoutes = require('./routes/components');
const menusRoutes = require('./routes/menus');
const menuTreesRoutes = require('./routes/menuTrees');
const publishRoutes = require('./routes/publish');

const app = express();
const PORT = process.env.PORT || 3001;

app.use(helmet());
app.use(compression());
app.use(cors());
app.use(bodyParser.json({ limit: '10mb' }));
app.use(bodyParser.urlencoded({ extended: true, limit: '10mb' }));

if (process.env.NODE_ENV === 'development') {
  app.use(morgan('dev'));
} else {
  app.use(morgan('combined'));
}

app.get('/api/health', (req, res) => {
  res.json({
    code: 200,
    success: true,
    message: 'Menu Admin Backend is running',
    timestamp: Date.now(),
    environment: process.env.NODE_ENV || 'development'
  });
});

app.use('/api/slots', slotsRoutes);
app.use('/api/pages', pagesRoutes);
app.use('/api/components', componentsRoutes);
app.use('/api/menus', menusRoutes);
app.use('/api/menu-trees', menuTreesRoutes);
app.use('/api/publish', publishRoutes);

app.use(notFoundHandler);
app.use(errorHandler);

const startServer = async () => {
  try {
    console.log('正在启动管理端后端服务...');
    console.log('服务端口:', PORT);
    console.log('环境:', process.env.NODE_ENV || 'development');

    console.log('\n正在测试数据库连接...');
    const dbConnected = await testConnection();
    if (!dbConnected) {
      console.warn('警告: 数据库连接测试失败，请检查数据库配置');
    }

    console.log('\n正在连接Redis...');
    try {
      await connectRedis();
    } catch (redisError) {
      console.warn('警告: Redis连接失败:', redisError.message);
    }

    app.listen(PORT, () => {
      console.log('\n========================================');
      console.log('管理端后端服务已启动!');
      console.log(`服务地址: http://localhost:${PORT}`);
      console.log(`API基础路径: http://localhost:${PORT}/api`);
      console.log(`健康检查: http://localhost:${PORT}/api/health`);
      console.log('========================================\n');
    });
  } catch (error) {
    console.error('服务启动失败:', error.message);
    console.error(error.stack);
    process.exit(1);
  }
};

const gracefulShutdown = async () => {
  console.log('\n正在优雅关闭服务...');
  
  try {
    await closeRedis();
    console.log('Redis连接已关闭');
  } catch (e) {
    console.warn('关闭Redis时出错:', e.message);
  }

  console.log('服务已停止');
  process.exit(0);
};

process.on('SIGTERM', gracefulShutdown);
process.on('SIGINT', gracefulShutdown);

startServer();

module.exports = app;
