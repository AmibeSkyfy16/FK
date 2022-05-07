package ch.skyfy.fk.commands.featured;

import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.features.data.BlockPos;
import ch.skyfy.fk.logic.FKGame;
import ch.skyfy.fk.logic.GameUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("ClassCanBeRecord")
public class CaptureCmd implements Command<ServerCommandSource> {

    private final AtomicReference<Optional<FKGame>> optFKGameRef;


    public CaptureCmd(final AtomicReference<Optional<FKGame>> optFKGameRef) {
        this.optFKGameRef = optFKGameRef;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("capture")
                .executes(this)
        );
    }

    @SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (optFKGameRef.get().isEmpty()) return 0;

        var fkGame = optFKGameRef.get().get();

        var playerAttacker = context.getSource().getPlayer();

        if (!GameUtils.areAssaultEnabled(fkGame.getTimeline().getTimelineData().getDay())) {
            playerAttacker.sendMessage(new LiteralText("Assault are not enabled! So you cannot win").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
            return 0;
        }

        if (!Configs.CHEST_ROOM_CONFIG.config.isEnabled()) {
            playerAttacker.sendMessage(new LiteralText("Chest Room Feature is not enabled").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
            return 0;
        }

        var playerName = playerAttacker.getName().asString();

        if (!GameUtils.isFKPlayer(playerName)) {
            playerAttacker.sendMessage(new LiteralText("Only a FKPlayer can run this command").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
            return 0;
        }

        var playerPos = new Vec3d(playerAttacker.getX(), playerAttacker.getY(), playerAttacker.getZ());
        var optFKTeamVictim = GameUtils.getTeamByCoordinate(playerPos);

        if (optFKTeamVictim.isEmpty()) {
            playerAttacker.sendMessage(new LiteralText("There is no fkTeam here !").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
            return 0;
        }

        var fkTeamVictim = optFKTeamVictim.get();
        var optVault = GameUtils.getVaultByTeamName(fkTeamVictim.getName());

        if (optVault.isEmpty()) {
            playerAttacker.sendMessage(new LiteralText("No vault found for team " + fkTeamVictim.getName()).setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            return 0;
        }

        var vault = optVault.get();

        if (!vault.isValid()) {
            playerAttacker.sendMessage(new LiteralText("vault found for team " + fkTeamVictim.getName() + " is not a valid vault").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            return 0;
        }

        var chestRoomFeature = fkGame.getChestRoomFeature();
        switch (chestRoomFeature.whereIsThePlayer(playerAttacker)) {
            case INSIDE_HIS_OWN_BASE -> {
                playerAttacker.sendMessage(new LiteralText("You are inside your own base, but not inside your vault").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
            }
            case INSIDE_THE_VAULT_OF_HIS_OWN_BASE -> {
                playerAttacker.sendMessage(new LiteralText("You cannot capture your own vault").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
            }
            case INSIDE_AN_ENEMY_BASE -> {
                playerAttacker.sendMessage(new LiteralText("You are inside an enemy base, but not inside the vault").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
            }
            case INSIDE_THE_VAULT_OF_AN_ENEMY_BASE -> {
                chestRoomFeature.addCapture(vault, fkTeamVictim, GameUtils.getFKTeamOfPlayerByName(playerAttacker.getName().asString()), playerAttacker);
            }
        }

//        var whereIsThePlayer = (GameUtils.WhereIsThePlayer<Void>) (where) -> {
//            var box = BlockPos.toBox(vault.getBlockPos());
//            switch (where) {
//                case INSIDE_HIS_OWN_BASE -> {
//                    if (!box.contains(playerPos)) {
//                        playerAttacker.sendMessage(new LiteralText("You are inside your own base, but not inside your vault").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
//                        return null;
//                    }
//                    playerAttacker.sendMessage(new LiteralText("You cannot capture your own vault").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
//                    return null;
//                }
//                case INSIDE_AN_ENEMY_BASE -> {
//                    if (!box.contains(playerPos)) {
//                        playerAttacker.sendMessage(new LiteralText("You are inside an enemy base, but not inside the vault").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
//                        return null;
//                    }
//
//                    chestRoomFeature.addCapture(vault, fkTeamVictim, GameUtils.getFKTeamOfPlayerByName(playerAttacker.getName().asString()), playerAttacker);
//                    return null;
//                }
//            }
//
//
//            return null;
//        };
//
//        GameUtils.whereIsThePlayer(playerAttacker, playerPos, whereIsThePlayer);

        return 0;
    }

}
