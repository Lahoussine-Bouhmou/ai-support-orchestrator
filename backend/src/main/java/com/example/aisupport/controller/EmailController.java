// src/main/java/com/example/aisupport/controller/EmailController.java
package com.example.aisupport.controller;

import com.example.aisupport.model.EmailRequest;
import com.example.aisupport.model.Ticket;
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
        return ticketRepository.findAll();
    }
}
