package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty

data class Status(
        @JsonProperty("forum")
        val forumCount: Int,
        @JsonProperty("post")
        val postCount: Long,
        @JsonProperty("thread")
        val threadCount: Int,
        @JsonProperty("user")
        val userCount: Int)