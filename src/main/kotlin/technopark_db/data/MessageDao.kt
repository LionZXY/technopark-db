package technopark_db.data

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Controller
import technopark_db.models.api.Post
import technopark_db.models.local.MessageLocal
import java.sql.Date
import java.sql.ResultSet
import java.sql.Statement


@Controller
class MessageDao(private val template: JdbcTemplate) {

    companion object {
        private const val COLUMN_USER = "authornick"
        private const val COLUMN_CREATED = "created"
        private const val COLUMN_FORUM = "forumslug"
        private const val COLUMN_ID = "id"
        private const val COLUMN_ISEDIT = "isedit"
        private const val COLUMN_TEXT = "message"
        private const val COLUMN_PARENTID = "parentid"
        private const val COLUMN_THREAD = "threadid"


        val MESSAGEDAO = RowMapper<MessageLocal> { rs: ResultSet, _ ->
            MessageLocal(rs.getInt(COLUMN_ID),
                    rs.getDate(COLUMN_CREATED),
                    rs.getBoolean(COLUMN_ISEDIT),
                    rs.getString(COLUMN_TEXT),
                    rs.getInt(COLUMN_THREAD),
                    rs.getLong(COLUMN_PARENTID),
                    rs.getString(COLUMN_USER),
                    rs.getString(COLUMN_FORUM))
        }
    }

    fun create(threadId: Int, posts: List<Post>): List<MessageLocal> {
        var returnVal: List<MessageLocal>? = null
        val currentDate = Date(System.currentTimeMillis())
        template.dataSource.connection.let {
            it.autoCommit = false
            val ps = it.prepareStatement("INSERT INTO messages (authornick, message, parentid, threadid, created) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
            posts.forEach({
                ps.apply {
                    setString(1, it.author)
                    setString(2, it.message)
                    setLong(3, it.parent)
                    setInt(4, threadId)
                    setDate(5, currentDate)
                    addBatch()
                }
            })
            val ps2 = it.prepareStatement("UPDATE t2 " +
                    "SET t2.posts = t2.posts + ? " +
                    "FROM thread AS t1 " +
                    "JOIN forum AS t2 ON t1.forumslug = t2.slug " +
                    "WHERE t1.id = ?;").apply {
                setInt(1, posts.count())
                setInt(2, threadId)
            }
            try {
                ps.executeUpdate()
                ps2.executeUpdate()
                it.commit()
            } catch (e: Exception) {
                it.rollback()
                throw e
            }
            ps.generatedKeys.use { gk ->
                {
                    returnVal = posts.map {
                        MessageLocal(gk.getInt("id"),
                                currentDate,
                                false,
                                it.message,
                                threadId,
                                it.parent,
                                it.author!!)
                    }
                }
            }
        }
        return returnVal!!
    }
}