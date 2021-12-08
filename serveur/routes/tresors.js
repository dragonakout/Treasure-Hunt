const express = require('express');
const router = express.Router();

const gen = require('../Generateur');

/* GET parties listing. */
router.get('/', function (req, res, next) {
  res.send(gen.listeTresors);
});

router.get('/:id', function (req, res, next) {
  res.send(gen.listeTresors[req.params.id]);
});

module.exports = router;
