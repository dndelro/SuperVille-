import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * @authors Dion Del Rosario, Drew Kuykendall
 * @version 1.0
 * Course: ITEC 3860 Summer 2023
 *
 *
 *
 * Purpose: Create room objects
 *
 */
public class Room {
    private int Number;
    private String Name;
    private String description;
    private int north;
    private int west;
    private int south;
    private int east;
    private boolean visited = false;
    private String monsterName;
    private Monster monster;
    private int playerHealth;


    public Room(String name, int number, String description, int north, int west, int south, int east, String monsterName) {
        Number = number;
        Name = name;
        this.description = description;
        this.north = north;
        this.west = west;
        this.south = south;
        this.east = east;
        this.monsterName = monsterName;
    }

    public Room(ResultSet rs) throws SQLException {
        this.Number = rs.getInt("roomNumber");
        this.Name = rs.getString("roomDescription"); // Assuming the "name" maps to "roomDescription" in DB
        this.description = rs.getString("secondDescription");
        this.north = rs.getInt("north");
        this.west = rs.getInt("west");
        this.south = rs.getInt("south");
        this.east = rs.getInt("east");
        this.visited = false; // initially setting visited as false
        this.monsterName = rs.getString("monsterName");
    }

    // Other getter methods...

    public boolean isVisited() {
        return visited;
    }

    public void visitedRoom() {
        this.visited = true;
    }

    public static Room getRoomByNumber(SQLiteDB db, int roomNumber) throws SQLException {
        String query = "SELECT * FROM Room WHERE roomNumber = ?";
        PreparedStatement pstmt = db.getConnection().prepareStatement(query);
        pstmt.setInt(1, roomNumber);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return new Room(rs);
        } else {
            return null;
        }
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNorth() {
        return north;
    }

    public void setNorth(int north) {
        this.north = north;
    }

    public int getWest() {
        return west;
    }

    public void setWest(int west) {
        this.west = west;
    }

    public int getSouth() {
        return south;
    }

    public void setSouth(int south) {
        this.south = south;
    }

    public int getEast() {
        return east;
    }

    public void setEast(int east) {
        this.east = east;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public String getMonsterName() {
        return monsterName;
    }

    public void setMonsterName(String monsterName) {
        this.monsterName = monsterName;
    }
    public void setMonster(Monster monster) {
        this.monster = monster;
    }

    public Monster getMonster() {
        return monster;
    }

    public boolean hasMonster() {
        return monster != null;
    }

}