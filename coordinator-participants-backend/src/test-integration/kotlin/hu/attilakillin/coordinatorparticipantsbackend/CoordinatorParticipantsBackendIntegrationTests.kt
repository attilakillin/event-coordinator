package hu.attilakillin.coordinatorparticipantsbackend

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import hu.attilakillin.coordinatorparticipantsbackend.dal.Participant
import hu.attilakillin.coordinatorparticipantsbackend.dal.ParticipantRepository
import hu.attilakillin.coordinatorparticipantsbackend.dto.ParticipantRequestDTO
import hu.attilakillin.coordinatorparticipantsbackend.dto.ParticipantResponseDTO
import hu.attilakillin.coordinatorparticipantsbackend.services.AuthService
import io.jsonwebtoken.Jwts
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
class CoordinatorParticipantsBackendIntegrationTests {
	@MockkBean
	private lateinit var authService: AuthService

	@Autowired
	private lateinit var mvc: MockMvc

	@Autowired
	private lateinit var repository: ParticipantRepository

	private val mapper = jacksonObjectMapper().findAndRegisterModules()

	@BeforeEach
	fun initialize() {
		every { authService.getVerifiedClaims(any()) } returns Jwts.claims(mapOf("sub" to "any"))
		every { authService.validateClaims(any()) } returns true
		every { authService.isTokenValid(any()) } returns true

		repository.deleteAll()
	}

	@Test
	fun `getAllParticipants() succeeds with no participants`() {
		val response = mvc
			.get("/")
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val list = mapper.readValue(response, List::class.java)

		Assertions.assertEquals(0, list.size)
	}

	@Test
	fun `getAllParticipants() succeeds with one participant`() {
		repository.save(Participant(
			lastName = "Last",
			firstName = "First",
			email = "email@email.com",
			phoneNumber = null, address = null, notes = null
		))

		val response = mvc
			.get("/")
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val list = mapper.readValue(response, List::class.java)

		Assertions.assertEquals(1, list.size)
	}

	@Test
	fun `getParticipantByEmail() fails with nonexistent email`() {
		mvc
			.get("/email/email@email.com")
			.andExpect { status { isNotFound() } }
	}

	@Test
	fun `getParticipantByEmail() succeeds with existing email`() {
		val original = repository.save(Participant(
			lastName = "Last",
			firstName = "First",
			email = "email@email.com",
			phoneNumber = null, address = null, notes = null
		))

		val response = mvc
			.get("/email/email@email.com")
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val dto = mapper.readValue(response, ParticipantResponseDTO::class.java)

		Assertions.assertEquals(original.firstName, dto.firstName)
		Assertions.assertEquals(original.lastName, dto.lastName)
		Assertions.assertEquals(original.email, dto.email)
	}

	@Test
	fun `postParticipant() fails with invalid fields (empty first name)`() {
		val request = ParticipantRequestDTO(
			firstName = "",
			lastName = "Last",
			email = "email@email.com",
			phoneNumber = null, address = null, notes = null
		)

		mvc
			.post("/") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `postParticipant() fails with invalid fields (empty last name)`() {
		val request = ParticipantRequestDTO(
			firstName = "First",
			lastName = "",
			email = "email@email.com",
			phoneNumber = null, address = null, notes = null
		)

		mvc
			.post("/") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `postParticipant() fails with invalid fields (wrong email)`() {
		val request = ParticipantRequestDTO(
			firstName = "First",
			lastName = "Last",
			email = "wrong",
			phoneNumber = null, address = null, notes = null
		)

		mvc
			.post("/") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `postParticipant() succeeds with valid fields`() {
		val request = ParticipantRequestDTO(
			firstName = "First",
			lastName = "Last",
			email = "email@email.com",
			phoneNumber = null, address = null, notes = null
		)

		mvc
			.post("/") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isOk() } }
	}

	@Test
	fun `deleteParticipant() succeeds with nonexistent ID`() {
		mvc
			.delete("/1")
			.andExpect { status { isNoContent() } }
	}

	@Test
	fun `deleteParticipant() succeeds with existing ID`() {
		val participant = repository.save(Participant(
			lastName = "Last",
			firstName = "First",
			email = "email@email.com",
			phoneNumber = null, address = null, notes = null
		))

		mvc
			.delete("/${participant.id}")
			.andExpect { status { isNoContent() } }
	}

	@Test
	fun `validateParticipant() fails with nonexistent email`() {
		mvc
			.post("/validate") {
				contentType = MediaType.TEXT_PLAIN
				content = "email@email.com"
			}
			.andExpect { status { isNotFound() } }
	}

	@Test
	fun `validateParticipant() succeeds with existing email`() {
		val original = repository.save(Participant(
			lastName = "Last",
			firstName = "First",
			email = "email@email.com",
			phoneNumber = null, address = null, notes = null
		))

		mvc
			.post("/validate") {
				contentType = MediaType.TEXT_PLAIN
				content = original.email
			}
			.andExpect { status { isOk() } }
	}
}
