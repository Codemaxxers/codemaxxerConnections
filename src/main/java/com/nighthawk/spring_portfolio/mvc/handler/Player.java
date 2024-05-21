package com.nighthawk.spring_portfolio.mvc.handler;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class Player {
    private final String name;
    private final int attack;
    private int health;
    private final WebSocketSession session;

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
