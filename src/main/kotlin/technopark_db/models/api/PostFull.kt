package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty

data class PostFull(
        @JsonProperty("author")
        var author: User?,
        @JsonProperty("forum")
        var forum: Forum?,
        @JsonProperty("post")
        var post: Post?,
        @JsonProperty("thread")
        var thread: ForumThread?) {
    constructor() : this(null, null, null, null)
}