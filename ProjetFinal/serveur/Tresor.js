class Tresor {
  constructor (id, nom, montantEstime, montantActuel, latitude, longitude) {
    this.id = id;
    this.name = nom;
    this.estimated_value = montantEstime;
    this.actual_value = montantActuel;
    this.latitude = latitude;
    this.longitude = longitude;
    this.collected_timestamp = "-1"
    this.is_new = true
  }


  toJSON () {
      return {
        'id': this.id,
        'name': this.name,
        'estimated_value': this.estimated_value,
        'actual_value': this.actual_value,
        'quest_length': this.quest_length,
        'latitude': this.latitude,
        'longitude': this.longitude,
        'collected_timestamp': this.collected_timestamp,
        'is_new': this.is_new
      }
  }
}

module.exports = Tresor;
