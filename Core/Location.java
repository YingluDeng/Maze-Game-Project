package byow.Core;

import java.io.Serializable;

public class Location implements Serializable {
    int x;
    int y;
    int closeRoomIndex;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
        this.closeRoomIndex = 0;
    }

    public Location(Location loc, int roomIndex) {
        this.x = loc.x;
        this.y = loc.y;
        this.closeRoomIndex = roomIndex;
    }



    @Override
    public int hashCode() {
        //@source:
        // https://stackoverflow.com/questions/21103000/issue-with-contains-hashset-method-java
        int hash = 3;
        hash = 47 * hash + this.x;
        hash = 47 * hash + this.y;
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        Location loc = (Location) o;
        return (loc.x == this.x) && (loc.y == this.y);
    }


}
