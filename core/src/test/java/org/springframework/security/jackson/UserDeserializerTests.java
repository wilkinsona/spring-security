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

import java.util.Collections;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.junit.jupiter.api.TestTemplate;
import org.skyscreamer.jsonassert.JSONAssert;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.junit.jackson.JsonMixinTest;
import org.springframework.security.junit.jackson.JsonProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Jitendra Singh
 * @since 4.2
 */
@JsonMixinTest
public class UserDeserializerTests extends AbstractMixinTests {

	public static final String USER_PASSWORD = "\"1234\"";

	// @formatter:off
	public static final String USER_JSON = "{"
		+ "\"@class\": \"org.springframework.security.core.userdetails.User\", "
		+ "\"username\": \"admin\","
		+ " \"password\": " + USER_PASSWORD + ", "
		+ "\"accountNonExpired\": true, "
		+ "\"accountNonLocked\": true, "
		+ "\"credentialsNonExpired\": true, "
		+ "\"enabled\": true, "
		+ "\"authorities\": " + SimpleGrantedAuthorityMixinTests.AUTHORITIES_SET_JSON
	+ "}";
	// @formatter:on
	@TestTemplate
	public void serializeUserTest(JsonProcessor jsonProcessor) throws JSONException {
		User user = createDefaultUser();
		String userJson = jsonProcessor.serialize(user);
		JSONAssert.assertEquals(userWithPasswordJson(user.getPassword()), userJson, true);
	}

	@TestTemplate
	public void serializeUserWithoutAuthority(JsonProcessor jsonProcessor) throws JSONException {
		User user = new User("admin", "1234", Collections.<GrantedAuthority>emptyList());
		String userJson = jsonProcessor.serialize(user);
		JSONAssert.assertEquals(userWithNoAuthoritiesJson(), userJson, true);
	}

	@TestTemplate
	public void deserializeUserWithNullPasswordEmptyAuthorityTest(JsonProcessor jsonProcessor) {
		String userJsonWithoutPasswordString = USER_JSON.replace(SimpleGrantedAuthorityMixinTests.AUTHORITIES_SET_JSON,
				"[]");
		assertThatExceptionOfType(jsonProcessor.deserializationFailure())
			.isThrownBy(() -> jsonProcessor.deserialize(userJsonWithoutPasswordString, User.class));
	}

	@TestTemplate
	public void deserializeUserWithNullPasswordNoAuthorityTest(JsonProcessor jsonProcessor) {
		String userJsonWithoutPasswordString = jsonProcessor.removeFromJson(userWithNoAuthoritiesJson(), "password");
		User user = jsonProcessor.deserialize(userJsonWithoutPasswordString, User.class);
		assertThat(user).isNotNull();
		assertThat(user.getUsername()).isEqualTo("admin");
		assertThat(user.getPassword()).isNull();
		assertThat(user.getAuthorities()).isEmpty();
		assertThat(user.isEnabled()).isEqualTo(true);
	}

	@TestTemplate
	public void deserializeUserWithNoClassIdInAuthoritiesTest(JsonProcessor jsonProcessor) {
		String userJson = USER_JSON.replace(SimpleGrantedAuthorityMixinTests.AUTHORITIES_SET_JSON,
				"[{\"authority\": \"ROLE_USER\"}]");
		assertThatExceptionOfType(jsonProcessor.deserializationFailure())
			.isThrownBy(() -> jsonProcessor.deserialize(userJson, User.class));
	}

	@TestTemplate
	public void deserializeUserWithClassIdInAuthoritiesTest(JsonProcessor jsonProcessor) {
		User user = jsonProcessor.deserialize(userJson(), User.class);
		assertThat(user).isNotNull();
		assertThat(user.getUsername()).isEqualTo("admin");
		assertThat(user.getPassword()).isEqualTo("1234");
		assertThat(user.getAuthorities()).hasSize(1).contains(new SimpleGrantedAuthority("ROLE_USER"));
	}

	public static String userJson() {
		return USER_JSON;
	}

	public static String userWithPasswordJson(String password) {
		return userJson().replaceAll(Pattern.quote(USER_PASSWORD), "\"" + password + "\"");
	}

	public static String userWithNoAuthoritiesJson() {
		return userJson().replace(SimpleGrantedAuthorityMixinTests.AUTHORITIES_SET_JSON,
				SimpleGrantedAuthorityMixinTests.NO_AUTHORITIES_SET_JSON);
	}

}
