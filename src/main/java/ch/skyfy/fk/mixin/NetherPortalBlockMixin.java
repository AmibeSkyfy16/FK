package ch.skyfy.fk.mixin;

import ch.skyfy.fk.events.PlayerEnterPortalCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setInNetherPortal(Lnet/minecraft/util/math/BlockPos;)V", shift = At.Shift.BEFORE), method = "onEntityCollision", cancellable = true)
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo callbackInfo) {
        if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            var actionResult = PlayerEnterPortalCallback.EVENT.invoker().onPlayerCollision(serverPlayerEntity, DimensionTypes.THE_NETHER_ID);
            if (actionResult == ActionResult.FAIL) {
                callbackInfo.cancel();
            }
        }
    }

}
