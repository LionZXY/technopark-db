package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty

data class Vote(
        @JsonProperty("nickname")
        val nickname: String,
        @JsonProperty("voice")
        val voice: Int) {
    constructor() : this("", 0)
}