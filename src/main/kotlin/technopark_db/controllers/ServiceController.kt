package technopark_db.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import technopark_db.models.api.ServiceModel
import technopark_db.repositories.ServiceRepository

@RestController
class ServiceController(private val serviceRepository: ServiceRepository) {

    @GetMapping("/service/status")
    fun info(): ServiceModel {
        return serviceRepository.info()
    }

    @PostMapping("/service/clear")
    fun clear() {
        serviceRepository.clear()
    }
}