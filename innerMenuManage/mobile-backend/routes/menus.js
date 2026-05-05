const express = require('express');
const router = express.Router();
const menuController = require('../controllers/menuController');

router.get('/slots/:slotCode/pages', menuController.getPagesBySlotCode);

router.get('/slots/:slotCode/menu-trees', menuController.getMenuTreesBySlotCode);

router.get('/slots/:slotCode', menuController.getSlotData);

router.post('/menus/with-permissions', menuController.getMenusWithPermissions);

router.get('/menus/all', menuController.getAllMenus);

router.get('/pages/route/*', menuController.getPageByRoute);

router.post('/cache/refresh', menuController.refreshCache);

module.exports = router;
