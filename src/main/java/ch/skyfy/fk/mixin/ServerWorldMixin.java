package ch.skyfy.fk.mixin;

import ch.skyfy.fk.events.TimeOfDayUpdatedCallback;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(at = @At("HEAD"), method = "setTimeOfDay", cancellable = true)
    public void onTimeOfDayUpdate(long timeOfDay, CallbackInfo callbackInfo){
        var actionResult = TimeOfDayUpdatedCallback.EVENT.invoker().onTimeOfDayUpdated(timeOfDay);
        if(actionResult == ActionResult.FAIL){
            callbackInfo.cancel();
        }
    }

}
