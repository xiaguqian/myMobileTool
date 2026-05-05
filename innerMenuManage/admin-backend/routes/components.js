const express = require('express');
const router = express.Router();
const componentController = require('../controllers/componentController');

router.get('/types', componentController.getComponentTypes);

router.get('/', componentController.getComponents);

router.get('/:id', componentController.validateId, componentController.getComponentById);

router.post('/', componentController.validateComponent, componentController.createComponent);

router.put('/:id', componentController.validateId, componentController.updateComponent);

router.delete('/:id', componentController.validateId, componentController.deleteComponent);

router.get('/:id/using-pages', componentController.validateId, componentController.getPagesUsingComponent);

module.exports = router;
