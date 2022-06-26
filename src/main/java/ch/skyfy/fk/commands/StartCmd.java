package ch.skyfy.fk.commands;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.logic.FKGame;
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

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"CommentedOutCode"})
public class StartCmd implements Command<ServerCommandSource> {

    private final AtomicReference<Optional<FKGame>> optFKGameRef;

    public StartCmd(final AtomicReference<Optional<FKGame>> optFKGameRef) {
        this.optFKGameRef = optFKGameRef;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("FKStart").executes(this));
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        var player = context.getSource().getPlayer();
        if (player == null) return 0;

        if (!GameUtils.isAdminByName(player.getName().getString())) {
            player.sendMessage(Text.literal("Only admin can run this command").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            return 0;
        }

        switch (PersistantFKGame.FK_GAME_DATA.data.getGameState()) {
            case PAUSED ->
                    player.sendMessage(Text.literal("The game cannot be started because it is paused !").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            case RUNNING ->
                    player.sendMessage(Text.literal("The game has already started !").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            case NOT_STARTED -> {
                // TODO UNCOMMENT
//                if (GameUtils.getMissingFKPlayer(source.getServer().getPlayerManager().getPlayerList()).size() > 0) {
//                    GameUtils.sendMissingPlayersMessage(player, source.getServer().getPlayerManager().getPlayerList());
//                    return 0;
//                }
                PersistantFKGame.FK_GAME_DATA.data.setGameState(FKMod.GameState.RUNNING);
                optFKGameRef.get().ifPresent(FKGame::start);
            }
        }

        return 0;
    }

}
