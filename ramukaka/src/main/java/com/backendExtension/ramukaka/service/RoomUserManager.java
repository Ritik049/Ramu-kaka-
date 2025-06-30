package com.backendExtension.ramukaka.service;



import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoomUserManager {

    private final Map<String, Set<String>> roomUsers = new HashMap<>();

    public synchronized void addUserToRoom(String roomId, String userName) {
        roomUsers.computeIfAbsent(roomId, k -> new HashSet<>()).add(userName);
    }

    public synchronized void removeUserFromRoom(String roomId, String userName) {
        if (roomUsers.containsKey(roomId)) {
            roomUsers.get(roomId).remove(userName);
            if (roomUsers.get(roomId).isEmpty()) {
                roomUsers.remove(roomId);
            }
        }
    }

    public synchronized Set<String> getUsersInRoom(String roomId) {
        return roomUsers.getOrDefault(roomId, Collections.emptySet());
    }

    public synchronized Map<String, Set<String>> getAllRoomUsers() {
        return roomUsers;
    }
}
