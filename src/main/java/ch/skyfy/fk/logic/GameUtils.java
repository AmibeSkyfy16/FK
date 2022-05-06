package ch.skyfy.fk.logic;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.config.data.FKTeam;
import ch.skyfy.fk.logic.data.FKGameAllData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ch.skyfy.fk.config.Configs.TEAMS;

@SuppressWarnings({"unused"})
public class GameUtils {

    @FunctionalInterface
    public interface WhereIsThePlayer<T> {
        T impl(boolean isPlayerInHisOwnBase, boolean isPlayerInAnEnemyBase, boolean isPlayerCloseToHisOwnBase, boolean isPlayerCloseToAnEnemyBase);

    }

    /**
     * If among the connected players, one is missing, the game is not started
     */
    public static List<String> ArePlayersReady(List<ServerPlayerEntity> onlinePlayers) {
        var missingPlayers = new ArrayList<String>();
        for (var fkTeam : TEAMS.config.getTeams()) {
            for (var fkPlayerName : fkTeam.getPlayers()) {
                if (onlinePlayers.stream().noneMatch(serverPlayerEntity -> serverPlayerEntity.getName().asString().equals(fkPlayerName))) {
                    missingPlayers.add(fkPlayerName);
                }
            }
        }
        return missingPlayers;
    }

    /**
     * @param playerName The name of the player to be verified
     * @return True if the player is part of the game. False otherwise
     */
    public static boolean isFKPlayer(String playerName) {
        return TEAMS.config.getTeams().stream().map(FKTeam::getName).anyMatch(fkPlayerName -> fkPlayerName.equals(playerName));
    }

    /**
     * @param onlinePlayers The list of players who are currently connected
     * @return A list with only the players participating in the FK
     */
    public static List<ServerPlayerEntity> getAllConnectedFKPlayers(List<ServerPlayerEntity> onlinePlayers) {
        return TEAMS.config.getTeams().stream()
                .flatMap(fkTeam -> onlinePlayers.stream()
                        .filter(player -> fkTeam.getPlayers().contains(player.getName().asString())))
                .toList();
    }

    @Nullable
    public static BlockPos getBaseCoordinateByPlayer(String name) {
        for (var fkTeam : TEAMS.config.getTeams())
            if (fkTeam.getPlayers().stream().anyMatch(name::equals))
                return new BlockPos(fkTeam.getBase().getCube().getX(), fkTeam.getBase().getCube().getY(), fkTeam.getBase().getCube().getZ());
        return null;
    }

    @Nullable
    public static FKTeam getFKTeamOfPlayerByName(String name) {
        for (var fkTeam : TEAMS.config.getTeams())
            if (fkTeam.getPlayers().stream().anyMatch(name::equals)) return fkTeam;
        return null;
    }

    public static boolean isInTheSameTeam(String playerName1, String playerName2) {
        for (var fkTeam : TEAMS.config.getTeams())
            if (fkTeam.getPlayers().stream().anyMatch(playerName1::equals) && fkTeam.getPlayers().stream().anyMatch(playerName2::equals))
                return true;
        return false;
    }

    public static boolean areMissingPlayers(List<ServerPlayerEntity> onlinePlayers) {
        return !GameUtils.ArePlayersReady(onlinePlayers).isEmpty();
    }

    public static void sendMissingPlayersMessage(ServerPlayerEntity player, List<ServerPlayerEntity> onlinePlayers) {
        var missingPlayers = GameUtils.ArePlayersReady(onlinePlayers);
        if (!missingPlayers.isEmpty()) {
            var sb = new StringBuilder();
            missingPlayers.forEach(missingPlayer -> sb.append(missingPlayer).append("\n"));
            player.sendMessage(Text.of("the game cannot be started/resumed because the following players are missing\n" + sb), false);
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    public static <T> T whereIsThePlayer(PlayerEntity player, Vec3d blockPos, WhereIsThePlayer<T> whereIsThePlayer) {

        var isPlayerInHisOwnBase = false;

        var isPlayerInAnEnemyBase = false;

        // Is the player close to his own base, but not inside
        var isPlayerCloseToHisOwnBase = false;

        // Is the player close to an enemy base, but not inside
        var isPlayerCloseToAnEnemyBase = false;

        for (FKTeam team : TEAMS.config.getTeams()) {
            var baseSquare = team.getBase().getCube();

            // Is this base the base of the player who break the block ?
            var isBaseOfPlayer = team.getPlayers().stream().anyMatch(fkPlayerName -> player.getName().asString().equals(fkPlayerName));

            var isPlayerCloseToABase = false;

//            var proximitySquare = new Cube((short) (baseSquare.getSize() + 20), baseSquare.getNumberOfBlocksDown() + 10, baseSquare.getNumberOfBlocksUp() + 10, baseSquare.getX(), baseSquare.getY(), baseSquare.getZ());
            if (Utils.isPlayerInsideCube(team.getBase().getProximityCube(), blockPos)) {
                isPlayerCloseToABase = true;
            }

            // If player is inside a base
            if (Utils.isPlayerInsideCube(baseSquare, blockPos)) {

                // And this base is not his own
                if (!isBaseOfPlayer) {
                    isPlayerInAnEnemyBase = true;
                } else {
                    isPlayerInHisOwnBase = true;
                }

            } else {

                // If the player is close to a base, but not inside
                if (isPlayerCloseToABase) {
                    if (!isPlayerInHisOwnBase) {
                        if (isBaseOfPlayer) isPlayerCloseToHisOwnBase = true;
                        else isPlayerCloseToAnEnemyBase = true;
                    } else if (!isPlayerInAnEnemyBase) {
                        if (!isBaseOfPlayer) isPlayerCloseToAnEnemyBase = true;
                        else isPlayerCloseToHisOwnBase = true;
                    }
                }

            }

        }

        return whereIsThePlayer.impl(isPlayerInHisOwnBase, isPlayerInAnEnemyBase, isPlayerCloseToHisOwnBase, isPlayerCloseToAnEnemyBase);
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isGameStateRUNNING() {
        return FKGameAllData.FK_GAME_DATA.config.getGameState() == FKMod.GameState.RUNNING;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isGameState_PAUSED() {
        return FKGameAllData.FK_GAME_DATA.config.getGameState() == FKMod.GameState.PAUSED;
    }

    public static boolean isGameStateNOT_STARTED() {
        return FKGameAllData.FK_GAME_DATA.config.getGameState() == FKMod.GameState.NOT_STARTED;
    }

    public static boolean areAssaultEnabled(int currentDay) {
        return currentDay >= Configs.FK_CONFIG.config.getDayOfAuthorizationOfTheAssaults();
    }

    public static boolean isNetherEnabled(int currentDay) {
        return currentDay >= Configs.FK_CONFIG.config.getDayOfAuthorizationOfTheEntryInTheNether();
    }

    public static boolean isEndEnabled(int currentDay) {
        return currentDay >= Configs.FK_CONFIG.config.getDayOfAuthorizationOfTheEntryInTheEnd();
    }

    public static boolean isPvPEnabled(int currentDay) {
        return currentDay >= Configs.FK_CONFIG.config.getDayOfAuthorizationOfThePvP();
    }

}
