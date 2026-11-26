package com.agrilink.usermanagementservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(UserNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler({ProfileAlreadyExistsException.class, DataIntegrityViolationException.class})
    ResponseEntity<ErrorResponse> conflict(Exception ex, HttpServletRequest req) {
        String msg = (ex instanceof ProfileAlreadyExistsException)
                ? ex.getMessage() : "Conflit avec une donnee existante";
        return build(HttpStatus.CONFLICT, msg, req);
    }

    @ExceptionHandler(ForbiddenRoleException.class)
    ResponseEntity<ErrorResponse> forbidden(ForbiddenRoleException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), req);
    }

    @ExceptionHandler(UnauthenticatedException.class)
    ResponseEntity<ErrorResponse> unauthenticated(
            UnauthenticatedException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> invalid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " : " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, msg, req);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ErrorResponse> badRequest(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST,
                "Requete invalide : JSON mal forme ou valeur inconnue (ex. role)", req);
    }

    /**
     * Echec de communication avec Keycloak : retourne 502 Bad Gateway.
     * Le message interne n'est pas expose pour eviter de divulguer
     * des details d'infrastructure sensibles.
     */
    @ExceptionHandler(KeycloakException.class)
    ResponseEntity<ErrorResponse> keycloakError(KeycloakException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_GATEWAY,
                "Erreur de synchronisation avec le service d'authentification. "
                        + "Veuillez reessayer ou contacter l'administrateur.", req);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest req) {
        return ResponseEntity.status(status).body(new ErrorResponse(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                message, req.getRequestURI()));
    }
}