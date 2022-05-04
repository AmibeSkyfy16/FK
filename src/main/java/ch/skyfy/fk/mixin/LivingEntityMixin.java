package ch.skyfy.fk.mixin;

import ch.skyfy.fk.events.PlayerDamageCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @SuppressWarnings("CancellableInjectionUsage")
    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        var livingEntity = (LivingEntity)(Object)this;
        if(livingEntity instanceof ServerPlayerEntity player) {
            var actionResult = PlayerDamageCallback.EVENT.invoker().onDamage(player, source, amount);
            if(actionResult == ActionResult.FAIL){
                cir.cancel();
            }
        }
    }

}
