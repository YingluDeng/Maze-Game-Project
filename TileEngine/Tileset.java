package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('웃', Color.black, new Color(222, 211, 140),
            "player");
    public static final TETile WALL = new TETile('✙', new Color(252, 157, 154),
            new Color(130, 57, 53), "wall");
    public static final TETile FLOOR = new TETile('·', new Color(255, 255, 224),
            new Color(222, 211, 140), "floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink,
            "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', new Color(137, 190, 178),
            new Color(130, 57, 53), "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', new Color(137, 190, 178),
            new Color(130, 57, 53), "unlocked door");
    public static final TETile ENCOUNTER = new TETile('▒', Color.blue,
            Color.lightGray, "encounter");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
    public static final TETile COIN = new TETile('◉', Color.orange,
            new Color(222, 211, 140), "coin");


}


