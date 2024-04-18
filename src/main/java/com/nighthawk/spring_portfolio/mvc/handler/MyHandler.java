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
import java.util.Arrays;
import java.util.stream.Collectors;


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

        commandMap.put("playerposition", (session, message) -> {
            try {
                handlePlayerPosition(session, message);
            } catch (IOException e) {
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

    private void handlePlayerPosition(WebSocketSession session, String params) throws IOException {
        // Assuming params are in the format "name=John;x=100;y=200"
        Map<String, String> paramMap = Arrays.stream(params.split(";"))
                                             .map(p -> p.split("="))
                                             .collect(Collectors.toMap(p -> p[0], p -> p[1]));
    
        String name = paramMap.get("name");
        String x = paramMap.get("x");
        String y = paramMap.get("y");
    
        // Now send these to a function that handles them
        processPlayerPosition(name, x, y);
    
        // Optionally send a response back to the client
        session.sendMessage(new TextMessage("Position for " + name + " processed."));
    }
    
    private void processPlayerPosition(String name, String x, String y) {
        // This function currently does nothing, but you can add logic as needed.
        log.warn("Processing position - Name: {}, X: {}, Y: {}", name, x, y);
    }
    

    // Define other methods for different commands
}
