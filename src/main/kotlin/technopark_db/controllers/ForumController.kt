package technopark_db.controllers

import org.springframework.web.bind.annotation.*
import technopark_db.models.api.Forum
import technopark_db.models.mappers.ForumMapper
import technopark_db.repositories.ForumRepository

@RestController
class ForumController(private val repo: ForumRepository,
                      private val mapper: ForumMapper) {

    @PostMapping("/forum/create")
    fun create(@RequestBody(required = false) forum: Forum): Forum {
        return mapper.map(repo.createForum(forum))
    }

    @GetMapping("/forum/{slug}/details")
    fun get(@PathVariable slug: String): Forum {
        return mapper.map(repo.get(slug))
    }
}