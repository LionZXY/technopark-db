package technopark_db.repositories

import org.springframework.stereotype.Service
import technopark_db.data.MessageDao
import technopark_db.models.api.Post
import technopark_db.models.local.MessageLocal

@Service
class MessageRepository(private val messageDao: MessageDao) {
    fun create(slugOrId: String, posts: List<Post>): List<MessageLocal> {
        try {
            return messageDao.create(slugOrId, posts)
        } catch (e: Exception) {
            throw e
        }
    }
}