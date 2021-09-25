package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 60;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /** Adds a hexagon of side length s to a given position in the world.
     * x and y denote the leftmost position of the bottom row. */
    public static void addHexagon(TETile[][] world, int x, int y, int s, TETile tile) {
        if (s < 2) {
            return;
            //throw new IllegalArgumentException("Hexagon must be at least size 2.");
        }
        addBottom(world, x, y, s, tile);
        addTop(world, x, y, s, tile);
    }

    /** Adds the bottom half of a hexagon of side length s to a given position in the world. */
    private static void addBottom(TETile[][] world, int x, int y, int s, TETile tile) {
        for (int height = 0; height < s; height += 1) { //height of bottom is s
            for (int rowPos = 0; rowPos < s + (2 * height); rowPos += 1) {
                //starting row position is -1 * relative height to the bottom row
                //the number of tile is s + 2 * relative height to the bottom row
                world[(x - height) + rowPos][y + height] = tile;
            }
        }
    }

    /** Adds the bottom half of a hexagon of side length s to a given position in the world. */
    private static void addTop(TETile[][] world, int x, int y, int s, TETile tile) {
        int startCol = y + 2 * s - 1;
        for (int height = 0; height < s; height += 1) { //height of top is s
            for (int rowPos = 0; rowPos < s + (2 * height); rowPos += 1) {
                //starting row position is -1 * relative height to the top row
                //the number of tile is s + 2 * relative height to the top row
                world[(x - height) + rowPos][startCol - height] = tile;
            }
        }
    }

    /** Draw a column of n hexagons. */
    private static void addHexagonColumn(TETile[][] world, int x, int y, int s, int n) {
        for (int i = 0; i < n; i += 1) {
            int startingHeight = getHeightOfHexagonInColumn(y, s, i);
            TETile tile = randomTile();
            addHexagon(world, x, startingHeight, s, tile);
        }
    }

    /** Get the height of starting position of i-th hexagon in a column. */
    private static int getHeightOfHexagonInColumn(int y, int s, int i) {
        int height = y + 2 * s * i;
        return height;
    }

    @Test
    public void testGetHeightOfHexagonInColumn() {
        assertEquals(0, getHeightOfHexagonInColumn(0, 3, 0));
        assertEquals(6, getHeightOfHexagonInColumn(0, 3, 1));
        assertEquals(12, getHeightOfHexagonInColumn(0, 3, 2));
        assertEquals(18, getHeightOfHexagonInColumn(0, 3, 3));
    }

    /** Specific Tesselation. */
    public static void drawTesselationOfHexagons(TETile[][] world, int x, int y, int s, int n) {
        //take the central column as 0th column
        for (int i = -2; i < 3; i += 1) {
            int row = getRowOfTesselation(x, y, s, i);
            int col = getColOfTesselation(x, y, s, i);
            int num = -Math.abs(i);
            addHexagonColumn(world, row, col, s, n + num);
        }
    }

    /** Helper function for tesselation. Get starting row position of a column. */
    private static int getRowOfTesselation(int x, int y, int s, int i) {
        return x + (2 * s - 1) * i;
    }

    /** Helper function for tesselation. Get starting col position of a column. */
    private static int getColOfTesselation(int x, int y, int s, int i) {
        i = Math.abs(i);
        return y + s * i;
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0: return Tileset.TREE;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.WATER;
            case 3: return Tileset.MOUNTAIN;
            case 4: return Tileset.GRASS;
            default: return Tileset.NOTHING;
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] hexWorld = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                hexWorld[x][y] = Tileset.NOTHING;
            }
        }

        drawTesselationOfHexagons(hexWorld, 25, 20, 3, 5);
        ter.renderFrame(hexWorld);
    }

}
