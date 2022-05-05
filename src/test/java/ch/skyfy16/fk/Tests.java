package ch.skyfy16.fk;

import org.junit.jupiter.api.Test;

public class Tests {

    @Test
    public void tes(){
        String str="This#string%contains^special*characters&.";
        str=str.replaceAll("[^a-zA-Z\\d]","");
        System.out.println(str);
    }

}
