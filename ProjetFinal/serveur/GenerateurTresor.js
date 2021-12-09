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

function genererTresor(nb, user) {
  for (i = 0; i < nb; i++) {
    let nom = bootySize[Math.floor(Math.random() * bootySize.length)] + " " + bootyName[Math.floor(Math.random()*bootyName.length)] + " " + bootyAdjective[Math.floor(Math.random()*bootyAdjective.length)];
    let montantEstime = (Math.floor(Math.random() * 20) + 5) * 1000;
    let montantActuel = Math.floor(((Math.random() * 1) + 1) * montantEstime);
    let lat = MINIMUM_LATTITUDE + Math.random() * (MAXIMUM_LATTITUDE - MINIMUM_LATTITUDE)
    let long = MINIMUM_LONGITIDE + Math.random() * (MAXIMUM_LONGITIDE - MINIMUM_LONGITIDE)
  
    user.quetes.push(new Tresor(i, nom, montantEstime, montantActuel, lat, long));
  }
  user.last_daily_quest_redeem = new Date().getTime()
}

module.exports = {};
module.exports.userSet = userSet;
module.exports.genererTresor = genererTresor;