package technopark_db.data

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Controller
import technopark_db.models.api.ForumThread
import technopark_db.models.exceptions.ForumThreadNotFound
import technopark_db.models.local.ForumThreadLocal
import java.sql.ResultSet
import java.util.*


/**
 * На стороне sql мы инкрементим forum
 */
@Controller
class ForumThreadDao(private val template: JdbcTemplate) {
    companion object {
        private const val COLUMN_USER = "tmp_nickname"
        private const val COLUMN_ID = "id"
        private const val COLUMN_CREATED = "created"
        private const val COLUMN_FORUM = "tmp_forumslug"
        private const val COLUMN_TEXT = "messagetext"
        private const val COLUMN_SLUG = "slug"
        private const val COLUMN_TITILE = "title"
        private const val COLUMN_VOTES = "votes"

        val THREADMAPPER = RowMapper<ForumThreadLocal> { rs: ResultSet, _ ->
            ForumThreadLocal(rs.getInt(COLUMN_ID),
                    rs.getString(COLUMN_USER),
                    rs.getTimestamp(COLUMN_CREATED),
                    rs.getString(COLUMN_TEXT),
                    rs.getString(COLUMN_SLUG),
                    rs.getString(COLUMN_TITILE),
                    rs.getString(COLUMN_FORUM),
                    rs.getInt(COLUMN_VOTES))
        }
    }

    fun create(forumThread: ForumThread): ForumThreadLocal {
        return template.queryForObject(
                "INSERT INTO thread (userid, tmp_nickname, forumid, tmp_forumslug, messagetext, slug, title, created)\n" +
                        "  SELECT\n" +
                        "    usr.id,\n" +
                        "    usr.nickname,\n" +
                        "    frm.id,\n" +
                        "    frm.slug,\n" +
                        "    ?,\n" +
                        "    ?,\n" +
                        "    ?,\n" +
                        "    ?::timestamptz\n" +
                        "  FROM forum AS frm,\n" +
                        "    (SELECT id, nickname\n" +
                        "     FROM \"user\"\n" +
                        "     WHERE nickname = ? :: CITEXT) AS usr\n" +
                        "  WHERE frm.slug = ? :: CITEXT\n" +
                        "RETURNING *;",
                THREADMAPPER,
                forumThread.message,
                forumThread.slug,
                forumThread.title,
                forumThread.created,
                forumThread.author,
                forumThread.forum)
    }

    fun getBySlug(slug: String): ForumThreadLocal {
        return template.query("SELECT * FROM thread WHERE slug = ?",
                PreparedStatementSetter {
                    it.setString(1, slug)
                },
                THREADMAPPER).firstOrNull() ?: throw ForumThreadNotFound()
    }

    fun getById(id: Int): ForumThreadLocal {
        return template.query("SELECT * FROM thread WHERE id = ?",
                PreparedStatementSetter {
                    it.setInt(1, id)
                },
                THREADMAPPER).firstOrNull() ?: throw ForumThreadNotFound()
    }
}