package com.backendExtension.ramukaka.service;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationSessionManager {

    // Stores full conversation per room (user messages + RAMU's replies)
    private final Map<String, StringBuilder> fullConversations = new ConcurrentHashMap<>();

    // Stores the most recent code snippet shared in each room
    private final Map<String, String> codeSnippets = new ConcurrentHashMap<>();

    // Stores the most recent response RAMU gave in each room
    private final Map<String, String> lastBotReply = new ConcurrentHashMap<>();

    // store the latest shared code per room
    private final Map<String, String> latestSharedCode = new ConcurrentHashMap<>();

    private final Map<String,String>latestReviewedCode = new ConcurrentHashMap<>();

    //corresponding to chat7.html
    private final Map<String, Set<String>> roomUsers = new ConcurrentHashMap<>();

    private final Map<String, String> userEmailMap = new ConcurrentHashMap<>();

    public void registerEmailForUser(String username, String email) {
        userEmailMap.put(username, email);
    }

    public void unregisterEmail(String username) {
        userEmailMap.remove(username);
    }

    public Map<String, String> getEmailsForRoom(String roomId) {
        Set<String> users = getAllUsersInRoom(roomId);
        Map<String, String> emails = new HashMap<>();
        for (String user : users) {
            if (userEmailMap.containsKey(user)) {
                emails.put(user, userEmailMap.get(user));
            }
        }
        return emails;
    }


    public void addUser(String room, String user) {
        roomUsers.computeIfAbsent(room, k -> ConcurrentHashMap.newKeySet()).add(user);
    }

    public void removeUser(String room, String user) {
        Set<String> users = roomUsers.get(room);
        if (users != null) users.remove(user);
    }

    public Set<String> getAllUsersInRoom(String room) {
        return roomUsers.getOrDefault(room, Set.of());
    }

//    private final Map<String, Map<String, String>> roomUsers = new ConcurrentHashMap<>();
//
//    public void addUser(String roomId, String username, String email) {
//        roomUsers.computeIfAbsent(roomId, r -> new ConcurrentHashMap<>()).put(username, email);
//    }
//
//    public void removeUser(String roomId, String username) {
//        if (roomUsers.containsKey(roomId)) {
//            roomUsers.get(roomId).remove(username);
//        }
//    }
//
//    public Map<String, String> getUsersWithEmail(String roomId) {
//        return roomUsers.getOrDefault(roomId, Collections.emptyMap());
//    }




    public void storeLatestReviewedCode(String room, String code)
    {
        latestReviewedCode.put(room,code);
    }

    public String  getLatestReviewedCode(String room)
    {
        return latestReviewedCode.getOrDefault(room,"");
    }

    public void storeLatestSharedCode(String room, String code) {
        latestSharedCode.put(room, code);
    }

    public String getLatestSharedCode(String room) {
        return latestSharedCode.getOrDefault(room, "");
    }


    public void appendToConversation(String room, String user, String message) {
        fullConversations
                .computeIfAbsent(room, r -> new StringBuilder())
                .append(user).append(": ").append(message).append("\n");
    }

    public String getConversation(String room) {
        return fullConversations.getOrDefault(room, new StringBuilder()).toString();
    }

    public void setCode(String room, String code) {
        codeSnippets.put(room, code);
    }

    public String getCode(String room) {
        return codeSnippets.getOrDefault(room, "");
    }

    public void setLastBotReply(String room, String reply) {
        lastBotReply.put(room, reply);
    }

    public String getLastBotReply(String room) {
        return lastBotReply.getOrDefault(room, "");
    }
}

