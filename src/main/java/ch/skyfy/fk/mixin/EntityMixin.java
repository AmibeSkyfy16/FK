package ch.skyfy.fk.mixin;

import ch.skyfy.fk.events.EntityMoveCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(at = @At("HEAD"), method = "move", cancellable = true)
    public void onMove(MovementType movementType, Vec3d movement, CallbackInfo callbackInfo){

        var entity = (Entity)(Object)this;

        if(!(entity instanceof ServerPlayerEntity)){

            var result = EntityMoveCallback.EVENT.invoker().onMove(entity, movementType, movement);
            if (result == ActionResult.FAIL) {
                callbackInfo.cancel();
            }

        }

    }

}
