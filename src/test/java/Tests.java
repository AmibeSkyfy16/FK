import net.minecraft.entity.ItemEntity;
import org.junit.jupiter.api.Test;

public class Tests {

    @Test
    public void test() throws NoSuchFieldException {
        Class<ItemEntity> itemEntityClass = ItemEntity.class;

        var itemAgeField = itemEntityClass.getDeclaredField("itemAge");
        itemAgeField.setAccessible(true);
    }

}
