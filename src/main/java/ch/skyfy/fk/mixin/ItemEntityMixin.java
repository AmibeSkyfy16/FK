package ch.skyfy.fk.mixin;

import ch.skyfy.fk.events.ItemDespawnCallback;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    public void tick(CallbackInfo callbackInfo) {
        var itemEntity = (ItemEntity) (Object) this;
        var result = ItemDespawnCallback.EVENT.invoker().onDespawn(itemEntity);
        if (result == ActionResult.FAIL)
            callbackInfo.cancel();
    }

}
