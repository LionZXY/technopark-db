package technopark_db.models.utils

import org.springframework.jdbc.core.JdbcTemplate
import technopark_db.data.MessageDao
import technopark_db.models.local.MessageLocal
import java.util.*

class PTreeSort : SortSqlGeneration() {
    override fun getSql(template: JdbcTemplate, threadid: Int, limit: Int, since: Int, desc: Boolean): List<MessageLocal> {
        var argsObject = ArrayList<Any>()

        var sql = """SELECT *
          FROM messages
          WHERE path[1] IN (
            SELECT id
            FROM messages
            WHERE threadid = ? AND parentid = 0 """

        argsObject.add(threadid)


        if (since != -1) {
            sql += if (desc) {
                "AND path < (SELECT path FROM messages WHERE id = ?) "
            } else {
                "AND path > (SELECT path FROM messages Where id = ?) "
            }
            argsObject.add(since)
        }

        sql += if (desc) {
            "ORDER BY path DESC, id DESC "
        } else {
            "ORDER BY path ASC, id ASC "
        }

        if (limit != -1) {
            sql += "LIMIT ?"
            argsObject.add(limit)
        }

        sql += ") "


        sql += if (desc) {
            "ORDER BY path DESC, id DESC "
        } else {
            "ORDER BY path ASC, id ASC "
        }


        return template.query(sql, argsObject.toArray(), MessageDao.MESSAGEDAO)
    }

}