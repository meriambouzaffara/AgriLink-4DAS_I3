package com.agrilink.order.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderRequestValidationTests {
    private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = FACTORY.getValidator();

    @AfterAll
    static void closeFactory() {
        FACTORY.close();
    }

    @Test
    void acceptsValidNestedItem() {
        var request = new OrderRequest(List.of(new OrderItemRequest(101L, new BigDecimal("10.000"))));
        assertTrue(VALIDATOR.validate(request).isEmpty());
    }

    @Test
    void rejectsMissingAndEmptyLines() {
        assertTrue(VALIDATOR.validate(new OrderRequest(null)).stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("lignes")));
        assertTrue(VALIDATOR.validate(new OrderRequest(List.of())).stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("lignes")));
    }

    @Test
    void validatesNestedIdsAndQuantityPrecision() {
        var request = new OrderRequest(List.of(new OrderItemRequest(0L, new BigDecimal("1000000000000.1234"))));
        var violations = VALIDATOR.validate(request);
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().contains("produitId")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().contains("quantite")));
    }

    @Test
    void rejectsNullAndNonPositiveNestedValues() {
        var request = new OrderRequest(List.of(new OrderItemRequest(null, BigDecimal.ZERO)));
        var violations = VALIDATOR.validate(request);
        assertEquals(2, violations.size());
    }
}
