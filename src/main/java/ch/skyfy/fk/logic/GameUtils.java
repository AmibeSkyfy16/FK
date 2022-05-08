package ch.skyfy.fk.logic;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.config.data.FKTeam;
import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.features.data.Vault;
import ch.skyfy.fk.features.data.VaultConstant;
import ch.skyfy.fk.logic.data.FKGameAllData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static ch.skyfy.fk.config.Configs.TEAMS;

@SuppressWarnings({"unused"})
public class GameUtils {

    @FunctionalInterface
    public interface WhereIsThePlayer<T> {
        T impl(Where where);

    }

    /**
     * If among the connected players, one is missing, the game is not started
     */
    public static List<String> getMissingFKPlayer(List<ServerPlayerEntity> onlinePlayers) {
        var missingPlayers = new ArrayList<String>();
        for (var fkTeam : TEAMS.data.getTeams()) {
            for (var fkPlayerName : fkTeam.getPlayers()) {
                if (onlinePlayers.stream().noneMatch(serverPlayerEntity -> serverPlayerEntity.getName().asString().equals(fkPlayerName)))
                    missingPlayers.add(fkPlayerName);
            }
        }
        return missingPlayers;
    }

    /**
     * @param playerName The name of the player to be verified
     * @return True if the player is part of the game. False otherwise
     */
    public static boolean isFKPlayer(String playerName) {
        return TEAMS.data.getTeams().stream().flatMap(fkTeam -> fkTeam.getPlayers().stream()).anyMatch(fkPlayerName -> fkPlayerName.equals(playerName));
    }

    /**
     * @param onlinePlayers The list of players who are currently connected
     * @return A list with only the players participating in the FK
     */
    public static List<ServerPlayerEntity> getAllConnectedFKPlayers(List<ServerPlayerEntity> onlinePlayers) {
        return TEAMS.data.getTeams().stream()
                .flatMap(fkTeam -> onlinePlayers.stream()
                        .filter(player -> fkTeam.getPlayers().contains(player.getName().asString())))
                .toList();
    }

    public static List<ServerPlayerEntity> getPlayersFromNames(PlayerManager playerManager, List<String> names) {
        var list = new ArrayList<ServerPlayerEntity>();
        for (ServerPlayerEntity serverPlayerEntity : playerManager.getPlayerList())
            if (names.contains(serverPlayerEntity.getName().asString())) list.add(serverPlayerEntity);
        return list;
    }

    @Nullable
    public static BlockPos getBaseCoordinateByPlayer(String name) {
        for (var fkTeam : TEAMS.data.getTeams())
            if (fkTeam.getPlayers().stream().anyMatch(name::equals))
                return new BlockPos(fkTeam.getBase().getCube().getX(), fkTeam.getBase().getCube().getY(), fkTeam.getBase().getCube().getZ());
        return null;
    }

    public static Optional<FKTeam> getTeamByCoordinate(Vec3d pos) {
        for (var team : TEAMS.data.getTeams())
            if (Utils.isAPosInsideCube(team.getBase().getCube(), pos))
                return Optional.of(team);
        return Optional.empty();
    }

    public static Optional<Vault> getVaultByTeamName(String teamName) {
        return VaultConstant.VAULTS.data.getVaults().stream().filter(vault -> vault.getTeamId().equals(getFKTeamIdentifierByName(teamName))).findFirst();
    }

    public static Optional<ServerWorld> getServerWorldByIdentifier(MinecraftServer server, String id) {
        return StreamSupport.stream(server.getWorlds().spliterator(), false)
                .filter(serverWorld -> serverWorld.getDimension().getEffects().toString().equals(id))
                .findFirst();
    }

    public static FKTeam getFKTeamOfPlayerByName(String name) {
        for (var fkTeam : TEAMS.data.getTeams())
            if (fkTeam.getPlayers().stream().anyMatch(name::equals)) return fkTeam;
        return null;
    }

    public static boolean isInTheSameTeam(String playerName1, String playerName2) {
        for (var fkTeam : TEAMS.data.getTeams())
            if (fkTeam.getPlayers().stream().anyMatch(playerName1::equals) && fkTeam.getPlayers().stream().anyMatch(playerName2::equals))
                return true;
        return false;
    }

    public static void sendMissingPlayersMessage(ServerPlayerEntity player, List<ServerPlayerEntity> onlinePlayers) {
        var missingPlayers = GameUtils.getMissingFKPlayer(onlinePlayers);
        if (!missingPlayers.isEmpty()) {
            var sb = new StringBuilder();
            missingPlayers.forEach(missingPlayer -> sb.append(missingPlayer).append("\n"));
            player.sendMessage(Text.of("the game cannot be started/resumed because the following players are missing\n" + sb), false);
        }
    }

    public static <T> T whereIsThePlayer(PlayerEntity player, Vec3d pos, WhereIsThePlayer<T> whereIsThePlayer) {

        Where where = null;

        for (var team : TEAMS.data.getTeams()) {
            var baseCube = team.getBase().getCube();

            // Is this base the base of the player who break the block ?
            var isBaseOfPlayer = team.getPlayers().stream().anyMatch(fkPlayerName -> player.getName().asString().equals(fkPlayerName));

            // If player is inside a base
            if (Utils.isAPosInsideCube(baseCube, pos)) {

                // And this base is not his own
                if (!isBaseOfPlayer)
                    where = Where.INSIDE_AN_ENEMY_BASE;
                else
                    where = Where.INSIDE_HIS_OWN_BASE;
                nestsVault(where, pos);
            } else {
                if (Utils.isAPosInsideCube(team.getBase().getProximityCube(), pos)) {
                    if (isBaseOfPlayer) where = Where.CLOSE_TO_HIS_OWN_BASE;
                    else where = Where.CLOSE_TO_AN_ENEMY_BASE;
                }
            }

            if (where == null) where = Where.IN_THE_WILD;
        }

        return whereIsThePlayer.impl(where);
    }

    private static void nestsVault(Where where, Vec3d pos){
        if(!Configs.VAULT_CONFIG.data.isEnabled())return;
        Box box;
        for (var vault : VaultConstant.VAULTS.data.getVaults()) {
            if (vault.getBlockPos()[0] == null || vault.getBlockPos()[1] == null) continue;
            box = ch.skyfy.fk.features.data.BlockPos.toBox(vault.getBlockPos());
            switch (where) {
                case INSIDE_HIS_OWN_BASE -> {
                    if (box.contains(pos))
                        where.withNested(Where.INSIDE_THE_VAULT_OF_HIS_OWN_BASE);
                }
                case INSIDE_AN_ENEMY_BASE -> {
                    if (box.contains(pos))
                        where.withNested(Where.INSIDE_THE_VAULT_OF_AN_ENEMY_BASE);
                }
            }
        }
    }

    public static String getFKTeamIdentifierByName(String fkteamName) {
        return fkteamName.replaceAll("[^a-zA-Z\\d]", "");
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isGameState_RUNNING() {
        return FKGameAllData.FK_GAME_DATA.data.getGameState() == FKMod.GameState.RUNNING;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isGameState_PAUSED() {
        return FKGameAllData.FK_GAME_DATA.data.getGameState() == FKMod.GameState.PAUSED;
    }

    public static boolean isGameState_NOT_STARTED() {
        return FKGameAllData.FK_GAME_DATA.data.getGameState() == FKMod.GameState.NOT_STARTED;
    }

    public static boolean areAssaultEnabled(int currentDay) {
        return currentDay >= Configs.FK_CONFIG.data.getDayOfAuthorizationOfTheAssaults();
    }

    public static boolean isNetherEnabled(int currentDay) {
        return currentDay >= Configs.FK_CONFIG.data.getDayOfAuthorizationOfTheEntryInTheNether();
    }

    public static boolean isEndEnabled(int currentDay) {
        return currentDay >= Configs.FK_CONFIG.data.getDayOfAuthorizationOfTheEntryInTheEnd();
    }

    public static boolean isPvPEnabled(int currentDay) {
        return currentDay >= Configs.FK_CONFIG.data.getDayOfAuthorizationOfThePvP();
    }

}
