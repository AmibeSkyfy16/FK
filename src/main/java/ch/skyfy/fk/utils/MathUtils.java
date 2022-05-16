package ch.skyfy.fk.utils;

import ch.skyfy.fk.data.Cube;
import ch.skyfy.fk.data.SpawnLocation;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class MathUtils {

    /**
     * <a href="https://math.stackexchange.com/questions/3086589/determine-if-a-rectangle-is-inside-overlaps-doesnt-overlaps-another-rectangle">https://math.stackexchange.com/questions/3086589/determine-if-a-rectangle-is-inside-overlaps-doesnt-overlaps-another-rectangle</a>
     * @return return true if the area of the two cube intersect
     */
    public static boolean intersect(Cube cube1, Cube cube2) {
        var x1 = cube1.getX();
        var z1 = cube1.getZ();
        var w1 = cube1.getSize() * 2;
        var h1 = cube1.getSize() * 2;

        var x2 = cube2.getX();
        var z2 = cube2.getZ();
        var w2 = cube2.getSize() * 2;
        var h2 = cube2.getSize() * 2;

        var intersect = !(x1 + (w1 / 2d) < x2 - (w2 / 2d));

        if (x1 - (w1 / 2d) > x2 + (w2 / 2d))
            intersect = false;
        if (z1 + (h1 / 2d) < z2 - (h2 / 2d))
            intersect = false;
        if (z1 - (h1 / 2d) > z2 + (h2 / 2d))
            intersect = false;
        return intersect;
    }

    /**
     * return true if the second cube area is inside the second
     */
    public static boolean isInside(Cube cube1, Cube inside) {
        var x1 = cube1.getX();
        var z1 = cube1.getZ();
        var width1 = cube1.getSize() * 2;
        var height1 = cube1.getSize() * 2;

        var x2 = inside.getX();
        var z2 = inside.getZ();
        var width2 = inside.getSize() * 2;
        var height2 = inside.getSize() * 2;

        var isSecondCubeInsideFirst = false;

        if (x2 + (width2 / 2d) <= x1 + (width1 / 2d)) {
            if (x2 - (width2 / 2d) >= x1 - (width1 / 2d)) {
                if (z2 + (height2 / 2d) <= z1 + (height1 / 2d)) {
                    if (z2 - (height2 / 2d) >= z1 - (height1 / 2d)) {
                        isSecondCubeInsideFirst = true;
                    }
                }
            }
        }

        return isSecondCubeInsideFirst;
    }

    /**
     * @return True if the player is in a given square area. False otherwise
     */
    public static boolean isPlayerInsideArea(Cube cube, Vec3d pos) {
        if ((pos.getX() <= cube.getX() + cube.getSize()) && (pos.getX() >= cube.getX() - cube.getSize()))
            return (pos.getZ() <= cube.getZ() + cube.getSize()) && (pos.getZ() >= cube.getZ() - cube.getSize());
        return false;
    }

    /**
     * @return True if the player is in a given cube. False otherwise
     */
    public static boolean isAPosInsideCube(Cube cube, Vec3d pos) {
        if(isPlayerInsideArea(cube, pos))
            return (pos.getY() <= cube.getY() + cube.getNumberOfBlocksUp()) && (pos.getY() >= cube.getY() - cube.getNumberOfBlocksDown());
        return false;
    }

    /**
     * If a player is inside a cube, and tries to get out, he is teleported one step back
     */
    public static boolean cancelPlayerFromLeavingAnArea(Cube cube, ServerPlayerEntity player, @Nullable SpawnLocation spawnLocation) {
        Vec3d vec = null;

        if (player.getX() >= cube.getX() + cube.getSize()) {
            vec = new Vec3d(player.getX() - 1, player.getY(), player.getZ());
        } else if (player.getX() <= cube.getX() - cube.getSize()) {
            vec = new Vec3d(player.getX() + 1, player.getY(), player.getZ());
        } else if (player.getZ() >= cube.getZ() + cube.getSize()) {
            vec = new Vec3d(player.getZ() - 1, player.getY(), player.getZ() - 1);
        } else if (player.getZ() <= cube.getZ() - cube.getSize()) {
            vec = new Vec3d(player.getX(), player.getY(), player.getZ() + 1);
        }

        if (vec != null){
            if(spawnLocation != null)
                vec = new Vec3d(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
            player.teleport(vec.x, vec.y, vec.z);
        }

        return vec != null;
    }

}
