package com.backendExtension.ramukaka.service;

import com.backendExtension.ramukaka.aiservice.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GenerateSummaryService {

    @Autowired private ConversationSessionManager sessionService;
    @Autowired private EmailService emailService;
   @Autowired private  UserMessageService messageService;
    @Autowired private AiService aiService;

    public void generateAndSendSummary(String roomId) {
        Set<String> users = sessionService.getAllUsersInRoom(roomId);
        Map<String, String> emails = sessionService.getEmailsForRoom(roomId);
        List<String> userEmails = new ArrayList<>(emails.values());

        // Get the full conversation from the session
        String fullConversation = sessionService.getConversation(roomId);

        // Build a clear, non-hallucinating prompt for Gemini
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are RAMU Kaka AI. The following is a full chat transcript between users and yourself.\n")
                .append("Your job is to generate a professional, structured summary without inventing participants or projects.\n")
                .append("Include a section for participants, a clean summary of what happened, and list any key concepts explained.\n")
                .append("Use real names as shown. Maintain the order of conversation. Include RAMU kaka 🤖 as a participant.\n")
                .append("Keep the tone informative and readable, no corporate filler.\n\n")
                .append("Keep the code snippets shared in orders with user also")
                .append("Chat Transcript:\n")
                .append(fullConversation);

        // Call Gemini
        aiService.askGemini(prompt.toString()).subscribe(aiSummary -> {
            String cleaned = cleanGeminiMarkdown(aiSummary);

            String fullSummary =
                    "📄 RAMU Chat Summary\n" +
                            "---------------------------\n" +
                            "Date       : " + LocalDate.now() + "\n" +
                            "Time       : " + LocalTime.now().truncatedTo(ChronoUnit.MINUTES) + "\n" +
                            "Room ID    : " + roomId + "\n\n" +
                            "Chat Summary\n" +
                            "===========================\n" +
                            cleaned;

            emailService.sendEmail(userEmails, "RAMU Chat Summary - " + roomId, fullSummary);

           // messageService.clearMessagesAfterSummary(); // Optional
        });
    }

    private String cleanGeminiMarkdown(String raw) {
        return raw
                .replaceAll("\\*\\*(.*?)\\*\\*", "$1")      // Remove bold like **text**
                .replaceAll("(?m)^\\* ", "- ")              // Replace bullets with dash
                .replaceAll("(?m)^- ", "- ")                // Consistent dash formatting
                .replaceAll("(?m)^#+\\s*", "")              // Remove markdown headings
                .replaceAll("(?m)^>\\s*", "")               // Remove blockquotes if any
                .replaceAll("\\n{3,}", "\n\n")              // Limit multiple line breaks
                .trim();
    }


}
