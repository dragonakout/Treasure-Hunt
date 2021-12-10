const User = require('./User');
const Tresor = require('./Tresor');

const bootySize = ["Gigantesque", "Immense", "Gros", "Abondant", "Grand", "Maigre", "Petit", "Massif"];
const bootyName = ["trésor", "héritage", "magot", "butin"];
const bootyAdjective = ["maudit", "mythique", "fantastique", "légendaire", "épique", "glorieux", "oublié", "prisé", "sanglant", "royal", "scintillant", "inimaginable"];

const userSet = new Map();

// Correspond à la l'ensemble de la ville de Sherbrooke
const MINIMUM_LATTITUDE = 45.37167696186306
const MAXIMUM_LATTITUDE = 45.429208924836395
const MINIMUM_LONGITIDE = -71.96296752784556
const MAXIMUM_LONGITIDE = -71.86304785226521

const EARTH_RADIUS = 6371000

function genererTresor(nb, user, pos_lat, pos_long) {
  let offset = user.tresors.length
  for (i = offset; i < nb + offset; i++) {
    let position = getRandomPositionFromCenter(pos_lat, pos_long, user.prefs.average_distance)
    let nom = bootySize[Math.floor(Math.random() * bootySize.length)] + " " + bootyName[Math.floor(Math.random()*bootyName.length)] + " " + bootyAdjective[Math.floor(Math.random()*bootyAdjective.length)];
    let montantEstime = (Math.floor(Math.random() * 20) + 5) * 1000;
    let montantActuel = Math.floor(((Math.random() * 1) + 1) * montantEstime);

  
    user.quetes.unshift(new Tresor(i, nom, montantEstime, montantActuel, position[0], position[1]));
  }
  user.last_daily_quest_redeem = new Date().getTime()
}


function getRandomPositionFromCenter(position_lat, position_long, rangeInKm) {
  let lat = MINIMUM_LATTITUDE + Math.random() * (MAXIMUM_LATTITUDE - MINIMUM_LATTITUDE)
  let long = MINIMUM_LONGITIDE + Math.random() * (MAXIMUM_LONGITIDE - MINIMUM_LONGITIDE)
  if(position_lat != 0 && position_long != 0) {
      let MINIMUM_NEGATIVE_LATTITUDE = (+position_lat - +distanceInKilometersToAngle(rangeInKm* 0.75))
      let MAXIMUM_NEGATIVE_LATTITUDE = (+position_lat - +distanceInKilometersToAngle(rangeInKm * 1.33))
      let MINIMUM_POSITIVE_LATTITUDE = (+position_lat + +distanceInKilometersToAngle(rangeInKm * 0.75))
      let MAXIMUM_POSITIVE_LATTITUDE = (+position_lat + +distanceInKilometersToAngle(rangeInKm * 1.33))

      let MINIMUM_NEGATIVE_LONGITUDE = (+position_long - +distanceInKilometersToAngle(rangeInKm * 0.75))
      let MAXIMUM_NEGATIVE_LONGITUDE = (+position_long - +distanceInKilometersToAngle(rangeInKm * 1.33))
      let MINIMUM_POSITIVE_LONGITUDE = (+position_long + +distanceInKilometersToAngle(rangeInKm * 0.75))
      let MAXIMUM_POSITIVE_LONGITUDE = (+position_long + +distanceInKilometersToAngle(rangeInKm * 1.33))

      let neg_lat = MINIMUM_NEGATIVE_LATTITUDE + Math.random() * (MAXIMUM_NEGATIVE_LATTITUDE - MINIMUM_NEGATIVE_LATTITUDE)
      let pos_lat = MINIMUM_POSITIVE_LATTITUDE + Math.random() * (MAXIMUM_POSITIVE_LATTITUDE - MINIMUM_POSITIVE_LATTITUDE)

      let neg_long = MINIMUM_NEGATIVE_LONGITUDE + Math.random() * (MAXIMUM_NEGATIVE_LONGITUDE - MINIMUM_NEGATIVE_LONGITUDE)
      let pos_long = MINIMUM_POSITIVE_LONGITUDE + Math.random() * (MAXIMUM_POSITIVE_LONGITUDE - MINIMUM_POSITIVE_LONGITUDE)

      let lat_array = [neg_lat, pos_lat]
      let long_array = [neg_long, pos_long]

      lat = lat_array[parseInt(Math.random() * 10 / 5 )]
      long = long_array[parseInt(Math.random()* 10 / 5 )]
  }
  return [lat, long]
}

function distanceInKilometersToAngle(distance) {
    return (distance * 1000 * 360) /( 2 * Math.PI * EARTH_RADIUS)
}


module.exports = {};
module.exports.userSet = userSet;
module.exports.genererTresor = genererTresor;