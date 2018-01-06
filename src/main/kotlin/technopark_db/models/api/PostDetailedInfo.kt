package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty

class PostDetailedInfo(
        @JsonProperty("post")
        public var post: Post? = null,
        @JsonProperty("author")
        public var user: User? = null,
        @JsonProperty("forum")
        public var forum: Forum? = null,
        @JsonProperty("thread")
        public var thread: ForumThread? = null)
