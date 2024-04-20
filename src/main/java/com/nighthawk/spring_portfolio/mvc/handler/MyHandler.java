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
        commandMap.put("getplayerposition", (session, playerName) -> {
            try {
                handleGetPlayerPosition(session, playerName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        commandMap.put("getallplayerposition", (session, message) -> {
            try {
                handleGetAllPlayerPositions(session);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Add more commands and their corresponding methods here
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.warn("Received message: {}", message.getPayload());
        String payload = message.getPayload();
        int indexOfColon = payload.indexOf(':');
        String command = payload.substring(0, indexOfColon != -1 ? indexOfColon : payload.length());
        String params = indexOfColon != -1 ? payload.substring(indexOfColon + 1) : "";

        BiConsumer<WebSocketSession, String> handler = commandMap.get(command);
        if (handler != null) {
            handler.accept(session, params.trim());
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
        // Splitting the params on semicolon which are expected to be in the order: name, x, y
        String[] parts = params.split(";");
        if (parts.length < 3) {
            // Not enough parts, handle error appropriately
            session.sendMessage(new TextMessage("Invalid input format. Expecting 'name;x;y'."));
            return;
        }

        String name = parts[0];
        String x = parts[1];
        String y = parts[2];

        // Now send these to a function that handles them
        processPlayerPosition(name, x, y);

        // Optionally send a response back to the client
        session.sendMessage(new TextMessage("Position for " + name + " processed."));
    }
    
    private void processPlayerPosition(String name, String x, String y) {
        // This function currently does nothing, but you can add logic as needed.
        log.warn("Processing position - Name: {}, X: {}, Y: {}", name, x, y);
    }

    private void handleGetPlayerPosition(WebSocketSession session, String playerName) throws IOException {
        try {
            // Log the request
            log.warn("Fetching position for player: {}", playerName);
    
            // Here you would typically retrieve the player's position from your data store
            String position = "X: 100, Y: 200";  // This is an arbitrary position for demonstration
    
            // Send the response back to the client
            session.sendMessage(new TextMessage("Position for " + playerName + ": " + position));
        } catch (Exception e) {
            log.error("Error fetching player position: ", e);
            session.sendMessage(new TextMessage("Error fetching player position"));
        }
    }

    private void handleGetAllPlayerPositions(WebSocketSession session) throws IOException {
        try {
            // Log the request
            log.warn("Fetching positions for all players");
    
            // Here you would typically retrieve all player positions from your data store
            // For demonstration, we use arbitrary data
            String positions = "Player1: X: 100, Y: 200; Player2: X: 150, Y: 250";
    
            // Send the response back to the client
            session.sendMessage(new TextMessage("All Player Positions: " + positions));
        } catch (Exception e) {
            log.error("Error fetching all player positions: ", e);
            session.sendMessage(new TextMessage("Error fetching all player positions"));
        }
    }
    
    // Define other methods for different commands
}
