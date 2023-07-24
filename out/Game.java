import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.ArrayList;


public class Game {
    private Room currentRoom;
    private Map<Integer, Room> rooms;
    private Scanner input;
    private boolean escape = false;
    private List<Inventory> playerInventory;


    public Game() {
        rooms = new HashMap<>();
        playerInventory = new ArrayList<>();

        try {
            ResultSet rs = SQLiteDB.getConnection().createStatement().executeQuery("SELECT * FROM Room");
            while (rs.next()) {
                Room room = new Room(rs);
                rooms.put(room.getNumber(), room);
            }

            // assuming the player's inventory is represented by the first row in the Inventory table
            ResultSet inventoryRS = SQLiteDB.getConnection().createStatement().executeQuery("SELECT * FROM Inventory LIMIT 1");
            while (inventoryRS.next()) {
                Inventory item = new Inventory(inventoryRS);
                playerInventory.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        input = new Scanner(System.in);
    }

    public void play() {
        currentRoom = rooms.get(1); //assuming room numbers start from 1

        System.out.println("Welcome to the game!");

        while (!escape){
            enterRoom(currentRoom);
            currentRoom = rooms.get(move());
        }
    }

    private void enterRoom(Room thisRoom){
        System.out.println("You are currently in room " + currentRoom.getNumber() + ".");

        try {
            // Fetch items in the current room.
            List<Item> items = Item.getItemsByLocation(SQLiteDB.getConnection(), thisRoom.getNumber());
            if (!items.isEmpty()) {
                System.out.println("Item in this room:");
                for (Item item : items) {
                    System.out.println(item.getName());
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while fetching items.");
            e.printStackTrace();
        }


        String directions = "You can leave to the ";
        if (thisRoom.getNorth() > 0){
            directions += "[north], ";
        }
        if (thisRoom.getEast() > 0){
            directions += "[east], ";
        }
        if (thisRoom.getSouth() > 0){
            directions += "[south], ";
        }
        if (thisRoom.getWest() > 0){
            directions += "[west], ";
        }
        directions =  directions.substring(0, directions.length() - 2) + ".";
        System.out.println(directions);
    }

    private int move(){
        int nextRoomNumber = currentRoom.getNumber(); // let next room number start from current room
        boolean moved = false;
        while (!moved) {
            String direction = input.nextLine().toLowerCase();
            switch (direction) {

                //DIRECTIONAL CASES


                case "n", "north" -> {
                    if (currentRoom.getNorth() > 0) {
                        Room nextRoom = rooms.get(currentRoom.getNorth());
                        if (nextRoom != null && (nextRoom.getNorthKeyID() == 0 || inventoryContainsKey(nextRoom.getNorthKeyID()))) {
                            nextRoomNumber = currentRoom.getNorth();
                            moved = true;
                        } else {
                            System.out.println("The door to the north is locked. You need the key: " + nextRoom.getNorthKeyID());
                        }
                    } else {
                        System.out.println("There is no way north, try a different direction.");
                    }
                }

                case "e", "east" -> {
                    if (currentRoom.getEast() > 0) {
                        Room nextRoom = rooms.get(currentRoom.getEast());
                        if (nextRoom != null && (nextRoom.getEastKeyID() == 0 || inventoryContainsKey(nextRoom.getEastKeyID()))) {
                            nextRoomNumber = currentRoom.getEast();
                            moved = true;
                        } else {
                            System.out.println("The door to the east is locked. You need the key: " + nextRoom.getEastKeyID());
                        }
                    } else {
                        System.out.println("There is no way east, try a different direction.");
                    }
                }

                case "s", "south" -> {
                    if (currentRoom.getSouth() > 0) {
                        Room nextRoom = rooms.get(currentRoom.getSouth());
                        if (nextRoom != null && (nextRoom.getSouthKeyID() == 0 || inventoryContainsKey(nextRoom.getSouthKeyID()))) {
                            nextRoomNumber = currentRoom.getSouth();
                            moved = true;
                        } else {
                            System.out.println("The door to the south is locked. You need the key: " + nextRoom.getSouthKeyID());
                        }
                    } else {
                        System.out.println("There is no way south, try a different direction.");
                    }
                }

                case "w", "west" -> {
                    if (currentRoom.getWest() > 0) {
                        Room nextRoom = rooms.get(currentRoom.getWest());
                        if (nextRoom != null && (nextRoom.getWestKeyID() == 0 || inventoryContainsKey(nextRoom.getWestKeyID()))) {
                            nextRoomNumber = currentRoom.getWest();
                            moved = true;
                        } else {
                            System.out.println("The door to the west is locked. You need the key: " + nextRoom.getWestKeyID());
                        }
                    } else {
                        System.out.println("There is no way west, try a different direction.");
                    }
                }
                case "exit", "x" -> {
                    escape = true;
                    moved = true;
                    nextRoomNumber = 0; // only in this case can nextRoomNumber become 0
                }
                //INVENTORY CASES

                case "backpack", "inventory" -> {
                    printInventory();
                }

                case "grab", "pickup" -> {
                    for (Inventory item : playerInventory) {
                        item.pickupItem(currentRoom.getNumber(), SQLiteDB.getConnection());
                    }
                    System.out.println("You picked up the item.");
                }

                case "drop" -> {
                    System.out.println("Which item do you want to drop?");
                    String itemName = input.nextLine();
                    for (Inventory item : playerInventory) {
                        if(item.getName().equalsIgnoreCase(itemName)) {
                            item.dropItem(itemName, currentRoom.getNumber(), SQLiteDB.getConnection());
                            break;
                        }
                    }
                }



                default -> System.out.println("Please try a different command.");
            }
            if (rooms.get(nextRoomNumber) == null) {
                System.out.println("You cannot go that way.");
                nextRoomNumber = currentRoom.getNumber(); // reset the room number to current room if the next room does not exist
            } else {
                moved = true;
            }



        }
        return nextRoomNumber;
    }
    private void printInventory() {
        System.out.println("Your inventory:");
        Inventory.printAllItems(SQLiteDB.getConnection());
    }


    private boolean inventoryContainsKey(int keyId) {
        for (Inventory item : playerInventory) {
            if (item.getItemNumber() == keyId) {
                return true;
            }
        }
        return false;
    }


    }

