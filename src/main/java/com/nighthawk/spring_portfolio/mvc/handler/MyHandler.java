package com.nighthawk.spring_portfolio.mvc.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyHandler extends TextWebSocketHandler {

    @Getter
    private Map<String, Player> players = new HashMap<>();
    private Map<String, BiConsumer<WebSocketSession, String>> commandMap = new HashMap<>();

    private final LobbyManager lobbyManager;

    public MyHandler(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;

        commandMap.put("hello", (session, params) -> {
            try {
                handleHello(session, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        commandMap.put("register", (session, params) -> {
            try {
                handleRegister(session, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        commandMap.put("attack", (session, params) -> {
            try {
                handleAttack(session, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        commandMap.put("connect", (session, params) -> {
            try {
                handleConnect(session, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

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

    private void handleConnect(WebSocketSession session, String params) throws IOException {
        // Expected params format: "playerName;lobbyId"
        String[] parts = params.split(";");
        if (parts.length < 2) {
          session.sendMessage(new TextMessage("Invalid input format. Expecting 'playerName;lobbyId'."));
          return;
        }
      
        String playerName = parts[0];
        String lobbyId = parts[1];
      
        LobbyManager.Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby == null) {
          session.sendMessage(new TextMessage("Lobby " + lobbyId + " does not exist."));
          return;
        }
      
        Player player = players.get(playerName);
        if (player == null) {
          session.sendMessage(new TextMessage("Player " + playerName + " not registered."));
          return;
        }
    }
      

    private void handleRegister(WebSocketSession session, String params) throws IOException {
        // Expected params format: "playerName;lobbyId;attack;health"
        String[] parts = params.split(";");
        if (parts.length < 4) {
            session.sendMessage(new TextMessage("Invalid input format. Expecting 'playerName;lobbyId;attack;health'."));
            return;
        }

        String playerName = parts[0];
        String lobbyId = parts[1];
        int attack = Integer.parseInt(parts[2]);
        int health = Integer.parseInt(parts[3]);

        Player player = new Player(playerName, attack, health);
        players.put(playerName, player);
        
        LobbyManager.Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            lobby.addPlayer(player);
            session.sendMessage(new TextMessage("Player " + playerName + " registered with Attack: " + attack + ", Health: " + health + " and joined lobby " + lobbyId));
        } else {
            session.sendMessage(new TextMessage("Lobby " + lobbyId + " does not exist."));
        }
    }

    private void handleAttack(WebSocketSession session, String params) throws IOException {
        // Expected params format: "attackerName;targetName"
        String[] parts = params.split(";");
        if (parts.length < 2) {
            session.sendMessage(new TextMessage("Invalid input format. Expecting 'attackerName;targetName'."));
            return;
        }

        String attackerName = parts[0];
        String targetName = parts[1];

        Player attacker = players.get(attackerName);
        Player target = players.get(targetName);

        if (attacker == null || target == null) {
            session.sendMessage(new TextMessage("Invalid player names."));
            return;
        }

        target.setHealth(target.getHealth() - attacker.getAttack());
        session.sendMessage(new TextMessage("Player " + targetName + " was attacked by " + attackerName + ". New Health: " + target.getHealth()));
    }
}
