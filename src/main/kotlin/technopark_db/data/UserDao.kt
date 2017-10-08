package technopark_db.data

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import technopark_db.models.api.User
import technopark_db.models.local.UserLocal
import java.sql.ResultSet
import java.util.*

@Service
open class UserDao(private val template: JdbcTemplate,
                   private val namedTemplate: NamedParameterJdbcTemplate) {
    companion object {
        private const val COLUMN_ABOUT = "about"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_FULLNAME = "fullname"
        private const val COLUMN_NICKNAME = "nickname" // Primary key

        val USERMAPPER = RowMapper<UserLocal> { rs: ResultSet, _ ->
            val v = rs.metaData.columnCount
            val tmp = UserLocal(rs.getString(COLUMN_NICKNAME),
                    rs.getString(COLUMN_EMAIL),
                    rs.getString(COLUMN_FULLNAME),
                    rs.getString(COLUMN_ABOUT))
            return@RowMapper tmp
        }
    }

    fun create(user: User): UserLocal {
        template.update({
            val pst = it.prepareStatement("INSERT INTO \"user\" (nickname, about, email, fullname) VALUES (?,?,?,?);")
            pst.setString(1, user.nickname)
            pst.setString(2, user.about)
            pst.setString(3, user.fullname)
            pst.setString(4, user.fullname)
            return@update pst
        })
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
