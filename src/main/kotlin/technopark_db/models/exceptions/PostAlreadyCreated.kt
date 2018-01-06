package technopark_db.models.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Parent post was created in another thread")
class PostAlreadyCreated : RuntimeException()