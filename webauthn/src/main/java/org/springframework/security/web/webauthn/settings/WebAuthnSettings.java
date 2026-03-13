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

package org.springframework.security.web.webauthn.settings;

import java.util.HashSet;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialRpEntity;
import org.springframework.security.web.webauthn.management.JdbcPublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.JdbcUserCredentialRepository;
import org.springframework.security.web.webauthn.management.MapPublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.MapUserCredentialRepository;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.security.web.webauthn.management.WebAuthnRelyingPartyOperations;
import org.springframework.security.web.webauthn.management.Webauthn4JRelyingPartyOperations;
import org.springframework.util.Assert;

/**
 * WebAuthn settings. Uses in-memory, map-based repositories by default.
 *
 * @author Andy Wilkinson
 */
public class WebAuthnSettings {

	private UserCredentialRepository userCredentialRepository;

	private PublicKeyCredentialUserEntityRepository userEntityRepository;

	private @Nullable WebAuthnRelyingPartyOperations relyingPartyOperations;

	private @Nullable String rpId;

	private @Nullable String rpName;

	private Set<String> allowedOrigins = new HashSet<>();

	public WebAuthnSettings(ObjectProvider<UserCredentialRepository> userCredentialRepository,
			ObjectProvider<PublicKeyCredentialUserEntityRepository> userEntityRepository,
			ObjectProvider<WebAuthnRelyingPartyOperations> relyingPartyOperations) {
		this.userCredentialRepository = userCredentialRepository.getIfAvailable(MapUserCredentialRepository::new);
		this.userEntityRepository = userEntityRepository
			.getIfAvailable(MapPublicKeyCredentialUserEntityRepository::new);
		this.relyingPartyOperations = relyingPartyOperations.getIfAvailable();
	}

	/**
	 * Configure the use of JDBC-backed repositories.
	 * @param jdbcOperations used by the repositories for JDBC access
	 */
	public void jdbc(JdbcOperations jdbcOperations) {
		this.userCredentialRepository = new JdbcUserCredentialRepository(jdbcOperations);
		this.userEntityRepository = new JdbcPublicKeyCredentialUserEntityRepository(jdbcOperations);
	}

	/**
	 * Returns the configured the user credential repository.
	 * @return the user credential repository
	 */
	public UserCredentialRepository getUserCredentialRepository() {
		return this.userCredentialRepository;
	}

	/**
	 * Returns the configured user entity repository.
	 * @return the user entity repository
	 */
	public PublicKeyCredentialUserEntityRepository getUserEntityRepository() {
		return this.userEntityRepository;
	}

	/**
	 * Sets the relying party ID.
	 * @param rpId the relying party ID
	 */
	public void rpId(String rpId) {
		this.rpId = rpId;
	}

	/**
	 * Sets the relying party name
	 * @param rpName the relying party name
	 */
	public void rpName(String rpName) {
		this.rpName = rpName;
	}

	/**
	 * Convenience method for {@link #allowedOrigins(Set)}
	 * @param allowedOrigins the allowed origins
	 * @see #allowedOrigins(Set)
	 */
	public void allowedOrigins(String... allowedOrigins) {
		allowedOrigins(Set.of(allowedOrigins));
	}

	/**
	 * Sets the allowed origins.
	 * @param allowedOrigins the allowed origins
	 * @see #allowedOrigins(String...)
	 */
	public void allowedOrigins(Set<String> allowedOrigins) {
		Assert.notNull(allowedOrigins, "allowedOrigins can't be null");
		this.allowedOrigins = allowedOrigins;
	}

	public @Nullable WebAuthnRelyingPartyOperations getRelyingPartyOperations() {
		if (this.relyingPartyOperations != null) {
			return this.relyingPartyOperations;
		}
		if (this.rpId != null) {
			String rpName = (this.rpName != null) ? this.rpName : this.rpId;
			this.relyingPartyOperations = new Webauthn4JRelyingPartyOperations(this.userEntityRepository,
					this.userCredentialRepository,
					PublicKeyCredentialRpEntity.builder().id(this.rpId).name(rpName).build(), this.allowedOrigins);
		}
		return this.relyingPartyOperations;
	}

}
