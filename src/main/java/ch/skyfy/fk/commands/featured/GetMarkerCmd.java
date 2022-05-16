package ch.skyfy.fk.commands.featured;

import ch.skyfy.fk.msg.MsgBase;
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
 * Allows a player to collect a special item that will be used to determine the dimension of the vault
 */
public class GetMarkerCmd implements Command<ServerCommandSource> {

    private static class Msg extends MsgBase {
        private static final Msg MESSAGE = new Msg("""
                You have received an item allowing you to define the dimensions of your vault,
                (like in WorldEdit). This will allow the server to automatically detect the vault rooms
                """, Formatting.GREEN);
        protected Msg(String text, Formatting formatting) {
            super(text, formatting);
        }
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("getMarker").executes(this));
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var markerItem = Items.WOODEN_HOE.getDefaultStack();
        markerItem.setCustomName(new LiteralText("marker").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
        markerItem.setDamage(0);
        context.getSource().getPlayer().dropItem(markerItem, false);
        Msg.MESSAGE.send(context.getSource().getPlayer());
        return 0;
    }
}
