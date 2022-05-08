package ch.skyfy16.fk.test;

import ch.skyfy.fk.config.data.Cube;
import net.minecraft.util.math.Vec3d;

public class MathUtils {

    /**
     * @return True if the player is in a given cube. False otherwise
     */
    public static boolean isAPosInsideCube(Cube cube, Vec3d pos) {
        if ((pos.getX() <= cube.getX() + cube.getSize()) && (pos.getX() >= cube.getX() - cube.getSize()))
            if ((pos.getZ() <= cube.getZ() + cube.getSize()) && (pos.getZ() >= cube.getZ() - cube.getSize()))
                return (pos.getY() <= cube.getY() + cube.getNumberOfBlocksUp()) && (pos.getY() >= cube.getY() - cube.getNumberOfBlocksDown());
        return false;
    }

}
