const asyncHandler = require('express-async-handler');
const fs = require('fs').promises;
const path = require('path');
const { v4: uuidv4 } = require('uuid');
const PublishRecord = require('../models/PublishRecord');
const Slot = require('../models/Slot');
const MenuTree = require('../models/MenuTree');
const Page = require('../models/Page');
const Menu = require('../models/Menu');
const response = require('../utils/response');
require('dotenv').config();

const OUTPUT_DIR = process.env.PUBLISH_OUTPUT_DIR || './output';

const ensureOutputDir = async () => {
  try {
    await fs.access(OUTPUT_DIR);
  } catch {
    await fs.mkdir(OUTPUT_DIR, { recursive: true });
  }
};

const generateVersion = () => {
  const now = new Date();
  const timestamp = now.getTime();
  const dateStr = now.toISOString().slice(0, 10).replace(/-/g, '');
  return `v${dateStr}_${timestamp}`;
};

const collectAllData = async () => {
  const { pool } = require('../config/database');

  const slots = await Slot.findAll({ status: 1 }, 'id ASC');
  
  const allData = {
    version: null,
    publishTime: null,
    slots: [],
    pages: [],
    menus: [],
    menuTrees: []
  };

  for (const slot of slots) {
    const slotData = {
      id: slot.id,
      slotCode: slot.slot_code,
      slotName: slot.slot_name,
      description: slot.description,
      pages: [],
      menuTrees: []
    };

    const pages = await Slot.getAssociatedPages(slot.id);
    for (const page of pages) {
      if (page.status !== 1 || page.relation_status !== 1) continue;
      
      const components = await Page.getComponents(page.id);
      const pageComponents = components
        .filter(c => c.relation_status === 1)
        .map(c => ({
          componentId: c.component_id,
          componentCode: c.component_code,
          componentName: c.component_name,
          componentType: c.component_type,
          defaultConfig: c.default_config,
          config: c.component_config,
          sortOrder: c.sort_order
        }));

      slotData.pages.push({
        id: page.id,
        pageCode: page.page_code,
        pageName: page.page_name,
        routePath: page.route_path,
        description: page.description,
        components: pageComponents,
        sortOrder: page.sort_order
      });
    }

    const menuTrees = await Slot.getAssociatedMenuTrees(slot.id);
    for (const mt of menuTrees) {
      if (mt.status !== 1 || mt.relation_status !== 1) continue;
      
      const treeStructure = await MenuTree.buildTreeStructure(mt.id);
      slotData.menuTrees.push({
        id: mt.id,
        treeCode: mt.tree_code,
        treeName: mt.tree_name,
        description: mt.description,
        structure: treeStructure,
        sortOrder: mt.sort_order
      });
    }

    allData.slots.push(slotData);
  }

  const pages = await Page.findAll({ status: 1 }, 'id ASC');
  allData.pages = pages.map(p => ({
    id: p.id,
    pageCode: p.page_code,
    pageName: p.page_name,
    routePath: p.route_path,
    description: p.description
  }));

  const menus = await Menu.getAllActiveMenus();
  allData.menus = menus.map(m => ({
    id: m.id,
    menuCode: m.menu_code,
    menuName: m.menu_name,
    menuIcon: m.menu_icon,
    routeUrl: m.route_url,
    pageId: m.page_id,
    permissionCode: m.permission_code
  }));

  const menuTrees = await MenuTree.findAll({ status: 1 }, 'id ASC');
  for (const mt of menuTrees) {
    const treeStructure = await MenuTree.buildTreeStructure(mt.id);
    allData.menuTrees.push({
      id: mt.id,
      treeCode: mt.tree_code,
      treeName: mt.tree_name,
      description: mt.description,
      structure: treeStructure
    });
  }

  return allData;
};

const publish = asyncHandler(async (req, res) => {
  const { description, publishUser = 'admin' } = req.body;

  await ensureOutputDir();

  const version = generateVersion();
  const now = new Date();

  const allData = await collectAllData();
  allData.version = version;
  allData.publishTime = now.toISOString();

  const jsonContent = JSON.stringify(allData, null, 2);
  const fileName = `menu_data_${version}.json`;
  const filePath = path.join(OUTPUT_DIR, fileName);

  await fs.writeFile(filePath, jsonContent, 'utf8');

  await PublishRecord.createRecord({
    version,
    publishUser,
    fileName,
    filePath,
    description,
    status: 1
  });

  response.success(res, {
    version,
    fileName,
    publishTime: allData.publishTime,
    downloadUrl: `/api/publish/download/${fileName}`
  }, '发布成功');
});

const getLatestPublish = asyncHandler(async (req, res) => {
  const latest = await PublishRecord.getLatest();
  
  if (!latest) {
    return response.notFound(res, '暂无发布记录');
  }

  response.success(res, latest);
});

const getPublishHistory = asyncHandler(async (req, res) => {
  const { page = 1, pageSize = 20 } = req.query;
  
  const result = await PublishRecord.getHistory(parseInt(page), parseInt(pageSize));
  response.pagination(res, result.list, result.total, result.page, result.pageSize);
});

const downloadFile = asyncHandler(async (req, res) => {
  const { fileName } = req.params;

  const record = await PublishRecord.getByVersion(fileName.replace('.json', ''));
  if (!record && !fileName.startsWith('menu_data_')) {
    return response.notFound(res, '文件不存在');
  }

  const filePath = path.join(OUTPUT_DIR, fileName);
  
  try {
    await fs.access(filePath);
  } catch {
    return response.notFound(res, '文件不存在');
  }

  res.download(filePath, fileName, (err) => {
    if (err) {
      console.error('下载错误:', err);
      return response.error(res, '文件下载失败');
    }
  });
});

const getPublishByVersion = asyncHandler(async (req, res) => {
  const { version } = req.params;
  
  const record = await PublishRecord.getByVersion(version);
  if (!record) {
    return response.notFound(res, '发布记录不存在');
  }

  response.success(res, record);
});

const getAllPublishedData = asyncHandler(async (req, res) => {
  const latest = await PublishRecord.getLatest();
  
  if (!latest) {
    return response.notFound(res, '暂无发布数据');
  }

  const filePath = path.join(OUTPUT_DIR, latest.file_name);
  
  try {
    const content = await fs.readFile(filePath, 'utf8');
    const data = JSON.parse(content);
    response.success(res, data);
  } catch (err) {
    console.error('读取发布数据失败:', err);
    response.error(res, '读取发布数据失败');
  }
});

module.exports = {
  publish,
  getLatestPublish,
  getPublishHistory,
  downloadFile,
  getPublishByVersion,
  getAllPublishedData
};
