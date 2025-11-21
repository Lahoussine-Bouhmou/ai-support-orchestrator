// src/main/java/com/example/aisupport/openai/OpenAiMessage.java
package com.example.aisupport.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiMessage {
    private String role;    // "system", "user", ...
    private String content;
}
