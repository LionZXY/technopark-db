package technopark_db.data

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Controller
import technopark_db.models.api.Post
import technopark_db.models.api.SortType
import technopark_db.models.exceptions.ForumNotFound
import technopark_db.models.exceptions.ForumThreadNotFound
import technopark_db.models.exceptions.UserNotFound
import technopark_db.models.local.MessageLocal
import technopark_db.utils.isNumeric
import java.sql.*


@Controller
class MessageDao(private val template: JdbcTemplate) {

    companion object {
        private const val COLUMN_USER = "tmp_nickname"
        private const val COLUMN_CREATED = "created"
        private const val COLUMN_FORUM = "tmp_forumslug"
        private const val COLUMN_ID = "id"
        private const val COLUMN_ISEDIT = "isedited"
        private const val COLUMN_TEXT = "message"
        private const val COLUMN_PARENTID = "parentid"
        private const val COLUMN_THREAD = "threadid"


        public val MESSAGEDAO = RowMapper<MessageLocal> { rs: ResultSet, _ ->
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

    fun create(idOrSlug: String, posts: List<Post>?): List<MessageLocal> {

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

        if (posts == null || posts.isEmpty()) {
            return listOf()
        }

        // Квота на ключи
        val idsResultSet = template.queryForRowSet("SELECT nextval('messages_id_seq') FROM generate_series(1, ?);", posts.size)

        val ps = connection.prepareStatement(
                "INSERT INTO messages (userid, tmp_nickname, id, message, parentid, threadid, tmp_threadslug, created, tmp_forumslug)\n" +
                        "  SELECT\n" +
                        "    usr.id,\n" +
                        "    usr.nickname,\n" +
                        "    ?,\n" +
                        "    ?,\n" +
                        "    ?,\n" +
                        "    ?,\n" +
                        "    ?,\n" +
                        "    ?::timestamptz,\n" +
                        "    ?\n" +
                        "  FROM \"user\" AS usr\n" +
                        "  WHERE usr.nickname = ? :: CITEXT;", Statement.NO_GENERATED_KEYS)
        posts.forEach({
            ps.apply {
                idsResultSet.next()
                it.id = idsResultSet.getInt(1)
                setInt(1, it.id)
                setString(2, it.message)
                setLong(3, it.parent)
                setInt(4, threadId)
                setString(5, threadSlug)
                setTimestamp(6, it.created ?: currentDate)
                setString(7, forumslug)
                setString(8, it.author)
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
            val changedArray = ps.executeBatch()
            val changed = ps2.executeUpdate()
            if (changedArray.contains(0)) {
                throw UserNotFound()
            }
            connection.commit()
        } catch (e: Exception) {
            connection.rollback()
            throw e
        } finally {
            connection.autoCommit = true
            connection.close()
        }

        returnVal = posts.map {
            MessageLocal(it.id,
                    currentDate,
                    false,
                    it.message,
                    threadId,
                    it.parent,
                    it.author!!,
                    forumslug)
        }


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
        if (!rs.next()) {
            throw ForumThreadNotFound()
        }
        return rs
    }

    fun get(id: Int): MessageLocal {
        return template.queryForObject("SELECT * FROM messages WHERE id = ?",
                MESSAGEDAO,
                id)
    }

    fun update(post: Post): MessageLocal {
        return template.queryForObject("UPDATE messages SET (message, isedited) = (coalesce(?, message), TRUE) WHERE id = ? RETURNING *;",
                MESSAGEDAO,
                post.message,
                post.id)
    }

    private fun getForumResultSet(con: Connection, threadId: Int): ResultSet {
        val prepareStatementForum = con.prepareStatement("SELECT\n" +
                "  forum.slug,\n" +
                "  forum.id\n" +
                "FROM thread \n" +
                "  JOIN forum ON thread.forumid = forum.id\n" +
                "WHERE thread.id = ?;")

        prepareStatementForum.setInt(1, threadId)
        val rs = prepareStatementForum.executeQuery()
        if (!rs.next()) {
            throw ForumNotFound()
        }
        return rs
    }

    fun getMessages(slugOrId: String, limit: Int, since: Int, desc: Boolean, sortType: SortType): List<MessageLocal> {
        var threadId = if (slugOrId.isNumeric()) {
            slugOrId.toInt()
        } else {
            val rs = template.queryForRowSet("SELECT id FROM thread WHERE slug = ?::CITEXT", slugOrId)
            rs.next()
            rs.getInt(1)
        }

        return sortType.sortSqlGeneration.getSql(template,
                threadId,
                limit,
                since,
                desc)
    }
}