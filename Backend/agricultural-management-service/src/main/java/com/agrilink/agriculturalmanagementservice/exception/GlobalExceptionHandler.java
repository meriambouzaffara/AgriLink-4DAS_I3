package com.agrilink.agriculturalmanagementservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<ErreurReponse> gererRessourceIntrouvable(
            RessourceIntrouvableException ex, HttpServletRequest request) {
        ErreurReponse erreur = new ErreurReponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erreur);
    }

    @ExceptionHandler(DonneesInvalidesException.class)
    public ResponseEntity<ErreurReponse> gererDonneesInvalides(
            DonneesInvalidesException ex, HttpServletRequest request) {
        ErreurReponse erreur = new ErreurReponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }

    @ExceptionHandler(AccesInterditException.class)
    public ResponseEntity<ErreurReponse> gererAccesInterdit(
            AccesInterditException ex, HttpServletRequest request) {
        ErreurReponse erreur = new ErreurReponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erreur);
    }

    @ExceptionHandler(ConflitMetierException.class)
    public ResponseEntity<ErreurReponse> gererConflitMetier(
            ConflitMetierException ex, HttpServletRequest request) {
        ErreurReponse erreur = new ErreurReponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erreur);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurReponse> gererValidationArguments(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> erreurs = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erreurs.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErreurReponse erreur = new ErreurReponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Erreur de validation des données fournies",
                request.getRequestURI(),
                erreurs
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErreurReponse> gererConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> erreurs = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String property = violation.getPropertyPath().toString();
            erreurs.put(property, violation.getMessage());
        });

        ErreurReponse erreur = new ErreurReponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Erreur de contrainte sur les paramètres",
                request.getRequestURI(),
                erreurs
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurReponse> gererErreurInterne(
            Exception ex, HttpServletRequest request) {
        ErreurReponse erreur = new ErreurReponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Une erreur inattendue est survenue : " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
    }
}
