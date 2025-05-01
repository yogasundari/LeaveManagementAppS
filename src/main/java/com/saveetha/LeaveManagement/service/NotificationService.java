package com.saveetha.LeaveManagement.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final Map<String, List<String>> notificationMap = new ConcurrentHashMap<>();

    public void sendNotification(String empId, String message) {
        System.out.println("Sending notification to: " + empId + " -> " + message);
        notificationMap.computeIfAbsent(empId, k -> new ArrayList<>()).add(message);
    }

    public List<String> getNotifications(String empId) {
        return notificationMap.getOrDefault(empId, Collections.emptyList());
    }

    public void clearNotifications(String empId) {
        notificationMap.remove(empId);
    }


}
