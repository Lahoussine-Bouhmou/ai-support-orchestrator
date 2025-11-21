// src/main/java/com/example/aisupport/model/EmailRequest.java
package com.example.aisupport.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequest {

    @NotBlank
    @Email
    private String from;

    @NotBlank
    private String subject;

    @NotBlank
    private String body;

    // plus tard : pièces jointes, metadata, etc.
}
