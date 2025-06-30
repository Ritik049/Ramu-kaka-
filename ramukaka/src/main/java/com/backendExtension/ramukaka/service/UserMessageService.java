package com.backendExtension.ramukaka.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserMessageService {
    private final Map<String, List<String>> userMessageMap = new ConcurrentHashMap<>();

    public void saveUserMessage(String username, String message) {
        userMessageMap.computeIfAbsent(username, k -> new ArrayList<>()).add(message);
    }

    public Map<String, List<String>> getAllUserMessages() {
        return userMessageMap;
    }

    public void clearMessagesAfterSummary() {
        userMessageMap.clear();
    }

}

