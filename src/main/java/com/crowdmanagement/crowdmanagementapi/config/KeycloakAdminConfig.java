package com.crowdmanagement.crowdmanagementapi.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-client-id}")
    private String clientId;

    @Value("${keycloak.admin-client-secret}")
    private String clientSecret;

    /**
     * Singleton Keycloak admin client authenticated via the Client Credentials
     * (service account) grant. This client talks directly to the Keycloak Admin
     * REST API — no human session required.
     */
    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    /**
     * Convenience bean scoped to our realm so services don't repeat
     * .realm(name) everywhere.
     */
    @Bean
    public RealmResource realmResource(Keycloak keycloakAdminClient) {
        return keycloakAdminClient.realm(realm);
    }
}
