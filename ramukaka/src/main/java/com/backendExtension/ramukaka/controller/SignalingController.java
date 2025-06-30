package com.backendExtension.ramukaka.controller;

import com.backendExtension.ramukaka.aiservice.AiService;
import com.backendExtension.ramukaka.dto.SignalMessage;
import com.backendExtension.ramukaka.service.ConversationSessionManager;
import com.backendExtension.ramukaka.service.RoomUserManager;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SignalingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final AiService aiService;
    private final ConversationSessionManager sessionManager;
    private final RoomUserManager roomUserManager;

//    @MessageMapping("/chat")
//    public void handleChat(SignalMessage message) {
//        String room = message.getRoom();
//        String user = message.getFrom();
//        String text = String.valueOf(message.getData()).trim();
//
//        //adding user
//        roomUserManager.addUserToRoom(room, user);
//
//        // 1. Save and broadcast message
//        sessionManager.appendToConversation(room, user, text);
//        messagingTemplate.convertAndSend("/topic/chat/" + room, message);
//
//        //broadcast message
//        messagingTemplate.convertAndSend("/topic/users/" + room, roomUserManager.getUsersInRoom(room));
//
//        // 2. Let Gemini decide if RAMU should respond
//        if (text.toLowerCase().contains("ramu")) {
//            String convo = sessionManager.getConversation(room);
//
//            String prompt = """
//                You are RAMU kaka 🤖 — a warm, helpful assistant in a developer chatroom.
//                Here's the conversation so far:
//
//                %s
//
//                The user just said:
//                "%s"
//
//                Respond ONLY IF the user's latest message is a question, code-related request, or needs clarification.
//                Otherwise, reply with an empty string.
//                Your reply should sound friendly, concise, and human-like. Use markdown formatting for emphasis or code if helpful.
//                """.formatted(convo, text);
//
//            aiService.askGemini(prompt).subscribe(reply -> {
//                if (!reply.trim().isEmpty()) {
//                    sessionManager.setLastBotReply(room, reply);
//
//                    SignalMessage bot = new SignalMessage();
//                    bot.setFrom("RAMU kaka 🤖");
//                    bot.setRoom(room);
//                    bot.setType("chat");
//                    bot.setData(reply);
//                    messagingTemplate.convertAndSend("/topic/chat/" + room, bot);
//                }
//            });
//        }
//    }

    @MessageMapping("/chat")
    public void handleChat(SignalMessage message) {
        String room = message.getRoom();
        String user = message.getFrom();
        String type = message.getType();
        String text = String.valueOf(message.getData()).trim();
        String email = message.getEmail();

        switch (type) {
            case "join" -> {
                sessionManager.addUser(room, user);  // store user in room
                sessionManager.registerEmailForUser(user, email); // 📧 store email
                broadcastUserList(room);
                sendSystemMessage(room, user + " joined the meeting.");
            }

            case "leave" -> {
                sessionManager.removeUser(room, user);  // remove from room
                broadcastUserList(room);
                sendSystemMessage(room, user + " left the meeting.");
                sessionManager.unregisterEmail(user);
            }

            case "chat" -> {
                sessionManager.appendToConversation(room, user, text);
                messagingTemplate.convertAndSend("/topic/chat/" + room, message);

                if (text.toLowerCase().contains("ramu")) {
                    String convo = sessionManager.getConversation(room);
                    String prompt = """
                    You are RAMU kaka 🤖 — a warm, helpful assistant in a developer chatroom.
                    Here's the conversation so far:

                    %s

                    The user just said:
                    "%s"

                    Respond ONLY IF the user's latest message is a question, code-related request, or needs clarification.
                    Otherwise, reply with an empty string.
                    Your reply should sound friendly, concise, and human-like. Use markdown formatting for emphasis or code if helpful.
                """.formatted(convo, text);

                    aiService.askGemini(prompt).subscribe(reply -> {
                        if (!reply.trim().isEmpty()) {
                            sessionManager.setLastBotReply(room, reply);
                            SignalMessage bot = new SignalMessage();
                            bot.setFrom("RAMU kaka 🤖");
                            bot.setRoom(room);
                            bot.setType("chat");
                            bot.setData(reply);
                            messagingTemplate.convertAndSend("/topic/chat/" + room, bot);
                        }
                    });
                }
            }
        }
    }

    private void sendSystemMessage(String room, String message) {
        SignalMessage system = new SignalMessage();
        system.setRoom(room);
        system.setType("system");
        system.setData(message);
        messagingTemplate.convertAndSend("/topic/chat/" + room, system);
    }

    private void broadcastUserList(String room) {
        var users = sessionManager.getAllUsersInRoom(room);
        messagingTemplate.convertAndSend("/topic/users/" + room, users);
    }


    @MessageMapping("/code")
    public void handleCode(SignalMessage message) {
        String room = message.getRoom();
        String user = message.getFrom();
        String code = String.valueOf(message.getData());

        sessionManager.storeLatestSharedCode(room, code);  // <-- Add this line

        // Save code snippet
        sessionManager.setCode(room, code);

        sessionManager.appendToConversation(room, user, "📄 Shared Code:\n```\n" + code + "\n```");

        // Broadcast to all participants
        SignalMessage codeShare = new SignalMessage();
        codeShare.setFrom(message.getFrom());
        codeShare.setRoom(room);
        codeShare.setType("chat");
        codeShare.setData("📄 Shared Code:\n```java\n" + code + "\n```");

        messagingTemplate.convertAndSend("/topic/chat/" + room, codeShare);
    }
}

