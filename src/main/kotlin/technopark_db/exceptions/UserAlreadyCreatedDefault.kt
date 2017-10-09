package technopark_db.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT)
class UserAlreadyCreatedDefault : RuntimeException() {

}