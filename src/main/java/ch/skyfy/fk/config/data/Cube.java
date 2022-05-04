package ch.skyfy.fk.config.data;

import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
public class Cube {

    @Getter
    private final short size; // A base is always square

    @Getter
    private final int numberOfBlocksDown; // The number of blocks down from the center

    @Getter
    private final int numberOfBlocksUp; // The number of blocks up from the center

    @Getter
    private final double x, y, z; // Represents the center of the base (the center of the square)

    public Cube(short size, int numberOfBlocksDown, int numberOfBlocksUp, double x, double y, double z) {
        this.size = size;
        this.numberOfBlocksDown = numberOfBlocksDown;
        this.numberOfBlocksUp = numberOfBlocksUp;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
