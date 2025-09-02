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

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.junit.jackson.JsonMixinTest;
import org.springframework.security.junit.jackson.JsonProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Jitendra Singh
 * @since 4.2
 */
@JsonMixinTest
public class SimpleGrantedAuthorityMixinTests {

	// @formatter:off
	public static final String AUTHORITY_JSON = "{\"@class\": \"org.springframework.security.core.authority.SimpleGrantedAuthority\", \"authority\": \"ROLE_USER\"}";
	public static final String AUTHORITIES_ARRAYLIST_JSON = "[\"java.util.Collections$UnmodifiableRandomAccessList\", [" + AUTHORITY_JSON + "]]";
	public static final String AUTHORITIES_SET_JSON = "[\"java.util.Collections$UnmodifiableSet\", [" + AUTHORITY_JSON + "]]";
	public static final String NO_AUTHORITIES_ARRAYLIST_JSON = "[\"java.util.Collections$UnmodifiableRandomAccessList\", []]";
	public static final String EMPTY_AUTHORITIES_ARRAYLIST_JSON = "[\"java.util.Collections$EmptyList\", []]";
	public static final String NO_AUTHORITIES_SET_JSON = "[\"java.util.Collections$UnmodifiableSet\", []]";
	// @formatter:on

	@TestTemplate
	public void serializeSimpleGrantedAuthorityTest(JsonProcessor jsonProcessor) throws JSONException {
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
		String serializeJson = jsonProcessor.serialize(authority);
		JSONAssert.assertEquals(AUTHORITY_JSON, serializeJson, true);
	}

	@TestTemplate
	public void deserializeGrantedAuthorityTest(JsonProcessor jsonProcessor) {
		SimpleGrantedAuthority authority = jsonProcessor.deserialize(AUTHORITY_JSON, SimpleGrantedAuthority.class);
		assertThat(authority).isNotNull();
		assertThat(authority.getAuthority()).isNotNull().isEqualTo("ROLE_USER");
	}

	@TestTemplate
	public void deserializeGrantedAuthorityWithoutRoleTest(JsonProcessor jsonProcessor) {
		String json = "{\"@class\": \"org.springframework.security.core.authority.SimpleGrantedAuthority\"}";
		assertThatExceptionOfType(jsonProcessor.valueInstantiationFailure())
			.isThrownBy(() -> jsonProcessor.deserialize(json, SimpleGrantedAuthority.class));
	}

}
