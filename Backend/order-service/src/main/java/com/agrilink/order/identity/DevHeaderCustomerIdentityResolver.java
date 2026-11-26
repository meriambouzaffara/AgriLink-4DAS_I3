package com.agrilink.order.identity;

import com.agrilink.order.exception.InvalidOrderException;
import com.agrilink.order.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev-mock")
public class DevHeaderCustomerIdentityResolver implements CustomerIdentityResolver {
    @Override
    public Long currentCustomerId(HttpServletRequest request) {
        String header = request.getHeader("X-Customer-Id");
        if (header == null || header.isBlank())
            throw new UnauthorizedException("X-Customer-Id is required in development mode");
        try {
            long id = Long.parseLong(header.trim());
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException exception) {
            throw new InvalidOrderException("X-Customer-Id must be a positive numeric ID");
        }
    }
}
