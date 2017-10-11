package technopark_db.models.mappers

import org.springframework.stereotype.Component
import technopark_db.models.api.ForumThread
import technopark_db.models.local.ForumThreadLocal

@Component
class ForumThreadMapper : Mapper<ForumThreadLocal, ForumThread> {
    override fun map(input: ForumThreadLocal): ForumThread
            = ForumThread(input.authornick,
            input.messagetext,
            input.slug,
            input.title,
            input.created,
            input.forumSlug,
            input.localId,
            input.votes)

}