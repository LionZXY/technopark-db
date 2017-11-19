package technopark_db.data

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Controller
import technopark_db.models.api.Post
import technopark_db.models.local.MessageLocal
import technopark_db.utils.isNumeric
import java.sql.*


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
                    rs.getTimestamp(COLUMN_CREATED),
                    rs.getBoolean(COLUMN_ISEDIT),
                    rs.getString(COLUMN_TEXT),
                    rs.getInt(COLUMN_THREAD),
                    rs.getLong(COLUMN_PARENTID),
                    rs.getString(COLUMN_USER),
                    rs.getString(COLUMN_FORUM))
        }
    }

    fun create(idOrSlug: String, posts: List<Post>): List<MessageLocal> {
        // TODO: Просить квоту на ключи

        var returnVal: List<MessageLocal>
        val currentDate = Timestamp(System.currentTimeMillis())
        val connection = template.dataSource.connection

        connection.autoCommit = false

        val rsThread = getSlugAndIdByThread(connection, idOrSlug)
        val threadId = rsThread.getInt("id")
        val threadSlug = rsThread.getString("slug")

        val rsForum = getForumResultSet(connection, threadId)
        val forumslug = rsForum.getString("slug")
        val forumid = rsForum.getInt("id")

        val ps = connection.prepareStatement(
                "INSERT INTO messages (userid, tmp_nickname, message, parentid, threadid, tmp_threadslug, created)\n" +
                        "  SELECT\n" +
                        "    usr.id,\n" +
                        "    usr.nickname,\n" +
                        "    ?,\n" +
                        "    ?,\n" +
                        "    ?,\n" +
                        "    ?,\n" +
                        "    ?::timestamptz\n" +
                        "  FROM \"user\" AS usr\n" +
                        "  WHERE usr.nickname = ? :: CITEXT RETURNING id;", Statement.RETURN_GENERATED_KEYS)
        posts.forEach({
            ps.apply {
                setString(1, it.message)
                setLong(2, it.parent)
                setInt(3, threadId)
                setString(4, threadSlug)
                setTimestamp(5, it.created ?: currentDate)
                setString(6, it.author)
                addBatch()
            }
        })

        //TODO: Perfomance optimization
        val ps2 = connection.prepareStatement("UPDATE forum\n" +
                "SET posts = posts + ?\n" +
                "WHERE id = ?;").apply {
            setInt(1, posts.count())
            setInt(2, forumid)
        }
        try {
            ps.executeUpdate()
            ps2.executeUpdate()
            connection.commit()
        } catch (e: Exception) {
            connection.rollback()
            throw e
        }

        val gk = ps.generatedKeys
        returnVal = posts.map {
            gk.next()
            MessageLocal(gk.getInt("id"),
                    currentDate,
                    false,
                    it.message,
                    threadId,
                    it.parent,
                    it.author!!,
                    forumslug)
        }
        gk.close()

        return returnVal
    }

    private fun getSlugAndIdByThread(con: Connection, slugOrId: String): ResultSet {
        val ps: PreparedStatement
        if (slugOrId.isNumeric()) {
            ps = con.prepareStatement("SELECT id, slug FROM thread WHERE id = ?");
            ps.setInt(1, Integer.parseInt(slugOrId))
        } else {
            ps = con.prepareStatement("SELECT id, slug FROM thread WHERE slug = ?::CITEXT");
            ps.setString(1, slugOrId)
        }
        val rs = ps.executeQuery()
        rs.next()
        return rs
    }

    private inline fun getForumResultSet(con: Connection, threadId: Int): ResultSet {
        val prepareStatementForum = con.prepareStatement("SELECT\n" +
                "  forum.slug,\n" +
                "  forum.id\n" +
                "FROM thread \n" +
                "  JOIN forum ON thread.forumid = forum.id\n" +
                "WHERE thread.id = ?;")

        prepareStatementForum.setInt(1, threadId)
        val rs = prepareStatementForum.executeQuery()
        rs.next()
        return rs
    }
}