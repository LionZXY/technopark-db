package technopark_db.data

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import technopark_db.models.api.ForumThread
import technopark_db.models.api.User
import technopark_db.models.api.Vote
import technopark_db.models.exceptions.ForumThreadNotFound
import technopark_db.models.local.ForumThreadLocal
import technopark_db.models.local.UserLocal
import java.sql.ResultSet
import java.sql.Timestamp


/**
 * На стороне sql мы инкрементим forum
 */
@Controller
class ForumThreadDao(private val template: JdbcTemplate) {
    companion object {
        private const val COLUMN_USER = "tmp_nickname"
        private const val COLUMN_ID = "id"
        const val COLUMN_CREATED = "created"
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

    fun update(forumThread: ForumThread): ForumThreadLocal {
        return template.queryForObject("UPDATE thread SET (title, messagetext) = (coalesce(?, title), coalesce(?::CITEXT, messagetext)) WHERE slug = ?::CITEXT OR id = ? RETURNING *;",
                THREADMAPPER,
                forumThread.title,
                forumThread.message,
                forumThread.slug,
                forumThread.id)
    }

    fun create(forumThread: ForumThread): ForumThreadLocal {
        return template.queryForObject(
                "INSERT INTO thread (userid, tmp_nickname, forumid, tmp_forumslug, messagetext, slug, title, created)\n" +
                        "  SELECT\n" +
                        "    usr.id,\n" +
                        "    usr.nickname,\n" +
                        "    frm.id,\n" +
                        "    frm.slug::CITEXT,\n" +
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
                forumThread.created ?: Timestamp(System.currentTimeMillis()),
                forumThread.author,
                forumThread.forum)

    }

    fun getBySlug(slug: String): ForumThreadLocal {
        return template.query("SELECT * FROM thread WHERE slug = ?::CITEXT",
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

    fun voteBySlug(slug: String, vote: Vote): ForumThreadLocal {
        val thread = template.query("SELECT * FROM thread WHERE slug = ?::CITEXT",
                PreparedStatementSetter {
                    it.setString(1, slug)
                },
                THREADMAPPER).firstOrNull() ?: throw ForumThreadNotFound()

        template.update("INSERT INTO votes (usernick, voice, threadid) VALUES (?::CITEXT, ?, ?) ON CONFLICT (usernick, threadid) DO UPDATE SET voice = ?;", vote.nickname, vote.voice, thread.localId, vote.voice)

        return template.query("SELECT * FROM thread WHERE slug = ?::CITEXT",
                PreparedStatementSetter {
                    it.setString(1, slug)
                },
                THREADMAPPER).firstOrNull() ?: throw ForumThreadNotFound()
    }

    fun voteById(id: Int, vote: Vote): ForumThreadLocal {
        template.update("INSERT INTO votes (usernick, voice, threadid) VALUES (?::CITEXT, ?, ?) ON CONFLICT (usernick, threadid) DO UPDATE SET voice = ?;", vote.nickname, vote.voice, id, vote.voice)

        val thread = template.query("SELECT * FROM thread WHERE id = ?;",
                PreparedStatementSetter {
                    it.setInt(1, id)
                }, THREADMAPPER).firstOrNull() ?: throw ForumThreadNotFound()
        return thread
    }
}