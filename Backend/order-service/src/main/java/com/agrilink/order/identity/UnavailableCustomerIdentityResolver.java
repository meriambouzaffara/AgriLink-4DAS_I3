package com.agrilink.order.identity;

import com.agrilink.order.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev-mock")
public class UnavailableCustomerIdentityResolver implements CustomerIdentityResolver {
    @Override
    public Long currentCustomerId(HttpServletRequest request) {
        throw new UnauthorizedException("Customer authentication is not configured");
    }
}
