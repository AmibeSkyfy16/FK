package ch.skyfy.fk.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerMoveCallback {

    record MoveData(double lastX, double lastY, double lastZ, double updatedX, double updatedY, double updatedZ){ }

    Event<PlayerMoveCallback> EVENT = EventFactory.createArrayBacked(PlayerMoveCallback.class,
            (listeners) -> (moveData, player) -> {
                for (PlayerMoveCallback listener : listeners) {
                    ActionResult result = listener.onMove(moveData, player);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult onMove(MoveData moveData, ServerPlayerEntity player);

}
