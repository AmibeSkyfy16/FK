package ch.skyfy.fk.data;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.Box;

/**
 * Represent a block position.
 * Serialize and deserialize through Gson
 */
public class BlockPos {

    @Getter
    @Setter
    private double x;
    @Getter
    @Setter
    private double y;
    @Getter
    @Setter
    private double z;

    public BlockPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static BlockPos of(net.minecraft.util.math.BlockPos blockPos){
        return new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
    public static Box toBox(BlockPos[] blockPos){
        var minX = Math.min(blockPos[0].getX(), blockPos[1].getX());
        var maxX = Math.max(blockPos[0].getX(), blockPos[1].getX());
        var minY = Math.min(blockPos[0].getY(), blockPos[1].getY());
        var maxY = Math.max(blockPos[0].getY(), blockPos[1].getY());
        var minZ = Math.min(blockPos[0].getZ(), blockPos[1].getZ());
        var maxZ = Math.max(blockPos[0].getZ(), blockPos[1].getZ());
        return new Box(minX, minY, minZ, maxX, maxY, maxZ);
    }

}
