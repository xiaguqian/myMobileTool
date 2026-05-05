const express = require('express');
const router = express.Router();
const menuTreeController = require('../controllers/menuTreeController');

router.get('/', menuTreeController.getMenuTrees);

router.get('/:id', menuTreeController.validateId, menuTreeController.getMenuTreeById);

router.get('/:id/structure', menuTreeController.validateId, menuTreeController.getMenuTreeStructure);

router.post('/', menuTreeController.validateMenuTree, menuTreeController.createMenuTree);

router.put('/:id', menuTreeController.validateId, menuTreeController.updateMenuTree);

router.delete('/:id', menuTreeController.validateId, menuTreeController.deleteMenuTree);

router.post('/node', menuTreeController.addTreeNode);

router.put('/node', menuTreeController.updateTreeNode);

router.delete('/node/:id', menuTreeController.deleteTreeNode);

router.get('/node/:id', menuTreeController.getTreeNodeById);

router.post('/batch-update', menuTreeController.batchUpdateTreeNodes);

module.exports = router;
