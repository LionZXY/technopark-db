package technopark_db.repositories

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import technopark_db.data.ForumDao
import technopark_db.models.api.Forum
import technopark_db.models.exceptions.ForumAlreadyCreated
import technopark_db.models.exceptions.UserNotFound
import technopark_db.models.local.ForumLocal
import technopark_db.models.local.ForumThreadLocal
import technopark_db.models.mappers.ForumMapper

@Service
class ForumRepository(private val dao: ForumDao,
                      private val mapper: ForumMapper) {
    fun createForum(forum: Forum): ForumLocal {
        try {
            return dao.create(forum)
        } catch (e: DuplicateKeyException) {
            throw ForumAlreadyCreated(mapper.map(dao.get(forum.slug!!)))
        } catch (e: DataIntegrityViolationException) {
            throw UserNotFound()
        }
    }

    fun get(slug: String): ForumLocal {
        return dao.get(slug)
    }

    fun getThreadsByForum(slug: String): List<ForumThreadLocal>{
        return dao.getThreadsByForum(slug)
    }
}