package com.nighthawk.spring_portfolio.mvc.handler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Slf4j
public class MyHandler extends TextWebSocketHandler {
    @Getter
    private List<WebSocketSession> list = new ArrayList<>();
    private Map<String, BiConsumer<WebSocketSession, String>> commandMap = new HashMap<>();

    public MyHandler() {
        commandMap.put("hello", (t, u) -> {
            try {
                handleHello(t, u);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        commandMap.put("test", (t, u) -> {
            try {
                handleTest(t, u);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        // Add more commands and their corresponding methods here
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("Received message: {}", message.getPayload());
        String payload = message.getPayload();
        String[] parts = payload.split(":", 2);
        String command = parts[0];
        String params = parts.length > 1 ? parts[1] : "";

        BiConsumer<WebSocketSession, String> handler = commandMap.get(command);
        if (handler != null) {
            handler.accept(session, params);
        } else {
            session.sendMessage(new TextMessage("Unknown command: " + command));
        }
    }

    private void handleHello(WebSocketSession session, String params) throws IOException {
        session.sendMessage(new TextMessage("Hello, " + params));
    }

    private void handleTest(WebSocketSession session, String params) throws IOException {
        session.sendMessage(new TextMessage("Testing: " + params));
    }

    // Define other methods for different commands
}
