package com.example.PrototypeDesignPattern;

import java.util.ArrayList;
import java.util.List;

public class GameBotCharacters implements Prototype<GameBotCharacters> {
    // Tweak this to make "new" feel more (or less) expensive during the demo
    public static final int LOADING_TIME_MS = 2000;

    private String name;
    private int health;
    private int attackPower;
    private List<String> weapons;

    public GameBotCharacters(String name, int health, int attackPower, List<String> weapons) {

        // Expensive Operations
        System.out.println("Loading Character animations from DB.....");
        System.out.println("Loading sound effects from DB.....");
        System.out.println("Preparing AI Battle settings file.....");

        try {
            Thread.sleep(LOADING_TIME_MS);
        } catch (InterruptedException e) {
            System.out.println("Error in the thread");
        }

        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
        this.weapons = weapons;
    }

    // Copy constructor: copies the fields, skips the expensive loading above
    private GameBotCharacters(GameBotCharacters gbc) {
        this.name = gbc.name;
        this.health = gbc.health;
        this.attackPower = gbc.attackPower;
        this.weapons = new ArrayList<>(gbc.weapons); // deep copy: the clone gets its OWN list
    }

    @Override
    public GameBotCharacters customizeClone() {
        return new GameBotCharacters(this);
    }

    // FOR DEMO ONLY: shows the bug you get when the list is shared instead of copied
    public GameBotCharacters shallowClone() {
        GameBotCharacters copy = new GameBotCharacters(this);
        copy.weapons = this.weapons; // shallow copy: both bots point at the SAME list
        return copy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public void setWeapons(List<String> weapons) {
        this.weapons = weapons;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public List<String> getWeapons() {
        return weapons;
    }

    @Override
    public String toString() {
        return "GameBotCharacters{" +
                "name='" + name + '\'' +
                ", health=" + health +
                ", attackPower=" + attackPower +
                ", weapons=" + weapons +
                '}';
    }
}
