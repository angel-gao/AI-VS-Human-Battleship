package sample;

//This class is taking reference from Randomizer program from CodeHS to generate random numbers
import java.util.*;
public class Randomizer {
    //a class Random variable for generating pseudorandom number
    public static Random ins = null;

    //empty constructor
    public Randomizer() {
    }

    //create a Random object
    public static Random getIns() {
        if(ins == null) {ins = new Random();}
        return ins;
    }

    //taking an integer n as parameter and return a random integer from 0 to n (exclusive)
    public static int nextInt(int n) {
        return Randomizer.getIns().nextInt(n);
    }

    //taking two integers min and max as parameters and return a random integer between min and max inclusive
    public static int nextInt(int min, int max) {
        return min + Randomizer.nextInt(max - min + 1);
    }
}
