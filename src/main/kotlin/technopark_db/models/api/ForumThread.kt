package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import technopark_db.models.mappers.DateAdapterDeseriliazation
import technopark_db.models.mappers.DateAdapterSeriliazation
import java.sql.Timestamp

data class ForumThread(
        @JsonProperty("author")
        val author: String?,
        @JsonProperty("message")
        val message: String,
        @JsonProperty("slug")
        var slug: String?,
        @JsonProperty("title")
        val title: String,
        @JsonProperty("created")
        @JsonDeserialize(using = DateAdapterDeseriliazation::class)
        @JsonSerialize(using = DateAdapterSeriliazation::class)
        var created: Timestamp?,
        @JsonProperty("forum")
        var forum: String?,
        @JsonProperty("id")
        var id: Int = -1,
        @JsonProperty("votes")
        var votes: Int = 0) {
    constructor() : this("", "", null, "", Timestamp(0L), "")
}