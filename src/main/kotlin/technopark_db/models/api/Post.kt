package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import technopark_db.models.mappers.DateAdapterDeseriliazation
import technopark_db.models.mappers.DateAdapterSeriliazation
import java.sql.Date
import java.sql.Timestamp

data class Post(
        @JsonProperty("author")
        var author: String?,
        @JsonProperty("forum")
        var forumSlug: String?,
        @JsonProperty("message")
        var message: String,
        @JsonProperty("thread")
        var threadId: Int = 0,
        @JsonProperty("id")
        var id: Int = 0,
        @JsonProperty("created")
        @JsonDeserialize(using = DateAdapterDeseriliazation::class)
        @JsonSerialize(using = DateAdapterSeriliazation::class)
        var created: Timestamp? = null,
        @JsonProperty("isEdited")
        var isEdited: Boolean = false,
        @JsonProperty("parent")
        var parent: Long = 0L) {
    constructor() : this("", "", "", 0)
}