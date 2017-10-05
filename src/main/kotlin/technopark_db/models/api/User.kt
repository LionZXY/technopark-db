package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty

data class User(
        @JsonProperty("email")
        val email: String,
        @JsonProperty("fullname")
        val fullname: String,
        @JsonProperty("about")
        var about: String,
        @JsonProperty("nickname")
        var nickname: String)