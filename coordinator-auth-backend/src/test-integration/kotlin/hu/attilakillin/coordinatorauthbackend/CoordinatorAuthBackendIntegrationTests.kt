package hu.attilakillin.coordinatorauthbackend

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.attilakillin.coordinatorauthbackend.dto.AdministratorDTO
import hu.attilakillin.coordinatorauthbackend.dto.JwtDTO
import hu.attilakillin.coordinatorauthbackend.dto.ValidationDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
class CoordinatorAuthBackendIntegrationTests {
	@Autowired private lateinit var mvc: MockMvc

	private val mapper = jacksonObjectMapper().findAndRegisterModules()

	@Test
	fun `login() succeeds with valid admin credentials`() {
		val request = AdministratorDTO(
			username = "admin",
			password = "admin"
		)

		val response = mvc
			.post("/login") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val dto = mapper.readValue(response, JwtDTO::class.java)

		Assertions.assertNotEquals(0, dto.token.length)
	}

	@Test
	fun `login() fails with invalid admin credentials (wrong password)`() {
		val request = AdministratorDTO(
			username = "admin",
			password = "wrong"
		)

		mvc
			.post("/login") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `login() fails with invalid admin credentials (wrong username)`() {
		val request = AdministratorDTO(
			username = "something",
			password = "other"
		)

		mvc
			.post("/login") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isBadRequest() } }
	}

	@Test
	fun `validate() fails with invalid token`() {
		val request = JwtDTO(
			token = "something"
		)

		val response = mvc
			.post("/validate") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
			}
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val dto = mapper.readValue(response, ValidationDTO::class.java)

		Assertions.assertEquals(false, dto.valid)
	}

	@Test
	fun `validate() succeeds with valid token`() {
		val loginRequest = AdministratorDTO(
			username = "admin",
			password = "admin"
		)

		val loginResponse = mvc
			.post("/login") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(loginRequest)
			}
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val validateRequest = mapper.readValue(loginResponse, JwtDTO::class.java)

		val validateResponse = mvc
			.post("/validate") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(validateRequest)
			}
			.andExpect { status { isOk() } }
			.andReturn().response.contentAsString

		val dto = mapper.readValue(validateResponse, ValidationDTO::class.java)

		Assertions.assertEquals(true, dto.valid)
	}
}
