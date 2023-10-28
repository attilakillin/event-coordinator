package hu.attilakillin.coordinatorarticlesbackend.services

import hu.attilakillin.coordinatorarticlesbackend.dal.Article
import hu.attilakillin.coordinatorarticlesbackend.dal.ArticleRepository
import hu.attilakillin.coordinatorarticlesbackend.dto.ArticleRequestDTO
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.time.OffsetDateTime

@ExtendWith(MockKExtension::class)
class ArticleServiceTests {
    private val repository = mockk<ArticleRepository>()

    private val service = ArticleService(repository)

    private val testArticle = Article(
        title = "Title",
        content = "Content",
        text = "Content",
        created = OffsetDateTime.now()
    )

    @Test
    fun `saveArticle() fails when title is empty`() {
        val dto = ArticleRequestDTO( title = "", content = "content" )

        val result = service.saveArticle(dto)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `saveArticle() fails when text is empty`() {
        val dto = ArticleRequestDTO( title = "title", content = "" )

        val result = service.saveArticle(dto)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `saveArticle() saves when title and text are given`() {
        every { repository.save(any()) } returnsArgument 0

        val dto = ArticleRequestDTO( title = "Title", content = "Content" )

        val result = service.saveArticle(dto)

        verify(exactly = 1) { repository.save(any()) }
        Assertions.assertEquals(dto.title, result?.title)
        Assertions.assertEquals(dto.content, result?.content)
    }

    @Test
    fun `saveArticle() removes simple attack script`() {
        every { repository.save(any()) } returnsArgument 0

        val dto = ArticleRequestDTO( title = "title", content = "text<script>alert('Attack!');</script>" )

        val result = service.saveArticle(dto)

        verify(exactly = 1) { repository.save(any()) }
        Assertions.assertEquals(dto.title, result?.title)
        Assertions.assertEquals("text", result?.content)
    }

    @Test
    fun `updateArticle() returns null with nonexistent ID`() {
        every { repository.findByIdOrNull(any()) } returns null

        val dto = ArticleRequestDTO( title = "Title", content = "Content" )

        val result = service.updateArticle(0, dto)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `updateArticle() fails when title is empty`() {
        every { repository.findByIdOrNull(any()) } returns testArticle

        val dto = ArticleRequestDTO( title = "", content = "content" )

        val result = service.updateArticle(0, dto)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `updateArticle() fails when text is empty`() {
        every { repository.findByIdOrNull(any()) } returns testArticle

        val dto = ArticleRequestDTO( title = "title", content = "" )

        val result = service.updateArticle(0, dto)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `updateArticle() saves when title and text are given`() {
        every { repository.findByIdOrNull(any()) } returns testArticle
        every { repository.save(any()) } returnsArgument 0

        val dto = ArticleRequestDTO( title = "Title", content = "Content" )

        val result = service.updateArticle(0, dto)

        verify(exactly = 1) { repository.save(any()) }
        Assertions.assertEquals(dto.title, result?.title)
        Assertions.assertEquals(dto.content, result?.content)
    }

    @Test
    fun `updateArticle() removes simple attack script`() {
        every { repository.findByIdOrNull(any()) } returns testArticle
        every { repository.save(any()) } returnsArgument 0

        val dto = ArticleRequestDTO( title = "title", content = "text<script>alert('Attack!');</script>" )

        val result = service.updateArticle(0, dto)

        verify(exactly = 1) { repository.save(any()) }
        Assertions.assertEquals(dto.title, result?.title)
        Assertions.assertEquals("text", result?.content)
    }

    @Test
    fun `deleteArticle() just runs`() {
        every { repository.deleteById(any()) } just runs

        Assertions.assertDoesNotThrow { service.deleteArticle(0) }
    }

    @Test
    fun `publishArticle() fails with invalid article ID`() {
        every { repository.findByIdOrNull(any()) } returns null

        val result = service.publishArticle(0)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `publishArticle() fails with already published article`() {
        every { repository.findByIdOrNull(any()) } returns
                testArticle.copy(published = true)

        val result = service.publishArticle(0)

        verify(exactly = 0) { repository.save(any()) }
        Assertions.assertEquals(null, result)
    }

    @Test
    fun `publishArticle() succeeds with correct arguments`() {
        every { repository.findByIdOrNull(any()) } returns testArticle
        every { repository.save(any()) } returnsArgument 0

        val result = service.publishArticle(0)

        verify(exactly = 1) { repository.save(any()) }
        Assertions.assertEquals(testArticle.title, result?.title)
        Assertions.assertEquals(testArticle.content, result?.content)
        Assertions.assertEquals(true, result?.published)
    }

    @Test
    fun `searchArticles()`() {
        // Cannot test properly, as the logic is written inside the database queries themselves.
    }

    @Test
    fun `getArticle returns null with invalid ID`() {
        every { repository.findByIdOrNull(any()) } returns null

        val result = service.getArticle(0)

        Assertions.assertEquals(null, result)
    }

    @Test
    fun `getArticle returns article with valid ID`() {
        every { repository.findByIdOrNull(any()) } returns testArticle

        val result = service.getArticle(0)

        Assertions.assertEquals(testArticle, result)
    }
}

