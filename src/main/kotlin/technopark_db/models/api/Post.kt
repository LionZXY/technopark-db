package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import technopark_db.models.mappers.DateAdapterDeseriliazation
import technopark_db.models.mappers.DateAdapterSeriliazation
import java.sql.Timestamp

class Post(
        @JsonProperty("author")
        public var author: String?,
        @JsonProperty("forum")
        public var forumSlug: String?,
        @JsonProperty("message")
        public var message: String?,
        @JsonProperty("thread")
        public var threadId: Int = 0,
        @JsonProperty("id")
        public var id: Int = 0,
        @JsonProperty("created")
        @JsonDeserialize(using = DateAdapterDeseriliazation::class)
        @JsonSerialize(using = DateAdapterSeriliazation::class)
        public var created: Timestamp? = null,
        isEdited: Boolean = false,
        @JsonProperty("parent")
        public var parent: Long = 0L) {

    @JsonProperty("isEdited")
    @JvmField
    public var isEdited: Boolean = isEdited

    constructor() : this("", "", "", 0)
}