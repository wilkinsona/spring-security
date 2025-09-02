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

/**
 * A processor of JSON.
 *
 * @author Andy Wilkinson
 */
public interface JsonProcessor {

	/**
	 * Deserializes {@code json} to an instance of the given {@code type}.
	 * @param <T> the type
	 * @param json the JSON to deserialize
	 * @param type the type
	 * @return the instance of the type
	 */
	<T> T deserialize(String json, Class<T> type);

	/**
	 * Serializes the given {@code object} to JSON.
	 * @param object the object to serialize
	 * @return the JSON
	 */
	String serialize(Object object);

	/**
	 * From the given {@code json}, removes the node identified by the key
	 * {@code toRemove}.
	 * @param json the JSON to process
	 * @param toRemove the key of the node to remove
	 * @return the updated JSON
	 */
	String removeFromJson(String json, String toRemove);

	/**
	 * Returns the type of exception thrown when a general deserialization failure occurs.
	 * @return the type of the deserialization failure exception
	 */
	Class<? extends Throwable> deserializationFailure();

	/**
	 * Returns the type of exception thrown when a failure occurs during value
	 * instantiation.
	 * @return the tyoe of the value instantiation failure exception
	 */
	Class<? extends Throwable> valueInstantiationFailure();

}
