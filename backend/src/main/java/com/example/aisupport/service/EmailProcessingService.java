// src/main/java/com/example/aisupport/service/EmailProcessingService.java
package com.example.aisupport.service;

import com.example.aisupport.model.*;
import com.example.aisupport.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailProcessingService {

    private final AiClient aiClient;
    private final TicketRepository ticketRepository;

    public Ticket processIncomingEmail(EmailRequest emailRequest) {
        EmailAnalysisResult analysis = aiClient.analyzeEmail(emailRequest);

        Ticket ticket = Ticket.builder()
                .fromEmail(emailRequest.getFrom())
                .subject(emailRequest.getSubject())
                .body(emailRequest.getBody())
                .category(analysis.getCategory())
                .clientName(analysis.getClientName())
                .invoiceNumber(analysis.getInvoiceNumber())
                .summary(analysis.getSummary())
                .suggestedReply(analysis.getSuggestedReply())
                .status(TicketStatus.NEW)   // 👈 nouveau
                .build();

        return ticketRepository.save(ticket);
    }
}
