package technopark_db.repositories

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import technopark_db.data.ForumThreadDao
import technopark_db.models.api.ForumThread
import technopark_db.models.exceptions.ThreadAlreadyCreated
import technopark_db.models.local.ForumThreadLocal
import technopark_db.models.mappers.ForumThreadMapper

@Service
class ForumThreadRepository(private val forumThreadDao: ForumThreadDao,
                            private val mapper: ForumThreadMapper) {
    fun create(forum: ForumThread): ForumThreadLocal {
        try {
            return forumThreadDao.create(forum)
        } catch (e: DuplicateKeyException) {
            throw ThreadAlreadyCreated(mapper.map(get(forum.slug!!)))
        }
    }

    fun get(slug: String): ForumThreadLocal {
        return forumThreadDao.get(slug)
    }
}