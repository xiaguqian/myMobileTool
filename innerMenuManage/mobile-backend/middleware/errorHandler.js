const errorHandler = (err, req, res, next) => {
  console.error('错误:', err.message);
  console.error('堆栈:', err.stack);

  let statusCode = err.statusCode || 500;
  let message = err.message || '服务器内部错误';

  if (err.name === 'JsonWebTokenError') {
    statusCode = 401;
    message = '无效的token';
  }

  if (err.name === 'TokenExpiredError') {
    statusCode = 401;
    message = 'token已过期';
  }

  res.status(statusCode).json({
    code: statusCode,
    success: false,
    message,
    timestamp: Date.now()
  });
};

const notFoundHandler = (req, res) => {
  res.status(404).json({
    code: 404,
    success: false,
    message: `请求的路径不存在: ${req.method} ${req.path}`,
    timestamp: Date.now()
  });
};

module.exports = {
  errorHandler,
  notFoundHandler
};
