package technopark_db.data

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import technopark_db.models.api.Forum
import technopark_db.models.exceptions.ForumNotFound
import technopark_db.models.local.ForumLocal
import java.sql.ResultSet

@Service
class ForumDao(private val template: JdbcTemplate) {

    companion object {
        private const val COLUMN_SLUG = "slug"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_NICKNAME = "nickname"
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
        template.update({
            it.prepareStatement("INSERT INTO \"forum\" (slug, title, nickname) VALUES (?, ?, ?)").apply {
                setString(1, forum.slug)
                setString(2, forum.title)
                setString(3, forum.user)
            }
        })
        return ForumLocal(forum.slug!!, forum.title, forum.user)
    }

    fun get(slug: String): ForumLocal {
        return template.query("SELECT * FROM forum WHERE slug = ?",
                PreparedStatementSetter { it.setString(1, slug) },
                FORUMMAPPER).firstOrNull() ?: throw ForumNotFound()
    }
}