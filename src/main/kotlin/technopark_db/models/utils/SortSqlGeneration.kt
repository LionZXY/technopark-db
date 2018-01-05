package technopark_db.models.utils

import org.springframework.jdbc.core.JdbcTemplate
import technopark_db.models.local.MessageLocal

abstract class SortSqlGeneration {
    abstract fun getSql(template: JdbcTemplate, threadid: Int, limit: Int, since: Int, desc: Boolean): List<MessageLocal>
}
