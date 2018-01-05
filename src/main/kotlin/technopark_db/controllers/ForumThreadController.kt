package technopark_db.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import technopark_db.models.api.ForumThread
import technopark_db.models.api.Post
import technopark_db.models.api.Vote
import technopark_db.models.mappers.ForumThreadMapper
import technopark_db.models.mappers.MessageMapper
import technopark_db.repositories.ForumThreadRepository
import technopark_db.repositories.MessageRepository

@RestController
class ForumThreadController(private val forumRepository: ForumThreadRepository,
                            private val messageRepository: MessageRepository,
                            private val threadRepository: ForumThreadRepository,
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
        if (posts == null || posts.isEmpty()) {
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

    @PostMapping("/thread/{slug_or_id}/vote")
    fun createVote(@PathVariable slug_or_id: String, @RequestBody(required = false) vote: Vote): ResponseEntity<ForumThread> {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.map(threadRepository
                        .vote(slug_or_id, vote))
                )
    }

    @GetMapping("/thread/{slug_or_id}/details")
    fun details(@PathVariable slug_or_id: String): ResponseEntity<ForumThread> {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.map(threadRepository
                        .get(slug_or_id))
                )
    }
}