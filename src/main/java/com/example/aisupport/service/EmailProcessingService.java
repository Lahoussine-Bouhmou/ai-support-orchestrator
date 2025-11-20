// src/main/java/com/example/aisupport/service/EmailProcessingService.java
package com.example.aisupport.service;

import com.example.aisupport.model.EmailAnalysisResult;
import com.example.aisupport.model.EmailRequest;
import com.example.aisupport.model.Ticket;
import com.example.aisupport.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailProcessingService {

    private final AiClient aiClient;
    private final TicketRepository ticketRepository;

    public Ticket processIncomingEmail(EmailRequest emailRequest) {
        // 1) Analyse par l’IA (fake pour l’instant)
        EmailAnalysisResult analysis = aiClient.analyzeEmail(emailRequest);

        // 2) Création du ticket
        Ticket ticket = Ticket.builder()
                .fromEmail(emailRequest.getFrom())
                .subject(emailRequest.getSubject())
                .body(emailRequest.getBody())
                .category(analysis.getCategory())
                .clientName(analysis.getClientName())
                .invoiceNumber(analysis.getInvoiceNumber())
                .summary(analysis.getSummary())
                .suggestedReply(analysis.getSuggestedReply())
                .build();

        // 3) Persistance
        return ticketRepository.save(ticket);
    }
}
