package ch.skyfy.fk.commands.featured;

import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.logic.FKGame;
import ch.skyfy.fk.logic.GameUtils;
import ch.skyfy.fk.msg.MsgBase;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CaptureCmd implements Command<ServerCommandSource> {

    private static class Msg extends MsgBase {

        public static Msg ASSAULT_NOT_ENABLED = new Msg("You cannot use this command, because the assaults are not enabled", Formatting.RED);
        public static Msg VAULT_FEATURE_NOT_ENABLED = new Msg("You cannot use this command, because Vault feature is not enabled", Formatting.RED);
        public static Msg NOT_A_FK_PLAYER = new Msg("You cannot use this command, because you are not an FKPlayer", Formatting.RED);
        public static Msg NO_BASE_HERE = new Msg("There is no base where you are", Formatting.GOLD);
        public static Msg NO_VAULT_FOUND_IN_BASE = new Msg(" No vault found inside the %s team base ! \n There should be a vault at this point in the game", Formatting.GOLD);
        public static Msg VAULT_IS_NOT_VALID = new Msg("The vault found inside the %s team base is not valid", Formatting.GOLD);
        public static Msg NOT_INSIDE_AN_ENEMY_VAULT = new Msg("You are not inside an enemy vault", Formatting.RED);
        public static Msg YOU_ARE_ELIMINATED = new Msg("You cannot use this command because you are eliminated", Formatting.RED);
        public static Msg TEAM_ALREADY_ELIMINATED = new Msg("The vault of the team you want to capture is already eliminated", Formatting.RED);

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
    public int run(CommandContext<ServerCommandSource> context) {
        if (optFKGameRef.get().isEmpty()) return 0;

        if (!GameUtils.isGameState_RUNNING()) return 0;

        var playerAttacker = context.getSource().getPlayer();
        if (playerAttacker == null) return 0;

        // The « capture » command only works if the « Vault » feature is enabled
        if (!Configs.VAULT_FEATURE_CONFIG.data.isEnabled())
            return Msg.VAULT_FEATURE_NOT_ENABLED.send(playerAttacker, 0);

        // A player who is part of a team that is eliminated can no longer use the command
        if (GameUtils.isFKPlayerEliminate(playerAttacker.getName().getString()))
            return Msg.YOU_ARE_ELIMINATED.send(playerAttacker, 0);

        var fkGame = optFKGameRef.get().get();

        // A player can only capture a vault if assaults are enabled
        if (!GameUtils.areAssaultEnabled(fkGame.getTimeline().getTimelineData().getDay()))
            return Msg.ASSAULT_NOT_ENABLED.send(playerAttacker, 0);

        var playerName = playerAttacker.getName().getString();

        // Only a player in the game can use the command
        if (!GameUtils.isFKPlayer(playerName))
            return Msg.NOT_A_FK_PLAYER.send(playerAttacker, 0);

        var playerPos = new Vec3d(playerAttacker.getX(), playerAttacker.getY(), playerAttacker.getZ());
        var optFKTeamVictim = GameUtils.getTeamByCoordinate(playerPos);

        // The « capture » command cannot be used outside an enemy base.
        if (optFKTeamVictim.isEmpty())
            return Msg.NO_BASE_HERE.send(playerAttacker, 0);

        var fkTeamVictim = optFKTeamVictim.get();
        var optVault = GameUtils.getVaultByTeamName(fkTeamVictim.getName());

        // If a team has not defined a vault with the « marker tool » from the « GetMarker » command
        if (optVault.isEmpty())
            return Msg.NO_VAULT_FOUND_IN_BASE.formatted(fkTeamVictim.getColor().toLowerCase()).send(playerAttacker, 0);

        var vault = optVault.get();

        // If a team has incorrectly defined the location of the vault using the « marker tool »
        if (!vault.isValid())
            return Msg.VAULT_IS_NOT_VALID.formatted(fkTeamVictim.getColor().toLowerCase()).send(playerAttacker, 0);

        var chestRoomFeature = fkGame.getVaultFeature();

        if (chestRoomFeature.whereIsThePlayer(playerAttacker) == Where.INSIDE_THE_VAULT_OF_AN_ENEMY_BASE) {
            // A player can no longer capture the room of a team that is already eliminated
            if (GameUtils.isFKTeamEliminate(fkTeamVictim))
                return Msg.TEAM_ALREADY_ELIMINATED.send(playerAttacker, 0);

            chestRoomFeature.addCapture(vault, fkTeamVictim, GameUtils.getFKTeamOfPlayerByName(playerAttacker.getName().getString()), playerAttacker);
        } else {
            return Msg.NOT_INSIDE_AN_ENEMY_VAULT.send(playerAttacker, 0);
        }
        return 0;
    }

}
