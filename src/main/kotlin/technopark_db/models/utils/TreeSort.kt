package technopark_db.models.utils

import org.springframework.jdbc.core.JdbcTemplate
import technopark_db.models.local.MessageLocal

class TreeSort: SortSqlGeneration(){
    override fun getSql(template: JdbcTemplate, threadid: Int, limit: Int, since: Int, desc: Boolean): List<MessageLocal> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}