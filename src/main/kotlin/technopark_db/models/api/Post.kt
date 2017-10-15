package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Date

data class Post(
        @JsonProperty("id")
        var id: Int = 0,
        @JsonProperty("author")
        var author: String?,
        @JsonProperty("forum")
        var forumSlug: String?,
        @JsonProperty("message")
        var message: String,
        @JsonProperty("thread")
        var threadId: Int,
        @JsonProperty("created")
        var created: Date? = null,
        @JsonProperty("isEdited")
        var isEdited: Boolean = false,
        @JsonProperty("parent")
        var parent: Long = 0L) {
    constructor() : this(0, "", "", "", 0)
}