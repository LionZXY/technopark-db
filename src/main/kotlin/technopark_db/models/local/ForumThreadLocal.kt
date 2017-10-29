package technopark_db.models.local

import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.OffsetDateTime

data class ForumThreadLocal(var localId: Int,
                            var authornick: String,
                            var created: Timestamp,
                            var messagetext: String,
                            var slug: String?,
                            var title: String,
                            var forumSlug: String,
                            var votes: Int = 0)