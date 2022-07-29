package net.catenax.traceability.assets.infrastructure.adapters.rest

import net.catenax.traceability.IntegrationSpec
import net.catenax.traceability.common.security.KeycloakRole
import org.hamcrest.Matchers
import org.springframework.http.MediaType

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasItems
import static org.hamcrest.Matchers.not
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AssetsControllerIT extends IntegrationSpec {

	def "should return assets for authenticated user with role"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)

		expect:
			mvc.perform(get("/assets/1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
	}

	def "should return assets with manufacturer name"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			bpnApiReturnsBusinessPartnerDataFor(
				"BPNL00000003AXS3",
				"BPNL00000003AYRE",
				"BPNL00000003B0Q0",
				"BPNL00000003B2OM",
				"BPNL00000003B3NX"
			)

		expect:
			mvc.perform(get("/assets").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.content[*].manufacturerName', not(equalTo("--"))))

		and:
			verifyKeycloakApiCalledOnceForToken()
			verifyBpnApiCalledForBusinessPartnerDetails(5)
	}

	def "should return assets with manufacturer name using values from cache"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			bpnApiReturnsBusinessPartnerDataFor(
				"BPNL00000003AXS3",
				"BPNL00000003AYRE",
				"BPNL00000003B0Q0",
				"BPNL00000003B2OM",
				"BPNL00000003B3NX"
			)

		when:
			0..3.each {
				mvc.perform(get("/assets").contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath('$.content[*].manufacturerName', not(equalTo("--"))))
			}

		then:
			verifyKeycloakApiCalledOnceForToken()
			verifyBpnApiCalledForBusinessPartnerDetails(5)
	}

	def "should return assets without manufacturer name when name was not returned by BPN API"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			bpnApiReturnsBusinessPartnerDataWithoutNamesFor(
				"BPNL00000003AXS3",
				"BPNL00000003AYRE",
				"BPNL00000003B0Q0",
				"BPNL00000003B2OM",
				"BPNL00000003B3NX"
			)

		expect:
			mvc.perform(get("/assets").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.content[*].manufacturerName', hasItems(equalTo("--"))))
	}

	def "should return assets without manufacturer name when BPN API has no data"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			bpnApiReturnsNoBusinessPartnerDataFor(
				"BPNL00000003AXS3",
				"BPNL00000003AYRE",
				"BPNL00000003B0Q0",
				"BPNL00000003B2OM",
				"BPNL00000003B3NX"
			)

		expect:
			mvc.perform(get("/assets").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.content[*].manufacturerName', hasItems(equalTo("--"))))
	}

	def "should return assets country map"() {
		given:
			authenticatedUser()

		expect:
			mvc.perform(get("/assets/countries").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
	}

	def "should not return assets country map when user is not authorized"() {
		given:
			unauthenticatedUser()

		expect:
			mvc.perform(get("/assets/countries").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
	}

	def "should not return assets when user is not authenticated"() {
		given:
			unauthenticatedUser()

		expect:
			mvc.perform(get("/assets/1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
	}

	def "should get a page of assets"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		expect:
			mvc.perform(get("/assets")
					.queryParam("page", "2")
					.queryParam("size", "2")
					.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.page', Matchers.is(2)))
				.andExpect(jsonPath('$.pageSize', Matchers.is(2)))
	}

	def "should not update quality type for not existing asset"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)

		expect:
			mvc.perform(patch("/assets/1234")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					asJson(
						[qualityType: 'Critical']
					)
				),
			)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath('$.message', equalTo("Asset with id 1234 was not found.")))
	}

	def "should not update quality type with invalid request body"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)

		expect:
			mvc.perform(patch("/assets/1234")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					asJson(
						requestBody
					)
				),
			)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath('$.message', equalTo(errorMessage)))

		where:
			requestBody                                | errorMessage
			[qualityType: 'NOT_EXISTING_QUALITY_TYPE'] | "Failed to deserialize request body."
			[qualityType: 'CRITICAL']                  | "Failed to deserialize request body."
			[qualityType: '']                          | "Failed to deserialize request body."
			[qualityType: ' ']                         | "Failed to deserialize request body."
			[qualityType: null]                        | "qualityType must be present"
	}

	def "should update quality type for existing asset"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			def existingAssetId = "urn:uuid:d3cca507-5515-4595-adb2-4c7706a75488"

		expect:
			mvc.perform(get("/assets/$existingAssetId").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.qualityType', equalTo("Ok")))

		and:
			authenticatedUser(KeycloakRole.ADMIN)

			mvc.perform(patch("/assets/$existingAssetId")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					asJson(
						[qualityType: 'Critical']
					)
				),
			)
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.qualityType', equalTo("Critical")))

		and:
			authenticatedUser(KeycloakRole.ADMIN)

			mvc.perform(get("/assets/$existingAssetId").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.qualityType', equalTo("Critical")))
	}
}
