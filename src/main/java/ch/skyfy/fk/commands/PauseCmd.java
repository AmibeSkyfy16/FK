package ch.skyfy.fk.commands;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.logic.FKGame;
import ch.skyfy.fk.logic.data.FKGameAllData;
import ch.skyfy.fk.logic.data.FKGameData;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.util.Util.NIL_UUID;

@SuppressWarnings("ClassCanBeRecord")
public class PauseCmd implements Command<ServerCommandSource> {

    private final AtomicReference<Optional<FKGame>> optFKGameRef;

    public PauseCmd(final AtomicReference<Optional<FKGame>> optFKGameRef) {
        this.optFKGameRef = optFKGameRef;
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
            case NOT_STARTED ->
                    player.sendMessage(new LiteralText("The game cannot be paused because it is not started !").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            case PAUSED ->
                    player.sendMessage(new LiteralText("The game is already paused !").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            case RUNNING -> {

                source.getServer().getPlayerManager().broadcast(Text.of("The game has been paused"), MessageType.CHAT, NIL_UUID);

                FKGameAllData.FK_GAME_DATA.config.setGameState(FKMod.GameState.PAUSED);
                optFKGameRef.get().ifPresent(FKGame::pause);

            }
        }

        return 0;
    }

}
