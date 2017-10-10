package technopark_db.data

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import technopark_db.exceptions.UserNotFound
import technopark_db.models.api.User
import technopark_db.models.local.UserLocal
import java.sql.ResultSet
import java.util.*

@Service
open class UserDao(private val template: JdbcTemplate) {
    companion object {
        private const val COLUMN_ABOUT = "about"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_FULLNAME = "fullname"
        private const val COLUMN_NICKNAME = "nickname" // Primary key

        val USERMAPPER = RowMapper<UserLocal> { rs: ResultSet, _ ->
            UserLocal(rs.getString(COLUMN_NICKNAME),
                    rs.getString(COLUMN_EMAIL),
                    rs.getString(COLUMN_FULLNAME),
                    rs.getString(COLUMN_ABOUT))
        }
    }

    fun create(user: User): UserLocal {
        template.update({
            it.prepareStatement("INSERT INTO \"user\" (nickname, about, email, fullname) VALUES (?,?,?,?);").apply {
                setString(1, user.nickname)
                setString(2, user.about)
                setString(3, user.email)
                setString(4, user.fullname)
            }
        })
        return UserLocal(user.nickname!!, user.email, user.fullname, user.about)
    }

    fun update(user: User): UserLocal {
        val rows = template.update({
            it.prepareStatement("UPDATE \"user\" SET (about, email, fullname) = (?, ?, ?) WHERE nickname = ?;").apply {
                setString(1, user.about)
                setString(2, user.email)
                setString(3, user.fullname)
                setString(4, user.nickname)
            }
        })
        if (rows == 0) {
            throw UserNotFound()
        }
        return UserLocal(user.nickname!!, user.email, user.fullname, user.about)
    }

    fun getUser(nickname: String, email: String? = null): List<UserLocal> {
        return if (email == null) {
            template.query("SELECT * FROM \"user\" WHERE nickname = ?",
                    PreparedStatementSetter { it.setString(1, nickname) },
                    USERMAPPER)
        } else {
            template.query("SELECT * FROM \"user\" WHERE nickname = ? OR email = ?",
                    PreparedStatementSetter {
                        it.setString(1, nickname)
                        it.setString(2, email)
                    },
                    USERMAPPER)
        }
    }

    fun getUsers(): List<UserLocal> {
        try {
            return template.query("SELECT * FROM \"user\"", USERMAPPER);
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Collections.emptyList()
    }
}
