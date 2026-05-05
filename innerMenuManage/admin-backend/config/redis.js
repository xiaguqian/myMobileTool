const { createClient } = require('redis');
require('dotenv').config();

let redisClient = null;

const getRedisClient = () => {
  if (!redisClient) {
    const redisConfig = {
      socket: {
        host: process.env.REDIS_HOST || 'localhost',
        port: parseInt(process.env.REDIS_PORT) || 6379,
      },
      password: process.env.REDIS_PASSWORD || undefined,
    };

    redisClient = createClient(redisConfig);

    redisClient.on('error', (err) => {
      console.error('Redis连接错误:', err.message);
    });

    redisClient.on('connect', () => {
      console.log('Redis连接成功');
    });

    redisClient.on('reconnecting', () => {
      console.log('Redis正在重连...');
    });
  }
  return redisClient;
};

const connectRedis = async () => {
  const client = getRedisClient();
  if (!client.isOpen) {
    await client.connect();
  }
  return client;
};

const closeRedis = async () => {
  if (redisClient && redisClient.isOpen) {
    await redisClient.quit();
    redisClient = null;
    console.log('Redis连接已关闭');
  }
};

module.exports = {
  getRedisClient,
  connectRedis,
  closeRedis
};
