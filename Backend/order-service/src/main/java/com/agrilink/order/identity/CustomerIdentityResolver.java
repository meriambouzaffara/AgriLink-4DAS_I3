package com.agrilink.order.identity;

import jakarta.servlet.http.HttpServletRequest;

public interface CustomerIdentityResolver {
    Long currentCustomerId(HttpServletRequest request);
}
