package com.nighthawk.spring_portfolio.mvc.handler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Slf4j
public class MyHandler extends TextWebSocketHandler {

    private final LobbyManager lobbyManager = new LobbyManager();
    @Getter
    private final Map<String, Player> players = new HashMap<>();
    private final Map<String, BiConsumer<WebSocketSession, String>> commandMap = new HashMap<>();

    public MyHandler() {
        commandMap.put("hello", (t, u) -> {
            try {
                handleHello(t, u);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        commandMap.put("register", (t, u) -> {
            try {
                handleRegister(t, u);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        commandMap.put("attack", (t, u) -> {
            try {
                handleAttack(t, u);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        commandMap.put("createLobby", (t, u) -> {
            try {
                handleCreateLobby(t, u);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        commandMap.put("joinLobby", (t, u) -> {
            try {
                handleJoinLobby(t, u);
            } catch (IOException e) {
                // TODO Auto-generated catch block
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

    private void handleRegister(WebSocketSession session, String params) throws IOException {
        String[] parts = params.split(";");
        if (parts.length < 3) {
            session.sendMessage(new TextMessage("Invalid input format. Expecting 'playerName;attack;health'."));
            return;
        }

        String playerName = parts[0];
        int attack = Integer.parseInt(parts[1]);
        int health = Integer.parseInt(parts[2]);

        players.put(playerName, new Player(playerName, attack, health, session));
        session.sendMessage(new TextMessage("Player " + playerName + " registered with Attack: " + attack + ", Health: " + health));
    }

    private void handleAttack(WebSocketSession session, String params) throws IOException {
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
        target.getSession().sendMessage(new TextMessage("You were attacked by " + attackerName + ". Your new Health: " + target.getHealth()));
    }

    private void handleCreateLobby(WebSocketSession session, String params) throws IOException {
        String lobbyId = params.trim();
        lobbyManager.createLobby(lobbyId);
        session.sendMessage(new TextMessage("Lobby " + lobbyId + " created."));
    }

    private void handleJoinLobby(WebSocketSession session, String params) throws IOException {
        String[] parts = params.split(";");
        if (parts.length < 2) {
            session.sendMessage(new TextMessage("Invalid input format. Expecting 'lobbyId;playerName'."));
            return;
        }

        String lobbyId = parts[0];
        String playerName = parts[1];
        Player player = players.get(playerName);

        if (player == null) {
            session.sendMessage(new TextMessage("Player " + playerName + " not found."));
            return;
        }

        LobbyManager.Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            session.sendMessage(new TextMessage("Lobby " + lobbyId + " not found."));
            return;
        }

        if (lobby.isFull()) {
            session.sendMessage(new TextMessage("Lobby " + lobbyId + " is full."));
            return;
        }

        lobby.addPlayer(player);
        session.sendMessage(new TextMessage("Player " + playerName + " joined lobby " + lobbyId + "."));
        player.getSession().sendMessage(new TextMessage("You joined lobby " + lobbyId + "."));
    }
}
