const express = require('express');
const router = express.Router();

const Tresor = require('../Tresor.js');
const User = require('../User.js');
const gen = require('../GenerateurTresor.js')

/* GET home page. */
router.get('/', function (req, res, next) {
    res.send('');
});

/* GET user with corresponding id*/
router.get('/treasures/:id', function (req, res, next) {
  console.log("Nouvelle requete!")
  res.send(gen.listeTresors[req.params.id]);
});

/* GET user with corresponding id*/
router.get('/treasures', function (req, res, next) {
  console.log("Nouvelle requete!")
  res.send(gen.listeTresors);
});

/* GET all uncompleted quests for user */
router.get('/:id/quests', function (req, res, next) {
  res.send(gen.listeTresors);
});

/* GET all completed treasure for user*/
router.get('/:id/treasures', function (req, res, next) {
  res.send(gen.listeTresors);
});

/* POST all completed treasures from the user*/
router.post('/:id/treasures', function (req, res, next) {

});

/* POST register a user or its preferences in the server */
router.post('/:id', function (req, res, next) {

});





module.exports = router;
