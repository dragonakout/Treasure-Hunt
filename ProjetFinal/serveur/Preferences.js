class Preferences {
    constructor () {
      this.prefers_distance_measured_in_time = true,
      this.prefers_walking = true,
      this.assume_go_back = true,
      this.average_distance = 2
      this.nb_daily_quests = 2
    }

  toJSON () {
    return {
      'prefers_distance_measured_in_time': this.prefers_distance_measured_in_time,
      'prefers_walking': this.prefers_walking,
      'assume_go_back': this.assume_go_back,
      'average_distance': this.average_distance
    };
  }
}
module.exports = Preferences;