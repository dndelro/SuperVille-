import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * @authors Dion Del Rosario, Drew Kuykendall
 * @version 3.0
 * Course: ITEC 3860 Summer 2023
 *
 *
 *
 * Purpose: Create inventory object and methods involving inventory manipulation
 *
 */
public class Inventory {
    private int itemNumber;
    private String name;
    private String description;
    private int hasItem;

    public Inventory(int itemNumber, String name, String description, int hasItem) {
        this.itemNumber = itemNumber;
        this.name = name;
        this.description = description;
        this.hasItem = hasItem;
    }

    public Inventory(ResultSet rs) throws SQLException {
        this.itemNumber = rs.getInt("ItemNumber");
        this.name = rs.getString("Name");
        this.description = rs.getString("Description");
        this.hasItem = rs.getInt("hasItem");
    }

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

    public int getHasItem() {
        return hasItem;
    }

    public void setHasItem(int hasItem) {
        this.hasItem = hasItem;
    }

    public void printItem() {
        System.out.println(this.name + ", " + this.description);
    }

    public static void printAllItems(Connection conn) {
        String sql = "SELECT * FROM Inventory WHERE hasItem = 1";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Inventory inventory = new Inventory(rs);
                inventory.printItem();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void pickupItem(int roomNum, Connection conn) {
        PreparedStatement itemStmt = null;
        PreparedStatement inventoryStmt = null;

        try {
            // Prepare and execute the SQL statement to find the item in the specified room
            String itemSql = "SELECT * FROM Item WHERE Location = ?";
            itemStmt = conn.prepareStatement(itemSql);
            itemStmt.setInt(1, roomNum);
            ResultSet rs = itemStmt.executeQuery();

            while(rs.next()) {
                int itemNumber = rs.getInt("ItemNumber");

                // Update the Item's location to 0
                String updateItemSql = "UPDATE Item SET Location = 0 WHERE ItemNumber = ?";
                PreparedStatement updateItemStmt = conn.prepareStatement(updateItemSql);
                updateItemStmt.setInt(1, itemNumber);
                updateItemStmt.executeUpdate();

                // Update the Inventory's hasItem to 1
                String updateInventorySql = "UPDATE Inventory SET hasItem = 1 WHERE ItemNumber = ?";
                inventoryStmt = conn.prepareStatement(updateInventorySql);
                inventoryStmt.setInt(1, itemNumber);
                inventoryStmt.executeUpdate();
            }

        } catch(SQLException e) {
            // handle any errors
            e.printStackTrace();
        } finally {
            // Close the PreparedStatements
            try {
                if(itemStmt != null) {
                    itemStmt.close();
                }
                if(inventoryStmt != null) {
                    inventoryStmt.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void dropItem(String itemName, int roomNum, Connection conn) {
        PreparedStatement itemStmt = null;
        PreparedStatement inventoryStmt = null;

        try {
            // Prepare and execute the SQL statement to find the specified item in the player's inventory
            String inventorySql = "SELECT * FROM Inventory WHERE hasItem = 1 AND Name = ?";
            inventoryStmt = conn.prepareStatement(inventorySql);
            inventoryStmt.setString(1, itemName);
            ResultSet rs = inventoryStmt.executeQuery();

            if (rs.next()) {
                int itemNumber = rs.getInt("ItemNumber");

                // Check if the item is already present in the target room
                String itemInRoomSql = "SELECT * FROM Item WHERE ItemNumber = ? AND Location = ?";
                PreparedStatement itemInRoomStmt = conn.prepareStatement(itemInRoomSql);
                itemInRoomStmt.setInt(1, itemNumber);
                itemInRoomStmt.setInt(2, roomNum);
                ResultSet itemInRoomRs = itemInRoomStmt.executeQuery();

                if (itemInRoomRs.next()) {
                    System.out.println("There is already a " + itemName + " in this room.");
                } else {
                    // Update the Item's location to the current room number
                    String updateItemSql = "UPDATE Item SET Location = ? WHERE ItemNumber = ?";
                    itemStmt = conn.prepareStatement(updateItemSql);
                    itemStmt.setInt(1, roomNum);
                    itemStmt.setInt(2, itemNumber);
                    itemStmt.executeUpdate();

                    // Update the Inventory's hasItem to 0
                    String updateInventorySql = "UPDATE Inventory SET hasItem = 0 WHERE ItemNumber = ?";
                    PreparedStatement updateInventoryStmt = conn.prepareStatement(updateInventorySql);
                    updateInventoryStmt.setInt(1, itemNumber);
                    updateInventoryStmt.executeUpdate();

                    System.out.println("You dropped the " + itemName + " in this room.");
                }
            } else {
                System.out.println("You do not have a " + itemName + " in your inventory.");
            }

        } catch (SQLException e) {
            // handle any errors
            e.printStackTrace();
        } finally {
            // Close the PreparedStatements
            try {
                if (itemStmt != null) {
                    itemStmt.close();
                }
                if (inventoryStmt != null) {
                    inventoryStmt.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


}
