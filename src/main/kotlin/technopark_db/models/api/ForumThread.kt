package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonClassDescription
import com.fasterxml.jackson.annotation.JsonProperty

data class ForumThread(
        @JsonProperty("author")
        val author: String,
        @JsonProperty("message")
        val message: String,
        @JsonProperty("title")
        val title: String,
        @JsonProperty("created")
        var created: String,
        @JsonProperty("forum")
        var forum: String,
        @JsonProperty("id")
        var id: Int,
        @JsonProperty("slug")
        var slug: String,
        @JsonProperty("votes")
        var votes: Int)