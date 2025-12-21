package com.example.simple_notification_service.controller;

import com.example.simple_notification_service.handler.NotificationHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationHandler handler;

    public NotificationController(NotificationHandler handler) {
        this.handler = handler;
    }

    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, Object>> broadcast(@RequestBody String message) {
        int sent = handler.broadcast(message);
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "sentTo", sent,
                "message", message
        ));
    }

    @PostMapping("/sendTo")
    public ResponseEntity<Map<String, Object>> sendToUser(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String message = body.get("message");

        handler.sendMessageTo(userId, message);

        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "sentTo", userId,
                "message", message
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
                "activeConnections", handler.getActiveConnections()
        ));
    }
}
