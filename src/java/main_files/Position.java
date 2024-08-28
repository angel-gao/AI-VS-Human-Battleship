package sample;

//This class will define a single location (cell) in 10x10 board, and construct the basic components of Grids class
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
public class Position extends Rectangle {

    //public compile-time constants (class variables) that use numbers to indicate whether a cell has been hit, missed, sunk of not guessed yet
    public static final int SUNK = 2;       //number = 2, cell is sunk
    public static final int HIT = 1;        //number = 1, cell is hit
    public static final int MISS = 0;       //number = 0, cell is miss
    public static final int UNGUESS = -1;   //number = -1, cell is unguessed

    //instance variables for Position object
    private String shipName;        //name of the ship locates on current cell
    private int lengthOfShip;       //length of the ship locates on current cell
    private int directionOfShip;    //direction of ship locates on current cell
    private int positionStatus;     //status of current cell (hit/miss/sunk/unguessed)
    private boolean hasShip;        //if current cell places any ship
    private int x, y;               //x = row # of current cell in the board, y = column # of current cell in the board

    //the constructor that takes no parameters and set default values for each position
    //it will only use for creating Player object
    public Position(){
        lengthOfShip = -1;
        directionOfShip = -1;
        positionStatus = UNGUESS;
        hasShip = false;
        shipName = "UNKNOWN";
    }

    //this constructor will take row # and column # as parameter, set default values for each position and build initial square cell
    //with color in GUI. It will only use for creating Grids object for demonstrating on GUI
    public Position(int x, int y) {
        super(30,30);
        this.x = x;
        this.y= y;
        setFill(Color.LIGHTGREY);
        setStroke(Color.BLACK);
        lengthOfShip = -1;
        directionOfShip = -1;
        positionStatus = UNGUESS;
        hasShip = false;
        shipName = "UNKNOWN";
    }

    //setter methods that take corresponding parameter type and set value to corresponding attributes
    public void setShipName(String name){
        shipName = name;
    }
    public void setHasShip(boolean containShip){
        hasShip = containShip;
    }
    public void setPositionStatus(int status){
        positionStatus = status;
    }
    public void setLengthOfShip(int length){
        lengthOfShip = length;
    }
    public void setDirectionOfShip(int dire){
        directionOfShip = dire;
    }

    //getter methods that return corresponding attributes
    public String getShipName(){
        return shipName;
    }
    public boolean getHasShip(){
        return hasShip;
    }
    public int getPositionStatus(){
        return positionStatus;
    }
    public int getR() {return x;}
    public int getC() {return y;}

    //check if current status of cell is hit and return true or false
    public boolean isHit(){return positionStatus == 1;}

    //check if current status of cell is miss and return true or false
    public boolean isMiss(){return positionStatus == 0;}

    //check if current status of cell is hit and return true or false
    public boolean isSunk() {return positionStatus == 2;}

    //check if current status of cell haven't been guessed and return true or false
    public boolean isNotGuessed() {return positionStatus == -1;}

    //Below these convenient methods can help to mark miss, hit or sunk of a cell without passing any parameters using the setPositionStatus method in the same class
    //update current status of cell into hit
    public void markHit() {
        setPositionStatus(HIT);
    }

    //update color of the cell when being hit on GUI
    public void markHitGUI() {
        setPositionStatus(HIT);
        setFill(Color.RED);
    }

    //update current status of cell into miss
    public void markMiss() {
        setPositionStatus(MISS);
    }

    //update color of the cell when miss the target on GUI
    public void markMissGUI() {
        setPositionStatus(MISS);
        setFill(Color.LIGHTSKYBLUE);
    }

    //update current status of cell into sunk
    public void markSunk() {
        setPositionStatus(SUNK);
    }

    //update color of the cell when being sunk on GUI
    public void markSunkGUI() {
        setFill(Color.BLACK);
        setPositionStatus(SUNK);
    }
}


