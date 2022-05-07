package ch.skyfy16.fk;

import ch.skyfy.fk.config.data.Cube;
import ch.skyfy.fk.utils.MathUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Tests {

    public class CubeTest {
        Cube cube1;
        Cube cube1_proximity;
        Cube cube2;
        Cube cube2_proximity;

        public CubeTest(Cube cube1, Cube cube1_proximity, Cube cube2, Cube cube2_proximity) {
            this.cube1 = cube1;
            this.cube1_proximity = cube1_proximity;
            this.cube2 = cube2;
            this.cube2_proximity = cube2_proximity;
        }
    }

    @Test
    void testWith_ArgumentAccessor() {
        System.out.println("test intersect");
        var map = new HashMap<CubeTest, Boolean>();

//        // Test #1, should be false
//        map.put(new CubeTest(
//                new Cube((short) 3, 255, 255, -50, -31, -50),
//                new Cube((short) 5, 255, 255, -50, -31, -50),
//                new Cube((short) 3, 255, 255, -39, -31, -50),
//                new Cube((short) 5, 255, 255, -39, -31, -50)
//        ), false);
//
//        // Test #2, should be true
//        map.put(new CubeTest(
//                new Cube((short) 3, 255, 255, -50, -31, -50),
//                new Cube((short) 5, 255, 255, -50, -31, -50),
//                new Cube((short) 3, 255, 255, -39, -31, -50),
//                new Cube((short) 5, 255, 255, -40, -31, -50)
//        ), true);
//
//        // Test #3, should be true
//        map.put(new CubeTest(
//                new Cube((short) 3, 255, 255, -50, -31, -50),
//                new Cube((short) 5, 255, 255, -50, -31, -50),
//                new Cube((short) 3, 255, 255, -39, -31, -50),
//                new Cube((short) 5, 255, 255, -40, -31, -51)
//        ), true);

        // Test #4, should be true
        map.put(new CubeTest(
                new Cube((short) 3, 255, 255, -50, -31, -50),
                new Cube((short) 50, 255, 255, -50, -31, -50),
                new Cube((short) 10, 255, 255, -50, -31, -50),
                new Cube((short) 20, 255, 255, -50, -31, -50)
        ), true);

        for (Map.Entry<CubeTest, Boolean> cubeTestBooleanEntry : map.entrySet()) {
            var cubeTest = cubeTestBooleanEntry.getKey();
            var expected = cubeTestBooleanEntry.getValue();
            if(MathUtils.isInside(cubeTest.cube1, cubeTest.cube2)){
                System.out.println("cube2 is inside cube1");
            }
            if(MathUtils.intersect(cubeTest.cube1, cubeTest.cube2)){
                System.out.println("intersect");
            }
        }

//        Cube cube1 = new Cube((short) 3, 255, 255, -50, -31, -50);
//        Cube cube1_proximity = new Cube((short) 5, 255, 255, -50, -31, -50);
//        Cube cube2 = new Cube((short) 3, 255, 255, -39, -31, -50);
//        Cube cube2_proximity = new Cube((short) 5, 255, 255, -40, -31, -50);
//        var result = MathUtils.intersect(cube1, cube2);
//        System.out.println(result);
//
//        var result2 = MathUtils.intersect(cube1_proximity, cube2_proximity);
//        System.out.println(result2);
    }

    @Test
    public void mapTest(){

    }

}
