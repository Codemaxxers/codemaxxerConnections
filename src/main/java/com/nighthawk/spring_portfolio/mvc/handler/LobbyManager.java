package com.nighthawk.spring_portfolio.mvc.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
public class LobbyManager {
    @Getter
    private final Map<String, Lobby> lobbies = new HashMap<>();
    @Getter
    private final Map<String, Player> players = new HashMap<>(); // Global map for all players

    public void createLobby(String lobbyId) {
        lobbies.put(lobbyId, new Lobby(lobbyId));
    }

    public Lobby getLobby(String lobbyId) {
        return lobbies.get(lobbyId);
    }

    public void removeLobby(String lobbyId) {
        Lobby lobby = lobbies.remove(lobbyId);
        if (lobby != null) {
            lobby.getPlayers().forEach((name, player) -> players.remove(name));
        }
    }

    public void registerPlayer(Player player) {
        players.put(player.getName(), player);
    }

    public Player getPlayer(String playerName) {
        return players.get(playerName);
    }

    @Getter
    public static class Lobby {
        private final String id;
        private final Map<String, Player> players = new HashMap<>();

        public Lobby(String id) {
            this.id = id;
        }

        public void addPlayer(Player player) {
            if (players.size() < 2) {
                players.put(player.getName(), player);
            }
        }

        public boolean isFull() {
            return players.size() >= 2;
        }
    }
}
