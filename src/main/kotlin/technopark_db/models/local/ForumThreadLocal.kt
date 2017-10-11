package technopark_db.models.local

import java.sql.Date

data class ForumThreadLocal(var localId: Int,
                            var authornick: String,
                            var created: Date,
                            var messagetext: String,
                            var slug: String?,
                            var title: String,
                            var forumSlug: String,
                            var votes: Int = 0)