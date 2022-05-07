package ch.skyfy.fk.features;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.config.data.FKTeam;
import ch.skyfy.fk.config.features.ChestRoomFeatureConfig;
import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.events.PlayerMoveCallback;
import ch.skyfy.fk.features.data.BlockPos;
import ch.skyfy.fk.features.data.Vault;
import ch.skyfy.fk.features.data.VaultConstant;
import ch.skyfy.fk.features.data.Vaults;
import ch.skyfy.fk.logic.FKGame;
import ch.skyfy.fk.logic.GameUtils;
import ch.skyfy.fk.logic.Utils;
import ch.skyfy.fk.utils.ReflectionUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.skyfy.fk.constants.Where.INSIDE_THE_VAULT_OF_AN_ENEMY_BASE;
import static net.minecraft.util.Util.NIL_UUID;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class ChestRoomFeature {

    static {
        ReflectionUtils.loadClassesByReflection(new Class[]{VaultConstant.class});
        System.out.println("VaultData LOADED WITH NO ERROR");
    }

    private final ChestRoomFeatureConfig config = Configs.CHEST_ROOM_CONFIG.config;

    private final Vaults vaults = VaultConstant.VAULTS.config;

    private final Map<FKTeam, Capture> captureMap;

    public ChestRoomFeature() {

        captureMap = new HashMap<>();

        if (!config.isEnabled()) return;

        registerEvents();

        // TODO Chest Room detection, eliminate if none chest room built when assault day is enabled, create chest room
    }

    private void registerEvents() {
        UseBlockCallback.EVENT.register(FKGame.FKGameEvents.FIRST, this::updatePlayerVaultDimension);
        PlayerMoveCallback.EVENT.register((moveData, player) -> {
            if (whereIsThePlayer(player) == INSIDE_THE_VAULT_OF_AN_ENEMY_BASE) return ActionResult.PASS;

            // Check if player leave a vault while capturing
            FKTeam toRemove = null;
            for (Map.Entry<FKTeam, Capture> entry : captureMap.entrySet()) {
                var fkTeamAttacker = entry.getKey();
                var capture = entry.getValue();

                if (capture.playerAttackers.contains(player)) {
                    if (capture.playerAttackers.size() == 1) {
                        capture.cancel(player);
                        toRemove = fkTeamAttacker;
                    } else
                        capture.leave(player);
                }
            }

            if (toRemove != null) {
                captureMap.remove(toRemove);
            }

            return ActionResult.PASS;
        });
    }

    public void addCapture(Vault vault, FKTeam fkTeamVictim, FKTeam fkTeamAttacker, ServerPlayerEntity playerAttacker) {

        for (var entry : captureMap.entrySet()) {
            var fkTeamAttacker2 = entry.getKey();
            var capture = entry.getValue();
            if (capture.started.get()) {
                if (fkTeamAttacker2 != fkTeamAttacker) {
                    playerAttacker.sendMessage(new LiteralText("Another is already capturing this vault").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
                } else {
                    if (!capture.playerAttackers.contains(playerAttacker))
                        capture.playerAttackers.add(playerAttacker);
                    else
                        playerAttacker.sendMessage(new LiteralText("You are already capturing this vault").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
                }
            }

        }
        captureMap.putIfAbsent(fkTeamAttacker, new Capture(vault, fkTeamVictim, playerAttacker, System.currentTimeMillis()));
    }

    public Where whereIsThePlayer(ServerPlayerEntity player) {
        var where = GameUtils.whereIsThePlayer(player, new Vec3d(player.getX(), player.getY(), player.getZ()), w -> w);
        var playerPos = new Vec3d(player.getX(), player.getY(), player.getZ());
        Box box;
        for (var vault : VaultConstant.VAULTS.config.getVaults()) {
            if (vault.getBlockPos()[0] == null || vault.getBlockPos()[1] == null) continue;
            box = BlockPos.toBox(vault.getBlockPos());
            switch (where) {
                case INSIDE_HIS_OWN_BASE -> {
                    if (box.contains(playerPos))
                        return Where.INSIDE_THE_VAULT_OF_HIS_OWN_BASE;
                }
                case INSIDE_AN_ENEMY_BASE -> {
                    if (box.contains(playerPos))
                        return INSIDE_THE_VAULT_OF_AN_ENEMY_BASE;
                }
            }
        }
        return where;
    }

    @SuppressWarnings("ConstantConditions")
    private ActionResult updatePlayerVaultDimension(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        var itemInHand = player.getStackInHand(hand);

        if (!GameUtils.isFKPlayer(player.getName().asString())) {
            player.sendMessage(new LiteralText("You cannot use this feature, because you're not a FKPlayer").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
            return ActionResult.PASS;
        }

        if (itemInHand.hasCustomName()) {
            if (itemInHand.getName().asString().equals("marker")) {
                var valid = false;
                var fkTeam = GameUtils.getFKTeamOfPlayerByName(player.getName().asString());
                var fkTeamId = GameUtils.getFKTeamIdentifierByName(fkTeam.getName());

                var optVault = vaults.getVaults().stream().filter(vault -> vault.getTeamId().equals(fkTeamId)).findFirst();
                if (optVault.isEmpty()) {
                    var blockPos = new BlockPos[2];
                    blockPos[0] = BlockPos.of(hitResult.getBlockPos());
                    vaults.getVaults().add(new Vault(false, fkTeamId, blockPos));
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
                        VaultConstant.VAULTS.jsonManager.save(vaults);
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

        var minY = Math.min(pos1.getX(), pos2.getY());
        var fkTeam = GameUtils.getFKTeamOfPlayerByName(player.getName().asString());
        var base = fkTeam.getBase();

        var minYRules = base.getCube().getY() - config.getMaximumNumberOfBlocksDown();

        var valid = false;

        if (Utils.isAPosInsideCube(base.getCube(), new Vec3d(pos1.getX(), pos1.getY(), pos1.getZ())) && Utils.isAPosInsideCube(base.getCube(), new Vec3d(pos2.getX(), pos2.getY(), pos2.getZ()))) {
            if (minY < minYRules) {
                var centerPoint = "x: " + base.getCube().getX() + " y: " + base.getCube().getY() + " z: " + base.getCube().getZ();
                var str = """
                        According to the rules, your vault must be located no more than %s blocks below the center point (%s) of your base
                        """.formatted(config.getMaximumNumberOfBlocksDown(), centerPoint);
                player.sendMessage(new LiteralText("Your vault doesn't respect rules").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
                player.sendMessage(new LiteralText(str).setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            } else {
                player.sendMessage(new LiteralText("Your vault has been set").setStyle(Style.EMPTY.withColor(Formatting.GREEN)), false);
                valid = true;
            }
        } else {
            player.sendMessage(new LiteralText("Your vault is outside your base").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
        }

        var width = Math.min(box.maxX - box.minX, box.maxZ - box.minZ);
        var length = Math.max(box.maxX - box.minX, box.maxZ - box.minZ);
        var height = box.maxY - box.minY;

        if (width < config.getMinWidth()) {
            player.sendMessage(new LiteralText("Width is too small").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
        }

        if (length < config.getMinLength()) {
            player.sendMessage(new LiteralText("Length is too small").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
        }

        if (height < config.getMinHeight()) {
            player.sendMessage(new LiteralText("Height is too small").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
        }

        return true;
    }

    public static class Capture {

        private final Vault vault;
        private final FKTeam fkTeamVictim;
        private final List<ServerPlayerEntity> playerAttackers;

        private final Long startTime;

        private final AtomicBoolean started = new AtomicBoolean(false);

        private final AtomicBoolean cancelled = new AtomicBoolean(false);
        private final AtomicBoolean win = new AtomicBoolean(false);

        private final AtomicInteger captureTime = new AtomicInteger(0);


        public Capture(Vault vault, FKTeam fkTeamVictim, ServerPlayerEntity playerAttacker, Long startTime) {
            this.vault = vault;
            this.fkTeamVictim = fkTeamVictim;
            this.startTime = startTime;

            playerAttackers = new ArrayList<>();
            playerAttackers.add(playerAttacker);

            playerAttacker.sendMessage(new LiteralText("Capture begin ...").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
            startCapture();
        }

        public void startCapture() {
            started.set(true);

            ServerTickEvents.END_SERVER_TICK.register(server -> {
                if(cancelled.get() || win.get())return;
                if(captureTime.get() >= 1200){
                    server.getPlayerManager().broadcast(new LiteralText("The Game Is End").setStyle(Style.EMPTY.withColor(Formatting.GREEN)), MessageType.CHAT, NIL_UUID);
                    server.getPlayerManager().broadcast(new LiteralText(fkTeamVictim.getName() + " has lost").setStyle(Style.EMPTY.withColor(Formatting.GREEN)), MessageType.CHAT, NIL_UUID);
                    win.set(true);
                }
                captureTime.getAndIncrement();
            });
        }

        public void cancel(ServerPlayerEntity player) {
            cancelled.set(true);
            player.sendMessage(new LiteralText("You was the last one to trying to capture the vault, but you go outside").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);
        }

        public void leave(ServerPlayerEntity player) {
            player.sendMessage(new LiteralText("You gone outside the vault, but some of your teammate still inside, so").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);

        }

    }

}
