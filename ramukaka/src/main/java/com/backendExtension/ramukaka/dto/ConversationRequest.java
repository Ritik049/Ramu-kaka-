package com.backendExtension.ramukaka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationRequest {
    private String text;
    private String room;
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
