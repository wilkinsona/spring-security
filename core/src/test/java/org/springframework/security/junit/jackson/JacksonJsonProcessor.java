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

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.ValueInstantiationException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import org.springframework.security.jackson.SecurityJacksonModules;

/**
 * A Jackson 3 based {@link JsonProcessor}.
 *
 * @author Andy Wilkinson
 */
class JacksonJsonProcessor implements JsonProcessor {

	private final JsonMapper jsonMapper;

	JacksonJsonProcessor() {
		this.jsonMapper = JsonMapper.builder()
			.addModules(SecurityJacksonModules.getModules(getClass().getClassLoader()))
			.build();
	}

	@Override
	public <T> T deserialize(String json, Class<T> type) {
		return this.jsonMapper.readValue(json, type);
	}

	@Override
	public String serialize(Object object) {
		return this.jsonMapper.writeValueAsString(object);
	}

	@Override
	public Class<? extends Exception> deserializationFailure() {
		return MismatchedInputException.class;
	}

	@Override
	public Class<? extends Throwable> valueInstantiationFailure() {
		return ValueInstantiationException.class;
	}

	@Override
	public String removeFromJson(String json, String toRemove) {
		try (JsonParser parser = this.jsonMapper.createParser(json)) {
			ObjectNode node = parser.readValueAsTree();
			node.remove(toRemove);
			String result = this.jsonMapper.writeValueAsString(node);
			JSONAssert.assertNotEquals(json, result, false);
			return result;
		}
		catch (JSONException ex) {
			throw new RuntimeException(ex);
		}
	}

}
