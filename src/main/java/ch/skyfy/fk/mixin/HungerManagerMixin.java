package ch.skyfy.fk.mixin;


import ch.skyfy.fk.events.PlayerHungerCallback;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {

    @Inject(at = @At("HEAD"), method = "update", cancellable = true)
    public void update(PlayerEntity player, CallbackInfo callbackInfo) {
        var actionResult = PlayerHungerCallback.EVENT.invoker().onUpdate(player);
        if (actionResult == ActionResult.FAIL) {
            callbackInfo.cancel();
        }
    }

}
