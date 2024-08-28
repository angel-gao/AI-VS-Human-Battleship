package sample;

//A class that will create Score object to record user score and computer score for each round of battleship game
public class Score {
    //instance variables
    private int score;  //an integer used to record user/computer score

    //constructor that set score to default value
    public Score() { score = 0; }

    //get current score value
    public int getScore() { return score; }

    //update score when hitting a ship
    public void hitShipUpdateScore() {score += 50;}

    //get bonus score when sunk a ship
    public void sunkShipUpdateScore() {
        score += 100;
    }

    //require an integer parameter and set current score value to that parameter
    public void setScore(int newScore) { score = newScore;}
}


