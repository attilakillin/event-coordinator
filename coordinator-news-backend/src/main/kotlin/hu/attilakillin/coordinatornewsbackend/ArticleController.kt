package hu.attilakillin.coordinatornewsbackend

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleController(
    private val repository: ArticleRepository
) {

    @GetMapping("/")
    fun getAllArticles(): List<Article> {
        return repository.findAll()
    }

    @PostMapping("/")
    fun postArticle() {

    }
}
