package com.utplist.proyecto.exception;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ValidationErrorResponse {
    private int status;
    private String error;
    private Instant timestamp;
    private List<ValidationError> errors;
} 