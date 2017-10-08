package technopark_db.controllers

import org.springframework.web.bind.annotation.*
import technopark_db.models.api.User
import technopark_db.models.local.UserLocal
import technopark_db.repositories.UserRepository

@RestController
class UserController(
        private val userRepository: UserRepository) {

    @PostMapping("/user/{nickname}/create")
    fun сreate(@PathVariable nickname: String, @RequestBody(required = false) user: User): UserLocal {
        user.nickname = nickname
        return userRepository.create(user);
    }
}