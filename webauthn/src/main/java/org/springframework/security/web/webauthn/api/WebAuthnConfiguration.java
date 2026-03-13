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

package org.springframework.security.web.webauthn.api;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.security.web.webauthn.management.WebAuthnRelyingPartyOperations;
import org.springframework.security.web.webauthn.settings.WebAuthnSettings;
import org.springframework.security.web.webauthn.settings.WebAuthnSettingsConfigurer;

/**
 * Shared configuration for WebAuthn.
 */
@Configuration(proxyBeanMethods = false)
class WebAuthnConfiguration {

	@Bean
	WebAuthnSettings webAuthnSettings(ObjectProvider<UserCredentialRepository> userCredentialRepository,
			ObjectProvider<PublicKeyCredentialUserEntityRepository> userEntityRepository,
			ObjectProvider<WebAuthnRelyingPartyOperations> relyingPartyOperations,
			ObjectProvider<WebAuthnSettingsConfigurer> configurers) {
		WebAuthnSettings settings = new WebAuthnSettings(userCredentialRepository, userEntityRepository,
				relyingPartyOperations);
		configurers.orderedStream().forEach((configurer) -> configurer.configure(settings));
		return settings;
	}

}
