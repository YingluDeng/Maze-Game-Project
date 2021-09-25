# Classes and Data Structures
## Main
interact with engine.interactWithInputString() and engine.interactWithKeyboard() methods.

## WorldGenerator
public void setSeed(long s) -- pass the seed to random

public void buildARandomRoom(TETile[][] world) -- Build a room of random width and height at the given location.

private boolean checkRoomPossible(Location newLoc, int newWidth, int newHeight) -- check if a random room can be built.

private Location buildRoomHelper(TETile[][] world, int locX, int locY, int w, int h)

private Location pickRandomLoc() -- Pick a random Location to build a random room.

private boolean notExceedingMap(int locX, int locY, int randomWidth, int randomHeight) -- Check if a random room is possible to build and will not go outside the map at the given location.

private boolean notOverlapOthers(int locX, int locY, int randomWidth, int randomHeight) -- check if a random room is possible to build and will not overlap other rooms at the given location.

private int generateRandomWidth()

private int generateRandomHeight()

public static void fillWithNOTHINGTiles(TETile[][] world) -- fills the given world with NOTHING tiles

public void connectTwoRooms(TETile[][] world, Location r1, Location r2)

private void buildLHallway(TETile[][] world, Location r1, Location r2)

private void buildHorizontalHallway(TETile[][] world, Location r1, Location r2)

private void buildVerticalHallway(TETile[][] world, Location r1, Location r2)

public void buildWalls(TETile[][] world)

private boolean checkNeighborsFloor(TETile[][] world, int x, int y)

public void removeWall(TETile[][] world) -- remove the blocked wall and makes the map connected

private boolean checkRemoveCase1(TETile[][] world, int x, int y)

private boolean checkRemoveCase2(TETile[][] world, int x, int y)

private boolean checkRemoveCase3(TETile[][] world, int x, int y)

public boolean checkIfAllConnected(TETile[][] world) -- check if all lands are connected

private void dfs(boolean[][] grid, int i, int j) -- helper method

private boolean[][] createdBooleanGrid (TETile[][] world) -- make the world as a boolean world

private void buildRandomHall(TETile[][] world, int i, int j)

private void wallThenWall(TETile[][] world, int i, int j)

private void connectUpDown(TETile[][] world, int i, int j)

# Engine
public TETile[][] interactWithInputString(String input)



# Algorithms
dfs -- check if the map connected

# Persistence