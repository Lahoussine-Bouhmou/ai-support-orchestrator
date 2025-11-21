// src/main/java/com/example/aisupport/repository/TicketRepository.java
package com.example.aisupport.repository;

import com.example.aisupport.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
