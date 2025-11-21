// src/main/java/com/example/aisupport/model/UpdateTicketStatusRequest.java
package com.example.aisupport.model;

import lombok.Data;

@Data
public class UpdateTicketStatusRequest {
    private TicketStatus status;
}
