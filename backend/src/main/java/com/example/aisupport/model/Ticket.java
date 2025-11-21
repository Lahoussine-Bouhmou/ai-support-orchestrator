// src/main/java/com/example/aisupport/model/Ticket.java
package com.example.aisupport.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromEmail;
    private String subject;

    @Lob
    private String body;

    @Enumerated(EnumType.STRING)
    private EmailCategory category;

    private String clientName;
    private String invoiceNumber;

    @Lob
    private String summary;

    @Lob
    private String suggestedReply;
}
