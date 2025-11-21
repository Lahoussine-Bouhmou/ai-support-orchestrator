// src/main/java/com/example/aisupport/controller/EmailController.java
package com.example.aisupport.controller;

import com.example.aisupport.model.*;
import com.example.aisupport.repository.TicketRepository;
import com.example.aisupport.service.EmailProcessingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailProcessingService emailProcessingService;
    private final TicketRepository ticketRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ticket receiveEmail(@Valid @RequestBody EmailRequest emailRequest) {
        return emailProcessingService.processIncomingEmail(emailRequest);
    }

    @GetMapping("/tickets")
    public List<Ticket> listTickets() {
        // tu peux ajouter un tri ici si tu veux
        return ticketRepository.findAll();
    }

    @PutMapping("/tickets/{id}/status")
    public Ticket updateTicketStatus(@PathVariable Long id,
                                     @RequestBody UpdateTicketStatusRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket non trouvé : " + id));

        ticket.setStatus(request.getStatus());
        return ticketRepository.save(ticket);
    }
}
