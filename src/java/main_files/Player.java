package sample;

//This class will create a Player object that contain all Ships for a player, a Grid object as the game board for placing player's
// ships and a Grid object as the opponent's game board for marking hits and misses
public class Player{
    //private compile-time constants (class variables)
    private static final int[] SHIP_LENGTH = {5,4,3,3,2};   //length of each ship according to the order in SHIP_NAME
    private static final int NUM_OF_SHIPS = 5;              //total number of ships

    //public instance variables for convenience access in main class (need to use in main class many times)
    public Ship[] ships;            //all player's ships (total 5)
    public Grids playerGrid;        //all information about player's own Grid (initially is empty if player is user, and has set up ship placements if player is computer)
    public Grids oppGrid;           //all information about player's opponent's Grid (initially is empty no matter who's the player)
                                    //Store name of each type of ship; the order of SHIP_NAME must match with SHIP_LENGTH
    public final String[] SHIP_NAME = {"carrier", "battleship", "cruiser", "submarine", "destroyer"};

    //constructor for initializing all ships, playerGrid and oppGrid for Player object and store Ship objects in ships array
    public Player() {
        ships = new Ship[NUM_OF_SHIPS];
        for(int i = 0; i < ships.length; i++) {
            ships[i] = new Ship(SHIP_LENGTH[i], SHIP_NAME[i]);
        }
        playerGrid = new Grids();
        oppGrid = new Grids();
    }
}