package com.agrimart.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String reply;
    // New fields for offline/guided flow
    private List<String> suggestions;
    private boolean aiUsed;

    // Constructor for legacy usage (just reply)
    public ChatResponse(String reply) {
        this.reply = reply;
        this.suggestions = List.of();
        this.aiUsed = false;
    }
}
