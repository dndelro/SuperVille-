import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
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
public class Game {
    private Room currentRoom;
    private Map<Integer, Room> rooms;
    private Scanner input;
    private boolean escape = false;
    private String playerName;
    private int playerHealth = 9999;

    private List<Inventory> playerInventory;

    public Game(String playerName) {
        rooms = new HashMap<>();
        this.playerName = playerName;
        playerInventory = new ArrayList<>();

        try {
            ResultSet rs = SQLiteDB.getConnection().createStatement().executeQuery("SELECT * FROM Room");
            while (rs.next()) {
                Room room = new Room(rs);
                rooms.put(room.getNumber(), room);
            }
            ResultSet inventoryRS = SQLiteDB.getConnection().createStatement().executeQuery("SELECT * FROM Inventory LIMIT 1");
            while (inventoryRS.next()) {
                Inventory item = new Inventory(inventoryRS);
                playerInventory.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        input = new Scanner(System.in);

        assignMonsters();
    }



    public void play() {
        System.out.println("--Welcome to your journey!---");
        // Load the last saved room number for the player
        int savedRoomNumber = SQLiteDB.loadGame(playerName);
        if (savedRoomNumber != -1) {
            currentRoom = rooms.get(savedRoomNumber);
            System.out.println("Game and Progress loaded successfully." + "\n---Welcome back, " + this.playerName + ".---\n");
        } else {
            currentRoom = rooms.get(1); // Start from the first room if no saved game exists
        }

        enterRoom(currentRoom); // Display the initial room before the loop

        while (!escape) {
            currentRoom = rooms.get(move());
        }

        // Save the game state when the player decides to exit
        SQLiteDB.saveGame(playerName, currentRoom.getNumber());
        System.out.println("Game saved successfully.");
    }


    private void enterRoom(Room room) {
        System.out.println("You are in [Room #" + room.getNumber() + "]");
        System.out.println("Description: " + room.getName());

        // Check if there is a monster in the room
        if (room.getMonsterName() != null) {
            System.out.println("A monster has appeared in this room!");
            System.out.println("Monster name: " + room.getMonsterName());
            startCombat(); // Start combat if there is a monster in the room
        } else {
            // Display available directions
            System.out.print("You can leave to the: ");
            if (room.getNorth() > 0) {
                System.out.print("[North]");
            }
            if (room.getEast() > 0) {
                System.out.print("[East]");
            }
            if (room.getSouth() > 0) {
                System.out.print("[South]");
            }
            if (room.getWest() > 0) {
                System.out.print("[West]");
            }
            System.out.println(); // Add a new line after printing available directions
        }

        // Mark the room as visited
        room.visitedRoom();

        // Update the current room
        currentRoom = room;

        // Save the game progress
        SQLiteDB.saveGame(playerName, currentRoom.getNumber());
    }





    private int move() {
        int nextRoomNumber = 0;
        boolean moved = false;
        while (!moved) {
            String direction = input.nextLine().toLowerCase();
            switch (direction) {
                case "n", "north" -> {
                    if (currentRoom.getNorth() > 0) {
                        nextRoomNumber = currentRoom.getNorth();
                        moved = true;
                    } else {
                        System.out.println("There is no way north, try a different direction.");
                    }
                }
                case "e", "east" -> {
                    if (currentRoom.getEast() > 0) {
                        nextRoomNumber = currentRoom.getEast();
                        moved = true;
                    } else {
                        System.out.println("There is no way east, try a different direction.");
                    }
                }
                case "s", "south" -> {
                    if (currentRoom.getSouth() > 0) {
                        nextRoomNumber = currentRoom.getSouth();
                        moved = true;
                    } else {
                        System.out.println("There is no way south, try a different direction.");
                    }
                }
                case "w", "west" -> {
                    if (currentRoom.getWest() > 0) {
                        nextRoomNumber = currentRoom.getWest();
                        moved = true;
                    } else {
                        System.out.println("There is no way west, try a different direction.");
                    }
                }

                //ITEMS
                case "backpack", "inventory" -> {
                    printInventory();
                }

                case "look" -> {
                    try {
                        // Fetch items in the current room.
                        List<Item> items = Item.getItemsByLocation(SQLiteDB.getConnection(), currentRoom.getNumber());
                        if (!items.isEmpty()) {
                            System.out.println("Item(s) in this room:");
                            for (Item item : items) {
                                System.out.println(item.getName());
                            }
                        } else {
                            System.out.println("There are no items in this room.");
                        }
                    } catch (SQLException e) {
                        System.out.println("An error occurred while fetching items.");
                        e.printStackTrace();
                    }
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

                case "help" -> {
                    System.out.println("*You can try to open your BACKPACK, LOOK around the room, GRAB something? and DROP something? and ATTACK Depending on where you are*");
                    System.out.println("Something tells you getting to Room #30 will complete your journey...");
                }


                case "exit", "x" -> {
                    escape = true;
                    moved = true;
                    nextRoomNumber = currentRoom.getNumber();
                }
                default -> System.out.println("Please try one of the four cardinal directions or 'exit' to quit.");
            }

            if (moved) {
                enterRoom(rooms.get(nextRoomNumber));
            }
        }
        return nextRoomNumber;
    }


    //INVENTORY HANDLING
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



    private void assignMonsterToRoom(int roomNumber, String monsterName) {
        Room room = rooms.get(roomNumber);
        if (room != null) {
            room.setMonsterName(monsterName);
        } else {
            System.out.println("Invalid room number. Monster assignment failed.");
        }
    }

    private void assignMonsters() {
        assignMonsterToRoom(30, "FINAL BOSS: Mummy!");
        assignMonsterToRoom(24, "Vampire");
        assignMonsterToRoom(18, "Yeti");
        assignMonsterToRoom(12, "Zombie");
        assignMonsterToRoom(6, "Dragon");
    }

    public void startCombat() {
        if (currentRoom.getMonsterName() != null) {
            Monster monster = new Monster(currentRoom.getMonsterName(), 100);
            Player player = new Player(playerName, playerHealth);

            Combat combat = new Combat(player, monster);
            combat.startCombat();

            if (player.isAlive()) {
                System.out.println(player.getName() + " wins!");
                System.out.println("You may continue on your journey. Try any direction. Enter 'Exit' or 'x' to end your journey");

                // Remove the defeated monster from the room
                currentRoom.setMonsterName(null);
            }
        } else {
            System.out.println("There is no monster in this room. Keep exploring!");
        }
    }





}







