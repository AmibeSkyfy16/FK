package ch.skyfy.fk.commands.featured;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

/**
 * Allows a player to collect a special item that will be used to determine the area of the vault
 */
public class GetMarkerCmd implements Command<ServerCommandSource> {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("getMarker")
                .executes(this)
        );
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var markerItem = Items.WOODEN_AXE.getDefaultStack();
        markerItem.setCustomName(new LiteralText("marker").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
        markerItem.setDamage(0);

        var strMessage = """
                You have received an item allowing you to define the dimensions of your vault,
                (like in WorldEdit). This will allow the server to automatically detect the vault rooms
                """;
        var message = new LiteralText("strMessage").setStyle(Style.EMPTY.withColor(Formatting.GREEN));

        context.getSource().getPlayer().dropItem(markerItem, false);
        context.getSource().getPlayer().sendMessage(message, false);

        return 0;
    }
}
