package technopark_db.models.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import technopark_db.models.api.ForumThread

@ResponseStatus(HttpStatus.CONFLICT)
class ThreadAlreadyCreated(private val forumThread: ForumThread) : RuntimeException(), ISelfErrorMessageGenerating {
    override fun generate(): Any {
        return forumThread
    }

}