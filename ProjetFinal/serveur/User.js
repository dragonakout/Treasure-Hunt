 Preferences = require('./Preferences.js');

class User {
  constructor (id, photoProfil) {
    this.id = id;
    this.quetes = [];
    this.tresors = [];
    this.prefs = new Preferences();
    this.photoProfil = photoProfil;
    this.last_daily_quest_redeem = 0
  }

  toJSON () {
    return {
      'id': this.id,
      'quests': this.quetes,
      'treasures': this.tresors,
      'prefs': this.prefs,
      'photoProfil': this.photoProfil,
    };
  }
}

module.exports = User;