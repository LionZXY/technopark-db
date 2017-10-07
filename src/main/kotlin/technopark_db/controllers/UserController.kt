package technopark_db.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import technopark_db.models.local.UserLocal
import technopark_db.repositories.UserRepository

@RestController
class UserController(
        private val userRepository: UserRepository) {

    @GetMapping("/api/signup")
    fun test(): List<UserLocal> {
        val test = userRepository.test()
        return test;
    }
}