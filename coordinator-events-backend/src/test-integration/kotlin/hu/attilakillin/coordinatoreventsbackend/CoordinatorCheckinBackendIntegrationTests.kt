package hu.attilakillin.coordinatoreventsbackend

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import hu.attilakillin.coordinatoreventsbackend.dal.CheckinStatus
import hu.attilakillin.coordinatoreventsbackend.dal.Event
import hu.attilakillin.coordinatoreventsbackend.dal.EventRepository
import hu.attilakillin.coordinatoreventsbackend.services.AuthService
import io.jsonwebtoken.Jwts
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.OffsetDateTime

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
class CoordinatorCheckinBackendIntegrationTests {
	@MockkBean
	private lateinit var authService: AuthService

	@Autowired
	private lateinit var mvc: MockMvc

	@Autowired
	private lateinit var repository: EventRepository

	private val mapper = jacksonObjectMapper().findAndRegisterModules()

	@BeforeEach
	fun initialize() {
		every { authService.getVerifiedClaims(any()) } returns Jwts.claims(mapOf("sub" to "any"))
		every { authService.validateClaims(any()) } returns true
		every { authService.isTokenValid(any()) } returns true

		repository.deleteAll()
	}

	@Test
	fun `retrieveAll() fails with bad event ID structure`() {
		mvc
			.get("/checkin/wrong")
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `retrieveAll() fails with invalid event ID`() {
		mvc
			.get("/checkin/1")
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `retrieveAll() succeeds with no participants`() {
		val event = repository.save(Event(
			title = "Title",
			created = OffsetDateTime.now(),
			participants = mutableMapOf()
		))

		val response = mvc
			.get("/checkin/${event.id}")
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val list = mapper.readValue(response, List::class.java)

		Assertions.assertEquals(0, list.size)
	}

	@Test
	fun `retrieveAll() succeeds with one participant`() {
		val event = repository.save(Event(
			title = "Title",
			created = OffsetDateTime.now(),
			participants = mutableMapOf("email@email.com" to CheckinStatus.UNKNOWN)
		))

		val response = mvc
			.get("/checkin/${event.id}")
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val list = mapper.readValue(response, List::class.java)

		Assertions.assertEquals(1, list.size)
	}
}
