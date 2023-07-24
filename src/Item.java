import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/*
 * Authors: Drew and Dion
 *
 * The Item class represents an item in the game. It contains information about the item such as its
 * item number, name, description, and location. The class provides methods to retrieve items by location
 * from the database and to save items to the database.
 */

public class Item {
    private int itemNumber;
    private String name;
    private String description;
    private int location;

    public Item(int itemNumber, String name, String description, int location) {
        this.itemNumber = itemNumber;
        this.name = name;
        this.description = description;
        this.location = location;
    }

    public Item(ResultSet rs) throws SQLException {
        this.itemNumber = rs.getInt("ItemNumber");
        this.name = rs.getString("Name");
        this.description = rs.getString("Description");
        this.location = rs.getInt("Location");
    }

    // Getters and setters...

    public int getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public static List<Item> getItemsByLocation(Connection conn, int location) throws SQLException {
        String query = "SELECT * FROM Item WHERE Location = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, location);
        ResultSet rs = pstmt.executeQuery();
        List<Item> items = new ArrayList<>();
        while (rs.next()) {
            items.add(new Item(rs));
        }
        return items;
    }


//SQliteDB db pass might need to be Connection Conn
    public void save(SQLiteDB db) throws SQLException {
        String query = "INSERT INTO Item (ItemNumber, Name, Description, Location) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = db.getConnection().prepareStatement(query);
        pstmt.setInt(1, this.itemNumber);
        pstmt.setString(2, this.name);
        pstmt.setString(3, this.description);
        pstmt.setInt(4, this.location);
        pstmt.executeUpdate();
    }
}
