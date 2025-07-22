package com.utplist.proyecto.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KafkaEventDTO {
    private String event;
    private String email;
    private String role;
    private String timestamp;
} 