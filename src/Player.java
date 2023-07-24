import java.util.ArrayList;
import java.util.List;

/**
 * @authors Dion Del Rosario, Drew Kuykendall
 * @version 1.0
 * Course: ITEC 3860 Summer 2023
 *
 *
 *
 * Purpose: Handles player information
 *
 */
public class Player {
    private String name;
    private int health;
    private List<Move> moves;

    public Player(String name, int health) {
        this.name = name;
        this.health = health;
        this.moves = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void addMove(Move move) {
        moves.add(move);
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void performMove(Move move, Monster target) {
        int damage = 9999; // Set the damage to maximum value
        target.takeDamage(damage);
        System.out.println(name + " performs " + move.getName() + " on " + target.getName() + " for " + damage + " damage!");
    }

}
