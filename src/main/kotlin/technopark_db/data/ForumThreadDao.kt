package technopark_db.data

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Controller
import technopark_db.models.api.ForumThread
import technopark_db.models.exceptions.ForumThreadNotFound
import technopark_db.models.local.ForumThreadLocal
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * На стороне sql мы инкрементим forum
 */
@Controller
class ForumThreadDao(private val template: JdbcTemplate) {
    companion object {
        private const val COLUMN_USER = "authornick"
        private const val COLUMN_ID = "id"
        private const val COLUMN_CREATED = "created"
        private const val COLUMN_FORUM = "forumslug"
        private const val COLUMN_TEXT = "messagetext"
        private const val COLUMN_SLUG = "slug"
        private const val COLUMN_TITILE = "title"
        private const val COLUMN_VOTES = "votes"

        val THREADMAPPER = RowMapper<ForumThreadLocal> { rs: ResultSet, _ ->
            ForumThreadLocal(rs.getInt(COLUMN_ID),
                    rs.getString(COLUMN_USER),
                    rs.getDate(COLUMN_CREATED),
                    rs.getString(COLUMN_TEXT),
                    rs.getString(COLUMN_SLUG),
                    rs.getString(COLUMN_TITILE),
                    rs.getString(COLUMN_FORUM),
                    rs.getInt(COLUMN_VOTES))
        }
    }

    fun create(forumThread: ForumThread): ForumThreadLocal {
        val gkh = GeneratedKeyHolder()
        template.update({
            it.prepareStatement("INSERT INTO thread (authornick, forumslug, messagetext, slug, title, created) VALUES (?,?,?,?,?,?) RETURNING id",
                    PreparedStatement.RETURN_GENERATED_KEYS).apply {
                setString(1, forumThread.author)
                setString(2, forumThread.forum)
                setString(3, forumThread.message)
                setString(4, forumThread.slug)
                setString(5, forumThread.title)
                setDate(6, forumThread.created)
            }
        }, gkh)
        return ForumThreadLocal(gkh.key as Int, forumThread.author, forumThread.created, forumThread.message, forumThread.slug, forumThread.title, forumThread.forum!!)
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