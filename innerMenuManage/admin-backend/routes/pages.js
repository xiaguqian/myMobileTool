const express = require('express');
const router = express.Router();
const pageController = require('../controllers/pageController');

router.get('/', pageController.getPages);

router.get('/:id', pageController.validateId, pageController.getPageById);

router.post('/', pageController.validatePage, pageController.createPage);

router.put('/:id', pageController.validateId, pageController.updatePage);

router.delete('/:id', pageController.validateId, pageController.deletePage);

router.get('/:id/components', pageController.validateId, pageController.getPageComponents);

router.post('/add-component', pageController.addPageComponent);

router.post('/remove-component', pageController.removePageComponent);

router.put('/update-component-config', pageController.updateComponentConfig);

router.post('/batch-configure-components', pageController.batchConfigureComponents);

module.exports = router;
