const express = require('express');
const router = express.Router();
const slotController = require('../controllers/slotController');

router.get('/', slotController.getSlots);

router.get('/:id', slotController.validateId, slotController.getSlotById);

router.post('/', slotController.validateSlot, slotController.createSlot);

router.put('/:id', slotController.validateId, slotController.updateSlot);

router.delete('/:id', slotController.validateId, slotController.deleteSlot);

router.get('/:id/pages', slotController.validateId, slotController.getAssociatedPages);

router.post('/associate-page', slotController.associatePage);

router.post('/disassociate-page', slotController.disassociatePage);

router.get('/:id/menu-trees', slotController.validateId, slotController.getAssociatedMenuTrees);

router.post('/associate-menu-tree', slotController.associateMenuTree);

router.post('/disassociate-menu-tree', slotController.disassociateMenuTree);

module.exports = router;
