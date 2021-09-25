# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer: Staff's solution shows me another way to build up from scratch. I actually
did consider using drawing a row to help build drawing a hexagon, but then I figured out it's too complicated.
I actually like my way better. Lessons: always give tests on helper methods. I stuck on the index for a while. 
Always break a big task down into many small tasks.
-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer: Tessellating hexagons is like putting rooms and hallways in a world.
Hexagon is room or hallway. Tesselation is like randomly generating a world.

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer: First, building room or hallway. Second, choosing where to put room and where to put hallway.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer: The width and height of rooms should be random.
Hallways should have a width of 1 or 2 tiles and a random length.
Hallways are like narrow rooms.
