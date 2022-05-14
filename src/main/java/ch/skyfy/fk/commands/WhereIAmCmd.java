package ch.skyfy.fk.commands;

import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.constants.WhereMsg;
import ch.skyfy.fk.logic.GameUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class WhereIAmCmd implements Command<ServerCommandSource> {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("WhereIAm").executes(this));
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayer();

        var whereIsThePlayer = (GameUtils.WhereIsThePlayer<Void>) (where) -> {
            switch (where.getRoot()) {
                case INSIDE_HIS_OWN_BASE -> {
                    if (where.getNested() != null && where.getNested() == Where.INSIDE_THE_VAULT_OF_HIS_OWN_BASE)
                        WhereMsg.IN_THE_VAULT_OF_YOUR_OWN_BASE.send(player);
                    else
                        WhereMsg.IN_YOUR_OWN_BASE.send(player);
                }
                case CLOSE_TO_HIS_OWN_BASE -> WhereMsg.CLOSE_TO_YOUR_OWN_BASE.send(player);
                case INSIDE_AN_ENEMY_BASE -> {
                    if (where.getNested() != null && where.getNested() == Where.INSIDE_THE_VAULT_OF_AN_ENEMY_BASE)
                        WhereMsg.IN_VAULT_OF_ENEMY_BASE.send(player);
                    else
                        WhereMsg.IN_AN_ENEMY_BASE.send(player);
                }
                case CLOSE_TO_AN_ENEMY_BASE -> WhereMsg.CLOSE_TO_AN_ENEMY_BASE.send(player);
                case IN_THE_WILD -> WhereMsg.IN_THE_WILD.send(player);
                default -> WhereMsg.UNKNOWN_LOCATION.send(player);
            }
            return null;
        };

        GameUtils.whereIsThePlayer(player, player.getPos(), whereIsThePlayer);

        return 0;
    }
}
