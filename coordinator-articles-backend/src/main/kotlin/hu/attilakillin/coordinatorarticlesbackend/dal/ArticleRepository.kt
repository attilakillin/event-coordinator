package hu.attilakillin.coordinatorarticlesbackend.dal

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * JPA repository. The database table is initialized from the 'schema.sql' resource file.
 * In order to use full-text indexing, the underlying database engine must be MySQL.
 */
@Repository
interface ArticleRepository : JpaRepository<Article, Long> {

    /**
     * Return a page of all articles that match on the specified keywords
     * either in their title or in their text field.
     * Based on the published flag, returns either a list of published, or a list
     * of draft articles.
     *
     * Orders the results in a semi-intelligent way.
     */
    @Query(value = """
        SELECT * FROM articles
        WHERE published = :published
        AND (title LIKE CONCAT('%', :keywords, '%') OR text LIKE CONCAT('%', :keywords, '%'))
        ORDER BY
            title LIKE CONCAT(:keywords, '%') desc,
            IFNULL(NULLIF(INSTR(title, CONCAT(' ', :keywords)), 0), 99999),
            IFNULL(NULLIF(INSTR(title, :keywords), 0), 99999),
            
            text LIKE CONCAT(:keywords, '%') desc,
            IFNULL(NULLIF(INSTR(text, CONCAT(' ', :keywords)), 0), 99999),
            IFNULL(NULLIF(INSTR(text, :keywords), 0), 99999),
            
            created DESC;
    """, nativeQuery = true)
    fun searchArticlesByPublication(keywords: String, published: Boolean, pageable: Pageable): Page<Article>

    /**
     * Return a page of all articles.
     * Based on the published flag, returns either a list of published, or a list
     * of draft articles in descending order of their creation.
     */
    @Query(value = """
        SELECT * FROM articles
        WHERE published = :published
        ORDER BY created DESC
    """, nativeQuery = true)
    fun findAllArticlesByPublication(published: Boolean): List<Article>
}
