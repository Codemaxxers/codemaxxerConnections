package com.nighthawk.spring_portfolio.mvc.handler;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lobby")
public class LobbyController {

    private final LobbyManager lobbyManager;

    public LobbyController(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @PostMapping("/createLobby")
    public String createLobby(@RequestParam String lobbyId) {
        lobbyManager.createLobby(lobbyId);
        return "Lobby " + lobbyId + " created.";
    }

    @PostMapping("/registerAndJoin")
    public String registerAndJoin(@RequestParam String lobbyId, @RequestParam String playerName, @RequestParam int attack, @RequestParam int health) {
        Player player = new Player(playerName, attack, health, null); // WebSocket session will be set later
        lobbyManager.registerPlayer(player);

        LobbyManager.Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            return "Lobby " + lobbyId + " does not exist.";
        }

        if (lobby.isFull()) {
            return "Lobby " + lobbyId + " is full.";
        }

        lobby.addPlayer(player);
        return "Player " + playerName + " registered with Attack: " + attack + ", Health: " + health + " and joined lobby " + lobbyId;
    }

    @GetMapping("/list")
    public Map<String, LobbyManager.Lobby> listLobbies() {
        return lobbyManager.getLobbies();
    }

    @DeleteMapping("/removeLobby")
    public String removeLobby(@RequestParam String lobbyId) {
        lobbyManager.removeLobby(lobbyId);
        return "Lobby " + lobbyId + " removed.";
    }
}
