package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class ForumThread(
        @JsonProperty("author")
        val author: String?,
        @JsonProperty("message")
        val message: String?,
        @JsonProperty("slug")
        var slug: String?,
        @JsonProperty("title")
        val title: String?,
        @JsonProperty("created")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        var created: Timestamp?,
        @JsonProperty("forum")
        var forum: String?,
        @JsonProperty("id")
        var id: Int = -1,
        @JsonProperty("votes")
        var votes: Int = 0) {
    constructor() : this("", "", null, "", Timestamp(0L), "")
}