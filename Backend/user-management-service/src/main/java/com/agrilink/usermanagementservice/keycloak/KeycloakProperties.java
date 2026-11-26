
package com.agrilink.usermanagementservice.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak.admin")
public record KeycloakProperties(
        String baseUrl,
        String realm,
        String clientId,
        String clientSecret
) {
}
