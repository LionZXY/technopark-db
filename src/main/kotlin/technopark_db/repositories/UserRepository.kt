package technopark_db.repositories

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import technopark_db.data.UserDao
import technopark_db.exceptions.UserAlreadyCreated
import technopark_db.models.api.User
import technopark_db.models.local.UserLocal

@Service
class UserRepository(private val userDao: UserDao) {
    fun create(user: User): UserLocal {
        try {
            return userDao.create(user)
        } catch (e: DuplicateKeyException) {
            val existUsers = userDao.getUser(user.nickname!!, user.email)
            throw UserAlreadyCreated(existUsers)
        }
    }
}