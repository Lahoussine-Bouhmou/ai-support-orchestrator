// src/main/java/com/example/aisupport/openai/OpenAiChatRequest.java
package com.example.aisupport.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiChatRequest {
    private String model;
    private List<OpenAiMessage> messages;
    private Double temperature;
}
