const express = require('express');
const router = express.Router();
const menuController = require('../controllers/menuController');

router.get('/active', menuController.getAllActiveMenus);

router.get('/', menuController.getMenus);

router.get('/:id', menuController.validateId, menuController.getMenuById);

router.post('/', menuController.validateMenu, menuController.createMenu);

router.put('/:id', menuController.validateId, menuController.updateMenu);

router.delete('/:id', menuController.validateId, menuController.deleteMenu);

module.exports = router;
