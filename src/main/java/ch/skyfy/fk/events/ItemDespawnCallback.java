package ch.skyfy.fk.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.ActionResult;

public interface ItemDespawnCallback {
    Event<ItemDespawnCallback> EVENT = EventFactory.createArrayBacked(ItemDespawnCallback.class,
            (listeners) -> (entity) -> {
                for (ItemDespawnCallback listener : listeners) {
                    var result = listener.onDespawn(entity);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });
    ActionResult onDespawn(ItemEntity itemEntity);
}
