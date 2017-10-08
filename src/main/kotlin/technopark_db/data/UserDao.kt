package technopark_db.data

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
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
            UserLocal(rs.getString(COLUMN_NICKNAME),
                    rs.getString(COLUMN_EMAIL),
                    rs.getString(COLUMN_FULLNAME),
                    rs.getString(COLUMN_ABOUT))
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
