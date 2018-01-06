package technopark_db.controllers

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import technopark_db.models.api.Forum
import technopark_db.models.api.ForumThread
import technopark_db.models.api.MyTimestamp
import technopark_db.models.mappers.ForumMapper
import technopark_db.models.mappers.ForumThreadMapper
import technopark_db.repositories.ForumRepository

@RestController
class ForumController(private val repo: ForumRepository,
                      private val mapper: ForumMapper,
                      private val mapperThread: ForumThreadMapper) {

    companion object {
        private val log = LoggerFactory.getLogger("controller")
    }

    @PostMapping("/forum/create")
    fun create(@RequestBody(required = false) forum: Forum): ResponseEntity<Forum> {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(repo.createForum(forum)))
    }

    @GetMapping("/forum/{slug}/details")
    fun get(@PathVariable slug: String): Forum {
        return mapper.map(repo.get(slug))
    }

    @GetMapping("/forum/{slug}/threads")
    fun getThreads(@PathVariable slug: String,
                   @RequestParam(required = false, defaultValue = "-1") limit: Long,
                   @RequestParam(required = false) since: MyTimestamp? = null,
                   @RequestParam(required = false, defaultValue = "false") desc: Boolean,
                   @RequestParam(required = false) sort: String? = null): List<ForumThread> {
        return repo.getThreadsByForum(slug, limit, since?.timestamp, desc).map { mapperThread.map(it) }
    }
}
