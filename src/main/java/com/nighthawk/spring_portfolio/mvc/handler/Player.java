package com.nighthawk.spring_portfolio.mvc.handler;

import org.springframework.web.socket.WebSocketSession;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
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
}
