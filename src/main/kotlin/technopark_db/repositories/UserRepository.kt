package technopark_db.repositories

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import technopark_db.data.UserDao
import technopark_db.models.api.User
import technopark_db.models.exceptions.UserAlreadyCreated
import technopark_db.models.exceptions.UserAlreadyCreatedDefault
import technopark_db.models.exceptions.UserNotFound
import technopark_db.models.local.UserLocal
import technopark_db.models.mappers.UserMapper

@Service
class UserRepository(private val userDao: UserDao,
                     private val mapper: UserMapper) {
    fun create(user: User): UserLocal {
        try {
            return userDao.create(user)
        } catch (e: DuplicateKeyException) {
            val existUsers = userDao.getUser(user.nickname!!, user.email)
            throw UserAlreadyCreated(existUsers.map { mapper.map(it) })
        }
    }

    fun getUser(nickname: String): UserLocal {
        return userDao.getUser(nickname).firstOrNull() ?: throw UserNotFound()
    }

    fun update(user: User): UserLocal {
        try {
            return userDao.update(user)
        } catch (e: DuplicateKeyException) {
            throw UserAlreadyCreatedDefault()
        } catch (e: UserNotFound) {
            throw e
        }
    }
}