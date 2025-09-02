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

package org.springframework.security.junit.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * A Jackson 2 based {@link JsonProcessor}.
 *
 * @author Andy Wilkinson
 */
class Jackson2JsonProcessor implements JsonProcessor {

	private final ObjectMapper objectMapper;

	@SuppressWarnings("removal")
	Jackson2JsonProcessor() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModules(
				org.springframework.security.jackson2.SecurityJackson2Modules.getModules(getClass().getClassLoader()));
	}

	@Override
	public <T> T deserialize(String json, Class<T> type) {
		try {
			return this.objectMapper.readValue(json, type);
		}
		catch (JsonProcessingException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String serialize(Object object) {
		try {
			return this.objectMapper.writeValueAsString(object);
		}
		catch (JsonProcessingException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String removeFromJson(String json, String toRemove) {
		try {
			ObjectNode node = this.objectMapper.getFactory().createParser(json).readValueAsTree();
			node.remove(toRemove);
			String result = this.objectMapper.writeValueAsString(node);
			JSONAssert.assertNotEquals(json, result, false);
			return result;
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Class<? extends Exception> deserializationFailure() {
		return IllegalArgumentException.class;
	}

	@Override
	public Class<? extends Throwable> valueInstantiationFailure() {
		return RuntimeException.class;
	}

}
