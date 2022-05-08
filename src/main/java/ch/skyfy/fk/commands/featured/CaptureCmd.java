package ch.skyfy.fk.commands.featured;

import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.constants.MsgBase;
import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.logic.FKGame;
import ch.skyfy.fk.logic.GameUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("ClassCanBeRecord")
public class CaptureCmd implements Command<ServerCommandSource> {

    private static class Msg extends MsgBase {

        public static Msg ASSAULT_NOT_ENABLED = new Msg("You cannot use this command, because the assaults are not enabled", Formatting.RED);
        public static Msg VAULT_FEATURE_NOT_ENABLED = new Msg("You cannot use this command, because Vault feature is not enabled", Formatting.RED);
        public static Msg NOT_A_FK_PLAYER = new Msg("You cannot use this command, because you are not an FKPlayer", Formatting.RED);
        public static Msg NO_BASE_HERE = new Msg("There is no base where you are", Formatting.GOLD);
        public static Msg NO_BASE_FOUND_IN_BASE = new Msg("No vault found inside the %s team base", Formatting.GOLD);
        public static Msg VAULT_IS_NOT_VALID = new Msg("The vault found inside the %s team base is not valid", Formatting.GOLD);

        protected Msg(String text, Formatting formatting) {
            super(text, formatting);
        }
    }

    private final AtomicReference<Optional<FKGame>> optFKGameRef;

    public CaptureCmd(final AtomicReference<Optional<FKGame>> optFKGameRef) {
        this.optFKGameRef = optFKGameRef;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("capture").executes(this));
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (optFKGameRef.get().isEmpty()) return 0;

        var playerAttacker = context.getSource().getPlayer();

        if (!Configs.VAULT_CONFIG.data.isEnabled()) {
            Msg.VAULT_FEATURE_NOT_ENABLED.send(playerAttacker);
            return 0;
        }

        var fkGame = optFKGameRef.get().get();

        if (!GameUtils.areAssaultEnabled(fkGame.getTimeline().getTimelineData().getDay())) {
            Msg.ASSAULT_NOT_ENABLED.send(playerAttacker);
            return 0;
        }

        var playerName = playerAttacker.getName().asString();

        if (!GameUtils.isFKPlayer(playerName)) {
            Msg.NOT_A_FK_PLAYER.send(playerAttacker);
            return 0;
        }

        var playerPos = new Vec3d(playerAttacker.getX(), playerAttacker.getY(), playerAttacker.getZ());
        var optFKTeamVictim = GameUtils.getTeamByCoordinate(playerPos);

        if (optFKTeamVictim.isEmpty()) {
            Msg.NO_BASE_HERE.send(playerAttacker);
            return 0;
        }

        var fkTeamVictim = optFKTeamVictim.get();
        var optVault = GameUtils.getVaultByTeamName(fkTeamVictim.getName());

        if (optVault.isEmpty()) {
            Msg.NO_BASE_FOUND_IN_BASE.formatted(fkTeamVictim.getColor().toLowerCase()).send(playerAttacker);
            return 0;
        }

        var vault = optVault.get();

        if (!vault.isValid()) {
            Msg.VAULT_IS_NOT_VALID.formatted(fkTeamVictim.getColor().toLowerCase()).send(playerAttacker);
            return 0;
        }

        var chestRoomFeature = fkGame.getVaultFeature();
        if(chestRoomFeature.whereIsThePlayer(playerAttacker) == Where.INSIDE_THE_VAULT_OF_AN_ENEMY_BASE)
            chestRoomFeature.addCapture(vault, fkTeamVictim, GameUtils.getFKTeamOfPlayerByName(playerAttacker.getName().asString()), playerAttacker);

        return 0;
    }

}
