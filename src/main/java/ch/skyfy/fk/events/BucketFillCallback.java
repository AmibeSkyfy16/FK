package ch.skyfy.fk.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public interface BucketFillCallback {
    Event<BucketFillCallback> EVENT = EventFactory.createArrayBacked(BucketFillCallback.class,
            (listeners) -> (world, player, hand, fillFluid,targetBlock, bucketItem, blockHitResult) -> {
                for (BucketFillCallback listener : listeners) {
                    var result = listener.onUse(world, player, hand, fillFluid,targetBlock, bucketItem, blockHitResult);
                    if (result.getResult() != ActionResult.PASS) {
                        return result;
                    }
                }
                return TypedActionResult.pass(ItemStack.EMPTY);
            });

    TypedActionResult<ItemStack> onUse(World world, PlayerEntity player, Hand hand, Fluid fillFluid, Block targetBlock, BucketItem bucketItem, BlockHitResult blockHitResult);
}
