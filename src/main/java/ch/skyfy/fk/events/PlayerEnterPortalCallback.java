package ch.skyfy.fk.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public interface PlayerEnterPortalCallback {

    Event<PlayerEnterPortalCallback> EVENT = EventFactory.createArrayBacked(PlayerEnterPortalCallback.class,
            (listeners) -> (player, dimensionId) -> {
                for (PlayerEnterPortalCallback listener : listeners) {
                    ActionResult result = listener.onPlayerCollision(player, dimensionId);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult onPlayerCollision(ServerPlayerEntity player, Identifier dimensionId);

}
