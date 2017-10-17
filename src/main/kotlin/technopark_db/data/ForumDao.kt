package technopark_db.data

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import technopark_db.models.api.Forum
import technopark_db.models.exceptions.ForumNotFound
import technopark_db.models.local.ForumLocal
import technopark_db.models.local.ForumThreadLocal
import technopark_db.models.local.UserLocal
import java.sql.ResultSet

@Service
class ForumDao(private val template: JdbcTemplate) {

    companion object {
        private const val COLUMN_SLUG = "slug"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_NICKNAME = "tmp_nickname"
        private const val COLUMN_THREADS = "threads"
        private const val COLUMN_POSTS = "posts"

        val FORUMMAPPER = RowMapper<ForumLocal> { rs: ResultSet, _ ->
            ForumLocal(rs.getString(COLUMN_SLUG),
                    rs.getString(COLUMN_TITLE),
                    rs.getString(COLUMN_NICKNAME),
                    rs.getInt(COLUMN_POSTS),
                    rs.getInt(COLUMN_THREADS))
        }
    }

    fun create(forum: Forum): ForumLocal {
        return template.queryForObject("INSERT INTO forum (userid, slug, title, tmp_nickname) SELECT ud.id, ?, ?, ud.nickname FROM \"user\" AS ud WHERE nickname = ? :: CITEXT RETURNING *;",
                FORUMMAPPER,
                forum.slug,
                forum.title,
                forum.user)
    }

    fun get(slug: String): ForumLocal {
        return template.query("SELECT *\n" +
                "FROM forum AS forum\n" +
                "  JOIN \"user\" AS usr ON usr.id = forum.userid\n" +
                "WHERE forum.slug = ? :: CITEXT;",
                PreparedStatementSetter { it.setString(1, slug) },
                FORUMMAPPER).firstOrNull() ?: throw ForumNotFound()
    }

    fun getThreadsByForum(slug: String): List<ForumThreadLocal> {
        return template.query("SELECT * FROM thread WHERE forumslug = ?::CITEXT",
                PreparedStatementSetter { it.setString(1, slug) },
                ForumThreadDao.THREADMAPPER)
    }

    fun getThreadsByUser(slug: String): List<UserLocal> {
        return template.query("SELECT * FROM user WHERE forumslug = ?",
                PreparedStatementSetter { it.setString(1, slug) },
                UserDao.USERMAPPER)
    }
}