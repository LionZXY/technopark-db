package technopark_db.models.utils

import org.springframework.jdbc.core.JdbcTemplate
import technopark_db.data.MessageDao
import technopark_db.models.local.MessageLocal

class FlatSort : SortSqlGeneration() {
    override fun getSql(template: JdbcTemplate, threadid: Int, limit: Int, since: Int, desc: Boolean): List<MessageLocal> {
        var argsObject = ArrayList<Any>()

        val sql = generate(threadid, limit, since, desc, argsObject)

        return template.query(sql, argsObject.toArray(), MessageDao.MESSAGEDAO)
    }

    private fun generate(threadid: Int, limit: Int, since: Int, desc: Boolean, argsObject: ArrayList<Any>): String {
        var sql = "SELECT * FROM messages WHERE threadid = ? "
        argsObject.add(threadid)


        if (since != -1) {
            sql += if (desc) {
                "AND id < ? "
            } else {
                "AND id > ? "
            }
            argsObject.add(since)
        }

        sql += if (desc) {
            "ORDER BY id DESC "
        } else {
            "ORDER BY id ASC "
        }

        if (limit != -1) {
            sql += "LIMIT ?"
            argsObject.add(limit)
        }
        return sql
    }
}