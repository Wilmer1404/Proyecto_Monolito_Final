package com.utplist.proyecto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la API REST.
 * Captura y responde de forma uniforme a los errores y validaciones en toda la aplicación.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Maneja la excepción cuando un documento no es encontrado.
     * @param ex Excepción DocumentoNoEncontradoException
     * @return Respuesta de error con estado 404
     */
    @ExceptionHandler(DocumentoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleDocumentoNotFound(DocumentoNoEncontradoException ex) {
        ErrorResponse err = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }
    /**
     * Maneja la excepción de permiso denegado.
     * @param ex Excepción PermisoDenegadoException
     * @return Respuesta de error con estado 403
     */
    @ExceptionHandler(PermisoDenegadoException.class)
    public ResponseEntity<ErrorResponse> handlePermisoDenied(PermisoDenegadoException ex) {
        ErrorResponse err = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
    }
    /**
     * Maneja la excepción cuando un feature flag no es encontrado.
     * @param ex Excepción FeatureFlagNotFoundException
     * @return Respuesta de error con estado 404
     */
    @ExceptionHandler(FeatureFlagNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFlagNotFound(FeatureFlagNotFoundException ex) {
        ErrorResponse err = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }
    /**
     * Maneja la excepción de invitación duplicada.
     * @param ex Excepción InvitacionDuplicadaException
     * @return Respuesta de error con estado 409
     */
    @ExceptionHandler(InvitacionDuplicadaException.class)
    public ResponseEntity<ErrorResponse> handleInvitacionDuplicada(InvitacionDuplicadaException ex) {
        ErrorResponse err = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }
    /**
     * Maneja errores de validación de argumentos en los controladores.
     * @param ex Excepción MethodArgumentNotValidException
     * @return Respuesta de error con detalles de los campos inválidos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ValidationError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> ValidationError.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
        ValidationErrorResponse err = ValidationErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .timestamp(Instant.now())
                .errors(fieldErrors)
                .build();
        return ResponseEntity.badRequest().body(err);
    }
    /**
     * Maneja cualquier otra excepción no controlada.
     * @param ex Excepción genérica
     * @return Respuesta de error con estado 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllOther(Exception ex) {
        ErrorResponse err = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Error interno del servidor")
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
} 