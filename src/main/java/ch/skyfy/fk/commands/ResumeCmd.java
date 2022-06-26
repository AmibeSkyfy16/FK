package ch.skyfy.fk.commands;

import ch.skyfy.fk.logic.FKGame;
import ch.skyfy.fk.logic.persistant.PersistantFKGame;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"CommentedOutCode"})
public class ResumeCmd implements Command<ServerCommandSource> {

    private final AtomicReference<Optional<FKGame>> optFKGameRef;

    public ResumeCmd(final AtomicReference<Optional<FKGame>> optFKGameRef) {
        this.optFKGameRef = optFKGameRef;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("FKResume").executes(this));
    }

    @SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        var source = context.getSource();
        var player = source.getPlayer();
        if (player == null) return 0;

        // TODO UNCOMMENT THIS
//        if (!player.hasPermissionLevel(4)) {
//            player.sendMessage(Text.of("You don't have required privileges to use this command"), false);
//            return 0;
//        }

        switch (PersistantFKGame.FK_GAME_DATA.data.getGameState()) {
            case NOT_STARTED ->
                    player.sendMessage(Text.literal("The game cannot be resumed because it is not started !").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            case RUNNING ->
                    player.sendMessage(Text.literal("The game cannot be resumed because it is running !").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            case PAUSED -> {
                // TODO UNCOMMENT
//                if(GameUtils.getMissingFKPlayer(source.getServer().getPlayerManager().getPlayerList()).size() > 0){
//                    GameUtils.sendMissingPlayersMessage(player, source.getServer().getPlayerManager().getPlayerList());
//                    return 0;
//                }
                optFKGameRef.get().ifPresent(FKGame::resume);
            }
        }

        return 0;
    }
}
