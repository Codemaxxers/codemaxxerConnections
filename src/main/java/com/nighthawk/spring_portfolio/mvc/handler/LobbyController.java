package com.nighthawk.spring_portfolio.mvc.handler;

import com.nighthawk.spring_portfolio.mvc.handler.LobbyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {

    @Autowired
    private LobbyManager lobbyManager;

    @GetMapping
    public Map<String, LobbyManager.Lobby> getLobbies() {
        return lobbyManager.getLobbies();
    }
}
