// src/main/java/com/example/aisupport/gemini/GeminiRequest.java
package com.example.aisupport.gemini;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequest {
    private List<GeminiContent> contents;
}
