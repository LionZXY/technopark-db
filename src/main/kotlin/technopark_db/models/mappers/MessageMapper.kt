package technopark_db.models.mappers

import org.springframework.stereotype.Component
import technopark_db.models.api.Post
import technopark_db.models.local.MessageLocal

@Component
class MessageMapper : Mapper<MessageLocal, Post> {
    override fun map(input: MessageLocal)
            = Post(
            input.author,
            input.forumSlug,
            input.message,
            input.threadId,
            input.localId,
            input.created,
            input.isEdited,
            input.parentId)

}