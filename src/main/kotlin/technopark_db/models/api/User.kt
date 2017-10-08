package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty

data class User(
        @JsonProperty("email")
        var email: String,
        @JsonProperty("fullname")
        var fullname: String,
        @JsonProperty("about")
        var about: String,
        @JsonProperty("nickname")
        var nickname: String? = "") {
    constructor() : this("", "", "", "")
}