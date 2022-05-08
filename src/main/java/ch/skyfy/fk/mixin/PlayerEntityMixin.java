package ch.skyfy.fk.mixin;

import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.config.data.FKTeam;
import ch.skyfy.fk.logic.FKGame;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @ModifyArg(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Team;decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"))
    private Text replaceName(Text text) {

        if (FKGame.configLoaded.get()) {
            for (FKTeam team : Configs.TEAMS.data.getTeams()) {
                for (String playerName : team.getPlayers()) {
                    if (playerName.equals("Skyfy16") || playerName.equals("Alex")) {
                        var mutableText = new LiteralText("[ " + team.getColor() + " ] " + playerName).setStyle(Style.EMPTY.withColor(Formatting.valueOf(team.getColor())));
                        text = mutableText;
                        System.out.println("new text is : " + text.getString());
                        return text;
                    }
                }
            }
        }

        return text;
    }

}
