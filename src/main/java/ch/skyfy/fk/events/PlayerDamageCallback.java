package ch.skyfy.fk.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerDamageCallback {
    Event<PlayerDamageCallback> EVENT = EventFactory.createArrayBacked(PlayerDamageCallback.class,
            (listeners) -> (player, source, amount) -> {
                for (PlayerDamageCallback listener : listeners) {
                    ActionResult result = listener.onDamage(player, source, amount);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult onDamage(ServerPlayerEntity player, DamageSource source, float amount);
}
