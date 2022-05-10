package ch.skyfy.fk.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

public interface EnderPearlCollisionCallback {
    Event<EnderPearlCollisionCallback> EVENT = EventFactory.createArrayBacked(EnderPearlCollisionCallback.class,
            (listeners) -> (player, pos) -> {
                for (EnderPearlCollisionCallback listener : listeners) {
                    ActionResult result = listener.onCollision(player, pos);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult onCollision(ServerPlayerEntity player, Vec3d pos);
}
