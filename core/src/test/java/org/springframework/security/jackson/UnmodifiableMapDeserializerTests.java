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
import java.util.Map;

import org.junit.jupiter.api.TestTemplate;
import org.skyscreamer.jsonassert.JSONAssert;

import org.springframework.security.junit.jackson.JsonMixinTest;
import org.springframework.security.junit.jackson.JsonProcessor;

import static org.assertj.core.api.Assertions.assertThat;

@JsonMixinTest
class UnmodifiableMapDeserializerTests {

	// @formatter:off
	private static final String DEFAULT_MAP_JSON = "{"
			+ "\"@class\": \"java.util.Collections$UnmodifiableMap\","
			+ "\"Key\": \"Value\""
			+ "}";
	// @formatter:on

	@TestTemplate
	void shouldSerialize(JsonProcessor jsonProcessor) throws Exception {
		String mapJson = jsonProcessor.serialize(Collections.unmodifiableMap(Collections.singletonMap("Key", "Value")));

		JSONAssert.assertEquals(DEFAULT_MAP_JSON, mapJson, true);
	}

	@TestTemplate
	@SuppressWarnings("unchecked")
	void shouldDeserialize(JsonProcessor jsonProcessor) {
		Map<String, String> map = jsonProcessor.deserialize(DEFAULT_MAP_JSON,
				Collections.unmodifiableMap(Collections.emptyMap()).getClass());

		assertThat(map).isNotNull()
			.isInstanceOf(Collections.unmodifiableMap(Collections.emptyMap()).getClass())
			.containsAllEntriesOf(Collections.singletonMap("Key", "Value"));
	}

}
