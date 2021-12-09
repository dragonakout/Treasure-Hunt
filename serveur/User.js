class User {
  constructor (nom, photoProfil, id) {
    this.id = id;
    this.nom = nom;
    this.tresorsCompletes = [];
    this.tresorsRestants = [];
    this.photoProfil = photoProfil;
  }

  toJSON () {
    return {
      'id': this.id,
      'nom': this.nom,
      'tresorsCompletes': this.tresorsCompletes,
      'tresorsRestants': this.tresorsRestants,
      'this.photoProfil': this.photoProfil
    };
  }
}

module.exports = User;