package com.utplist.proyecto.exception;

import lombok.*;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private Instant timestamp;
} 