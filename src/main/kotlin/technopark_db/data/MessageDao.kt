package technopark_db.data

import org.springframework.jdbc.core.RowMapper
import technopark_db.models.local.ForumThreadLocal
import java.sql.ResultSet

class MessageDao {

    companion object {
        private const val COLUMN_USER = "authornick"
        private const val COLUMN_CREATED = "created"
        private const val COLUMN_FORUM = "forumslug"
        private const val COLUMN_ID = "id"
        private const val COLUMN_ISEDIT = "isedit"
        private const val COLUMN_TEXT = "message"
        private const val COLUMN_PARENTID = "parentid"
        private const val COLUMN_THREAD = "threadid"

    }

}