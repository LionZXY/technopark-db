package technopark_db.data

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import technopark_db.models.api.User
import technopark_db.models.local.UserLocal
import java.sql.ResultSet
import java.util.*

@Service
class UserDao(private val template: JdbcTemplate) {
    private var dirty: Boolean = false;

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

    fun markDirty() {
        dirty = true
    }

    fun create(user: User): UserLocal {
        template.update({
            it.prepareStatement("INSERT INTO \"user\" (nickname, about, email, fullname) VALUES (?::CITEXT,?,?::CITEXT,?);").apply {
                setString(1, user.nickname)
                setString(2, user.about)
                setString(3, user.email)
                setString(4, user.fullname)
            }
        })
        return UserLocal(user.nickname!!, user.email!!, user.fullname!!, user.about!!)
    }

    fun update(user: User): UserLocal {
        return template.queryForObject("UPDATE \"user\" SET (about, email, fullname) = (coalesce(?, about), coalesce(?::CITEXT, email), coalesce(?, fullname)) WHERE nickname = ?::CITEXT RETURNING *;",
                USERMAPPER,
                user.about,
                user.email,
                user.fullname,
                user.nickname)


    }

    fun getUsers(slug: String, limit: Long, since: String?, desc: Boolean): List<UserLocal> {
        if (dirty) {
            synchronized(this) {
                if (dirty) {
                    generateFUTable()
                }
                dirty = false
            }
        }

        var argsObject = ArrayList<Any>()

        var sql = "SELECT *\n" +
                "FROM forum_user AS fu\n" +
                "  JOIN \"user\" AS usr ON usr.nickname = fu.nickname\n" +
                "WHERE forumslug = ?::CITEXT "
        argsObject.add(slug)
        argsObject.add(slug)

        if (since != null) {
            sql += if (desc) {
                "WHERE fu.nickname < ?::CITEXT "
            } else {
                "WHERE fu.nickname > ?::CITEXT "
            }
            argsObject.add(since)
        }

        sql += if (desc) {
            "ORDER BY fu.nickname DESC "
        } else {
            "ORDER BY fu.nickname ASC "
        }

        if (limit != -1L) {
            sql += "LIMIT ?"
            argsObject.add(limit)
        }

        return template.query(sql, argsObject.toArray(), USERMAPPER)
    }

    fun getUser(nickname: String, email: String? = null): List<UserLocal> {
        return if (email == null) {
            template.query("SELECT * FROM \"user\" WHERE nickname = ?::CITEXT",
                    PreparedStatementSetter { it.setString(1, nickname) },
                    USERMAPPER)
        } else {
            template.query("SELECT * FROM \"user\" WHERE nickname = ?::CITEXT OR email = ?::CITEXT",
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

    private fun generateFUTable() {
        template.update("INSERT INTO forum_user (forumslug, nickname)\n" +
                "    SELECT tmp_forumslug, tmp_nickname FROM thread\n" +
                "ON CONFLICT DO NOTHING;\n" +
                "INSERT INTO forum_user (forumslug, nickname)\n" +
                "  SELECT tmp_forumslug, tmp_nickname FROM messages\n" +
                "ON CONFLICT DO NOTHING;")
    }
}
