package sample;

//This class will define a 2D array object considered as a 10x10 board, and defining the Grids object is dependent on Position objects
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
public class Grids extends Parent {

    //public compile-time constants (class variables) that define total number of rows and columns
    public static final int NUM_OF_ROWS = 10;       //total 10 rows for a Grid
    public static final int NUM_OF_COLS = 10;       //total 10 columns for a Grid

    //instance variables
    private Position[][] grid;      //a 2D Position object array list, each element in this list is a Position with its corresponding attributes
    private int totalHits;          //integer rep. number of hits one player had received, when totalHit = 17, that player lose
    private VBox rows = new VBox(); //VBox used to construct and show multiple rows of cells on GUI


    //constructor used to initialize Grids object by setting each element with a default Position object
    //this constructor it's only used for creating Player object
    public Grids() {
        grid = new Position[NUM_OF_ROWS][NUM_OF_COLS];
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                grid[r][c] = new Position();
            }
        }
    }

    //taking EventHandler as parameter, it's a constructor used to demonstrate game boards on GUI and applying mouse click event to each cell on board
    //it's only used for updating color of the cells on each board
    public Grids(EventHandler<? super MouseEvent> handler) {
        grid = new Position[NUM_OF_ROWS][NUM_OF_COLS];
        for (int r = 0; r < grid.length; r++) {
            HBox row = new HBox();              //HBox is used to construct one row of cells on GUI
            for (int c = 0; c < grid[r].length; c++) {
                Position cell = new Position(r, c);
                row.getChildren().add(cell);
                cell.setOnMouseClicked(handler);
                grid[r][c] = new Position();
            }
            rows.getChildren().add(row);        //add one row of cells into VBox rows
        }
        getChildren().add(rows);                //add the multiple rows (entire board) to the whole Grid object
    }


    //taking row # and column # and parameters, get one specific Position object from Grids object created by the constructor for showing GUI
    public Position getOnePosition(int r, int c) {return (Position)((HBox)rows.getChildren().get(r)).getChildren().get(c);}

    //get the status of a Position with parameters row # and column # (using the corresponded method from Position class)
    public int getPositionStatus(int r, int c) {
        return grid[r][c].getPositionStatus();
    }

    //get if a Position contains ship with parameters row # and column # (using corresponded method from Position class)
    public boolean getHasShip(int r, int c) {
        return grid[r][c].getHasShip();
    }

    //get the name of the ship located on a Position with parameters row # and column # (using corresponded method from Position class)
    public String getShipName(int r, int c) {
        return grid[r][c].getShipName();
    }

    //get the total hits from a player based on how many cells are hit/sunk and return that value
    public int getTotalHits() { return totalHits;}


    //set if a Position contain ship with parameters row #, column # and a boolean type (using corresponded method from Position class)
    public void setHasShip(int r, int c, boolean containShip) {grid[r][c].setHasShip(containShip);}

    //set shipName of a Position with parameters row #, column # and the String shipName(using corresponded method from Position class)
    public void setShipName(int r, int c, String name) {grid[r][c].setShipName(name);}

    //taking an integer as parameter, set the total hits for a player
    public void setTotalHits(int hits) {
        totalHits = hits;
    }


    //When hit the target, taking row # and column # as parameters and calling markHit method from Position class to update status and increase totalHits
    public void markHit(int r, int c) {
        grid[r][c].markHit();
        totalHits++;
    }

    //When miss the target, taking row # and column # as parameters and calling markMiss method from Position class to update status
    public void markMiss(int r, int c) {grid[r][c].markMiss();}

    //When sunk the target, taking row # and column # as parameters and calling markSunk method from Position class to update status
    public void markSunk(int r, int c) {grid[r][c].markSunk();}


    //determine whether all ships on Grid are sunk, thus lose the game and return if current player lose
    public boolean isLost() {return totalHits >= 17;}

    //taking row # and column # as parameters to tell if current Position is guessed or not, return a boolean type
    public boolean isAlreadyGuessed(int r, int c) {return !grid[r][c].isNotGuessed();}


    //For DEBUGGING: Print current situations of all Position in Grid; Can print only status (1), only ship arrangements (2) or combine (3);
    //printType integer indicates which type of grids to print
    public void printGridsSituation(int printType) {
        System.out.println();

        //print column # (heading)
        System.out.print("  1 ");
        for(int c = 1; c < NUM_OF_COLS; c++) {
            System.out.print((c+1) + " ");
        }
        System.out.println();

        //print each row
        for(int r = 0; r < NUM_OF_ROWS; r++) {
            char rChar = (char)(r + 65);        //using ASCII table to convert int into uppercase char (A - J)
            System.out.print(rChar + " ");

            //print out each position's information on each row; depending on the printType value, print different info required
            for(int c = 0; c < NUM_OF_COLS; c++) {
                Position p = grid[r][c];
                if(printType == 1) {                    //print only position status
                    if(p.isHit()) {System.out.print("X ");}                 //X rep. hit status
                    else if(p.isMiss()) {System.out.print("o ");}           //o rep. miss status
                    else if(p.isSunk()) {System.out.print("# ");}           //# rep. sunk status
                    else if (p.isNotGuessed()) {System.out.print("- ");}    //- rep. unguessed status
                }

                else if(printType == 2) {               //print out only ship arrangements
                    if(p.getHasShip()) {                                                            //use only single letter to rep. types of ship for simplicity
                        if(p.getShipName().equals("destroyer")) {System.out.print("D ");}           //D rep. destroyer
                        else if(p.getShipName().equals("submarine")) {System.out.print("S ");}      //S rep. submarine
                        else if(p.getShipName().equals("cruiser")) {System.out.print("R ");}        //R rep. cruiser
                        else if(p.getShipName().equals("battleship")) {System.out.print("B ");}     //B rep. battleship
                        else if(p.getShipName().equals("carrier")) {System.out.print("A ");}        //A rep. carrier
                    } else { System.out.print("- ");}                            //- rep. no ship on current Position
                }

                else {                                  //print out status and ship arrangements (combined), the logic is the same for printing only status and only ship arrangements
                    if(p.isHit()) {System.out.print("X ");} //but just combine the two print types into one grid
                    else if(p.isSunk()) {System.out.print("# ");}
                    else if(p.isMiss()) {System.out.print("o ");}
                    else if(p.getHasShip()) {
                        if(p.getShipName().equals("destroyer")) {System.out.print("D ");}
                        else if(p.getShipName().equals("submarine")) {System.out.print("S ");}
                        else if(p.getShipName().equals("cruiser")) {System.out.print("R ");}
                        else if(p.getShipName().equals("battleship")) {System.out.print("B ");}
                        else if(p.getShipName().equals("carrier")) {System.out.print("A ");}
                    } else {System.out.print("- ");}
                }
            }
            System.out.println();
        }
    }


    //For recording all information about a Grid on a .txt file when user request to save the game progress, the logic is quite similar with printGridsSituation
    //but we only need to print all status and all ship arrangements (no combined); printType integer indicates which type of grids to print
    public void recordGridsSituation(int printType) {
        //record column # (heading)
        Battleship.pw.print("  1 ");                //we use the public PrintWriter pw from Battleship class to write on file
        for(int c = 1; c < NUM_OF_COLS; c++) {
            Battleship.pw.print((c+1) + " ");
        }
        Battleship.pw.println();

        //record each row
        for(int r = 0; r < NUM_OF_ROWS; r++) {
            char rChar = (char)(r + 65);
            Battleship.pw.print(rChar + " ");

            //record each position's information on each row depending on the printType value
            for(int c = 0; c < NUM_OF_COLS; c++) {
                Position p = grid[r][c];
                if(printType == 1) {                        //record all position status
                    if(p.isHit()) {Battleship.pw.print("X ");}
                    else if(p.isMiss()) {Battleship.pw.print("o ");}
                    else if(p.isSunk()) {Battleship.pw.print("# ");}
                    else if (p.isNotGuessed()) {Battleship.pw.print("- ");}
                }

                else if(printType == 2) {                   //record all ship arrangements
                    if(p.getHasShip()) {
                        if(p.getShipName().equals("destroyer")) {Battleship.pw.print("D ");}
                        else if(p.getShipName().equals("submarine")) {Battleship.pw.print("S ");}
                        else if(p.getShipName().equals("cruiser")) {Battleship.pw.print("R ");}
                        else if(p.getShipName().equals("battleship")) {Battleship.pw.print("B ");}
                        else if(p.getShipName().equals("carrier")) {Battleship.pw.print("A ");}
                    } else { Battleship.pw.print("- ");}
                }
            }
            Battleship.pw.println();
        }
    }


    //methods created to access printGridsSituation conveniently
    public void printStatus() {printGridsSituation(1);}
    public void printShipArrangements() {printGridsSituation(2);}
    public void printBoth() {printGridsSituation(3);}

    //methods created to access recordGridsSituation conveniently
    public void recordStatus() { recordGridsSituation(1);}
    public void recordShipArrangements() { recordGridsSituation(2);}


    //taking a Ship object as a parameter and add a Ship object into Grids object by updating each Position in Grid
    public void addShip(Ship s) {
        //getting information about the Ship being added into the Grid
        int row = s.getRow();
        int col = s.getCol();
        int length = s.getLength();
        int dire = s.getDirection();
        String name = s.getName();

        if(dire == Ship.HORI) {             //when ship is placing horizontally, loop through columns within a row to update status
            for(int c = col; c < col+length; c++) {
                grid[row][c].setHasShip(true);
                grid[row][c].setLengthOfShip(length);
                grid[row][c].setDirectionOfShip(dire);
                grid[row][c].setShipName(name);
            }
        } else if(dire == Ship.VERTI) {     //when ship is placing vertically, loop through rows within a column to update status
            for(int r = row; r < row+length; r++) {
                grid[r][col].setHasShip(true);
                grid[r][col].setLengthOfShip(length);
                grid[r][col].setDirectionOfShip(dire);
                grid[r][col].setShipName(name);
            }
        }
    }
}
