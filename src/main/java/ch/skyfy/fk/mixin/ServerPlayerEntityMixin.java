package ch.skyfy.fk.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "getPlayerListName", at = @At("TAIL"), cancellable = true)
    private void replacePlayerListName(CallbackInfoReturnable<Text> cir) {

        cir.setReturnValue(Text.of("HADDA_JIBOULA"));

    }

}
