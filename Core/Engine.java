package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import java.io.*;
import java.io.FileInputStream;
import java.util.*;
import java.io.File;

public class Engine {
    TERenderer ter = new TERenderer();
    /** Feel free to change the width and height. */
    public static final int WIDTH = 90;
    public static final int HEIGHT = 40;
    /** current working directory */
    public static final File CWD = new File(".");
    public static final File HIGHEST_SCORE_DIR = Utils.join(CWD, "highestScore.txt");
    public static final File SAVE_GAME = Utils.join(CWD, "saveGame.txt");
    public static final File SAVE_ENCOUNTER = Utils.join(CWD, "saveEncounter.txt");
    /** different variables */
    private long seed;
    private TETile[][] randomMap;
    private Location playerLocation;
    private int score;
    private int bonus;
    private int interval;
    Timer timer;
    private ArrayList<Location> flowersToFloor;
    private ArrayList<Location> encounterToFloor;
    private String invervalStr;

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        String cut = "[NnSsLl:]";
        String[] tokens = input.split(cut);

        // new world
        if (input.charAt(0) == 'N' || input.charAt(0) == 'n') {
            String s = tokens[1];
            seed = Long.parseLong(s);
            // generate the basic map
            WorldGenerator wg = new WorldGenerator();
            randomMap = wg.getWorldGenerator(WIDTH, HEIGHT, seed);
            // update player's location
            this.playerLocation = wg.addRandomAvatar(randomMap);
            this.score = 0;
            this.bonus = 0;
            this.flowersToFloor = new ArrayList<>();
            this.encounterToFloor = new ArrayList<>();
            interactWithStringHelper(input, 'S');
        } else {
            // load world
            loadGame("normal");
            interactWithStringHelper(input, 'L');
        }
        return randomMap;
    }

    private void interactWithStringHelper(String input, char direction) {
        for (int i = input.toUpperCase().indexOf(direction) + 1; i < input.length(); i++) {
            char movement = input.charAt(i);
            if (movement == 'W' || movement == 'w') {
                playerMovement(playerLocation.x, playerLocation.y + 1, "moveUp");
            }
            if (movement == 'S' || movement == 's') {
                playerMovement(playerLocation.x, playerLocation.y - 1, "moveDown");
            }
            if (movement == 'A' || movement == 'a') {
                playerMovement(playerLocation.x - 1, playerLocation.y, "moveLeft");
            }
            if (movement == 'D' || movement == 'd') {
                playerMovement(playerLocation.x + 1, playerLocation.y, "moveRight");
            }
            if (movement == ':' && (input.charAt(i + 1) == 'Q' || input.charAt(i + 1) == 'q')) {
                saveGame("normal");
                break;
            }
        }
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        drawMainMenu();
        WorldGenerator wg = new WorldGenerator();
        randomMap = wg.getWorldGenerator(WIDTH, HEIGHT, seed);
        playerLocation = wg.addRandomAvatar(randomMap);
        this.score = 0;
        this.bonus = 0;
        this.flowersToFloor = new ArrayList<>();
        this.encounterToFloor = new ArrayList<>();
        countDownDisplay();
        startGame("normal");
        ter.renderFrame(randomMap);
    }

    /** draw a main menu */
    private void drawMainMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 55));
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "CS61B: THE GAME");
        StdDraw.setPenColor(Color.orange);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 8, "Quit (Q)");
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                switch (key) {
                    case 'N': case 'n': {   //new world
                        enterSeed();
                        enterTime();
                        return;
                    }
                    case 'L': case 'l': {   //load
                        loadGame("normal");
                        startGame("normal");
                        break;
                    }
                    case 'Q': case 'q': {    //quit
                        timer.cancel();
                        System.exit(0);
                    }
                    default:
                        break;
                }
            }
        }
    }

    /** ask user to enter seed */
    private void enterSeed() {
        StringBuilder res = new StringBuilder();
        res.append('n');
        drawSeedFrame();
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3, res.toString());
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                // end with s or S
                if (res.length() > 2 && (key == 'S' || key == 's')) {
                    String cut = "[NnSs]";
                    String[] tokens = res.toString().split(cut);
                    String s = tokens[1];
                    seed = Long.parseLong(s);
                    return;
                }

                // if not a integer
                if (!(key == '0' || key == '1' || key == '2' || key == '3'
                        || key == '4' || key == '5' || key == '6'
                        || key == '7' || key == '8' || key == '9')) {
                    drawSeedFrame();
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3, res.toString());
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 - 9,
                            "It is not an integer, please re-enter.");
                } else {
                    res.append(key);
                    drawSeedFrame();
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3, res.toString());
                }
                StdDraw.show();
            }
        }
    }

    /** enterSeed's helper method */
    private void drawSeedFrame() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "Please Entering A Random Seed:");
        StdDraw.setPenColor(Color.orange);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 1, "(end with S or s)");
    }

    /** enter timer method */
    private void enterTime() {
        StringBuilder res = new StringBuilder();
        drawTimerFrame();
        StdDraw.show();

        // user input
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                // end with s or S
                if (res.length() > 0 && (key == 'S' || key == 's')) {
                    String cut = "[Ss]";
                    String[] tokens = res.toString().split(cut);
                    String s = tokens[0];
                    interval = Integer.parseInt(s);
                    invervalStr = String.valueOf(interval);
                    return;
                }
                // if not a integer
                if (!(key == '0' || key == '1' || key == '2' || key == '3'
                        || key == '4' || key == '5' || key == '6'
                        || key == '7' || key == '8' || key == '9')) {
                    drawTimerFrame();
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, res.toString());
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 - 9,
                            "It is not an integer, please re-enter.");
                } else {
                    res.append(key);
                    drawTimerFrame();
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, res.toString());
                }
                StdDraw.show();
            }
        }
    }

    private void drawTimerFrame() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 25));
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "How many seconds do "
                + "you want to challenge the game?");
        StdDraw.setPenColor(Color.ORANGE);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Easy: 100s; Medium: 70s; "
                + "Hard: 40s. (end with S or s)");
    }

    /** the process of playing games */
    public void startGame(String caseGame) {
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 15));
        ter.renderFrame(randomMap);
        while (true) {
            scoreTimerDisplay();
            mouseDisplay();
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                switch (key) {
                    case 'W' :
                    case 'w':
                        playerMovement(playerLocation.x, playerLocation.y + 1, "moveUp");
                        break;
                    case 'S':
                    case 's':
                        playerMovement(playerLocation.x, playerLocation.y - 1, "moveDown");
                        break;
                    case 'A':
                    case 'a':
                        playerMovement(playerLocation.x - 1, playerLocation.y, "moveLeft");
                        break;
                    case 'D':
                    case 'd':
                        playerMovement(playerLocation.x + 1, playerLocation.y, "moveRight");
                        break;
                    case 'P':
                    case 'p':
                        timer.cancel();
                        drawRestartMenu();
                        break;
                    case ':':    //quit/saving
                        while (true) {
                            if (StdDraw.hasNextKeyTyped()) {
                                char c = StdDraw.nextKeyTyped();
                                if (c == 'Q' || c == 'q') {
                                    saveGame("normal");
                                    System.exit(0);
                                } else {
                                    break;
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
            if (interval == 0 && caseGame.equals("normal")) {
                timeOutHelper();
                drawRestartMenu();
            }
            if (interval == 0 && caseGame.equals("encounter")) {
                saveBonus();
                loadGame("encounter");
                this.score = bonus;
            }
        }
    }

    /** player movement helper */
    private void playerMovement(int dx, int dy, String direction) {
        // have a wall -- do nothing
        if (randomMap[dx][dy].equals(Tileset.WALL)) {
            return;
        }
        // find a door -- win and exit
        if (randomMap[dx][dy].equals(Tileset.LOCKED_DOOR)) {
            exitDoor();
            drawRestartMenu();
        }
        // update points after collecting flowers
        if (randomMap[dx][dy].equals(Tileset.FLOWER)) {
            updateScore();
            Location flowerToFlo = new Location(dx, dy);
            flowersToFloor.add(flowerToFlo);
        }
        // open the encounter world
        if (randomMap[dx][dy].equals(Tileset.ENCOUNTER)) {
            moveHelper(dx, dy, direction);
            Location encounterToFlo = new Location(dx, dy);
            encounterToFloor.add(encounterToFlo);
            enterEncounterWorld();
        } else {
            moveHelper(dx, dy, direction);
        }
    }

    private void moveHelper(int dx, int dy, String direction) {
        // player move towards direction
        if (direction.equals("moveUp")) {
            randomMap[dx][dy - 1] = Tileset.FLOOR;
            randomMap[dx][dy] = Tileset.AVATAR;
            playerLocation.y += 1;
            ter.renderFrame(randomMap);
        }
        if (direction.equals("moveDown")) {
            randomMap[dx][dy + 1] = Tileset.FLOOR;
            randomMap[dx][dy] = Tileset.AVATAR;
            playerLocation.y -= 1;
            ter.renderFrame(randomMap);
        }
        if (direction.equals("moveLeft")) {
            randomMap[dx + 1][dy] = Tileset.FLOOR;
            randomMap[dx][dy] = Tileset.AVATAR;
            playerLocation.x -= 1;
            ter.renderFrame(randomMap);
        }
        if (direction.equals("moveRight")) {
            randomMap[dx - 1][dy] = Tileset.FLOOR;
            randomMap[dx][dy] = Tileset.AVATAR;
            playerLocation.x += 1;
            ter.renderFrame(randomMap);
        }
    }

    /** if find a door then exit the game */
    private void exitDoor() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 50));
        StdDraw.setPenColor(Color.WHITE);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (randomMap[i][j] == Tileset.FLOWER
                        || randomMap[i][j] == Tileset.ENCOUNTER) {
                    StdDraw.text(WIDTH / 2, HEIGHT / 2, "Game over, you did not finish the task!");
                    if (this.score > readHighestScore()) {
                        saveHighestScore();
                    }
                    StdDraw.show();
                    StdDraw.pause(2000);
                    timer.cancel();
                    return;
                }
            }
        }
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Congratulations!");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 5, "You score is " + score + " !");
        if (this.score > readHighestScore()) {
            saveHighestScore();
        }
        StdDraw.show();
        StdDraw.pause(2000);
        timer.cancel();
        return;
    }

    /** loading game */
    private void loadGame(String caseGame) {
        File file = null;
        if (caseGame.equals("normal")) {
            file = SAVE_GAME;
        } else {
            file = SAVE_ENCOUNTER;
        }
        File f = file;
        if (f.exists()) {
            try {
                FileInputStream fos = new FileInputStream(f);
                ObjectInputStream oos = new ObjectInputStream(fos);
                playerLocation = (Location) oos.readObject();
                seed = (long) oos.readObject();
                score = (int) oos.readObject();
                interval = (int) oos.readObject();
                flowersToFloor = (ArrayList<Location>) oos.readObject();
                encounterToFloor = (ArrayList<Location>) oos.readObject();
                WorldGenerator wg = new WorldGenerator();
                randomMap = wg.getWorldGenerator(WIDTH, HEIGHT, seed);
                countDownDisplay();
                for (Location turnFloor : flowersToFloor) {
                    randomMap[turnFloor.x][turnFloor.y] = Tileset.FLOOR;
                }
                for (Location turnFloor : encounterToFloor) {
                    randomMap[turnFloor.x][turnFloor.y] = Tileset.FLOOR;
                }
                randomMap[playerLocation.x][playerLocation.y] = Tileset.AVATAR;
                oos.close();
                fos.close();
            } catch (FileNotFoundException e) {
                System.out.println("File is not found.");
            } catch (IOException e) {
                System.out.println(e);
            } catch (ClassNotFoundException e) {
                System.out.println("Class is not found.");
            }
//            catch (Exception ex) {
//                ex.printStackTrace();
//            }
        }
    }

    /** saving game */
    // @source: https://www.journaldev.com/927/objectoutputstream-java-write-object-file#java-
    // objectoutputstream-example-to-write-object-to-file
    private void saveGame(String caseGame) {
        File file = null;
        if (caseGame.equals("normal")) {
            file = SAVE_GAME;
        } else {
            file = SAVE_ENCOUNTER;
        }
        File f = file;
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.playerLocation);
            oos.writeObject(this.seed);
            oos.writeObject(this.score);
            oos.writeObject(this.interval);
            oos.writeObject(this.flowersToFloor);
            oos.writeObject(this.encounterToFloor);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Heads Up Display -- include Text that describes the tile currently under
     * the mouse pointer. */
    private void mouseDisplay() {
        ter.renderFrame(randomMap);
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (x < WIDTH && x >= 0 && y < HEIGHT && y >= 0
                && !randomMap[x][y].equals(Tileset.NOTHING)) {
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 15));
            StdDraw.text(7, HEIGHT - 3, "Tile: " + randomMap[x][y].description());
            StdDraw.show();
        }
    }

    /** after collecting flower, the score add 100 points */
    private void scoreTimerDisplay() {
        ter.renderFrame(randomMap);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 15));
        StdDraw.text(WIDTH / 2, HEIGHT - 3, "Score: " + score);
        StdDraw.text(WIDTH - 10, HEIGHT - 3, "Count Down: " + interval + " seconds");
        StdDraw.text(10, 3, "Press p to restart the game.");
        StdDraw.text(WIDTH - 10, 3, ":Q exit the game");
        StdDraw.setPenColor(StdDraw.ORANGE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 15));
        StdDraw.text(30, HEIGHT - 6, "Goal: " + invervalStr + " seconds to collect "
                + "all flowers, finish the encounter game and exit the door!");
        StdDraw.show();
    }

    /** update the score */
    private int updateScore() {
        this.score += 100;
        return score;
    }

    /** set the count down timer */
    private void countDownDisplay() {
        int delay = 1000;
        int period = 1000;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                setInterval();
            }
        }, delay, period);
    }

    private int setInterval() {
        if (interval == 1) {
            timer.cancel();
        }
        return --interval;
    }

    /** if find a door then exit the game */
    private void timeOutHelper() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 40));
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Ohh, you lose!");
        StdDraw.show();
        StdDraw.pause(2000);
        timer.cancel();
    }

    private void drawRestartMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.yellow);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(WIDTH * 1 / 3 - 10, HEIGHT / 2 + 15, "Highest Score: " + readHighestScore());
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "Do you want to restart?");
        StdDraw.setPenColor(Color.orange);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3, "Restart The Same Map (I)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Quit (Q)");
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                switch (key) {
                    case 'N': case 'n': {   //start world
                        enterSeed();
                        enterTime();
                        ter.initialize(WIDTH, HEIGHT);
                        WorldGenerator wg = new WorldGenerator();
                        randomMap = wg.getWorldGenerator(WIDTH, HEIGHT, seed);
                        playerLocation = wg.addRandomAvatar(randomMap);
                        this.score = 0;
                        countDownDisplay();
                        ter.renderFrame(randomMap);
                        startGame("normal");
                        return;
                    }
                    case 'I': case 'i': {   //start world
                        ter.initialize(WIDTH, HEIGHT);
                        WorldGenerator wg = new WorldGenerator();
                        randomMap = wg.getWorldGenerator(WIDTH, HEIGHT, seed);
                        playerLocation = wg.addRandomAvatar(randomMap);
                        this.score = 0;
                        this.interval = Integer.parseInt(invervalStr);
                        countDownDisplay();
                        ter.renderFrame(randomMap);
                        startGame("normal");
                        return;
                    }
                    case 'Q': case 'q': {    //quit
                        System.exit(0);
                    }
                    default:
                        break;
                }
            }
        }
    }

    /** create a encounter world */
    private void enterEncounterWorld() {
        // save game
        saveGame("encounter");

        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "You have 10 seconds to collect the flowers");
        StdDraw.show();
        StdDraw.pause(2000);

        // build a new world
        ter.initialize(WIDTH, HEIGHT);
        WorldGenerator wg = new WorldGenerator();
        randomMap = wg.getEncounterWorld(WIDTH, HEIGHT, seed);
        playerLocation = wg.addRandomAvatar(randomMap);
        this.interval = 10;
        ter.renderFrame(randomMap);
        startGame("encounter");
    }

    private void saveBonus() {
        this.bonus = score;
    }

    private int readHighestScore() {
        int highestScore = 0;
        if (HIGHEST_SCORE_DIR.exists()) {
            try {
                FileInputStream fos = new FileInputStream(HIGHEST_SCORE_DIR);
                ObjectInputStream oos = new ObjectInputStream(fos);
                highestScore = (int) oos.readObject();
                oos.close();
                fos.close();
            } catch (FileNotFoundException e) {
                System.out.println("File is not found.");
            } catch (IOException e) {
                System.out.println(e);
            } catch (ClassNotFoundException e) {
                System.out.println("Class is not found.");
            }
        }
        return highestScore;
    }

    private void saveHighestScore() {
        try {
            if (!HIGHEST_SCORE_DIR.exists()) {
                HIGHEST_SCORE_DIR.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(HIGHEST_SCORE_DIR);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.score);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        TERenderer ter = new TERenderer();
//        ter.initialize(WIDTH, HEIGHT);
//        Engine engine = new Engine();
//        String input = "n123s:q";
//        TETile[][] world = engine.interactWithInputString(input);
//        ter.renderFrame(world);
//    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.interactWithKeyboard();
    }

}
