import java.util.List;
/**
 * @authors Dion Del Rosario, Drew Kuykendall
 * @version 5.0
 * Course: ITEC 3860 Summer 2023
 *
 *
 *
 * Purpose: Handle creation of Monster object
 *
 */
public class Monster {
    private String name;
    private int health;
    private List<Move> moves;

    public Monster(String name, int health) {
        this.name = name;
        this.health = health;
        this.moves = moves;
    }



    // Rest of the Monster class code...



    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void addMove(Move move) {
        moves.add(move);
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

    public void performMove(Move move, Player target) {
        int damage = move.getDamage();
        target.takeDamage(damage);
        System.out.println(name + " performs " + move.getName() + " on " + target.getName() + " for " + damage + " damage!");
    }
}