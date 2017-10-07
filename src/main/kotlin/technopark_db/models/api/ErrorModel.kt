package technopark_db.models.api

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorModel(
        @JsonProperty("message")
        val message: String?)