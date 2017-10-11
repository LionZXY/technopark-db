package technopark_db.controllers

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import technopark_db.models.api.ForumThread
import technopark_db.models.mappers.ForumThreadMapper
import technopark_db.repositories.ForumThreadRepository

@RestController
class ForumThreadController(private val forumRepository: ForumThreadRepository,
                            private val mapper: ForumThreadMapper) {
    @PostMapping("/forum/{slug}/create")
    fun create(@PathVariable slug: String, @RequestBody(required = false) forumThread: ForumThread): ForumThread {
        forumThread.forum = slug
        return mapper.map(forumRepository.create(forumThread))
    }
}