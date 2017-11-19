package technopark_db.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import technopark_db.models.api.ForumThread
import technopark_db.models.api.Post
import technopark_db.models.mappers.ForumThreadMapper
import technopark_db.models.mappers.MessageMapper
import technopark_db.repositories.ForumThreadRepository
import technopark_db.repositories.MessageRepository

@RestController
class ForumThreadController(private val forumRepository: ForumThreadRepository,
                            private val messageRepository: MessageRepository,
                            private val mapper: ForumThreadMapper,
                            private val messageMapper: MessageMapper) {
    @PostMapping("/forum/{slug}/create")
    fun create(@PathVariable slug: String, @RequestBody(required = false) forumThread: ForumThread): ResponseEntity<ForumThread> {
        forumThread.forum = slug
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.map(
                        forumRepository.create(forumThread)
                ))
    }

    @PostMapping("/thread/{slug_or_id}/create")
    fun createPosts(@PathVariable slug_or_id: String, @RequestBody(required = false) posts: List<Post>?): ResponseEntity<List<Post>> {
        if(posts == null || posts.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(emptyList<Post>())
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageRepository
                        .create(slug_or_id, posts)
                        .map {
                            messageMapper.map(it)
                        }
                )
    }
}