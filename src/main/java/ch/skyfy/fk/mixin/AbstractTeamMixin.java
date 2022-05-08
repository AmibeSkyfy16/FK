package ch.skyfy.fk.mixin;

import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.config.data.FKTeam;
import ch.skyfy.fk.logic.FKGame;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Team.class)
public class AbstractTeamMixin {

    @Inject(at = @At("TAIL"), method = "decorateName(Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;", cancellable = true)
    public void decorate(Text text, CallbackInfoReturnable<MutableText> callbackInfoReturnable) {
        if(0 == 0)return;
        if(FKGame.configLoaded.get()) {
            for (FKTeam team : Configs.TEAMS.data.getTeams()) {
                for (String playerName : team.getPlayers()) {
                    if(playerName.equals("Skyfy16") || playerName.equals("Alex")){
                        var mutableText = new LiteralText("[ "+team.getColor() + " ] " + playerName).setStyle(Style.EMPTY.withColor(Formatting.valueOf(team.getColor())));
                        text = mutableText;
                        System.out.println("new text is : " + text.getString());
                        callbackInfoReturnable.setReturnValue(mutableText);
                    }
                }
            }
        }
    }

}
