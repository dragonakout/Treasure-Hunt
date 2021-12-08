const uuid = require("uuid")
class Tresor {
  constructor (nom, montantEstime, montantActuel, duree, latitude, longitude) {
    this.id = uuid.v4();
    this.nom = nom;
    this.montantEstime = montantEstime;
    this.montantActuel = montantActuel;
    this.duree = duree;
    this.latitude = latitude;
    this.longitude = longitude;
  }


  toJSON () {
      return {
        'id': this.id,
        'nom': this.nom,
        'montantEstime': this.montantEstime,
        'montantActuel': this.montantActuel,
        'duree': this.duree,
        'latitude': this.latitude,
        'longitude': this.longitude
      }
  }
}

module.exports = Tresor;
