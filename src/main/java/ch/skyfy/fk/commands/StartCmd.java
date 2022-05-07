package ch.skyfy.fk.commands;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.logic.FKGame;
import ch.skyfy.fk.logic.GameUtils;
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
public class StartCmd implements Command<ServerCommandSource> {

    private final AtomicReference<Optional<FKGame>> optFKGameRef;

    public StartCmd(final AtomicReference<Optional<FKGame>> optFKGameRef) {
        this.optFKGameRef = optFKGameRef;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("FKStart").executes(this));
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

        switch (FKGameAllData.FK_GAME_DATA.config.getGameState()) {
            case PAUSED ->
                    player.sendMessage(new LiteralText("The game cannot be started because it is paused !").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            case RUNNING ->
                    player.sendMessage(new LiteralText("The game has already started !").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            case NOT_STARTED -> {

                // TODO UNCOMMENT
                if (GameUtils.getMissingFKPlayer(source.getServer().getPlayerManager().getPlayerList()).size() > 0) {
                    GameUtils.sendMissingPlayersMessage(player, source.getServer().getPlayerManager().getPlayerList());
                }

                source.getServer().getPlayerManager().broadcast(new LiteralText("The game begins !").setStyle(Style.EMPTY.withColor(Formatting.GREEN)), MessageType.CHAT, NIL_UUID);

                FKGameAllData.FK_GAME_DATA.config.setGameState(FKMod.GameState.RUNNING);

                optFKGameRef.get().ifPresent(FKGame::start);
            }
        }

        return 0;
    }

}
