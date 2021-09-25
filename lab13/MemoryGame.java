package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** Current encouragement string, changes on every round */
    private String encouragement;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        this.rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        String result = "";
        while (result.length() < n) {
            result += CHARACTERS[RandomUtils.uniform(this.rand, CHARACTERS.length)];
        }
        return result;
    }

//    public String generateRandomEncouragement() {
//        return ENCOURAGEMENT[RandomUtils.uniform(rand, ENCOURAGEMENT.length)];
//    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(this.width / 2, this.height / 2, s);
        //TODO: If game is not over, display relevant game information at the top of the screen
        if (!gameOver) {
            Font smallFont = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(smallFont);
            StdDraw.line(0, this.height - 2, this.width, this.height - 2);
            StdDraw.textLeft(0, this.height - 1, "Round: " + this.round);
            if (this.playerTurn) {
                StdDraw.text(this.width/2, this.height - 1, "Type!");
            } else {
                StdDraw.text(this.width/2, this.height - 1, "Watch!");
            }
            int randomEncouragement = RandomUtils.uniform(this.rand, ENCOURAGEMENT.length);
            StdDraw.textRight(this.width, this.height - 1, ENCOURAGEMENT[randomEncouragement]);
        }
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i++) {
            Character current = letters.charAt(i);
            this.drawFrame(current.toString());
            StdDraw.pause(500);
            this.drawFrame("");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        String userInput = "";
        while(userInput.length() < n) {
            if (StdDraw.hasNextKeyTyped()) {
                char current = StdDraw.nextKeyTyped();
                userInput += current;
                this.drawFrame(userInput);
            }
        }
        return userInput;
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        this.round = 1;
        String randomString = "";
        String userInput = "";

        //TODO: Establish Engine loop
        while(randomString.equals(userInput)) {
            this.playerTurn = false;
            StdDraw.pause(1000);
            this.drawFrame("Round: " + this.round);
            StdDraw.pause(1000);
            randomString = this.generateRandomString(this.round);
            this.flashSequence(randomString);
            this.playerTurn = true;
            userInput = this.solicitNCharsInput(this.round);
            this.round += 1;
        }

        this.round -= 1;
        this.gameOver = true;
        this.drawFrame("Game Over: You made it to round: " + round);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

}
