const User = require('./User');
const Tresor = require('./Tresor');

const bootySize = ["Gigantesque", "Immense", "Gros", "Abondant", "Grand", "Maigre", "Petit", "Massif"];
const bootyName = ["trésor", "héritage", "magot", "butin"];
const bootyAdjective = ["maudit", "mythique", "fantastique", "légendaire", "épique", "glorieux", "oublié", "prisé", "sanglant", "royal", "scintillant", "inimaginable"];

const listeTresors = [];



function genererTresor(nb) {
  for (i = 0; i < nb; i++) {
    let nom = bootySize[Math.floor(Math.random() * bootySize.length)] + " " + bootyName[Math.floor(Math.random()*bootyName.length)] + " " + bootyAdjective[Math.floor(Math.random()*bootyAdjective.length)];
    let montantEstime = (Math.floor(Math.random() * 20) + 5) * 1000;
    let montantActuel = Math.floor(((Math.random() * 1) + 1) * montantEstime);
  
    listeTresors.push(new Tresor(i, nom, montantEstime, montantActuel, 45.380329228831144, -71.9249397261185));
    console.log(listeTresors[i])
  }
}

module.exports = {};
module.exports.listeTresors = listeTresors;
module.exports.genererTresor = genererTresor;