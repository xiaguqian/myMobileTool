const errorHandler = (err, req, res, next) => {
  console.error('错误:', err.message);
  console.error('堆栈:', err.stack);

  let statusCode = err.statusCode || 500;
  let message = err.message || '服务器内部错误';
  let errors = err.errors || null;

  if (err.name === 'ValidationError') {
    statusCode = 400;
    message = '数据验证失败';
    errors = err.details;
  }

  if (err.code === 'ER_DUP_ENTRY') {
    statusCode = 400;
    message = '数据已存在，请勿重复添加';
  }

  if (err.code === 'ER_NO_REFERENCED_ROW_2') {
    statusCode = 400;
    message = '关联数据不存在';
  }

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
    errors,
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
