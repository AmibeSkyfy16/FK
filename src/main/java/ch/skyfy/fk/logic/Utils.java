package ch.skyfy.fk.logic;

import ch.skyfy.fk.config.data.Cube;
import ch.skyfy.fk.config.data.SpawnLocation;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class Utils {

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
        if ((pos.getX() <= cube.getX() + cube.getSize()) && (pos.getX() >= cube.getX() - cube.getSize()))
            if ((pos.getZ() <= cube.getZ() + cube.getSize()) && (pos.getZ() >= cube.getZ() - cube.getSize()))
                return (pos.getY() <= cube.getY() + cube.getNumberOfBlocksUp()) && (pos.getY() >= cube.getY() - cube.getNumberOfBlocksDown());
        return false;
    }

}
