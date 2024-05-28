package com.nighthawk.spring_portfolio.mvc.handler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private String name;
    private int attack;
    private int health;

    public Player(String name, int attack, int health) {
        this.name = name;
        this.attack = attack;
        this.health = health;
    }
}
