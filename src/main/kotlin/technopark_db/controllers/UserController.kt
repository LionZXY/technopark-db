package technopark_db.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import technopark_db.models.api.User
import technopark_db.models.local.UserLocal
import technopark_db.models.mappers.Mapper
import technopark_db.repositories.UserRepository

@RestController
class UserController(
        private val userRepository: UserRepository,
        private val userMapper: Mapper<UserLocal, User>) {

    @PostMapping("/user/{nickname}/create")
    fun сreate(@PathVariable nickname: String, @RequestBody(required = false) user: User): ResponseEntity<UserLocal> {
        user.nickname = nickname
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        userRepository.create(user)
                );
    }

    @GetMapping("/user/{nickname}/profile")
    fun getUser(@PathVariable nickname: String): UserLocal {
        return userRepository.getUser(nickname)
    }

    @PostMapping("/user/{nickname}/profile")
    fun update(@PathVariable nickname: String, @RequestBody(required = false) user: User?): UserLocal {
        val nonNullUser = user ?: User()
        nonNullUser.nickname = nickname
        return userRepository.update(nonNullUser);
    }
}