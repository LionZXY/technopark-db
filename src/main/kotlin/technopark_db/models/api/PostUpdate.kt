package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty

data class PostUpdate(
        @JsonProperty("message")
        var message: String)