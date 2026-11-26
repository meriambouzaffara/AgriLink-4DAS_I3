package com.agrilink.order.controller;

import com.agrilink.order.exception.GlobalExceptionHandler;
import com.agrilink.order.identity.UnavailableCustomerIdentityResolver;
import com.agrilink.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {OrderController.class, DevProductController.class, DevDeliveryController.class})
@Import({UnavailableCustomerIdentityResolver.class, GlobalExceptionHandler.class})
class ProductionBoundaryMvcTests {
    @Autowired MockMvc mvc;
    @MockitoBean OrderService orders;

    @Test
    void developmentEndpointsAreAbsentOutsideProfile() throws Exception {
        mvc.perform(get("/api/dev/products")).andExpect(status().isNotFound());
        mvc.perform(get("/api/dev/orders/1/status")).andExpect(status().isNotFound());
    }

    @Test
    void developmentIdentityHeaderCannotAuthorizeCustomerRequestOutsideProfile() throws Exception {
        mvc.perform(get("/api/orders/42").header("X-Customer-Id", "12"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
        verifyNoInteractions(orders);
    }
}
