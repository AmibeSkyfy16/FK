package ch.skyfy.fk.mixin;

import ch.skyfy.fk.events.BucketEmptyCallback;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.PowderSnowBucketItem;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBucketItem.class)
public class PowderSnowBucketItemMixin {

    @Inject(at = @At(
            value = "INVOKE",
            shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/item/BlockItem;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"),
            method = "useOnBlock", cancellable = true
    )
    public void onPlace(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        var object = (PowderSnowBucketItem)(Object)this;
        var player = context.getPlayer();
        if(player == null)return;
        var hand = player.getActiveHand();
        var result = BucketEmptyCallback.EVENT.invoker().onUse(context.getWorld(), player, hand, Fluids.EMPTY, object, context.getHitPos());
        if(result.getResult() == ActionResult.FAIL){
            cir.setReturnValue(ActionResult.FAIL);
            cir.cancel();
        }
    }

}
