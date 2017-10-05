package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty

data class ForumThreadUpdate(
        @JsonProperty("message")
        val message: String,
        @JsonProperty("title")
        val title: String)