package ch.skyfy.fk.commands;

import ch.skyfy.fk.logic.FKGame;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;

public class SetFKTimeCmd implements Command<ServerCommandSource> {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("setFKTime")
                .then(
                        CommandManager.argument("timeOfDay", IntegerArgumentType.integer(0, 144000))
                                .executes(this)
                )
        );
    }

    private final AtomicReference<Optional<FKGame>> optFKGameRef;

    public SetFKTimeCmd(final AtomicReference<Optional<FKGame>> optFKGameRef) {
        this.optFKGameRef = optFKGameRef;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        var time = getInteger(context, "timeOfDay");
        optFKGameRef.get().ifPresent(fkGame -> {
            fkGame.getTimeline().getTimelineData().setTimeOfDay(time);
            context.getSource().sendFeedback(Text.literal("time has been set to " + time).setStyle(Style.EMPTY.withColor(Formatting.GREEN)), true);
        });
        return SINGLE_SUCCESS;
    }

}
