package ch.skyfy.fk.mixin;

import ch.skyfy.fk.events.EntitySpawnCallback;
import ch.skyfy.fk.events.TimeOfDayUpdatedCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(at = @At("HEAD"), method = "setTimeOfDay", cancellable = true)
    public void onTimeOfDayUpdate(long timeOfDay, CallbackInfo callbackInfo){
        var actionResult = TimeOfDayUpdatedCallback.EVENT.invoker().onTimeOfDayUpdated(timeOfDay);
        if(actionResult == ActionResult.FAIL) callbackInfo.cancel();
    }

    @Inject(at = @At("HEAD"), method = "spawnEntity", cancellable = true)
    public void spawnEntity(Entity entity, CallbackInfoReturnable<Boolean> returnable){
        var result = EntitySpawnCallback.EVENT.invoker().onUse(entity);
        if(result == ActionResult.FAIL){
            returnable.setReturnValue(false);
            returnable.cancel();
        }
    }

}
