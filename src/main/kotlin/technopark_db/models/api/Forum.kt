package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty

data class Forum(
        @JsonProperty("slug")
        var slug: String?,
        @JsonProperty("title")
        var title: String,
        @JsonProperty("user")
        var user: String,
        var postCount: Int = 0,
        var threads: Int = 0 /* Веток */) {
    constructor() : this("", "", "")
}