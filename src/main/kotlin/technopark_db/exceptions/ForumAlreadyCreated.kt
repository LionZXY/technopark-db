package technopark_db.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import technopark_db.models.api.Forum

@ResponseStatus(code = HttpStatus.CONFLICT)
class ForumAlreadyCreated(private val forum: Forum) : RuntimeException(), ISelfErrorMessageGenerating {

    override fun generate(): Any {
        return forum
    }
}