package com.nighthawk.spring_portfolio.mvc.handler;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class LobbyManager {
    @Getter
    private final Map<String, Lobby> lobbies = new HashMap<>();

    public void createLobby(String lobbyId) {
        lobbies.put(lobbyId, new Lobby(lobbyId));
    }

    public Lobby getLobby(String lobbyId) {
        return lobbies.get(lobbyId);
    }

    public void removeLobby(String lobbyId) {
        lobbies.remove(lobbyId);
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
