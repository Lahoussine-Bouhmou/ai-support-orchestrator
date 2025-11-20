// src/main/java/com/example/aisupport/openai/OpenAiChatResponse.java
package com.example.aisupport.openai;

import lombok.Data;

import java.util.List;

@Data
public class OpenAiChatResponse {

    private List<Choice> choices;

    @Data
    public static class Choice {
        private int index;
        private OpenAiMessage message;
        private Object logprobs;
        private String finish_reason;
    }
}
