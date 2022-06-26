package ch.skyfy.fk.features.vault;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.config.featured.VaultFeatureConfig;
import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.data.BlockPos;
import ch.skyfy.fk.data.FKTeam;
import ch.skyfy.fk.events.PlayerMoveCallback;
import ch.skyfy.fk.features.vault.persistant.PersistantVault;
import ch.skyfy.fk.features.vault.persistant.PersistantVaultData;
import ch.skyfy.fk.features.vault.persistant.Vault;
import ch.skyfy.fk.logic.FKGame;
import ch.skyfy.fk.logic.GameUtils;
import ch.skyfy.fk.logic.persistant.PersistantFKGame;
import ch.skyfy.fk.msg.MsgBase;
import ch.skyfy.fk.utils.MathUtils;
import ch.skyfy.fk.utils.ReflectionUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.skyfy.fk.constants.Where.INSIDE_AN_ENEMY_BASE;
import static ch.skyfy.fk.constants.Where.INSIDE_THE_VAULT_OF_AN_ENEMY_BASE;
import static net.minecraft.util.Formatting.*;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class VaultFeature {

    private static class Msg extends MsgBase {
        private static final Msg GAME_IS_OVER = new Msg("The game is over ! ", GOLD);
        private static final Msg WINNER = new Msg("Team %s won by capturing vault of team %s", GOLD);
        private static final Msg NEW_TEAM_ELIMINATE = new Msg("%s team has been eliminated by %s team", GOLD);
        private static final Msg YOU_ARE_NOW_SPECTATOR = new Msg("You have been eliminated ! You are now spectator", GOLD);
        private static final Msg CAPTURE1 = new Msg("The %s player of the %s team has started the capture of your vault", GOLD);
        private static final Msg CAPTURE2 = new Msg("Player %s has begun the capture of the %s team's vault", GOLD);
        private static final Msg LEFT = new Msg("You just came out of the vault. As long as there are allies left inside, the capture will not be cancelled", GOLD);
        private static final Msg CANCELLED = new Msg("\nYou have just come out of the vault, and there is no ally of yours in it. The capture is cancelled", GOLD);
        private static final Msg ANOTHER_TEAM_ALREADY_CAPTURING_THIS_VAULT = new Msg("Another team is already capturing this vault", GOLD);
        private static final Msg YOU_ARE_ALREADY_CAPTURING_THIS_VAULT = new Msg("You are already capturing this vault", GOLD);
        private static final Msg VAULT_SET = new Msg("Your vault has been set successfully", GREEN);
        private static final Msg CANNOT_SET_VAULT_OUTSIDE_BASE = new Msg("You cannot create a vault outside your base", RED);
        private static final Msg VAULT_IS_TOO_DEEP = new Msg("""
                Your room is too deep
                According to the rules, your vault must be located no more
                than %d blocks below the center point (x:%.0f y:%.0f z:%.0f) of your base
                """, RED);

        private static final Msg VAULT_WIDTH_TOO_SMALL = new Msg("The width of the vault must be at least %d blocks", RED);
        private static final Msg VAULT_LENGTH_TOO_SMALL = new Msg("The length of the vault must be at least %d blocks", RED);
        private static final Msg VAULT_HEIGHT_TOO_SMALL = new Msg("The height of the vault must be at least %d blocks", RED);


        public Msg(String text, Formatting formatting) {
            super(text, formatting);
        }
    }

    static {
        ReflectionUtils.loadClassesByReflection(new Class[]{PersistantVault.class});
        FKMod.LOGGER.info(PersistantVault.class.getCanonicalName() + " loaded successfully");
    }

    public enum Mode {
        BATTLE_ROYAL,
        NORMAL
    }

    private final VaultFeatureConfig config = Configs.VAULT_FEATURE_CONFIG.data;
    private final PersistantVaultData persistantVaultData = PersistantVault.DATA.data;
    private final MinecraftServer server;
    private final FKGame fkGame;
    private final Map<FKTeam, Capture> captureMap;

    public VaultFeature(MinecraftServer server, FKGame fkGame) {
        this.server = server;
        this.fkGame = fkGame;
        captureMap = new HashMap<>();

        if (!config.isEnabled()) return;

        registerEvents();
    }

    private void registerEvents() {
        UseBlockCallback.EVENT.register(FKGame.FKGameEvents.FIRST, this::updatePlayerVaultDimension);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server1) -> {
            var player = handler.getPlayer();
            if (whereIsThePlayer(player) != INSIDE_THE_VAULT_OF_AN_ENEMY_BASE) return;
            captureMap.forEach((fkteam, capture) -> {
                if (fkteam.getPlayers().stream().anyMatch(name -> player.getName().getString().equals(name))) {
                    capture.leave(player);
                }
            });
        });
        PlayerMoveCallback.EVENT.register((moveData, player) -> {
            // We only need to check if a player is capturing a vault, and maybe he goes out
            if (captureMap.values().stream().noneMatch(capture -> capture.playerAttackers.contains(player)))
                return ActionResult.PASS;

            if (whereIsThePlayer(player) == INSIDE_THE_VAULT_OF_AN_ENEMY_BASE) return ActionResult.PASS;
            manageCapture(player);
            return ActionResult.PASS;
        });
    }

    /**
     * If a player leave or disconnect while in a vault, if he was the last one of her team inside, the capture is cancelled
     */
    private void manageCapture(ServerPlayerEntity player) {
        FKTeam toRemove = null;
        for (var entry : captureMap.entrySet()) {
            var fkTeamAttacker = entry.getKey();
            var capture = entry.getValue();
            if (capture.playerAttackers.contains(player)) {
                if (capture.playerAttackers.size() == 1) {
                    capture.cancel(player);
                    toRemove = fkTeamAttacker;
                } else {
                    capture.leave(player);
                }
            }
        }
        if (toRemove != null)
            captureMap.remove(toRemove);
    }

    public void addCapture(Vault vault, FKTeam fkTeamVictim, FKTeam fkTeamAttacker, ServerPlayerEntity playerAttacker) {
        boolean shouldCapture = true;
        for (var entry : captureMap.entrySet()) {
            var fkTeamAttacker2 = entry.getKey();
            var capture = entry.getValue();
            if (capture.started.get()) {
                if (fkTeamAttacker2 != fkTeamAttacker) {
                    Msg.ANOTHER_TEAM_ALREADY_CAPTURING_THIS_VAULT.send(playerAttacker);
                } else {
                    if (!capture.playerAttackers.contains(playerAttacker)) {
                        capture.playerAttackers.add(playerAttacker);
                        playerAttacker.sendMessage(Text.literal("You're now " + capture.playerAttackers.size() + " players capturing this vault").setStyle(Style.EMPTY.withColor(GREEN)), false);
                    } else {
                        Msg.YOU_ARE_ALREADY_CAPTURING_THIS_VAULT.send(playerAttacker);
                    }
                }
                shouldCapture = false;
            }
        }

        // Here we start a new capture
        if (shouldCapture) {
            playerAttacker.sendMessage(Text.literal("The capture of the vault has started").setStyle(Style.EMPTY.withColor(GREEN)), false);
            captureMap.putIfAbsent(fkTeamAttacker, new Capture(vault, fkTeamVictim, playerAttacker));
        }
    }

    public Where whereIsThePlayer(ServerPlayerEntity player) {
        var where = GameUtils.whereIsThePlayer(player, new Vec3d(player.getX(), player.getY(), player.getZ()), w -> w);
        if (where.getRoot() == INSIDE_AN_ENEMY_BASE && where.getNested() != null && where.getNested() == INSIDE_THE_VAULT_OF_AN_ENEMY_BASE)
            return where.getNested();
        return where.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    private ActionResult updatePlayerVaultDimension(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        var itemInHand = player.getStackInHand(hand);

        if (!GameUtils.isFKPlayer(player.getName().getString())) {
            player.sendMessage(Text.literal("You cannot use this feature, because you're not a FKPlayer").setStyle(Style.EMPTY.withColor(GOLD)), false);
            return ActionResult.PASS;
        }

        if (itemInHand.hasCustomName()) {
            if (itemInHand.getName().getString().equals("marker")) {
                var valid = false;
                var fkTeam = GameUtils.getFKTeamOfPlayerByName(player.getName().getString());
                var fkTeamId = GameUtils.getFKTeamIdentifierByName(fkTeam.getName());

                var optVault = persistantVaultData.getVaults().stream().filter(vault -> vault.getTeamId().equals(fkTeamId)).findFirst();
                if (optVault.isEmpty()) {
                    var blockPos = new BlockPos[2];
                    blockPos[0] = BlockPos.of(hitResult.getBlockPos());
                    persistantVaultData.getVaults().add(new Vault(false, fkTeamId, blockPos));
                    player.sendMessage(Text.of("first position set: " + hitResult.getBlockPos().toString()), false);
                    return ActionResult.PASS;
                }

                var blockPos = optVault.get().getBlockPos();

                if (blockPos[1] == null) {
                    blockPos[1] = BlockPos.of(hitResult.getBlockPos());
                    player.sendMessage(Text.of("second position set: " + hitResult.getBlockPos().toString()), false);
                    valid = validateVault(blockPos[0], blockPos[1], player);
                    if (valid) optVault.get().setValid(true);
                } else {
                    blockPos[1] = null;
                    blockPos[0] = BlockPos.of(hitResult.getBlockPos());
                    player.sendMessage(Text.of("first position set: " + hitResult.getBlockPos().toString()), false);
                }

                if (valid) {
                    try {
                        PersistantVault.DATA.jsonManager.save(persistantVaultData);
                    } catch (IOException e) {
                        FKMod.LOGGER.error("An error occurred when trying to save vault");
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    @SuppressWarnings("ConstantConditions")
    private boolean validateVault(BlockPos pos1, BlockPos pos2, PlayerEntity player) {
        var box = new Box(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());

        var minY = Math.min(pos1.getY(), pos2.getY());
        var fkTeam = GameUtils.getFKTeamOfPlayerByName(player.getName().getString());
        var base = fkTeam.getBase();

        var minYRules = base.getCube().getY() - config.getMaximumNumberOfBlocksDown();

        var valid = false;

        if (MathUtils.isAPosInsideCube(base.getCube(), new Vec3d(pos1.getX(), pos1.getY(), pos1.getZ())) && MathUtils.isAPosInsideCube(base.getCube(), new Vec3d(pos2.getX(), pos2.getY(), pos2.getZ()))) {
            if (minY < minYRules) {
                Msg.VAULT_IS_TOO_DEEP.formatted(config.getMaximumNumberOfBlocksDown(), base.getCube().getX(), base.getCube().getY(), base.getCube().getZ()).send(player);
            } else {
                Msg.VAULT_SET.send(player);
                valid = true;
            }
        } else {
            Msg.CANNOT_SET_VAULT_OUTSIDE_BASE.send(player);
        }


        var width = Math.min(box.maxX - box.minX, box.maxZ - box.minZ);
        var length = Math.max(box.maxX - box.minX, box.maxZ - box.minZ);
        var height = box.maxY - box.minY;

        if (width < config.getMinWidth())
            Msg.VAULT_WIDTH_TOO_SMALL.formatted(config.getMinWidth()).send(player);

        if (length < config.getMinLength())
            Msg.VAULT_LENGTH_TOO_SMALL.formatted(config.getMinLength()).send(player);

        if (height < config.getMinHeight())
            Msg.VAULT_HEIGHT_TOO_SMALL.formatted(config.getMinHeight()).send(player);

        return valid;
    }

    public class Capture {

        private final Vault vault;
        private final FKTeam fkTeamVictim, fkTeamAttacker;
        private final List<ServerPlayerEntity> playerAttackers;
        private final AtomicBoolean started = new AtomicBoolean(false);
        private final AtomicBoolean cancelled = new AtomicBoolean(false);
        private final AtomicBoolean win = new AtomicBoolean(false);
        private final AtomicInteger captureTime = new AtomicInteger(0);

        public Capture(Vault vault, FKTeam fkTeamVictim, ServerPlayerEntity playerAttacker) {
            this.vault = vault;
            this.fkTeamVictim = fkTeamVictim;

            playerAttackers = new ArrayList<>(List.of(playerAttacker));
            fkTeamAttacker = GameUtils.getFKTeamOfPlayerByName(playerAttacker.getName().getString());

            startCapture(playerAttacker);
        }

        public void startCapture(ServerPlayerEntity playerAttacker) {
            started.set(true);

            // Send a message to all victims to tell them that their base is being captured
            GameUtils.getPlayersFromNames(server.getPlayerManager(), fkTeamVictim.getPlayers()).forEach(player -> Msg.CAPTURE1.formatted(playerAttacker.getName().getString(), fkTeamAttacker.getName()).send(player));
            // Send a message to all allies of the attacker to tell them that the attacker is capturing a vault
            GameUtils.getPlayersFromNames(server.getPlayerManager(), fkTeamAttacker.getPlayers()).forEach(player -> Msg.CAPTURE2.formatted(playerAttacker.getName().getString(), fkTeamVictim.getName()));

            ServerTickEvents.END_SERVER_TICK.register(server -> {
                if (cancelled.get() || win.get()) return;
                if (captureTime.get() >= 1200) win(server);

                var second = captureTime.get() / 20;
                playerAttackers.forEach(player -> player.sendMessage(Text.literal("%d seconds left before you finish the capture".formatted(second)).setStyle(Style.EMPTY.withColor(GOLD)), true));

                captureTime.getAndIncrement();
            });
        }

        public void cancel(ServerPlayerEntity player) {
            cancelled.set(true);
            playerAttackers.clear();
            Msg.CANCELLED.send(player);
        }

        private void leave(ServerPlayerEntity player) {
            playerAttackers.remove(player);
            Msg.LEFT.send(player);
        }

        private void win(MinecraftServer server) {
            win.set(true);

            if (Configs.VAULT_FEATURE_CONFIG.data.getMode() == Mode.NORMAL) {
                server.getPlayerManager().broadcast(Msg.GAME_IS_OVER.text(), MessageType.CHAT);
                server.getPlayerManager().broadcast(Msg.WINNER.formatted(fkTeamAttacker.getName(), fkTeamVictim.getName()).text(), MessageType.CHAT);
                PersistantFKGame.FK_GAME_DATA.data.setGameState(FKMod.GameState.FINISHED);
                GameUtils.getAllConnectedFKPlayers(server.getPlayerManager().getPlayerList()).forEach(fkGame::updateSidebar);
            } else if (Configs.VAULT_FEATURE_CONFIG.data.getMode() == Mode.BATTLE_ROYAL) {
                Msg.NEW_TEAM_ELIMINATE.formatted(fkTeamVictim.getName(), fkTeamAttacker.getName()).broadcast(server.getPlayerManager());
                for (var serverPlayerEntity : GameUtils.getAllFKPlayerOfFKTeam(server.getPlayerManager(), fkTeamVictim)) {
                    serverPlayerEntity.changeGameMode(GameMode.SPECTATOR);
                    Msg.YOU_ARE_NOW_SPECTATOR.send(serverPlayerEntity);
                }
            }

            captureMap.remove(fkTeamAttacker);
            persistantVaultData.getEliminatedTeams().putIfAbsent(GameUtils.getFKTeamIdentifierByName(fkTeamVictim.getName()), GameUtils.getFKTeamIdentifierByName(fkTeamAttacker.getName()));
            try {
                PersistantVault.DATA.jsonManager.save(persistantVaultData);
            } catch (IOException ignored) {
            }
        }

    }

}
