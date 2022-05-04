package ch.skyfy.fk.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerHungerCallback {

    Event<PlayerHungerCallback> EVENT = EventFactory.createArrayBacked(PlayerHungerCallback.class,
            (listeners) -> (player) -> {
                for (PlayerHungerCallback listener : listeners) {
                    ActionResult result = listener.onUpdate(player);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult onUpdate(PlayerEntity player);

}
