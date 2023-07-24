import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Authors: Drew and Dion
 *
 * The Combat class represents the combat system in the game. It allows players and monsters to engage
 * in a turn-based combat.
 */


public class Combat {
    private Player player;
    private Monster monster;
    private Scanner input;

    public Combat(Player player, Monster monster) {
        this.player = player;
        this.monster = monster;
        this.input = new Scanner(System.in);
    }

    public void startCombat() {
        System.out.println("Combat starts between " + player.getName() + " and " + monster.getName() + "!");

        while (player.isAlive() && monster.isAlive()) {
            playerTurn();
            if (!monster.isAlive()) {
                break;
            }
            monsterTurn();
        }

        if (player.isAlive()) {
            System.out.println(player.getName() + " wins!");
        } else {
            System.out.println(monster.getName() + " wins!");
        }
    }

    private void playerTurn() {
        System.out.println("\n" + player.getName() + "'s Turn");

        // Perform the custom move with maximum damage
        Move customMove = new Move("Custom Move", Integer.MAX_VALUE);
        player.performMove(customMove, monster);

        if (!monster.isAlive()) {
            System.out.println(player.getName() + " wins!");
            return;
        }

        // Display monster's remaining health
        System.out.println(monster.getName() + " has " + monster.getHealth() + " health remaining.");

        // Prompt for next player action
        System.out.println("Press enter to continue...");
        input.nextLine();
    }



    private void monsterTurn() {
        System.out.println("\n" + monster.getName() + "'s Turn");

        // Create a Random object
        Random random = new Random();

        // Choose a random move for the monster
        List<Move> moves = monster.getMoves();
        int moveIndex = random.nextInt(moves.size());
        Move selectedMove = moves.get(moveIndex);
        monster.performMove(selectedMove, player);
    }

}
