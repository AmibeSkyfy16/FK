package ch.skyfy.fk.commands;

import ch.skyfy.fk.logic.GameUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public class WhereIAmCmd implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayer();

        var whereIsThePlayer = (GameUtils.WhereIsThePlayer<Void>) (where) -> {

            switch (where) {
                case INSIDE_HIS_OWN_BASE -> {
                    player.sendMessage(new LiteralText("You are in your own base").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)), false);
                    return null;
                }
                case CLOSE_TO_HIS_OWN_BASE -> {
                    player.sendMessage(new LiteralText("You are in the proximity area of your own base").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)), false);
                    return null;
                }
                case INSIDE_AN_ENEMY_BASE -> {
                    player.sendMessage(new LiteralText("You are in an enemy base").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)), false);
                    return null;
                }
                case CLOSE_TO_AN_ENEMY_BASE -> {
                    player.sendMessage(new LiteralText("You are in the proximity area of an enemy base").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)), false);
                    return null;
                }
                case IN_THE_WILD -> {
                    player.sendMessage(new LiteralText("You are in the wild").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)), false);
                    return null;
                }
                default -> {
                    player.sendMessage(new LiteralText("Hummm this is strange").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)), false);
                    return null;
                }
            }
        };

        GameUtils.whereIsThePlayer(player, player.getPos(), whereIsThePlayer);

        return 0;
    }
}
