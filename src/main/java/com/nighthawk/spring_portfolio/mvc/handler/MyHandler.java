package com.nighthawk.spring_portfolio.mvc.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyHandler extends TextWebSocketHandler {

    private final Map<String, BiConsumer<WebSocketSession, String>> commandMap = new HashMap<>();
    private final LobbyManager lobbyManager;

    public MyHandler(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
        commandMap.put("hello", (t, u) -> {
            try {
                handleHello(t, u);
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

    private void handleAttack(WebSocketSession session, String params) throws IOException {
        String[] parts = params.split(";");
        if (parts.length < 2) {
            session.sendMessage(new TextMessage("Invalid input format. Expecting 'attackerName;targetName'."));
            return;
        }

        String attackerName = parts[0];
        String targetName = parts[1];

        Player attacker = lobbyManager.getPlayer(attackerName);
        Player target = lobbyManager.getPlayer(targetName);

        if (attacker == null || target == null) {
            session.sendMessage(new TextMessage("Invalid player names."));
            return;
        }

        target.setHealth(target.getHealth() - attacker.getAttack());
        session.sendMessage(new TextMessage("Player " + targetName + " was attacked by " + attackerName + ". New Health: " + target.getHealth()));
        target.getSession().sendMessage(new TextMessage("You were attacked by " + attackerName + ". Your new Health: " + target.getHealth()));
    }
}
