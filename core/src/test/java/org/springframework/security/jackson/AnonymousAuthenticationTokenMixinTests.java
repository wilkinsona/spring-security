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

import org.json.JSONException;
import org.junit.jupiter.api.TestTemplate;
import org.skyscreamer.jsonassert.JSONAssert;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
public class AnonymousAuthenticationTokenMixinTests extends AbstractMixinTests {

	private static final String HASH_KEY = "key";

	// @formatter:off
	private static final String ANONYMOUS_JSON = "{"
		+ "\"@class\": \"org.springframework.security.authentication.AnonymousAuthenticationToken\", "
		+ "\"details\": null,"
		+ "\"principal\": " + UserDeserializerTests.USER_JSON + ","
		+ "\"authenticated\": true, "
		+ "\"keyHash\": " + HASH_KEY.hashCode() + ","
		+ "\"authorities\": " + SimpleGrantedAuthorityMixinTests.AUTHORITIES_ARRAYLIST_JSON
	+ "}";
	// @formatter:on

	@TestTemplate
	public void serializeAnonymousAuthenticationTokenTest(JsonProcessor jsonProcessor) throws JSONException {
		User user = createDefaultUser();
		AnonymousAuthenticationToken token = new AnonymousAuthenticationToken(HASH_KEY, user, user.getAuthorities());
		String actualJson = jsonProcessor.serialize(token);
		JSONAssert.assertEquals(ANONYMOUS_JSON, actualJson, true);
	}

	@TestTemplate
	public void deserializeAnonymousAuthenticationTokenTest(JsonProcessor jsonProcessor) {
		AnonymousAuthenticationToken token = jsonProcessor.deserialize(ANONYMOUS_JSON,
				AnonymousAuthenticationToken.class);
		assertThat(token).isNotNull();
		assertThat(token.getKeyHash()).isEqualTo(HASH_KEY.hashCode());
		assertThat(token.getAuthorities()).isNotNull().hasSize(1).contains(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@TestTemplate
	public void deserializeAnonymousAuthenticationTokenWithoutAuthoritiesTest(JsonProcessor jsonProcessor) {
		String jsonString = "{\"@class\": \"org.springframework.security.authentication.AnonymousAuthenticationToken\", \"details\": null,"
				+ "\"principal\": \"user\", \"authenticated\": true, \"keyHash\": " + HASH_KEY.hashCode() + ","
				+ "\"authorities\": [\"java.util.ArrayList\", []]}";
		assertThatExceptionOfType(jsonProcessor.valueInstantiationFailure())
			.isThrownBy(() -> jsonProcessor.deserialize(jsonString, AnonymousAuthenticationToken.class));
	}

	@TestTemplate
	public void serializeAnonymousAuthenticationTokenMixinAfterEraseCredentialTest(JsonProcessor jsonProcessor)
			throws JSONException {
		User user = createDefaultUser();
		AnonymousAuthenticationToken token = new AnonymousAuthenticationToken(HASH_KEY, user, user.getAuthorities());
		token.eraseCredentials();
		String actualJson = jsonProcessor.serialize(token);
		JSONAssert.assertEquals(ANONYMOUS_JSON.replace(UserDeserializerTests.USER_PASSWORD, "null"), actualJson, true);
	}

}
