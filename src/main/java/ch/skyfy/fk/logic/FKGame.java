package ch.skyfy.fk.logic;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.ScoreboardManager;
import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.events.*;
import ch.skyfy.fk.logic.data.FKGameAllData;
import lombok.Getter;
import me.bymartrixx.playerevents.api.event.PlayerJoinCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TntBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("FieldCanBeLocal")
public class FKGame {

    private final MinecraftServer server;

    @Getter
    private final Timeline timeline;

    private final PauseEvents pauseEvents;

    private final FKGameEvents fkGameEvents;

    public FKGame(MinecraftServer server, ServerPlayerEntity firstPlayerToJoin) {
        this.server = server;
        this.timeline = new Timeline();
        pauseEvents = new PauseEvents();
        fkGameEvents = new FKGameEvents();

        initialize(firstPlayerToJoin);

        setWorldSpawn();
        registerEvents();
    }

    private void initialize(ServerPlayerEntity firstPlayerToJoin) {
        if (GameUtils.isGameStateRUNNING())
            FKGameAllData.FK_GAME_DATA.config.setGameState(FKMod.GameState.PAUSED);
        update(firstPlayerToJoin);
        teleportPlayerToWaitingRoom(firstPlayerToJoin);
        setupWorldBorder();
    }

    @SuppressWarnings("ConstantConditions")
    public void start() {
        server.getOverworld().setTimeOfDay(0);
        timeline.startTimer();

        // Send a message to all fk player to tell them where their respective base is
        for (ServerPlayerEntity fkPlayer : GameUtils.getAllConnectedFKPlayers(server.getPlayerManager().getPlayerList())) {
            var fkTeam = GameUtils.getFKTeamOfPlayerByName(fkPlayer.getName().asString());
            var base = fkTeam.getBase();

            if (Configs.FK_CONFIG.config.isShouldTeleportPlayersToTheirOwnBaseWhenGameIsStarted()) {
                var spawnLoc = base.getSpawnLocation();
                var optServerWorld = GameUtils.getServerWorldByIdentifier(server, spawnLoc.getDimensionName());
                optServerWorld.ifPresent(serverWorld -> fkPlayer.teleport(serverWorld, spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ(), spawnLoc.getYaw(), spawnLoc.getPitch()));
            } else {
                var baseLoc = new BlockPos(base.getCube().getX(), base.getCube().getY(), base.getCube().getZ());
                var message = new LiteralText("Your base is at this coords: X: " + baseLoc.getX() + " Y: " + baseLoc.getY() + " Z: " + baseLoc.getZ())
                        .setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE));
                fkPlayer.sendMessage(message, false);
            }
        }
    }

    public void pause() {
    }

    public void resume() {
        // If the timeline wasn't started (in the case of a server restart with gamestate at PAUSE OR RUNNING)
        if (!timeline.getIsTimerStartedRef().get())
            timeline.startTimer();
    }

    private void update(ServerPlayerEntity player) {
        if (GameUtils.isFKPlayer(player.getName().asString())) {
            updateTeam(player);
        }
        updateSidebar(player);
    }

    private void updateTeam(ServerPlayerEntity player) {
        var playerName = player.getName().asString();

        var serverScoreboard = server.getScoreboard();

        var fkTeam = GameUtils.getFKTeamOfPlayerByName(playerName);
        if (fkTeam == null) return;

        var team = serverScoreboard.getTeam(fkTeam.getName());
        if (team == null) { // Create a new team
            team = serverScoreboard.addTeam(fkTeam.getName().replaceAll("[^a-zA-Z\\d]", "")); // minecraft internal team name can't have space or special char
            team.setDisplayName(new LiteralText(fkTeam.getName()).setStyle(Style.EMPTY.withColor(Formatting.byName(fkTeam.getColor()))));
            team.setColor(Formatting.byName(fkTeam.getColor()));
        }

        var playerTeam = serverScoreboard.getPlayerTeam(playerName);
        if (playerTeam == null) { // Player has no team
            serverScoreboard.addPlayerToTeam(playerName, team);
        }

        serverScoreboard.updateScoreboardTeamAndPlayers(team);
        serverScoreboard.updateScoreboardTeam(team);

    }

    private void updateSidebar(ServerPlayerEntity player) {
        var timelineData = timeline.getTimelineData();
        ScoreboardManager.getInstance().updateSidebar(player, timelineData.getDay(), timelineData.getMinutes(), timelineData.getSeconds());
    }

    private void teleportPlayerToWaitingRoom(ServerPlayerEntity player) {
        if (GameUtils.isGameStateNOT_STARTED()) {
            var spawnLoc = Configs.FK_CONFIG.config.getWaitingRoom().getSpawnLocation();
            GameUtils.getServerWorldByIdentifier(server, spawnLoc.getDimensionName()).ifPresent(serverWorld -> player.teleport(serverWorld, spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ(), spawnLoc.getYaw(), spawnLoc.getPitch()));
        }
    }

    private void setupWorldBorder() {
        var worldBorderCube = Configs.WORLD_CONFIG.config.getWorldBorderData().getCube();
        server.getOverworld().getWorldBorder().setSize(worldBorderCube.getSize() * 2);
        server.getOverworld().getWorldBorder().setCenter(worldBorderCube.getX(), worldBorderCube.getZ());
        server.getOverworld().getWorldBorder().tick();
    }

    private void setWorldSpawn() {
        var spawnLocation = Configs.FK_CONFIG.config.getWorldSpawn();
        server.getOverworld().setSpawnPos(new BlockPos(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ()), 1.0f);
    }

    private void registerEvents() {
        // Event use when the game state is "running"
        PlayerBlockBreakEvents.BEFORE.register(fkGameEvents::cancelPlayerFromBreakingBlocks);
        UseBlockCallback.EVENT.register(fkGameEvents::cancelPlayerFromPlacingBlocks);
        BucketFillCallback.EVENT.register(fkGameEvents::cancelPlayerFromFillingABucket);
        BucketEmptyCallback.EVENT.register(fkGameEvents::cancelPlayerFromEmptyingABucket);
        UseBlockCallback.EVENT.register(fkGameEvents::cancelPlayerFromFiringATNT);
        AttackEntityCallback.EVENT.register(fkGameEvents::cancelPlayerPvP);
        PlayerEnterPortalCallback.EVENT.register(fkGameEvents::cancelPlayerFromEnteringInPortal);
        PlayerMoveCallback.EVENT.register(fkGameEvents::onPlayerMove);
        PlayerDamageCallback.EVENT.register(fkGameEvents::onPlayerDamage);
        PlayerHungerCallback.EVENT.register(fkGameEvents::onPlayerHungerUpdate);
        PlayerJoinCallback.EVENT.register(fkGameEvents::onPlayerJoin);
        EntitySpawnCallback.EVENT.register(fkGameEvents::onEntitySpawn);
        ItemDespawnCallback.EVENT.register(fkGameEvents::onItemDespawn);

        // Event use when the game state is "pause"
        EntityMoveCallback.EVENT.register(pauseEvents::stopEntitiesFromMoving);
        TimeOfDayUpdatedCallback.EVENT.register(pauseEvents::cancelTimeOfDayToBeingUpdated);
    }

    /**
     * This class contains events that will be used when the game state is "RUNNING
     */
    public class FKGameEvents {

        @SuppressWarnings({"RedundantIfStatement"})
        private boolean cancelPlayerFromBreakingBlocks(World world, PlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity blockEntity) {
            if (player.hasPermissionLevel(4)) return true;

            if (!GameUtils.isGameStateRUNNING()) return false;

            var breakPlace = (GameUtils.WhereIsThePlayer<Boolean>) (isPlayerInHisOwnBase, isPlayerInAnEnemyBase, isPlayerCloseToHisOwnBase, isPlayerCloseToAnEnemyBase) -> {
                var block = world.getBlockState(pos).getBlock();

                // If the player is inside an enemy base
                if (isPlayerInAnEnemyBase) {
                    if (block != Blocks.TNT) { // We cancel the block that had to be broken except if it is TNT
                        return false;
                    }
                    return true;
                }

                if (isPlayerInHisOwnBase) {
                    return true;
                }

                if (isPlayerCloseToHisOwnBase) {
                    return true;
                }

                // In the rules of this FK, players can break blocks outside their base. Except near an enemy base
                if (isPlayerCloseToAnEnemyBase) {
                    if (block != Blocks.TNT) { // We cancel the block that had to be broken except if it is TNT
                        return false;
                    }
                    return true;
                }

                // Player is in the wild

                return true;
            };

            return GameUtils.whereIsThePlayer(player, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), breakPlace);

        }

        private ActionResult cancelPlayerFromPlacingBlocks(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS;

            if (!GameUtils.isGameStateRUNNING()) return ActionResult.FAIL;

            var placeBlock = (GameUtils.WhereIsThePlayer<ActionResult>) (isPlayerInHisOwnBase, isPlayerInAnEnemyBase, isPlayerCloseToHisOwnBase, isPlayerCloseToAnEnemyBase) -> {

                var placedItemStack = player.getStackInHand(player.getActiveHand());

                // If the player is inside an enemy base
                if (isPlayerInAnEnemyBase) {
                    if (!placedItemStack.isOf(Items.TNT))
                        return ActionResult.FAIL;
                    if (!GameUtils.areAssaultEnabled(timeline.getTimelineData().getDay()))
                        return ActionResult.FAIL;
                    return ActionResult.PASS;
                }

                if (isPlayerInHisOwnBase) {
                    return ActionResult.PASS;
                }

                if (isPlayerCloseToHisOwnBase) {
                    return ActionResult.FAIL;
                }

                // A player can place blocks outside his base, except if it is near another base (except TNT)
                if (isPlayerCloseToAnEnemyBase) {
                    if (!placedItemStack.isOf(Items.TNT))
                        return ActionResult.FAIL;
                    if (!GameUtils.areAssaultEnabled(timeline.getTimelineData().getDay()))
                        return ActionResult.FAIL;
                    return ActionResult.PASS;
                }

                // Player is in the wild

                return ActionResult.PASS;
            };

            var blockPos = hitResult.getBlockPos();
            return GameUtils.whereIsThePlayer(player, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), placeBlock);
        }

        private TypedActionResult<ItemStack> cancelPlayerFromFillingABucket(World world, PlayerEntity player, Hand hand, Fluid fillFluid, BucketItem bucketItem, BlockHitResult blockHitResult) {
            if (player.hasPermissionLevel(4)) return TypedActionResult.pass(player.getStackInHand(hand));

            if (!GameUtils.isGameStateRUNNING()) return TypedActionResult.fail(player.getStackInHand(hand));

            var fillBucketImpl = (GameUtils.WhereIsThePlayer<TypedActionResult<ItemStack>>) (isPlayerInHisOwnBase, isPlayerInAnEnemyBase, isPlayerCloseToHisOwnBase, isPlayerCloseToAnEnemyBase) -> {

                var placedItemStack = player.getStackInHand(player.getActiveHand());

                // If the player is inside an enemy base
                if (isPlayerInAnEnemyBase) {
                    // Player cannot fill a bucket with water or lava inside an enemy base
                    if (fillFluid instanceof LavaFluid || fillFluid instanceof WaterFluid) {
                        return TypedActionResult.fail(placedItemStack);
                    }
                    return TypedActionResult.pass(placedItemStack);
                }

                if (isPlayerInHisOwnBase) {
                    return TypedActionResult.pass(placedItemStack);
                }

                if (isPlayerCloseToHisOwnBase) {
                    return TypedActionResult.pass(placedItemStack);
                }

                // A player can fill bucket outside his base, except if it is near another base
                if (isPlayerCloseToAnEnemyBase) {
                    if (fillFluid instanceof LavaFluid || fillFluid instanceof WaterFluid) {
                        return TypedActionResult.fail(placedItemStack);
                    }
                    return TypedActionResult.pass(placedItemStack);
                }

                // Player is in the wild

                return TypedActionResult.pass(placedItemStack);
            };

            var blockPos = blockHitResult.getBlockPos();
            return GameUtils.whereIsThePlayer(player, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), fillBucketImpl);
        }

        private TypedActionResult<ItemStack> cancelPlayerFromEmptyingABucket(World world, PlayerEntity player, Hand hand, Fluid emptyFluid, BucketItem bucketItem, BlockHitResult blockHitResult) {
            if (player.hasPermissionLevel(4)) return TypedActionResult.pass(player.getStackInHand(hand));

            if (!GameUtils.isGameStateRUNNING()) return TypedActionResult.fail(player.getStackInHand(hand));

            var emptyBucketImpl = (GameUtils.WhereIsThePlayer<TypedActionResult<ItemStack>>) (isPlayerInHisOwnBase, isPlayerInAnEnemyBase, isPlayerCloseToHisOwnBase, isPlayerCloseToAnEnemyBase) -> {

                var placedItemStack = player.getStackInHand(player.getActiveHand());

                // If the player is inside an enemy base
                if (isPlayerInAnEnemyBase) {
                    // Player cannot empty a bucket with water or lava inside an enemy base
                    if (emptyFluid instanceof LavaFluid || emptyFluid instanceof WaterFluid) {
                        return TypedActionResult.fail(placedItemStack);
                    }
                    return TypedActionResult.pass(placedItemStack);
                }

                if (isPlayerInHisOwnBase) {
                    return TypedActionResult.pass(placedItemStack);
                }

                if (isPlayerCloseToHisOwnBase) {
                    return TypedActionResult.fail(placedItemStack);
                }

                // A player can empty bucket outside his base, except if it is near another base
                if (isPlayerCloseToAnEnemyBase) {
                    if (emptyFluid instanceof LavaFluid || emptyFluid instanceof WaterFluid) {
                        return TypedActionResult.fail(placedItemStack);
                    }
                    return TypedActionResult.pass(placedItemStack);
                }

                // Player is in the wild

                return TypedActionResult.pass(placedItemStack);
            };

            var blockPos = blockHitResult.getBlockPos();
            return GameUtils.whereIsThePlayer(player, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), emptyBucketImpl);
        }

        private ActionResult cancelPlayerFromFiringATNT(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS;

            if (!GameUtils.isGameStateRUNNING()) return ActionResult.FAIL;

            var emptyBucketImpl = (GameUtils.WhereIsThePlayer<ActionResult>) (isPlayerInHisOwnBase, isPlayerInAnEnemyBase, isPlayerCloseToHisOwnBase, isPlayerCloseToAnEnemyBase) -> {

                var stackInHand = player.getStackInHand(hand);

                var didPlayerTryToFireATNT = false;

                if (stackInHand.isOf(Items.FLINT_AND_STEEL)) {
                    var block = world.getBlockState(hitResult.getBlockPos()).getBlock();
                    if (block instanceof TntBlock)
                        didPlayerTryToFireATNT = true;
                }

                // If the player is inside an enemy base
                if (isPlayerInAnEnemyBase) {
                    if (didPlayerTryToFireATNT) {
                        if (!GameUtils.areAssaultEnabled(timeline.getTimelineData().getDay())) {
                            return ActionResult.FAIL;
                        }
                    }
                    return ActionResult.PASS;
                }

                if (isPlayerInHisOwnBase) {
                    return ActionResult.PASS;
                }

                if (isPlayerCloseToHisOwnBase) {
                    return ActionResult.PASS;
                }

                // A player can empty bucket outside his base, except if it is near another base
                if (isPlayerCloseToAnEnemyBase) {
                    if (didPlayerTryToFireATNT) {
                        if (!GameUtils.areAssaultEnabled(timeline.getTimelineData().getDay())) {
                            return ActionResult.FAIL;
                        }
                    }
                    return ActionResult.PASS;
                }

                // Player is in the wild

                return ActionResult.PASS;
            };

            var blockPos = hitResult.getBlockPos();
            return GameUtils.whereIsThePlayer(player, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), emptyBucketImpl);
        }

        private ActionResult cancelPlayerPvP(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS;

            if (!GameUtils.isGameStateRUNNING()) return ActionResult.FAIL;

            if (entity instanceof PlayerEntity)
                if (!GameUtils.isPvPEnabled(timeline.getTimelineData().getDay()))
                    return ActionResult.FAIL;
            return ActionResult.PASS;
        }

        private ActionResult cancelPlayerFromEnteringInPortal(ServerPlayerEntity player, Identifier dimensionId) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS;

            if (!GameUtils.isGameStateRUNNING()) return ActionResult.FAIL;

            if (dimensionId == DimensionType.THE_NETHER_ID) {
                if (!GameUtils.isNetherEnabled(timeline.getTimelineData().getDay()))
                    return ActionResult.FAIL;
            } else if (dimensionId == DimensionType.THE_END_ID) {
                if (!GameUtils.isEndEnabled(timeline.getTimelineData().getDay()))
                    return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        }

        private ActionResult onPlayerMove(PlayerMoveCallback.MoveData moveData, ServerPlayerEntity player) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS; // OP Player can move anymore

            if (GameUtils.isGameStateNOT_STARTED()) {
                var waitingRoom = Configs.FK_CONFIG.config.getWaitingRoom();
                if (Utils.cancelPlayerFromLeavingAnArea(waitingRoom.getCube(), player, waitingRoom.getSpawnLocation()))
                    return ActionResult.FAIL;
                return ActionResult.PASS;
            }

            // Cancel player from moving.
            if (GameUtils.isGameState_PAUSED())
                return ActionResult.FAIL;

            // Cancel the player from going too far into the map
            if (Utils.cancelPlayerFromLeavingAnArea(Configs.WORLD_CONFIG.config.getWorldBorderData().getCube(), player, null)) {
                player.sendMessage(new LiteralText("You reach the border limit !").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        }

        private ActionResult onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS;

            if (GameUtils.isGameState_PAUSED() || GameUtils.isGameStateNOT_STARTED())
                return ActionResult.FAIL;
            return ActionResult.PASS;
        }

        private ActionResult onPlayerHungerUpdate(PlayerEntity player) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS;

            if (GameUtils.isGameStateNOT_STARTED() || GameUtils.isGameState_PAUSED()) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        }

        private ActionResult onEntitySpawn(Entity entity) {
            if (entity instanceof MobEntity && GameUtils.isGameState_PAUSED())
                return ActionResult.FAIL;
            return ActionResult.PASS;
        }

        private ActionResult onItemDespawn(ItemEntity itemEntity) {
            if (!GameUtils.isGameState_PAUSED()) return ActionResult.PASS;
            return ActionResult.FAIL;
        }

        private void onPlayerJoin(ServerPlayerEntity player, MinecraftServer server) {
            teleportPlayerToWaitingRoom(player);
            update(player);
        }

    }

    static class PauseEvents {

        private ActionResult stopEntitiesFromMoving(Entity entity, MovementType movementType, Vec3d movement) {
            if (!GameUtils.isGameState_PAUSED()) return ActionResult.PASS;
            return ActionResult.FAIL;
        }

        private ActionResult cancelTimeOfDayToBeingUpdated(long time) {
            if (!GameUtils.isGameState_PAUSED()) return ActionResult.PASS;
            return ActionResult.FAIL;
        }

    }

}
