package technopark_db.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import technopark_db.models.api.Forum
import technopark_db.models.local.UserLocal

@ResponseStatus(code = HttpStatus.CONFLICT)
class ForumAlreadyCreated(private val forum: Forum) : RuntimeException(), ISelfErrorMessageGenerating {

    override fun generate(): Any {
        return forum
    }
}