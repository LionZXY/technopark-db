package technopark_db.controllers

import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import javax.servlet.http.HttpServletRequest


@ControllerAdvice
class ExceptionController {

    @ExceptionHandler(Exception::class)
    fun handleAllError(req: HttpServletRequest, ex: Exception): ResponseEntity<Error> {
        val responseStatus = AnnotatedElementUtils.findMergedAnnotation(ex.javaClass, ResponseStatus::class.java)
        if (responseStatus != null) {
            var reason: String? = ex.message ?: responseStatus.reason;

            return ResponseEntity.status(responseStatus.code).body(Error(reason))
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Error(ex.message))
    }
}