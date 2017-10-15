package technopark_db.repositories

import org.springframework.stereotype.Service
import technopark_db.data.MessageDao
import technopark_db.models.api.Post
import technopark_db.models.local.MessageLocal
import technopark_db.utils.isSlug

@Service
class MessageRepository(private val messageDao: MessageDao) {
    fun create(slugOrId: String, posts: List<Post>): List<MessageLocal> {
        if (slugOrId.isSlug()) {
            throw NotImplementedError()
        } else {
            val threadId = Integer.valueOf(slugOrId)
            return messageDao.create(threadId, posts)
        }
    }
}