package hu.attilakillin.coordinatornewsbackend

import org.springframework.data.jpa.repository.JpaRepository

interface ArticleRepository : JpaRepository<Article, Int> {
}