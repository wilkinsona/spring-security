/*
 * Copyright 2004-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.jackson;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import org.json.JSONException;
import org.junit.jupiter.api.TestTemplate;
import org.skyscreamer.jsonassert.JSONAssert;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.junit.jackson.JsonMixinTest;
import org.springframework.security.junit.jackson.JsonProcessor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jitendra Singh
 * @author Greg Turnquist
 * @author Onur Kagan Ozcan
 * @since 4.2
 */
@JsonMixinTest
public class UsernamePasswordAuthenticationTokenMixinTests extends AbstractMixinTests {

	private static final String AUTHENTICATED_JSON = "{"
			+ "\"@class\": \"org.springframework.security.authentication.UsernamePasswordAuthenticationToken\","
			+ "\"principal\": " + UserDeserializerTests.USER_JSON + ", " + "\"credentials\": \"1234\", "
			+ "\"authenticated\": true, " + "\"details\": null, " + "\"authorities\": "
			+ SimpleGrantedAuthorityMixinTests.AUTHORITIES_ARRAYLIST_JSON + "}";

	public static final String AUTHENTICATED_STRINGPRINCIPAL_JSON = AUTHENTICATED_JSON
		.replace(UserDeserializerTests.USER_JSON, "\"admin\"");

	private static final String NON_USER_PRINCIPAL_JSON = "{"
			+ "\"@class\": \"org.springframework.security.jackson.UsernamePasswordAuthenticationTokenMixinTests$NonUserPrincipal\", "
			+ "\"username\": \"admin\"" + "}";

	private static final String AUTHENTICATED_STRINGDETAILS_JSON = AUTHENTICATED_JSON.replace("\"details\": null, ",
			"\"details\": \"details\", ");

	private static final String AUTHENTICATED_NON_USER_PRINCIPAL_JSON = AUTHENTICATED_JSON
		.replace(UserDeserializerTests.USER_JSON, NON_USER_PRINCIPAL_JSON)
		.replaceAll(UserDeserializerTests.USER_PASSWORD, "null")
		.replace(SimpleGrantedAuthorityMixinTests.AUTHORITIES_ARRAYLIST_JSON,
				SimpleGrantedAuthorityMixinTests.NO_AUTHORITIES_ARRAYLIST_JSON);

	private static final String UNAUTHENTICATED_STRINGPRINCIPAL_JSON = AUTHENTICATED_STRINGPRINCIPAL_JSON
		.replace("\"authenticated\": true, ", "\"authenticated\": false, ")
		.replace(SimpleGrantedAuthorityMixinTests.AUTHORITIES_ARRAYLIST_JSON,
				SimpleGrantedAuthorityMixinTests.EMPTY_AUTHORITIES_ARRAYLIST_JSON);

	@TestTemplate
	public void serializeUnauthenticatedUsernamePasswordAuthenticationTokenMixinTest(JsonProcessor jsonProcessor)
			throws JSONException {
		UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated("admin",
				"1234");
		String serializedJson = jsonProcessor.serialize(token);
		JSONAssert.assertEquals(UNAUTHENTICATED_STRINGPRINCIPAL_JSON, serializedJson, true);
	}

	@TestTemplate
	public void serializeAuthenticatedUsernamePasswordAuthenticationTokenMixinTest(JsonProcessor jsonProcessor)
			throws JSONException {
		User user = createDefaultUser();
		UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken
			.authenticated(user.getUsername(), user.getPassword(), user.getAuthorities());
		String serializedJson = jsonProcessor.serialize(token);
		JSONAssert.assertEquals(AUTHENTICATED_STRINGPRINCIPAL_JSON, serializedJson, true);
	}

	@TestTemplate
	public void deserializeUnauthenticatedUsernamePasswordAuthenticationTokenMixinTest(JsonProcessor jsonProcessor) {
		UsernamePasswordAuthenticationToken token = jsonProcessor.deserialize(UNAUTHENTICATED_STRINGPRINCIPAL_JSON,
				UsernamePasswordAuthenticationToken.class);
		assertThat(token).isNotNull();
		assertThat(token.isAuthenticated()).isEqualTo(false);
		assertThat(token.getAuthorities()).isNotNull().hasSize(0);
	}

	@TestTemplate
	public void deserializeAuthenticatedUsernamePasswordAuthenticationTokenMixinTest(JsonProcessor jsonProcessor) {
		UsernamePasswordAuthenticationToken expectedToken = createToken();
		UsernamePasswordAuthenticationToken token = jsonProcessor.deserialize(AUTHENTICATED_STRINGPRINCIPAL_JSON,
				UsernamePasswordAuthenticationToken.class);
		assertThat(token).isNotNull();
		assertThat(token.isAuthenticated()).isTrue();
		assertThat(token.getAuthorities()).isEqualTo(expectedToken.getAuthorities());
	}

	@TestTemplate
	public void serializeAuthenticatedUsernamePasswordAuthenticationTokenMixinWithUserTest(JsonProcessor jsonProcessor)
			throws JSONException {
		UsernamePasswordAuthenticationToken token = createToken();
		String actualJson = jsonProcessor.serialize(token);
		JSONAssert.assertEquals(AUTHENTICATED_JSON, actualJson, true);
	}

	@TestTemplate
	public void deserializeAuthenticatedUsernamePasswordAuthenticationTokenWithUserTest(JsonProcessor jsonProcessor) {
		UsernamePasswordAuthenticationToken token = jsonProcessor.deserialize(AUTHENTICATED_JSON,
				UsernamePasswordAuthenticationToken.class);
		assertThat(token).isNotNull();
		assertThat(token.getPrincipal()).isNotNull().isInstanceOf(User.class);
		assertThat(((User) token.getPrincipal()).getAuthorities()).isNotNull()
			.hasSize(1)
			.contains(new SimpleGrantedAuthority("ROLE_USER"));
		assertThat(token.isAuthenticated()).isEqualTo(true);
		assertThat(token.getAuthorities()).hasSize(1).contains(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@TestTemplate
	public void serializeAuthenticatedUsernamePasswordAuthenticationTokenMixinAfterEraseCredentialInvoked(
			JsonProcessor jsonProcessor) throws JSONException {
		UsernamePasswordAuthenticationToken token = createToken();
		token.eraseCredentials();
		String actualJson = jsonProcessor.serialize(token);
		JSONAssert.assertEquals(AUTHENTICATED_JSON.replaceAll(UserDeserializerTests.USER_PASSWORD, "null"), actualJson,
				true);
	}

	@TestTemplate
	public void serializeAuthenticatedUsernamePasswordAuthenticationTokenMixinWithNonUserPrincipalTest(
			JsonProcessor jsonProcessor) throws JSONException {
		NonUserPrincipal principal = new NonUserPrincipal();
		principal.setUsername("admin");
		UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.authenticated(principal, null,
				new ArrayList<>());
		String actualJson = jsonProcessor.serialize(token);
		JSONAssert.assertEquals(AUTHENTICATED_NON_USER_PRINCIPAL_JSON, actualJson, true);
	}

	@TestTemplate
	public void deserializeAuthenticatedUsernamePasswordAuthenticationTokenWithNonUserPrincipalTest(
			JsonProcessor jsonProcessor) {
		UsernamePasswordAuthenticationToken token = jsonProcessor.deserialize(AUTHENTICATED_NON_USER_PRINCIPAL_JSON,
				UsernamePasswordAuthenticationToken.class);
		assertThat(token).isNotNull();
		assertThat(token.getPrincipal()).isNotNull().isInstanceOf(NonUserPrincipal.class);
	}

	@TestTemplate
	public void deserializeAuthenticatedUsernamePasswordAuthenticationTokenWithDetailsTest(
			JsonProcessor jsonProcessor) {
		UsernamePasswordAuthenticationToken token = jsonProcessor.deserialize(AUTHENTICATED_STRINGDETAILS_JSON,
				UsernamePasswordAuthenticationToken.class);
		assertThat(token).isNotNull();
		assertThat(token.getPrincipal()).isNotNull().isInstanceOf(User.class);
		assertThat(((User) token.getPrincipal()).getAuthorities()).isNotNull()
			.hasSize(1)
			.contains(new SimpleGrantedAuthority("ROLE_USER"));
		assertThat(token.isAuthenticated()).isEqualTo(true);
		assertThat(token.getAuthorities()).hasSize(1).contains(new SimpleGrantedAuthority("ROLE_USER"));
		assertThat(token.getDetails()).isExactlyInstanceOf(String.class).isEqualTo("details");
	}

	@TestTemplate
	public void serializingThenDeserializingWithNoCredentialsOrDetailsShouldWork(JsonProcessor jsonProcessor) {
		UsernamePasswordAuthenticationToken original = UsernamePasswordAuthenticationToken.unauthenticated("Frodo",
				null);
		String serialized = jsonProcessor.serialize(original);
		UsernamePasswordAuthenticationToken deserialized = jsonProcessor.deserialize(serialized,
				UsernamePasswordAuthenticationToken.class);
		assertThat(deserialized).isEqualTo(original);
	}

	// TODO Mapper customization
	// @TestTemplate
	// public void
	// serializingThenDeserializingWithConfiguredObjectMapperShouldWork(JsonProcessor
	// jsonProcessor) {
	// JsonMapper jsonMapper = this.mapper.rebuild()
	// .changeDefaultPropertyInclusion((p) -> Value.construct(Include.NON_ABSENT,
	// Include.NON_ABSENT))
	// .build();
	//
	// UsernamePasswordAuthenticationToken original =
	// UsernamePasswordAuthenticationToken.unauthenticated("Frodo",
	// null);
	// String serialized = jsonMapper.writeValueAsString(original);
	// UsernamePasswordAuthenticationToken deserialized = jsonMapper.readValue(serialized,
	// UsernamePasswordAuthenticationToken.class);
	// assertThat(deserialized).isEqualTo(original);
	// }

	private UsernamePasswordAuthenticationToken createToken() {
		User user = createDefaultUser();
		UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.authenticated(user,
				user.getPassword(), user.getAuthorities());
		return token;
	}

	@JsonClassDescription
	public static class NonUserPrincipal {

		private String username;

		public String getUsername() {
			return this.username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

	}

}
