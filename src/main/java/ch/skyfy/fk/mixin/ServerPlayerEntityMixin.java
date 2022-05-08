package ch.skyfy.fk.mixin;

import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.config.data.FKTeam;
import ch.skyfy.fk.logic.FKGame;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "getPlayerListName", at = @At("TAIL"), cancellable = true)
    private void replacePlayerListName(CallbackInfoReturnable<Text> cir) {

        var player = (ServerPlayerEntity)(Object)this;

        if(FKGame.configLoaded.get()) {
            System.out.println("called after config !");
            for (FKTeam team : Configs.TEAMS.data.getTeams()) {
                for (String playerName : team.getPlayers()) {
                    if(playerName.equals("Skyfy16") || playerName.equals("Alex")){
                        var mutableText = new LiteralText("[ "+team.getColor() + " ] " + playerName).setStyle(Style.EMPTY.withColor(Formatting.valueOf(team.getColor())));
                        System.out.println("new text is : " + mutableText.getString());
                        cir.setReturnValue(mutableText);
                    }
                }
            }
        }else{
            System.out.println("shit");
        }

    }

}
