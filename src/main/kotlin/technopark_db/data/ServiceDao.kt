package technopark_db.data

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import technopark_db.models.api.ServiceModel

@Service
class ServiceDao(private val template: JdbcTemplate) {
    companion object {
        private const val COLUMN_FORUM = "forum"
        private const val COLUMN_POST = "post"
        private const val COLUMN_THREAD = "thread"
        private const val COLUMN_USER = "users"

        private val SERVICE_MAPPER = RowMapper { res, _ ->
            ServiceModel(res.getInt(COLUMN_FORUM),
                    res.getInt(COLUMN_POST),
                    res.getInt(COLUMN_THREAD),
                    res.getInt(COLUMN_USER))
        }
    }

    fun info(): ServiceModel {
        val fptCount = template.queryForRowSet("SELECT\n" +
                "        count(*)     AS ${COLUMN_FORUM},\n" +
                "        sum(posts)   AS ${COLUMN_POST},\n" +
                "        sum(threads) AS ${COLUMN_THREAD}\n" +
                "      FROM forum")
        val userCount = template.queryForRowSet("SELECT count(*) AS ${COLUMN_USER} FROM \"user\"")
        fptCount.next()
        userCount.next()

        val output = ServiceModel()
        output.forum = fptCount.getInt(COLUMN_FORUM)
        output.post = fptCount.getInt(COLUMN_POST)
        output.thread = fptCount.getInt(COLUMN_THREAD)
        output.user = userCount.getInt(COLUMN_USER)

        return output
    }

    fun clear() {
        template.update("DELETE FROM messages;\n" +
                "DELETE FROM votes;\n" +
                "DELETE FROM thread;\n" +
                "DELETE FROM forum;\n" +
                "DELETE FROM \"user\";" +
                "DELETE FROM forum_user;")
    }
}