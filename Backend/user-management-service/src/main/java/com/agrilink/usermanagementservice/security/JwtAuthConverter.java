package com.agrilink.usermanagementservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthConverter {

    /** Construit le convertisseur utilise par Spring Security. */
    public JwtAuthenticationConverter build() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(realmRolesConverter());
        return converter;
    }

    // Lit realm_access.roles et ajoute le prefixe ROLE_ (necessaire pour hasRole('ADMIN'))
    private Converter<Jwt, Collection<GrantedAuthority>> realmRolesConverter() {
        return jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null || !(realmAccess.get("roles") instanceof Collection<?> roles)) {
                return List.<GrantedAuthority>of();
            }
            return roles.stream()
                    .map(String::valueOf)
                    .<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
        };
    }
}