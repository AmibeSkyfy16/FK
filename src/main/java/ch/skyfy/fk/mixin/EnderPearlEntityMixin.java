package ch.skyfy.fk.mixin;

import ch.skyfy.fk.events.EnderPearlCollisionCallback;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;hasVehicle()Z", shift = At.Shift.BEFORE), method = "onCollision", cancellable = true)
    public void onCollision(CallbackInfo callbackInfo) {
        var entity = (EnderPearlEntity) (Object) this;
        var playerEntity = (ServerPlayerEntity) entity.getOwner();
        var result = EnderPearlCollisionCallback.EVENT.invoker().onCollision(playerEntity, entity.getPos());
        if (result == ActionResult.FAIL) {
            callbackInfo.cancel();
            entity.discard();
        }
    }

}
