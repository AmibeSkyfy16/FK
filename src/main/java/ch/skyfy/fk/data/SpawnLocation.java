package ch.skyfy.fk.data;

import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
public final class SpawnLocation {
    @Getter
    private final String dimensionName;

    @Getter
    private final double x,y,z;

    @Getter
    private final float yaw;

    @Getter
    private final float pitch;

    public SpawnLocation(String dimensionName, double x, double y, double z, float yaw, float pitch) {
        this.dimensionName = dimensionName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
