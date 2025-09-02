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

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

/**
 * A {@code TestTemplateInvocationContextProvider} for {@link JsonMixinTest}.
 *
 * @author Andy Wilkinson
 */
class JsonMixinTestExtension implements TestTemplateInvocationContextProvider {

	@Override
	public boolean supportsTestTemplate(ExtensionContext context) {
		return true;
	}

	@Override
	public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
		return Stream.of(new JsonMixinTestTemplateInvocationContext(new Jackson2JsonProcessor(), "Jackson 2"),
				new JsonMixinTestTemplateInvocationContext(new JacksonJsonProcessor(), "Jackson 3"));
	}

	static class JsonMixinTestTemplateInvocationContext implements TestTemplateInvocationContext {

		private final JsonProcessor jsonProcessor;

		private final String displayName;

		JsonMixinTestTemplateInvocationContext(JsonProcessor jsonProcessor, String displayName) {
			this.jsonProcessor = jsonProcessor;
			this.displayName = displayName;
		}

		@Override
		public String getDisplayName(int invocationIndex) {
			return this.displayName;
		}

		@Override
		public List<Extension> getAdditionalExtensions() {
			return List.of(new JsonProcessorParameterResolver(this.jsonProcessor));
		}

	}

	static class JsonProcessorParameterResolver implements ParameterResolver {

		private final JsonProcessor jsonProcessor;

		JsonProcessorParameterResolver(JsonProcessor jsonProcessor) {
			this.jsonProcessor = jsonProcessor;
		}

		@Override
		public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
				throws ParameterResolutionException {
			return true;
		}

		@Override
		public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
				throws ParameterResolutionException {
			return this.jsonProcessor;
		}

	}

}
