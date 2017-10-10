package technopark_db.controllers

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import technopark_db.models.api.Forum
import technopark_db.models.mappers.ForumMapper
import technopark_db.repositories.ForumRepository

@RestController
class ForumController(private val repo: ForumRepository,
                      private val mapper: ForumMapper) {

    @PostMapping("/forum/create")
    fun create(forum: Forum): Forum {
        return mapper.map(repo.createUser(forum))
    }
}