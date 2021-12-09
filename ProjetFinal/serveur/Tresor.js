class Tresor {
  constructor (id, nom, montantEstime, montantActuel, latitude, longitude) {
    this.id = id;
    this.nom = nom;
    this.montantEstime = montantEstime;
    this.montantActuel = montantActuel;
    this.latitude = latitude;
    this.longitude = longitude;
    this.timestampCollecte = "-1"
  }


  toJSON () {
      return {
        'id': this.id,
        'name': this.nom,
        'estimated_value': this.montantEstime,
        'actual_value': this.montantActuel,
        'quest_length': this.duree,
        'latitude': this.latitude,
        'longitude': this.longitude,
        'collected_timestamp': this.timestampCollecte
      }
  }
}

module.exports = Tresor;
