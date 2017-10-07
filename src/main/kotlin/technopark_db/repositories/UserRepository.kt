package technopark_db.repositories

import org.springframework.stereotype.Service
import technopark_db.data.UserDao
import technopark_db.models.local.UserLocal

@Service
class UserRepository(private val userDao: UserDao) {
    fun test(): List<UserLocal> {
        return userDao.getUsers()
    }
}