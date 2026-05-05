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

const getCache = async (key) => {
  try {
    const client = getRedisClient();
    if (client.isOpen) {
      const value = await client.get(key);
      if (value) {
        return JSON.parse(value);
      }
    }
  } catch (err) {
    console.warn('Redis缓存读取失败:', err.message);
  }
  return null;
};

const setCache = async (key, value, ttl = 300) => {
  try {
    const client = getRedisClient();
    if (client.isOpen) {
      await client.set(key, JSON.stringify(value), { EX: ttl });
    }
  } catch (err) {
    console.warn('Redis缓存写入失败:', err.message);
  }
};

const deleteCache = async (key) => {
  try {
    const client = getRedisClient();
    if (client.isOpen) {
      await client.del(key);
    }
  } catch (err) {
    console.warn('Redis缓存删除失败:', err.message);
  }
};

module.exports = {
  getRedisClient,
  connectRedis,
  closeRedis,
  getCache,
  setCache,
  deleteCache
};
