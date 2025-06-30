package com.backendExtension.ramukaka.controller;

import com.backendExtension.ramukaka.service.ConversationSessionManager;
import com.backendExtension.ramukaka.service.EmailService;
import com.backendExtension.ramukaka.service.GenerateSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/summary")
public class EmailController {

//    private final ConversationSessionManager sessionManager;
//    private final EmailService emailService;
//
//    @GetMapping("/api/send-summary/{roomId}")
//    public ResponseEntity<String> sendSummary(@PathVariable String roomId) {
//        Map<String, String> userEmails = sessionManager.getEmailsForRoom(roomId);
//        List<String> recipients = new ArrayList<>(userEmails.values());
//
//        if (recipients.isEmpty()) return ResponseEntity.badRequest().body("No emails found.");
//
//        String subject = "📋 RAMU Chat Summary - Room: " + roomId;
//        String content = sessionManager.getConversation(roomId); // get chat history
//
//        emailService.sendEmail(recipients, subject, content);
//        return ResponseEntity.ok("Summary sent to: " + recipients);
//    }

    @Autowired
    private GenerateSummaryService summaryService;

    @PostMapping("/send/{roomId}")
    public ResponseEntity<String> sendSummary(@PathVariable String roomId) {
        summaryService.generateAndSendSummary(roomId);
        return ResponseEntity.ok("Summary is being generated and emailed.");
    }
}
