package hu.attilakillin.coordinatorarticlesbackend.dal

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {

    @Query(value = "SELECT * FROM articles WHERE MATCH(title, text) AGAINST(?1)", nativeQuery = true)
    fun searchArticles(keywords: String, pageable: Pageable): Page<Article>
}
