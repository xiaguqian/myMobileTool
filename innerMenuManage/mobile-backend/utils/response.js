const success = (res, data = null, message = '操作成功') => {
  return res.status(200).json({
    code: 200,
    success: true,
    message,
    data,
    timestamp: Date.now()
  });
};

const error = (res, message = '操作失败', code = 500, errors = null) => {
  return res.status(code).json({
    code,
    success: false,
    message,
    errors,
    timestamp: Date.now()
  });
};

const badRequest = (res, message = '请求参数错误', errors = null) => {
  return error(res, message, 400, errors);
};

const unauthorized = (res, message = '未授权访问') => {
  return error(res, message, 401);
};

const forbidden = (res, message = '没有权限访问') => {
  return error(res, message, 403);
};

const notFound = (res, message = '资源不存在') => {
  return error(res, message, 404);
};

const pagination = (res, list, total, page, pageSize, message = '查询成功') => {
  return res.status(200).json({
    code: 200,
    success: true,
    message,
    data: {
      list,
      pagination: {
        page,
        pageSize,
        total,
        totalPages: Math.ceil(total / pageSize)
      }
    },
    timestamp: Date.now()
  });
};

module.exports = {
  success,
  error,
  badRequest,
  unauthorized,
  forbidden,
  notFound,
  pagination
};
