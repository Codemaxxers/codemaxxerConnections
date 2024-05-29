package com.nighthawk.spring_portfolio.mvc.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public String createLobby() {
        String lobbyId;
        do {
            lobbyId = generateUniqueLobbyId();
        } while (lobbyManager.getLobby(lobbyId) != null);
    
        lobbyManager.createLobby(lobbyId);
        return "Lobby " + lobbyId + " created.";
    }
    
    private String generateUniqueLobbyId() {
        // Generate a random lobby ID, for example, a 6-digit number.
        int id = (int)(Math.random() * 1000000);
        return String.format("%06d", id);
    }

    @PostMapping("/registerAndJoin")
    public String registerAndJoin(@RequestParam String lobbyId, @RequestParam String playerName, @RequestParam int attack, @RequestParam int health) {
        int adjustedHealth = (int) (health * 1.5);
        Player player = new Player(playerName, attack, adjustedHealth);
        lobbyManager.registerPlayer(player);

        LobbyManager.Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            return "Lobby " + lobbyId + " does not exist.";
        }

        if (lobby.isFull()) {
            return "Lobby " + lobbyId + " is full.";
        }

        lobby.addPlayer(player);
        return "Player " + playerName + " registered with Attack: " + attack + ", Health: " + adjustedHealth + " and joined lobby " + lobbyId;
    }


    @GetMapping("/allLobbies")
    public Map<String, LobbyManager.Lobby> listLobbies() {
        return lobbyManager.getLobbies();
    }

    @GetMapping("/availableLobbies")
    public Map<String, LobbyManager.Lobby> getAvailableLobbies() {
        Map<String, LobbyManager.Lobby> availableLobbies = new HashMap<>();
        Map<String, LobbyManager.Lobby> allLobbies = lobbyManager.getLobbies();

        for (Map.Entry<String, LobbyManager.Lobby> entry : allLobbies.entrySet()) {
            LobbyManager.Lobby lobby = entry.getValue();
            if (!lobby.isFull()) {
                availableLobbies.put(entry.getKey(), lobby);
            }
        }

        return availableLobbies;
    }

    @GetMapping("/checkIfLobbyIsFull")
    public ResponseEntity<Map<String, Object>> checkIfLobbyIsFull(@RequestParam String lobbyId) {
        Map<String, Object> response = new HashMap<>();
        LobbyManager.Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            response.put("error", "Lobby " + lobbyId + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("lobbyId", lobbyId);
            response.put("isFull", lobby.isFull());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/lobbyInfo")
    public ResponseEntity<Map<String, Object>> lobbyInfo(@RequestParam String lobbyId, @RequestParam String type, @RequestParam String player, @RequestParam String target) {
        Map<String, Object> response = new HashMap<>();
        LobbyManager.Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            response.put("error", "Lobby " + lobbyId + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            // Get players and their health
            Map<String, Integer> playerHealth = new HashMap<>();
            lobby.getPlayers().forEach((playerName, playerData) ->
                playerHealth.put(playerName, playerData.getHealth()));

            // Find player with lowest health initially
            String currentPlayer = playerHealth.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            // Handle the case where the type is "turn" and set currentPlayer is now switched to target
            if ("turn".equals(type)) {
                currentPlayer = target;
            }

            if (currentPlayer == null) {
                response.put("error", "Unable to determine current player.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            response.put("lobbyId", lobbyId);
            response.put("currentPlayer", currentPlayer);
            response.put("players", lobby.getPlayers());
            return ResponseEntity.ok(response);
        }
    }



    @GetMapping("/lobbyInfoPerPlayer")
    public ResponseEntity<Map<String, Object>> lobbyInfoPerPlayer(@RequestParam String lobbyId, @RequestParam String playerName) {
        Map<String, Object> response = new HashMap<>();
        LobbyManager.Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            response.put("error", "Lobby " + lobbyId + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            Player player = lobby.getPlayers().get(playerName);
            if (player == null) {
                response.put("error", "Player " + playerName + " not found in lobby " + lobbyId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                response.put("lobbyId", lobbyId);
                response.put("player", player);
                return ResponseEntity.ok(response);
            }
        }
    }

    @PostMapping("/removeLobby")
    public String removeLobby(@RequestParam String lobbyId) {
        lobbyManager.removeLobby(lobbyId);
        return "Lobby " + lobbyId + " removed.";
    }

    @PostMapping("/attack")
    public String attack(@RequestParam String attackerName, @RequestParam String targetName) {
        Player attacker = lobbyManager.getPlayer(attackerName);
        Player target = lobbyManager.getPlayer(targetName);

        if (attacker == null || target == null) {
            return "Invalid player names.";
        }

        target.setHealth(target.getHealth() - attacker.getAttack());

        if (target.getHealth() <= 0) {
            return "Player " + targetName + " was attacked by " + attackerName + ". Player " + targetName + " has been defeated.";
        }

        return "Player " + targetName + " was attacked by " + attackerName + ". New Health: " + target.getHealth();
    }
}
