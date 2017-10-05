package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty

data class Post(
        @JsonProperty("id")
        val id: Long,
        @JsonProperty("author")
        val author: String,
        @JsonProperty("forum")
        val forumSlug: String,
        @JsonProperty("message")
        val message: String,
        @JsonProperty("thread")
        val threadId: Int,
        @JsonProperty("created")
        var created: String = "",
        @JsonProperty("isEdited")
        var isEdited: Boolean = false,
        @JsonProperty("parent")
        var parent: Long = 0L)