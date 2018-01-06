package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty

class PostDetailedInfo(
        @JsonProperty("post")
        public var post: Post? = null)
