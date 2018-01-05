package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty

class Vote(
        @JsonProperty("nickname")
        var nickname: String,
        @JsonProperty("voice")
        var voice: Int) {
    constructor() : this("", 0)
}