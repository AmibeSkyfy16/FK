package ch.skyfy.fk.commands;

import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.logic.GameUtils;
import ch.skyfy.fk.msg.WhereMsg;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;

public class WhereIAmCmd implements Command<ServerCommandSource> {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("WhereIAm").executes(this));
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        var player = context.getSource().getPlayer();
        if (player == null) return 0;

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

        GameUtils.whereIsThePlayer(player, new Vec3d(player.getBlockX(), player.getBlockY(), player.getBlockZ()), whereIsThePlayer);

        return 0;
    }
}
