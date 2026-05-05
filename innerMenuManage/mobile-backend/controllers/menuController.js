const asyncHandler = require('express-async-handler');
const dataService = require('../services/dataService');
const response = require('../utils/response');

const getPagesBySlotCode = asyncHandler(async (req, res) => {
  const { slotCode } = req.params;
  
  if (!slotCode) {
    return response.badRequest(res, '栏位号不能为空');
  }

  const pages = await dataService.getPagesBySlotCode(slotCode);
  response.success(res, pages);
});

const getMenuTreesBySlotCode = asyncHandler(async (req, res) => {
  const { slotCode } = req.params;
  
  if (!slotCode) {
    return response.badRequest(res, '栏位号不能为空');
  }

  const menuTrees = await dataService.getMenuTreesBySlotCode(slotCode);
  response.success(res, menuTrees);
});

const getSlotData = asyncHandler(async (req, res) => {
  const { slotCode } = req.params;
  
  if (!slotCode) {
    return response.badRequest(res, '栏位号不能为空');
  }

  const [pages, menuTrees] = await Promise.all([
    dataService.getPagesBySlotCode(slotCode),
    dataService.getMenuTreesBySlotCode(slotCode)
  ]);

  response.success(res, {
    slotCode,
    pages,
    menuTrees
  });
});

const getMenusWithPermissions = asyncHandler(async (req, res) => {
  const { permissionCodes } = req.body;
  
  if (!permissionCodes || !Array.isArray(permissionCodes)) {
    return response.badRequest(res, '权限编码列表不能为空');
  }

  const menus = await dataService.getMenusWithPermissions(permissionCodes);
  response.success(res, menus);
});

const getAllMenus = asyncHandler(async (req, res) => {
  const menus = await dataService.getAllMenus();
  response.success(res, menus);
});

const getPageByRoute = asyncHandler(async (req, res) => {
  const { routePath } = req.params;
  
  if (!routePath) {
    return response.badRequest(res, '路由路径不能为空');
  }

  const page = await dataService.getPageByRoute(decodeURIComponent(routePath));
  
  if (!page) {
    return response.notFound(res, '页面不存在');
  }

  response.success(res, page);
});

const refreshCache = asyncHandler(async (req, res) => {
  const { deleteCache } = require('../config/redis');
  
  await deleteCache('mobile:menus:all');
  await dataService.refreshPublishedDataCache();
  
  response.success(res, null, '缓存刷新成功');
});

module.exports = {
  getPagesBySlotCode,
  getMenuTreesBySlotCode,
  getSlotData,
  getMenusWithPermissions,
  getAllMenus,
  getPageByRoute,
  refreshCache
};
