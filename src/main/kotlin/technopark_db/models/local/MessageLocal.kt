package technopark_db.models.local

import java.sql.Date
import java.sql.Timestamp

data class MessageLocal(
        var localId: Int,
        var created: Timestamp,
        var isEdited: Boolean,
        var message: String,
        var threadId: Int = 0,
        var parentId: Long = 0,
        var author: String,
        var forumSlug: String = ""
)