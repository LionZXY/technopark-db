package technopark_db.repositories

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import technopark_db.data.ForumThreadDao
import technopark_db.models.api.ForumThread
import technopark_db.models.exceptions.ThreadAlreadyCreated
import technopark_db.models.exceptions.UserNotFound
import technopark_db.models.local.ForumThreadLocal
import technopark_db.models.mappers.ForumThreadMapper
import technopark_db.utils.Constants

@Service
class ForumThreadRepository(private val forumThreadDao: ForumThreadDao,
                            private val mapper: ForumThreadMapper) {
    fun create(forum: ForumThread): ForumThreadLocal {
        try {
            return forumThreadDao.create(forum)
        } catch (e: DuplicateKeyException) {
            throw ThreadAlreadyCreated(mapper.map(getThreadBySlug(forum.slug!!)))
        } catch (e: DataIntegrityViolationException) {
            throw UserNotFound()
        }
    }

    private fun getThreadBySlug(slug: String): ForumThreadLocal {
        return forumThreadDao.getBySlug(slug)
    }

    fun get(slugOrId: String): ForumThreadLocal {
        return if (Constants.slugPatter.matcher(slugOrId).matches()) {
            forumThreadDao.getBySlug(slugOrId)
        } else {
            val id = Integer.valueOf(slugOrId)
            forumThreadDao.getById(id)
        }
    }
}