package byow.Core;

import byow.TileEngine.Tileset;
import byow.TileEngine.TETile;
import java.util.*;

public class WorldGenerator {
    public static final int WIDTH = 90;
    public static final int HEIGHT = 40;
    public static final int MAX_ROOMS = 12;

    private long seed;
    private Random RANDOM;
    int roomBuilt = 0;
    private ArrayList<Location> rooms;
    private HashSet<Location> takenSpaces;

    public void setSeed(long s) {
        this.takenSpaces = new HashSet<>();
        this.rooms = new ArrayList<>();
        this.seed = s;
        this.RANDOM = new Random(s);
    }

    public ArrayList<Location> getRooms() {
        return this.rooms;
    }

    /** Build a room of random width and height at the given location. */
    public void buildARandomRoom(TETile[][] world) {
        Location newLoc = pickRandomLoc();
        int newWidth = generateRandomWidth();
        int newHeight = generateRandomHeight();
        // not exceed map and not overlap, return true
        if (checkRoomPossible(newLoc, newWidth, newHeight)) {
            // pt (random point in each room)
            Location pt = buildRoomHelper(world, newLoc.x, newLoc.y, newWidth, newHeight);
            rooms.add(pt);
            world[pt.x][pt.y] = Tileset.FLOWER;
            roomBuilt += 1;
        }
    }

    /** check if a random room can be built. */
    private boolean checkRoomPossible(Location newLoc, int newWidth, int newHeight) {
        return notExceedingMap(newLoc.x, newLoc.y, newWidth, newHeight)
                && notOverlapOthers(newLoc.x, newLoc.y, newWidth, newHeight);
    }

    private Location buildRoomHelper(TETile[][] world, int locX, int locY, int w, int h) {
        //draw floor
        HashSet<Location> floors = new HashSet<>();
        for (int i = 0; i < w; i += 1) {
            for (int j = 0; j < h; j += 1) {
                world[locX + i][locY + j] = Tileset.FLOOR;
                floors.add(new Location(locX + i, locY + j));
                takenSpaces.add(new Location(locX + i, locY + j));
            }
        }
        int ptW = RandomUtils.uniform(RANDOM, 0, w);
        int ptH = RandomUtils.uniform(RANDOM, 0, h);
        Location connectingPt = new Location(locX + ptW, locY + ptH);

        return connectingPt;
    }

    /** Pick a random Location to build a random room. */
    private Location pickRandomLoc() {
        int w = RandomUtils.uniform(RANDOM, 3,  WIDTH - 5);
        int h = RandomUtils.uniform(RANDOM, 8,  HEIGHT - 15);
        Location newLoc = new Location(w, h);
        return newLoc;
    }

    /** Check if a random room is possible to build and will not go outside the map
     *  at the given location. */
    private boolean notExceedingMap(int locX, int locY, int randomWidth, int randomHeight) {
        boolean widthEnough = (locX + randomWidth) <= WIDTH - 5;
        boolean heightEnough = (locY + randomHeight) <= HEIGHT - 5;
        return (widthEnough && heightEnough);
    }

    /** check if a random room is possible to build and will not overlap other rooms
     * at the given location. */
    private boolean notOverlapOthers(int locX, int locY, int randomWidth, int randomHeight) {
        boolean willNotOverlap = true;
        Location lowerLeftCorner = new Location(locX - 1, locY - 1);
        Location lowerRightCorner = new Location(locX + randomWidth, locY - 1);
        Location upperLeftCorner = new Location(locX - 1, locY + randomHeight);
        Location upperRightCorner = new Location(locX + randomWidth, locY + randomHeight);
        // keep the distance away
        for (int i = -1; i < 3; i++) {
            Location lowerLeftCornerNear = new Location(locX - 3,
                    locY + i);  //check left
            Location lowerRightCornerNear = new Location(locX + randomWidth - i,
                    locY - 3);  //check down
            Location upperRightCornerNear = new Location(locX + randomWidth + 3,
                    locY + randomHeight - i);  // check right
            Location upperLeftCornerNear = new Location(locX + i,
                    locY + randomHeight + 3);  //check up
            if (takenSpaces.contains(lowerLeftCorner) || takenSpaces.contains(lowerRightCorner)
                    || takenSpaces.contains(upperLeftCorner)
                    || takenSpaces.contains(upperRightCorner)
                    || takenSpaces.contains(lowerLeftCornerNear)
                    || takenSpaces.contains(lowerRightCornerNear)
                    || takenSpaces.contains(upperLeftCornerNear)
                    || takenSpaces.contains(upperRightCornerNear)) {
                // if one of corners is taken, will overlap
                willNotOverlap = false;
            } else {
                //check mid-points of four sides
                Location midDown = new Location(locX + randomWidth / 2, locY - 1);
                Location midTop = new Location(locX + randomWidth / 2, locY + randomHeight);
                Location midLeft = new Location(locX - 1, locY + randomHeight / 2);
                Location midRight = new Location(locX + randomWidth, locY + randomHeight / 2);
                if (takenSpaces.contains(midDown) || takenSpaces.contains(midTop)
                        || takenSpaces.contains(midLeft) || takenSpaces.contains(midRight)) {
                    // if one of mid-points is taken, will overlap
                    willNotOverlap = false;
                }
            }
        }
        return willNotOverlap;
    }

    private int generateRandomWidth() {
        int rW = RandomUtils.uniform(RANDOM, 3, 9);
        return rW;
    }

    private int generateRandomHeight() {
        int rH = RandomUtils.uniform(RANDOM, 3, 9);
        return rH;
    }

    /** fills the given world with NOTHING tiles */
    public static void fillWithNOTHINGTiles(TETile[][] world) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /** find the closest room to connect them */
    public void connectRooms(TETile[][] world) {
        ArrayList<Location> connected = new ArrayList<>();
        ArrayList<Location> toConnect = new ArrayList<>();
        for (Location r: rooms) {
            toConnect.add(r);
        }

        int leftMostPoint = 10000;
        Location leftMostRoom = null;
        for (Location room : rooms) {
            if (room.x < leftMostPoint) {
                leftMostPoint = room.x;
                leftMostRoom = room;
            }
        }

        toConnect.remove(leftMostRoom);
        connected.add(leftMostRoom);
        while (!toConnect.isEmpty()) {
            connectHelper(connected, toConnect, world);
        }
    }

    private void connectHelper(ArrayList<Location> con,
                               ArrayList<Location> toCon, TETile[][] world) {
        Location c = con.get(con.size() - 1);
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Location exploreLoc = new Location(c.x + i, j);
                if (toCon.contains(exploreLoc)) {
                    buildHalls(world, c, exploreLoc);
                    con.add(exploreLoc);
                    toCon.remove(exploreLoc);
                    return;
                }
            }
        }
    }

    private void buildHalls(TETile[][] world, Location currRoom, Location nextRoom) {
        if (currRoom.y < nextRoom.y) {
            for (int i = 1; i <= nextRoom.y - currRoom.y; i++) {  //up
                world[currRoom.x][currRoom.y + i] = Tileset.FLOOR;
            }
            for (int i = 1; i <= nextRoom.x - currRoom.x - 1; i++) {   //right
                world[currRoom.x + i][nextRoom.y] = Tileset.FLOOR;
            }
        } else if (currRoom.y > nextRoom.y) {
            for (int i = 1; i <= currRoom.y - nextRoom.y; i++) {  //down
                world[currRoom.x][currRoom.y - i] = Tileset.FLOOR;
            }
            for (int i = 1; i <= nextRoom.x - currRoom.x - 1; i++) {   //right
                world[currRoom.x + i][nextRoom.y] = Tileset.FLOOR;
            }
        } else {
            for (int i = 1; i <= nextRoom.x - currRoom.x - 1; i++) {   //right
                world[currRoom.x + i][nextRoom.y] = Tileset.FLOOR;
            }
        }
    }

    public void buildWalls(TETile[][] world) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                if (world[x][y] == Tileset.NOTHING) {
                    if (checkNeighborsFloor(world, x, y) || checkHallFloor(world, x, y)) {
                        world[x][y] = Tileset.WALL;

                    }
                }
            }
        }
    }

    private boolean checkNeighborsFloor(TETile[][] world, int x, int y) {
        int numNeighboringFloor = 0;
        for (int i = -1; i < 2; i += 1) {
            Location upRow = new Location(x + i, y + 1);
            Location downRow = new Location(x + i, y - 1);
            Location mid = new Location(x + i, y);
            if (takenSpaces.contains(upRow)) {
                numNeighboringFloor += 1;
            }
            if (takenSpaces.contains(downRow)) {
                numNeighboringFloor += 1;
            }
            if (takenSpaces.contains(mid)) {
                numNeighboringFloor += 1;
            }
        }
        return numNeighboringFloor > 0;
    }

    private boolean checkHallFloor(TETile[][] world, int x, int y) {
        int numNeighboringFloor = 0;
        //check wall for hallways
        if (x < WIDTH - 1 && world[x + 1][y] == Tileset.FLOOR) {  //right
            numNeighboringFloor += 1;
        }
        if (y > 0 && world[x][y - 1] == Tileset.FLOOR) {  //down
            numNeighboringFloor += 1;
        }
        if (y < HEIGHT - 1 && world[x][y + 1] == Tileset.FLOOR) {  //up
            numNeighboringFloor += 1;
        }
        if (x > 0 && world[x - 1][y] == Tileset.FLOOR) {  //left
            numNeighboringFloor += 1;
        }
        if (y > 0 && x < WIDTH - 1 && world[x + 1][y - 1] == Tileset.FLOOR) {  //rightDown
            numNeighboringFloor += 1;
        }
        if (y < HEIGHT - 1 && x < WIDTH - 1 && world[x + 1][y + 1] == Tileset.FLOOR) {  //leftDown
            numNeighboringFloor += 1;
        }

        return numNeighboringFloor > 0;
    }

    public void buildGoldenDoor(TETile[][] world) {
        for (int dy = HEIGHT - 1; dy > 0; dy--) {
            for (int dx = WIDTH - 1; dx > 0; dx--) {
                Location p = new Location(dx, dy);
                if (world[p.x][p.y].equals(Tileset.WALL)
                    && (world[p.x - 1][p.y].equals(Tileset.NOTHING)
                    || world[p.x + 1][p.y].equals(Tileset.NOTHING)
                    || world[p.x][p.y + 1].equals(Tileset.NOTHING)
                    || world[p.x][p.y - 1].equals(Tileset.NOTHING))
                    && (world[p.x - 1][p.y].equals(Tileset.WALL)
                    && world[p.x + 1][p.y].equals(Tileset.WALL))) {
                    world[p.x][p.y] = Tileset.LOCKED_DOOR;
                    return;
                }
            }
        }
    }

    public Location addRandomAvatar(TETile[][] world) {
        Location avatarLoc = new Location(0, 0);
        for (int dy = 1; dy < HEIGHT; dy++) {
            for (int dx = 1; dx < WIDTH; dx++) {
                Location p = new Location(dx, dy);
                if (world[p.x][p.y].equals(Tileset.FLOOR)) {
                    world[p.x][p.y] = Tileset.AVATAR;
                    avatarLoc = p;
                    return avatarLoc;
                }
            }
        }
        return avatarLoc;
    }

    private void buildEncounters(TETile[][] world) {
        int count = 0;
        for (int dy = 1; dy < HEIGHT - 1; dy++) {
            for (int dx = WIDTH - 1; dx > 0; dx--) {
                Location p = new Location(dx, dy);
                if (world[p.x][p.y].equals(Tileset.FLOOR)) {
                    world[p.x][p.y] = Tileset.ENCOUNTER;
                    count++;
                    if (count == 2) {
                        return;
                    }
                }
            }
        }
    }

    public TETile[][] getWorldGenerator(int width, int height, long randomSeed) {
        TETile[][] finalWorldFrame = new TETile[width][height];
        setSeed(randomSeed);
        fillWithNOTHINGTiles(finalWorldFrame);

        while (roomBuilt <= MAX_ROOMS) {
            buildARandomRoom(finalWorldFrame);
        }
        connectRooms(finalWorldFrame);
        buildWalls(finalWorldFrame);
        buildGoldenDoor(finalWorldFrame);
        buildEncounters(finalWorldFrame);
        return finalWorldFrame;
    }

    public TETile[][] getEncounterWorld(int width, int height, long randomSeed) {
        TETile[][] encounterWorldFrame = new TETile[width][height];
        setSeed(randomSeed);
        fillWithNOTHINGTiles(encounterWorldFrame);

        for (int dx = WIDTH * 2 / 5; dx <= WIDTH * 3 / 5; dx++) {
            for (int dy = 2; dy < HEIGHT * 1 / 3; dy++) {
                encounterWorldFrame[dx - 1][dy] = Tileset.FLOOR;
                encounterWorldFrame[WIDTH * 2 / 5 - 1][dy - 1] = Tileset.WALL; // left
                encounterWorldFrame[WIDTH * 3 / 5 - 1][dy] = Tileset.WALL; // right
                encounterWorldFrame[dx - 1][1] = Tileset.WALL; // down
                encounterWorldFrame[dx - 1][HEIGHT * 1 / 3 - 1] = Tileset.WALL; // up
            }
        }

        int flowerNum = 0;
        while (flowerNum < 15) {
            int ranWidth = RandomUtils.uniform(RANDOM, WIDTH * 2 / 5, WIDTH * 3 / 5);
            int ranHeight = RandomUtils.uniform(RANDOM, 2, HEIGHT * 1 / 3);
            if (encounterWorldFrame[ranWidth][ranHeight] == Tileset.FLOOR) {
                encounterWorldFrame[ranWidth][ranHeight] = Tileset.FLOWER;
            }
            flowerNum++;
        }
        return encounterWorldFrame;
    }

    /**public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        String s = "12345";
        long seed = Long.parseLong(s);
        WorldGenerator wg = new WorldGenerator();
        wg.setSeed(seed);
        wg.fillWithNOTHINGTiles(world);
        while (wg.roomBuilt <= 10) {
            wg.buildARandomRoom(world);
        }
        //ter.renderFrame(world);
    }*/

}
