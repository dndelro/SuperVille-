/**
 * @authors Dion Del Rosario, Drew Kuykendall
 * @version 5.0
 * Course: ITEC 3860 Summer 2023
 *
 *
 *
 * Purpose: Handle main Game loop and player inputs
 *
 */

public class Move {
    private String name;
    private int power;
    private int damage;

    public Move(String name, int power) {
        this.name = name;
        this.power = power;
    }

    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }
    public int getDamage() {
        return damage;
    }
}
