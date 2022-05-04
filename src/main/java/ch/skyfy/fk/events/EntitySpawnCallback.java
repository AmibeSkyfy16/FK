package ch.skyfy.fk.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;

public interface EntitySpawnCallback {
    Event<EntitySpawnCallback> EVENT = EventFactory.createArrayBacked(EntitySpawnCallback.class,
            (listeners) -> (entity) -> {
                for (EntitySpawnCallback listener : listeners) {
                    var result = listener.onUse(entity);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });
    ActionResult onUse(Entity entity);
}
