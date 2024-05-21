package com.nighthawk.spring_portfolio.mvc.handler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Slf4j
public class MyHandler extends TextWebSocketHandler {

    @Getter
    private Map<String, Player> players = new HashMap<>();
    private Map<String, BiConsumer<WebSocketSession, String>> commandMap = new HashMap<>();

    public MyHandler() {
        commandMap.put("hello", (session, params) -> {
            try {
                handleHello(session, params);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        commandMap.put("register", (session, params) -> {
            try {
                handleRegister(session, params);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        commandMap.put("attack", (session, params) -> {
            try {
                handleAttack(session, params);
            } catch (IOException e) {
                // TODO Auto-generated catch block
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

    private void handleRegister(WebSocketSession session, String params) throws IOException {
        // Expected params format: "playerName;attack;health"
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
        target.getSession().sendMessage(new TextMessage("You were attacked by " + attackerName + ". Your new Health: " + target.getHealth()));
    }

    @Getter
    public static class Player {
        private String name;
        private int attack;
        private int health;
        private WebSocketSession session;

        public Player(String name, int attack, int health, WebSocketSession session) {
            this.name = name;
            this.attack = attack;
            this.health = health;
            this.session = session;
        }

        public void setHealth(int health) {
            this.health = health;
        }
    }
}
