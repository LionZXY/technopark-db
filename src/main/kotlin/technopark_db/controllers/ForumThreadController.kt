package technopark_db.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import technopark_db.models.api.*
import technopark_db.models.mappers.ForumMapper
import technopark_db.models.mappers.ForumThreadMapper
import technopark_db.models.mappers.MessageMapper
import technopark_db.models.mappers.UserMapper
import technopark_db.repositories.ForumRepository
import technopark_db.repositories.ForumThreadRepository
import technopark_db.repositories.MessageRepository
import technopark_db.repositories.UserRepository

@RestController
class ForumThreadController(private val forumRepository: ForumRepository,
                            private val messageRepository: MessageRepository,
                            private val threadRepository: ForumThreadRepository,
                            private val userRepository: UserRepository,
                            private val userMapper: UserMapper,
                            private val forumMapper: ForumMapper,
                            private val mapper: ForumThreadMapper,
                            private val messageMapper: MessageMapper) {
    @PostMapping("/forum/{slug}/create")
    fun create(@PathVariable slug: String, @RequestBody(required = false) forumThread: ForumThread): ResponseEntity<ForumThread> {
        forumThread.forum = slug
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.map(
                        threadRepository.create(forumThread)
                ))
    }

    @PostMapping("/thread/{slug_or_id}/create")
    fun createPosts(@PathVariable slug_or_id: String, @RequestBody(required = false) posts: List<Post>?): ResponseEntity<List<Post>> {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageRepository
                        .create(slug_or_id, posts)
                        .map {
                            messageMapper.map(it)
                        }
                )
    }

    @GetMapping("/thread/{slug_or_id}/posts")
    fun getPosts(@PathVariable slug_or_id: String,
                 @RequestParam(required = false, defaultValue = "-1") limit: Int,
                 @RequestParam(required = false, defaultValue = "-1") since: Int,
                 @RequestParam(required = false, defaultValue = "false") desc: Boolean,
                 @RequestParam(required = false) sort: String? = null
    ): ResponseEntity<List<Post>> {
        val sortTypeEnum = SortType.valueOf(sort)

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messageRepository
                        .getMessages(slug_or_id, limit, since, desc, sortTypeEnum)
                        .map {
                            messageMapper.map(it)
                        })
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

    @PostMapping("/thread/{slug_or_id}/details")
    fun update(@PathVariable slug_or_id: String, @RequestBody(required = false) forumThread: ForumThread?): ResponseEntity<ForumThread> {
        if (forumThread == null) {
            return details(slug_or_id)
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.map(threadRepository
                        .update(slug_or_id, forumThread))
                )
    }

    @PostMapping("/post/{id}/details")
    fun updateMessage(@PathVariable id: String,
                      @RequestBody(required = false) message: Post?): ResponseEntity<Post> {
        if (message?.message == null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(info(id, null).body.post!!)
        }

        message.id = id.toInt()
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messageMapper.map(messageRepository.update(message)))
    }

    @GetMapping("/post/{id}/details")
    fun info(@PathVariable id: String,
             @RequestParam(required = false) related: Array<String>?): ResponseEntity<PostDetailedInfo> {
        val toOutput = PostDetailedInfo()
        toOutput.post = messageMapper.map(messageRepository.get(id.toInt()))

        if (related == null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(toOutput)
        }

        if (related.contains("user")) {
            toOutput.user = userMapper.map(userRepository.getUser(toOutput.post!!.author!!))
        }

        if (related.contains("forum")) {
            toOutput.forum = forumMapper.map(forumRepository.get(toOutput.post!!.forumSlug!!))
        }

        if (related.contains("thread")) {
            toOutput.thread = mapper.map(threadRepository.get(toOutput.post!!.threadId.toString()))
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(toOutput)
    }
}