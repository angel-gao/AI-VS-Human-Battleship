package sample;

//This class that will create a Ship object with related attributes
import javafx.scene.Parent;
public class Ship extends Parent {
    //public compile-time constants (class variables) that use number rep. the direction
    public static final int HORI = 0;   //number 0 = placing horizontally
    public static final int VERTI = 1;  //number 1 = placing vertically
    public static final int UNSET = -1; //number -1 = not placed yet

    //instance variables for Ship object
    private String name;    //name of a Ship (five types)
    private int row;        //which row Ship locates
    private int col;        //which column Ship locates
    private int length;     //length of a Ship
    private int direction;  //which direction a Ship is placed
    private int life;       //life of a Ship, where the default value = length

    //constructor methods to set the default values for a Ship, taking length of ship and ship name as parameters
    public Ship(int length, String name){
        row = -1;
        col = -1;
        this.length = length;
        direction = UNSET;
        this.name = name;
        life = length;
    }

    //set location of a Ship with row # and column #
    public void setLocation(int r, int c){
        row = r;
        col = c;
    }

    //Set direction of a Ship with an integer
    public void setDirection(int dire){
        direction = dire;
    }

    //Set life for a Ship with an integer
    public void setLife(int life){ this.life = life; }

    //Checks if a Ship is being placed on some valid location and return true or false
    public boolean isLocationReset(){return row != -1 && col != -1;}

    //Checks if a Ship is being placed with valid direction value and return true or false
    public boolean isDirectionReset(){return direction != UNSET;}


    //getter methods that return each attribute of a Ship
    public String getName() { return name; }

    public int getRow() { return row; }

    public int getCol() { return col; }

    public int getLength() { return length; }

    public int getDirection() { return direction; }

    public int getLife() { return life; }

    //Reduce one life of a Ship when it's being hit
    public void reduceLife(){ life --; }

    //tell if a Ship is sunk by checking life value
    public boolean isSunk(){ return life == 0; }
}

