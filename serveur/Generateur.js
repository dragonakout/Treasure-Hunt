const Joueur = require('./Joueur');
const Tresor = require('./Tresor');

const bootySize = ["Gigantesque", "Immense", "Gros", "Abondant", "Grand", "Maigre", "Petit", "Massif"];
const bootyName = ["trésor", "héritage", "magot", "butin"];
const bootyAdjective = ["maudit", "mythique", "fantastique", "légendaire", "épique", "glorieux", "oublié", "prisé", "sanglant", "royal", "scintillant", "inimaginable"];

const listeTresors = [];


genererTresor();

function genererTresor() {
  for (i = 0; i < 9; i++) {
    let nom = bootySize[Math.floor(Math.random()*bootySize.length)] + " " + bootyName[Math.floor(Math.random()*bootyName.length)] + " " + bootyAdjective[Math.floor(Math.random()*bootyAdjective.length)];
    let montantEstime = (Math.floor(Math.random() * 20) + 5) * 1000;
    let montantActuel = Math.floor(((Math.random() * 1) + 1) * montantEstime);
  
    listeTresors.push(new Tresor(nom, montantEstime, montantActuel, 1, 45.380329228831144, -71.9249397261185));
    console.log(listeTresors[i])
  }
}









/*
const modificateurVitesse = Math.max(process.argv[2], 1);

const listePartie = [];



listePartie.push(new Partie(new Joueur('Albert', 'Ramos', 28, 56, 'Espagne'), new Joueur('Milos', 'Raonic', 28, 16, 'Canada'), '1', 'Hale', '12h30', 0));
listePartie.push(new Partie(new Joueur('Andy', 'Murray', 28, 132, 'Angleterre'), new Joueur('Andy', 'Roddick', 36, 1000, 'États-Unis'), '2', 'Hale', '13h00', 30));

const demarrer = function () {
  let tick = 0;
  setInterval(function () {
    for (const partie in listePartie) {
      if (listePartie[partie].tick_debut === tick) {
        demarrerPartie(listePartie[partie]);
      }
    }

    tick += 1;
  }, Math.floor(1000 / modificateurVitesse));
};

function demarrerPartie (partie) {
  const timer = setInterval(function () {
    partie.jouerTour();
    if (partie.estTerminee()) {
      clearInterval(timer);
    }
  }, Math.floor(1000 / modificateurVitesse));
}

module.exports = {};
module.exports.demarrer = demarrer;
module.exports.liste_partie = listePartie;
*/