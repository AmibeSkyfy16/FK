package ch.skyfy.fk.utils;

import ch.skyfy.fk.config.data.Cube;

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

}
