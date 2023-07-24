import java.io.*;
import java.util.Scanner;

/**
 * Authors: Drew and Dion
 *
 * The GameTester class serves as the entry point for the game and handles the game initialization process,
 * including player registration and login.
 */

public class GameTester {
    public static String playerName;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String password;

        System.out.println("Do you have a save file? [yes/no]");
        String userInput = input.nextLine();

        if (userInput.toLowerCase().equals("no")) {
            System.out.println("What is your name?");
            playerName = input.nextLine();
            System.out.println("Please create a password");
            password = input.nextLine();

            if (isPlayerRegistered(playerName)) {
                System.out.println("Name already exists. Registration failed, relaunch the game");
            } else {
                registerUser(playerName, password);
                SQLiteDB.setupDatabase();
                Game theGame = new Game(playerName);
                theGame.play();
            }
        } else if (userInput.toLowerCase().equals("yes")) {
            System.out.println("What is your name?");
            playerName = input.nextLine();

            System.out.println("What is your password?");
            password = input.nextLine();

            if (checkCredentials(playerName, password)) {
                SQLiteDB.setupDatabase();
                Game theGame = new Game(playerName);
                theGame.play();
            } else {
                System.out.println("Incorrect username or password. Please try launching the game again.");
            }
        } else {
            System.out.println("Incorrect input made. Please relaunch the game and type 'yes' or 'no'.");
        }
    }

    public static void registerUser(String name, String password) {
        String filename = "user_credentials.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(name + " " + password);
            writer.newLine();
            System.out.println("User credentials have been saved to " + filename);
        } catch (IOException e) {
            System.err.println("Failed to write to file: " + e.getMessage());
        }
    }

    private static boolean checkCredentials(String username, String password) {
        String filename = "user_credentials.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;  // Credentials match
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read from file: " + e.getMessage());
        }

        return false;  // No match found
    }

    public static boolean isPlayerRegistered(String playerName) {
        String filename = "user_credentials.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2 && parts[0].equals(playerName)) {
                    return true;  // Player name found
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read from file: " + e.getMessage());
        }

        return false;  // Player name not found
    }
}
