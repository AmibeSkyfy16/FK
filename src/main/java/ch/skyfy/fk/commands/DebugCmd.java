package ch.skyfy.fk.commands;

import ch.skyfy.fk.logic.GameUtils;
import ch.skyfy.fk.logic.persistant.PersistantFKGame;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DebugCmd implements Command<ServerCommandSource> {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("FKDebug").executes(this));
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        var player = context.getSource().getPlayer();
        if (player == null) return 0;

        if (!GameUtils.isAdminByName(player.getName().getString())) {
            player.sendMessage(Text.literal("Only admin can run this command").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            return 0;
        }

        var newValue = !PersistantFKGame.FK_GAME_DATA.data.isDebug();
        PersistantFKGame.FK_GAME_DATA.data.setDebug(newValue);

        if (newValue)
            player.sendMessage(Text.literal("Debug mode is now enabled").setStyle(Style.EMPTY.withColor(Formatting.GREEN)), false);
        else
            player.sendMessage(Text.literal("Debug mode is now disabled").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);


        return SINGLE_SUCCESS;
    }

}
