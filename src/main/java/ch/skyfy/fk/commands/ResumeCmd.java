package ch.skyfy.fk.commands;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.logic.FKGame;
import ch.skyfy.fk.logic.data.FKGameAllData;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.util.Util.NIL_UUID;

@SuppressWarnings({"ClassCanBeRecord", "CommentedOutCode"})
public class ResumeCmd implements Command<ServerCommandSource> {

    private final AtomicReference<Optional<FKGame>> optFKGameRef;

    public ResumeCmd(final AtomicReference<Optional<FKGame>> optFKGameRef) {
        this.optFKGameRef = optFKGameRef;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("FKResume").executes(this));
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayer();

        // TODO UNCOMMENT THIS
//        if (!player.hasPermissionLevel(4)) {
//            player.sendMessage(Text.of("You dont have required privileges to use this command"), false);
//            return 0;
//        }

        switch (FKGameAllData.FK_GAME_DATA.data.getGameState()) {
            case NOT_STARTED ->
                    player.sendMessage(new LiteralText("The game cannot be resumed because it is not started !").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            case RUNNING ->
                    player.sendMessage(new LiteralText("The game cannot be resumed because it is running !").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            case PAUSED -> {
                // TODO UNCOMMENT
//                if(GameUtils.getMissingFKPlayer(source.getServer().getPlayerManager().getPlayerList()).size() > 0){
//                    GameUtils.sendMissingPlayersMessage(player, source.getServer().getPlayerManager().getPlayerList());
//                    return 0;
//                }

                FKGameAllData.FK_GAME_DATA.data.setGameState(FKMod.GameState.RUNNING);
                optFKGameRef.get().ifPresent(FKGame::resume);
            }
        }

        return 0;
    }
}
