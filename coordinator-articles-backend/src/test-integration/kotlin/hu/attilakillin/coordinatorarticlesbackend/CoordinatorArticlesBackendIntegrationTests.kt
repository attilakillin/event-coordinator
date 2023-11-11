package hu.attilakillin.coordinatorarticlesbackend

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import hu.attilakillin.coordinatorarticlesbackend.dal.Article
import hu.attilakillin.coordinatorarticlesbackend.dal.ArticleRepository
import hu.attilakillin.coordinatorarticlesbackend.dto.ArticleRequestDTO
import hu.attilakillin.coordinatorarticlesbackend.dto.ArticleResponseDTO
import hu.attilakillin.coordinatorarticlesbackend.services.AuthService
import io.jsonwebtoken.Jwts
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.*
import java.time.OffsetDateTime

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
class CoordinatorArticlesBackendIntegrationTests {
	@MockkBean
	private lateinit var authService: AuthService

	@Autowired
	private lateinit var mvc: MockMvc

	@Autowired
	private lateinit var repository: ArticleRepository

	private val mapper = jacksonObjectMapper().findAndRegisterModules()

	@BeforeEach
	fun initialize() {
		every { authService.getVerifiedClaims(any()) } returns Jwts.claims(mapOf("sub" to "any"))
		every { authService.validateClaims(any()) } returns true
		every { authService.isTokenValid(any()) } returns true

		repository.deleteAll()
	}

	@Test
	fun `searchPublishedArticles() succeeds with no articles`() {
		val response = mvc
			.get("/published") {
				param("keywords", "")
			}
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val list = mapper.readValue(response, List::class.java)

		Assertions.assertEquals(0, list.size)
	}

	@Test
	fun `searchPublishedArticles() succeeds with one article`() {
		repository.save(Article(
			created = OffsetDateTime.now(),
			title = "Title",
			content = "",
			text = "",
			published = true
		))

		val response = mvc
			.get("/published") {
				param("keywords", "")
			}
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val list = mapper.readValue(response, List::class.java)

		Assertions.assertEquals(1, list.size)
	}

	@Test
	fun `searchPublishedArticles() succeeds with filtered articles`() {
		repository.save(Article(
			created = OffsetDateTime.now(),
			title = "Title",
			content = "Content",
			text = "",
			published = true
		))

		val response = mvc
			.get("/published") {
				param("keywords", "something")
			}
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val list = mapper.readValue(response, List::class.java)

		Assertions.assertEquals(0, list.size)
	}

	@Test
	fun `searchDraftArticles() succeeds with no articles`() {
		val response = mvc
			.get("/drafts") {
				param("keywords", "")
			}
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val list = mapper.readValue(response, List::class.java)

		Assertions.assertEquals(0, list.size)
	}


	@Test
	fun `searchDraftArticles() succeeds with one article`() {
		repository.save(Article(
			created = OffsetDateTime.now(),
			title = "Title",
			content = "",
			text = ""
		))

		val response = mvc
			.get("/drafts") {
				param("keywords", "")
			}
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val list = mapper.readValue(response, List::class.java)

		Assertions.assertEquals(1, list.size)
	}

	@Test
	fun `searchDraftArticles() succeeds with filtered articles`() {
		repository.save(Article(
			created = OffsetDateTime.now(),
			title = "Title",
			content = "Content",
			text = ""
		))

		val response = mvc
			.get("/published") {
				param("keywords", "something")
			}
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val list = mapper.readValue(response, List::class.java)

		Assertions.assertEquals(0, list.size)
	}

	@Test
	fun `getArticle() fails with nonexistent ID`() {
		mvc
			.get("/administer/1")
			.andExpect { status { isNotFound() } }
	}

	@Test
	fun `getArticle() succeeds with valid ID`() {
		val article = repository.save(Article(
			created = OffsetDateTime.now(),
			title = "Title",
			content = "Content",
			text = ""
		))

		val response = mvc
			.get("/administer/${article.id}")
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val dto = mapper.readValue(response, ArticleResponseDTO::class.java)

		Assertions.assertEquals(article.title, dto.title)
		Assertions.assertEquals(article.content, dto.content)
	}

	@Test
	fun `postArticle() fails with empty title`() {
		val request = ArticleRequestDTO(
			title = "",
			content = "Content"
		)

		mvc
			.post("/administer") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `postArticle() fails with empty content`() {
		val request = ArticleRequestDTO(
			title = "Title",
			content = ""
		)

		mvc
			.post("/administer") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `postArticle() succeeds with valid fields`() {
		val request = ArticleRequestDTO(
			title = "Title",
			content = "Content"
		)

		mvc
			.post("/administer") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isCreated() } }
	}

	@Test
	fun `putArticle() fails with wrong ID structure`() {
		val request = ArticleRequestDTO(
			title = "New",
			content = "New"
		)

		mvc
			.put("/administer/wrong") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `putArticle() fails with nonexistent ID`() {
		val request = ArticleRequestDTO(
			title = "New",
			content = "New"
		)

		mvc
			.put("/administer/1") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isNotFound() } }
	}

	@Test
	fun `putArticle() succeeds with valid ID`() {
		val article = repository.save(Article(
			created = OffsetDateTime.now(),
			title = "Title",
			content = "Content",
			text = ""
		))

		val request = ArticleRequestDTO(
			title = "New",
			content = "New"
		)

		mvc
			.put("/administer/${article.id}") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isOk() } }

		val modified = repository.findByIdOrNull(article.id)!!

		Assertions.assertEquals(request.title, modified.title)
		Assertions.assertEquals(request.content, modified.content)
	}

	@Test
	fun `deleteArticle() succeeds with nonexistent ID`() {
		mvc
			.delete("/administer/1")
			.andExpect { status { isNoContent() } }
	}

	@Test
	fun `deleteArticle() succeeds with existing ID`() {
		val article = repository.save(Article(
			created = OffsetDateTime.now(),
			title = "Title",
			content = "Content",
			text = ""
		))

		mvc
			.delete("/administer/${article.id}")
			.andExpect { status { isNoContent() } }
	}

	@Test
	fun `publishArticle() fails with nonexistent article`() {
		mvc
			.post("/administer/1/publish")
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `publishArticle() fails if article isn't draft`() {
		val article = repository.save(Article(
			created = OffsetDateTime.now(),
			title = "Title",
			content = "Content",
			text = "",
			published = true
		))

		mvc
			.post("/administer/${article.id}/publish")
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `publishArticle() succeeds if everything valid`() {
		val article = repository.save(Article(
			created = OffsetDateTime.now(),
			title = "Title",
			content = "Content",
			text = ""
		))

		mvc
			.post("/administer/${article.id}/publish")
			.andExpect { status { isOk() } }

		val modified = repository.findByIdOrNull(article.id)!!

		Assertions.assertEquals(article.title, modified.title)
		Assertions.assertEquals(true, modified.published)
	}
}
