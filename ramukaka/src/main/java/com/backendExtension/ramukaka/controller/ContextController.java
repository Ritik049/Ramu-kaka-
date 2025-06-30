package com.backendExtension.ramukaka.controller;

import com.backendExtension.ramukaka.dto.ContextPayload;
import com.backendExtension.ramukaka.dto.FileData;
import com.backendExtension.ramukaka.service.ConversationSessionManager;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/context")
public class ContextController {
    private final ConversationSessionManager sessionManager;

    public ContextController(ConversationSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @PostMapping
    public void rememberWorkspace(@RequestBody ContextPayload payload) {
        StringBuilder memory = new StringBuilder();
        for (FileData f : payload.getFiles()) {
            memory.append("📄 ").append(f.getFilePath()).append("\n")
                    .append(f.getContent()).append("\n\n");
        }
        sessionManager.appendToConversation(payload.getRoom(), "Workspace", memory.toString());
        sessionManager.storeLatestSharedCode(payload.getRoom(), memory.toString());
    }




}
