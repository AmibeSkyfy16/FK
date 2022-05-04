package ch.skyfy.fk.mixin;

import ch.skyfy.fk.events.BucketEmptyCallback;
import ch.skyfy.fk.events.BucketFillCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class BucketMixin {

    @Shadow
    @Final
    private Fluid fluid;

    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    public void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> resultCallbackInfoReturnable) {
        var bucketItem = (BucketItem) (Object) this;

        var blockHitResult = ((ItemInvoker) bucketItem).invokeRaycast(world, user, this.fluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);

        var targetFluid = world.getFluidState(blockHitResult.getBlockPos()).getFluid();

        if(!(targetFluid instanceof EmptyFluid)){ // If there is lava or water fluid on the ground
            if(fluid instanceof EmptyFluid){ // And the player's bucket is empty
                var result = BucketFillCallback.EVENT.invoker().onUse(world, user, hand, targetFluid, bucketItem, blockHitResult);
                if(result.getResult() == ActionResult.FAIL){
                    resultCallbackInfoReturnable.setReturnValue(result);
                    resultCallbackInfoReturnable.cancel();
                }
            }
        }else{
            if(!(fluid instanceof EmptyFluid)){ // And the player's bucket is not empty
                var result = BucketEmptyCallback.EVENT.invoker().onUse(world, user, hand, fluid, bucketItem, blockHitResult);
                if(result.getResult() == ActionResult.FAIL){
                    resultCallbackInfoReturnable.setReturnValue(result);
                    resultCallbackInfoReturnable.cancel();
                }
            }
        }

    }

}
