import java.nio.file.*;
import java.sql.*;
import java.io.*;
/**
 * @authors Dion Del Rosario, Drew Kuykendall
 * @version 2.0
 * Course: ITEC 3860 Summer 2023
 *
 *
 *
 * Purpose: Handle database creation
 *
 */
public class SQLiteDB {
    private static final String URL = "jdbc:sqlite:"+ GameTester.playerName + ".db";
   // private static final String URL = "jdbc:sqlite:GameDB.db";

    public static void setupDatabase() {
        String commands;

        try {
            commands = new String(Files.readAllBytes(Paths.get("Room.txt")));
            executeCommands(URL, commands);
            createSaveDataTable(); // Create the SaveData table
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }

    private static void executeCommands(String url, String commands) {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // Split by semicolon and execute each command
            String[] commandList = commands.split(";");
            for (String command : commandList) {
                stmt.execute(command);
            }

            // Check if 'monsterName' column already exists
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(Room)");
            boolean columnExists = false;
            while (rs.next()) {
                String columnName = rs.getString("name");
                if (columnName.equals("monsterName")) {
                    columnExists = true;
                    break;
                }
            }

            // If 'monsterName' column does not exist, add it to the 'Room' table
            if (!columnExists) {
                String addColumnCommand = "ALTER TABLE Room ADD COLUMN monsterName TEXT";
                stmt.execute(addColumnCommand);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    private static void createSaveDataTable() {
        String sql = "CREATE TABLE IF NOT EXISTS SaveData (" +
                "playerName TEXT PRIMARY KEY," +
                "roomNumber INTEGER NOT NULL" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void saveGame(String playerName, int roomNumber) {
        String sql = "REPLACE INTO SaveData (playerName, roomNumber) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            pstmt.setInt(2, roomNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to save the game: " + e.getMessage());
        }
    }

    public static int loadGame(String playerName) {
        String sql = "SELECT roomNumber FROM SaveData WHERE playerName = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("roomNumber");
            }
        } catch (SQLException e) {
            System.out.println("Failed to load the game: " + e.getMessage());
        }
        return -1; // Return -1 if no saved game found
    }
//Unnecessary Delete sometime soon
    private static void printDB(String url) {
        String sql = "SELECT * FROM Room";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Get column count
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    System.out.print(rs.getString(i) + " ");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
