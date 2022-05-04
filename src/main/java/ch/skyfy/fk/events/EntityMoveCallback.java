package ch.skyfy.fk.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

public interface EntityMoveCallback {

    Event<EntityMoveCallback> EVENT = EventFactory.createArrayBacked(EntityMoveCallback.class,
            (listeners) -> (entity, movementType, movement) -> {
                for (EntityMoveCallback listener : listeners) {
                    ActionResult result = listener.onMove(entity, movementType, movement);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult onMove(Entity entity, MovementType movementType, Vec3d movement);

}
