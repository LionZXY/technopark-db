package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Date

data class ForumThread(
        @JsonProperty("author")
        val author: String,
        @JsonProperty("message")
        val message: String,
        @JsonProperty("slug")
        var slug: String?,
        @JsonProperty("title")
        val title: String,
        @JsonProperty("created")
        var created: Date,
        @JsonProperty("forum")
        var forum: String?,
        @JsonProperty("id")
        var id: Int = 0,
        @JsonProperty("votes")
        var votes: Int = 0) {
    constructor() : this("", "", "", "", Date(0L), "")
}