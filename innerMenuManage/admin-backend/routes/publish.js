const express = require('express');
const router = express.Router();
const publishController = require('../controllers/publishController');

router.post('/', publishController.publish);

router.get('/latest', publishController.getLatestPublish);

router.get('/history', publishController.getPublishHistory);

router.get('/data', publishController.getAllPublishedData);

router.get('/download/:fileName', publishController.downloadFile);

router.get('/version/:version', publishController.getPublishByVersion);

module.exports = router;
