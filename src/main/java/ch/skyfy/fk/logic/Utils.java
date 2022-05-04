package ch.skyfy.fk.logic;

import ch.skyfy.fk.config.data.Cube;
import ch.skyfy.fk.config.data.SpawnLocation;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

@SuppressWarnings("unused")
public class Utils {

    /**
     * If a player is inside a cube, and tries to get out, he is teleported one step back
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static boolean cancelPlayerFromLeavingACube(Cube cube, ServerPlayerEntity player, Optional<SpawnLocation> optSpawnLocation) {
        Vec3d vec = null;

        if (player.getX() >= cube.getX() + cube.getSize()) {
            vec = new Vec3d(player.getX() - 1, player.getY(), player.getZ());
        } else if (player.getX() <= cube.getX() - cube.getSize()) {
            vec = new Vec3d(player.getX() + 1, player.getY(), player.getZ());
        } else if (player.getZ() >= cube.getZ() + cube.getSize()) {
            vec = new Vec3d(player.getZ() - 1, player.getY(), player.getZ() - 1);
        } else if (player.getZ() <= cube.getZ() - cube.getSize()) {
            vec = new Vec3d(player.getX(), player.getY(), player.getZ() + 1);
        }else if(player.getY() >= cube.getY() + cube.getNumberOfBlocksUp()){
            vec = new Vec3d(player.getX(), player.getY() - 1, player.getZ());
        }else if(player.getY() <= cube.getY() - cube.getNumberOfBlocksDown()){
            // If the player falls from a platform, they are teleported back to their point of origin.
            // For example, if he falls from the waiting room, we teleport him to the waiting room
            if(optSpawnLocation.isPresent()){
                var spawnLocation = optSpawnLocation.get();
                vec = new Vec3d(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
            }else
                vec = new Vec3d(player.getX(), player.getY() - 1, player.getZ());
        }

        if (vec != null)
            player.teleport(vec.x, vec.y, vec.z);

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
    public static boolean isPlayerInsideCube(Cube cube, Vec3d pos) {
        if ((pos.getX() <= cube.getX() + cube.getSize()) && (pos.getX() >= cube.getX() - cube.getSize()))
            if ((pos.getZ() <= cube.getZ() + cube.getSize()) && (pos.getZ() >= cube.getZ() - cube.getSize()))
                return (pos.getY() <= cube.getY() + cube.getNumberOfBlocksUp()) && (pos.getY() >= cube.getY() - cube.getNumberOfBlocksDown());
        return false;
    }

}
