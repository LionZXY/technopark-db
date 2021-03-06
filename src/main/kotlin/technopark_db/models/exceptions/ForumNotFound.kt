package technopark_db.models.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Форум отсутсвует в системе.")
class ForumNotFound : RuntimeException()