package technopark_db.controllers

import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import technopark_db.exceptions.ISelfErrorMessageGenerating
import technopark_db.models.api.ErrorModel


@ControllerAdvice
class ExceptionController {

    @ExceptionHandler(Exception::class)
    fun handleAllError(ex: Exception): ResponseEntity<Any> {
        val responseStatus = AnnotatedElementUtils.findMergedAnnotation(ex.javaClass, ResponseStatus::class.java)
        if (responseStatus != null) {
            var reasonObject = if (ex is ISelfErrorMessageGenerating) {
                ex.generate()
            } else {
                ErrorModel(ex.message ?: responseStatus.reason);
            }

            return ResponseEntity.status(responseStatus.code).body(reasonObject)
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorModel(ex.message))
    }
}