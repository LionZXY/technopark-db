package technopark_db.repositories

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import technopark_db.data.ForumThreadDao
import technopark_db.models.api.ForumThread
import technopark_db.models.api.Vote
import technopark_db.models.exceptions.ThreadAlreadyCreated
import technopark_db.models.exceptions.UserNotFound
import technopark_db.models.local.ForumThreadLocal
import technopark_db.models.mappers.ForumThreadMapper
import technopark_db.utils.isNumeric

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
        } catch (e: EmptyResultDataAccessException) {
            throw UserNotFound()
        }
    }

    private fun getThreadBySlug(slug: String): ForumThreadLocal {
        return forumThreadDao.getBySlug(slug)
    }

    fun get(slugOrId: String): ForumThreadLocal {
        return if (slugOrId.isNumeric()) {
            forumThreadDao.getById(slugOrId.toInt())
        } else {
            forumThreadDao.getBySlug(slugOrId)
        }
    }

    fun vote(slugOrId: String, vote: Vote): ForumThreadLocal {
        return if (slugOrId.isNumeric()) {
            forumThreadDao.voteById(slugOrId.toInt(), vote)
        } else {
            forumThreadDao.voteBySlug(slugOrId, vote)
        }
    }
}