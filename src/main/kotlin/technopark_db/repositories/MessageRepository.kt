package technopark_db.repositories

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import technopark_db.data.MessageDao
import technopark_db.models.api.Post
import technopark_db.models.api.SortType
import technopark_db.models.exceptions.PostAlreadyCreated
import technopark_db.models.local.MessageLocal
import java.sql.BatchUpdateException

@Service
class MessageRepository(private val messageDao: MessageDao) {
    private val LOGGER = LoggerFactory.getLogger(MessageRepository::class.java)
    fun create(slugOrId: String, posts: List<Post>?): List<MessageLocal> {
        try {
            return messageDao.create(slugOrId, posts)
        } catch (e: BatchUpdateException) {
            if (e.nextException.message.equals("ERROR: invalid_foreign_key")) {
                throw PostAlreadyCreated();
            }
            throw e
        } catch (e: Exception){
            LOGGER.error("create", e)
            throw e
        }
    }

    fun update(post: Post): MessageLocal {
        return messageDao.update(post)
    }

    fun get(id: Int): MessageLocal {
        return messageDao.get(id)
    }

    fun getMessages(slugOrId: String, limit: Int, since: Int, desc: Boolean, sortType: SortType): List<MessageLocal> {
        return messageDao.getMessages(slugOrId, limit, since, desc, sortType)
    }
}