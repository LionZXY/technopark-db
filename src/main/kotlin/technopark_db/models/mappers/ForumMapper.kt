package technopark_db.models.mappers

import org.springframework.stereotype.Component
import technopark_db.models.api.Forum
import technopark_db.models.local.ForumLocal

@Component
class ForumMapper : Mapper<ForumLocal, Forum> {
    override fun map(input: ForumLocal): Forum {
        return Forum(input.slug,
                input.title,
                input.author,
                input.postCount,
                input.threads)
    }

}