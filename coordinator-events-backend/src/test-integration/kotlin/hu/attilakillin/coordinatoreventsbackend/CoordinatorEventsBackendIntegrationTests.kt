package hu.attilakillin.coordinatoreventsbackend

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import hu.attilakillin.coordinatoreventsbackend.dal.CheckinStatus
import hu.attilakillin.coordinatoreventsbackend.dal.Event
import hu.attilakillin.coordinatoreventsbackend.dal.EventRepository
import hu.attilakillin.coordinatoreventsbackend.dto.EventRequestDTO
import hu.attilakillin.coordinatoreventsbackend.dto.EventResponseDTO
import hu.attilakillin.coordinatoreventsbackend.dto.EventSummaryDTO
import hu.attilakillin.coordinatoreventsbackend.dto.RegisterRequestDTO
import hu.attilakillin.coordinatoreventsbackend.services.AuthService
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
class CoordinatorEventsBackendIntegrationTests {
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
	fun `searchEvents() succeeds with no events`() {
		val response = mvc
			.get("/events/") {
				param("keywords", "")
			}
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val list = mapper.readValue(response, List::class.java)

		Assertions.assertEquals(0, list.size)
	}

	@Test
	fun `searchEvents() succeeds with one event`() {
		repository.save(Event(
			title = "Title",
			created = OffsetDateTime.now(),
			participants = mutableMapOf()
		))

		val response = mvc
			.get("/events/") {
				param("keywords", "")
			}
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val list = mapper.readValue(response, List::class.java)

		Assertions.assertEquals(1, list.size)
	}

	@Test
	fun `searchEvents() succeeds with filtered events`() {
		repository.save(Event(
			title = "Title",
			created = OffsetDateTime.now(),
			participants = mutableMapOf()
		))

		val response = mvc
			.get("/events/") {
				param("keywords", "something")
			}
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val list = mapper.readValue(response, List::class.java)

		Assertions.assertEquals(0, list.size)
	}

	@Test
	fun `getEventSummary() fails with bad ID structure`() {
		mvc
			.get("/events/wrong")
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `getEventSummary() fails with nonexistent ID`() {
		mvc
			.get("/events/1")
			.andExpect { status { isNotFound() } }
	}

	@Test
	fun `getEventSummary() succeeds with valid ID`() {
		val event = repository.save(Event(
			title = "Title",
			created = OffsetDateTime.now(),
			participants = mutableMapOf("email@email.com" to CheckinStatus.UNKNOWN)
		))

		val response = mvc
			.get("/events/${event.id}")
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val dto = mapper.readValue(response, EventSummaryDTO::class.java)

		Assertions.assertEquals(event.title, dto.title)
		Assertions.assertEquals(event.participants.size, dto.participants)
	}

	@Test
	fun `getEventDetails() fails with bad ID structure`() {
		mvc
			.get("/events/administer/wrong")
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `getEventDetails() fails with nonexistent ID`() {
		mvc
			.get("/events/administer/1")
			.andExpect { status { isNotFound() } }
	}

	@Test
	fun `getEventDetails() succeeds with valid ID`() {
		val event = repository.save(Event(
			title = "Title",
			created = OffsetDateTime.now(),
			participants = mutableMapOf("email@email.com" to CheckinStatus.UNKNOWN)
		))

		val response = mvc
			.get("/events/administer/${event.id}")
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val dto = mapper.readValue(response, EventResponseDTO::class.java)

		Assertions.assertEquals(event.title, dto.title)
		Assertions.assertEquals(event.participants.size, dto.participants.size)
	}

	@Test
	fun `postEvent() fails with empty title`() {
		val request = EventRequestDTO(
			title = ""
		)

		mvc
			.post("/events/administer") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `postEvent() succeeds with valid event`() {
		val request = EventRequestDTO(
			title = "Title"
		)

		mvc
			.post("/events/administer") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isCreated() } }
	}

	@Test
	fun `putEvent() fails with wrong ID structure`() {
		val request = EventRequestDTO(
			title = "Title"
		)

		mvc
			.put("/events/administer/wrong") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `putEvent() fails with nonexistent ID`() {
		val request = EventRequestDTO(
			title = "Title"
		)

		mvc
			.put("/events/administer/1") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isNotFound() } }
	}

	@Test
	fun `putEvent() succeeds with valid ID`() {
		val event = repository.save(Event(
			title = "Title",
			created = OffsetDateTime.now(),
			participants = mutableMapOf("email@email.com" to CheckinStatus.UNKNOWN)
		))

		val request = EventRequestDTO(
			title = "New"
		)

		mvc
			.put("/events/administer/${event.id}") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isOk() } }

		val modified = repository.findByIdOrNull(event.id)!!

		Assertions.assertEquals(request.title, modified.title)
	}

	@Test
	fun `deleteEvent() succeeds with nonexistent ID`() {
		mvc
			.delete("/events/administer/1")
			.andExpect { status { isNoContent() } }
	}

	@Test
	fun `deleteEvent() succeeds with existing ID`() {
		val event = repository.save(Event(
			title = "Title",
			created = OffsetDateTime.now(),
			participants = mutableMapOf("email@email.com" to CheckinStatus.UNKNOWN)
		))

		mvc
			.delete("/events/administer/${event.id}")
			.andExpect { status { isNoContent() } }
	}

	@Test
	fun `registerToEvent()`() {
		// Unable to test like the rest because it uses a RestTemplate POST call.
	}

	@Test
	fun `unregisterFromEvent() fails with invalid event ID`() {
		val request = RegisterRequestDTO(
			email = "wrong"
		)

		mvc
			.post("/events/unregister/1") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `unregisterFromEvent() succeeds with valid event ID and invalid participant`() {
		val event = repository.save(Event(
			title = "Title",
			created = OffsetDateTime.now(),
			participants = mutableMapOf("email@email.com" to CheckinStatus.UNKNOWN)
		))

		val request = RegisterRequestDTO(
			email = "wrong"
		)

		mvc
			.post("/events/unregister/${event.id}") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isOk() } }
	}

	@Test
	fun `unregisterFromEvent() succeeds with valid event ID and valid participant`() {
		val event = repository.save(Event(
			title = "Title",
			created = OffsetDateTime.now(),
			participants = mutableMapOf("email@email.com" to CheckinStatus.UNKNOWN)
		))

		val request = RegisterRequestDTO(
			email = "email@email.com"
		)

		mvc
			.post("/events/unregister/${event.id}") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isOk() } }
	}
}
