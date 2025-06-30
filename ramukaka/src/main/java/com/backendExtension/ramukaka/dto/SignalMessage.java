package com.backendExtension.ramukaka.dto;

import lombok.Data;

@Data
public class SignalMessage {
    private String type;   // e.g. "chat", "code"
    private String from;   // User ID
    private String room;   // Room or session ID
    private String email;
    private Object data;   // Text message or code
}
