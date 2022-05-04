package ch.skyfy.fk.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public interface BucketEmptyCallback {
    Event<BucketEmptyCallback> EVENT = EventFactory.createArrayBacked(BucketEmptyCallback.class,
            (listeners) -> (world, player, hand, emptyFluid, bucketItem, blockHitResult) -> {
                for (BucketEmptyCallback listener : listeners) {
                    var result = listener.onUse(world, player, hand, emptyFluid, bucketItem, blockHitResult);
                    if (result.getResult() != ActionResult.PASS) {
                        return result;
                    }
                }
                return TypedActionResult.pass(ItemStack.EMPTY);
            });

    /**
     *
     * Called when a player try to empty a bucket
     *
     * @param world World
     * @param player PlayerEntity
     * @param hand Hand
     * @param emptyFluid Fluid, The fluid that the player is trying to drain
     * @param bucketItem BucketItem, The Bucket that the player tries to empty
     */
    TypedActionResult<ItemStack> onUse(World world, PlayerEntity player, Hand hand, Fluid emptyFluid, BucketItem bucketItem, BlockHitResult blockHitResult);
}
