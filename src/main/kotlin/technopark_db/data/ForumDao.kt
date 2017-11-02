package technopark_db.data

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import technopark_db.models.api.Forum
import technopark_db.models.exceptions.ForumNotFound
import technopark_db.models.local.ForumLocal
import technopark_db.models.local.ForumThreadLocal
import java.sql.ResultSet
import java.sql.Timestamp

@Service
class ForumDao(private val template: JdbcTemplate) {

    companion object {
        private val log = LoggerFactory.getLogger("application")
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

    fun getThreadsByForum(slug: String, limit: Long, since: Timestamp?, desc: Boolean): List<ForumThreadLocal> {
        val builder = StringBuilder("SELECT * FROM thread WHERE tmp_forumslug = ?::CITEXT")

        if (since != null) {
            if (desc) {
                builder.append(" AND created <= ?::timestamptz")
            } else {
                builder.append(" AND created >= ?::timestamptz")
            }
        }

        builder.append(" ORDER BY ${ForumThreadDao.COLUMN_CREATED}")
        if (desc) {
            builder.append(" DESC")
        } else {
            builder.append(" ASC")
        }


        if (limit > -1) {
            builder.append(" LIMIT $limit")
        }

        builder.append(";")

        return template.query(builder.toString(),
                PreparedStatementSetter {
                    it.setString(1, slug)
                    if (since != null) {
                        it.setTimestamp(2, since)
                    }
                },
                ForumThreadDao.THREADMAPPER)
    }
}