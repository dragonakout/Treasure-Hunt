const express = require('express');
const router = express.Router();

const User = require('../User.js');
const Preferences = require('../Preferences.js');
const gen = require('../GenerateurTresor.js')

/* GET home page. */
router.get('/', function (req, res, next) {
    res.send('');
});

/* GET user with corresponding id*/
router.get('/user/:id', function (req, res, next) {
  const user_id = req.params.id
  res.send(gen.userSet.get(user_id));
});

/* GET all uncompleted quests for user */
router.post('/quests', function (req, res, next) {
  const user_id = req.body.user_id
  const location_longitude = req.body.location_longitude
  const location_latitude = req.body.location_latitude
  if(gen.userSet.get(user_id) != undefined) {
    // If it has been more than 24hours since last time having new quests, generate new quests
    
    //if((new Date).getTime() - gen.userSet.get(user_id).last_daily_quest_redeem > 86400000) {
      gen.userSet.get(user_id).quetes.map (function(t) { 
        t.isNew = false
      })
      gen.genererTresor(gen.userSet.get(user_id).prefs.nb_daily_quests, gen.userSet.get(user_id))
    //}
    res.send(gen.userSet.get(user_id).quetes);
  } else {
    if(user_id != undefined) {
      gen.userSet.set(user_id, new User (user_id, "https://picsum.photos/10"))
    }
    res.send([]);
  }
});

/* GET all completed treasure for user*/
router.get('/:id/treasures', function (req, res, next) {
  const user_id = req.params.id
  if(gen.userSet.get(user_id) != undefined) {
    res.send(gen.userSet.get(user_id).tresors);
  } else {
    res.send([]);
  }
});

/* POST add a completed treasure from the user*/
router.post('/:id/treasure', function (req, res, next) {
  const user_id = req.params.id
  const treasure_id = req.body.treasure_id;
  const collected_timestamp = req.body.collected_timestamp;

  var treasure = gen.userSet.get(user_id).quetes.find(obj => {
    return obj.id == treasure_id
  })
  var treasure_copy = Object.assign({}, treasure)

  var index = gen.userSet.get(user_id).quetes.indexOf(treasure)
  if (index > -1) {
    gen.userSet.get(user_id).quetes.splice(index, 1);
  }
  
  treasure_copy.timestampCollecte = collected_timestamp
  gen.userSet.get(user_id).tresors.push(treasure_copy)

});

/* POST remove a quest from the user*/
router.post('/dropquest', function (req, res, next) {
  const user_id = req.body.user_id;
  const treasure_id = req.body.treasure_id;
  var index = gen.userSet.get(user_id).quetes.indexOf(obj => {
    return obj.treasure_id === treasure_id
  })
  if (index > -1) {
    gen.userSet.get(user_id).quetes.splice(index, 1);
  }

});

/* POST register a user or its preferences in the server */
router.post('/user', function (req, res, next) {
  const user_id = req.body.user_id;
  const prefers_distance_measured_in_time = req.body.prefers_distance_measured_in_time
  const prefers_walking = req.body.prefers_walking
  const assume_go_back = req.body.assume_go_back
  const average_distance = req.body.average_distance
  const nb_daily_quests = req.body.nb_daily_quests

  if(gen.userSet.get(user_id) == undefined) {
    gen.userSet.set(user_id, new User (user_id, "https://picsum.photos/10"))
  } else {
    let prefs = new Preferences()
    prefs.prefers_distance_measured_in_time = prefers_distance_measured_in_time
    prefs.prefers_walking = prefers_walking
    prefs.assume_go_back = assume_go_back
    prefs.average_distance = average_distance
    prefs.nb_daily_quests = nb_daily_quests
    gen.userSet.get(user_id).Preferences = prefs
  }
});


module.exports = router;
