package ch.skyfy.fk.logic;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.ScoreboardManager;
import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.constants.MsgBase;
import ch.skyfy.fk.events.*;
import ch.skyfy.fk.features.VaultFeature;
import ch.skyfy.fk.logic.data.FKGameAllData;
import ch.skyfy.fk.utils.ReflectionUtils;
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
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.Util.NIL_UUID;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class FKGame {

    private static class Msg extends MsgBase {

        public static Msg GAME_BEGINS = new Msg("The game is launched, good luck to all", Formatting.GREEN);
        public static Msg BASE_COORDINATES = new Msg("Here are the coordinates of your base : X:%d Y:%d Z:%d", Formatting.GREEN);
        public static Msg GAME_HAS_BEEN_PAUSED = new Msg("The game has been paused", Formatting.GOLD);
        public static Msg GAME_HAS_BEEN_RESUMED = new Msg("The game has been resumed", Formatting.GOLD);

        protected Msg(String text, Formatting formatting) {
            super(text, formatting);
        }
    }

    static {
        ReflectionUtils.loadClassesByReflection(new Class[]{FKGameAllData.class});
        FKMod.LOGGER.info(FKGameAllData.class.getCanonicalName() + " loaded successfully");
    }

    private final MinecraftServer server;

    @Getter
    private final Timeline timeline;

    private final PauseEvents pauseEvents;

    private final FKGameEvents fkGameEvents;

    @Getter
    private final VaultFeature vaultFeature;

    public FKGame(MinecraftServer server, ServerPlayerEntity firstPlayerToJoin) {
        this.server = server;
        this.timeline = new Timeline();
        pauseEvents = new PauseEvents();
        fkGameEvents = new FKGameEvents();

        vaultFeature = new VaultFeature(server);

        fkGameEvents.onPlayerJoin(firstPlayerToJoin, server); // Remind: when constructor is called, it's from a PlayerJoinCallback

        initialize();
        registerEvents();
    }

    private void initialize() {
        setWorldSpawn();
        setupWorldBorder();
    }

    @SuppressWarnings("ConstantConditions")
    public void start() {
        server.getOverworld().setTimeOfDay(0);
        timeline.startTimer();

        server.getPlayerManager().broadcast(Msg.GAME_BEGINS.text(), MessageType.CHAT, NIL_UUID);

        for (var fkPlayer : GameUtils.getAllConnectedFKPlayers(server.getPlayerManager().getPlayerList())) {
            var fkTeam = GameUtils.getFKTeamOfPlayerByName(fkPlayer.getName().asString());
            var base = fkTeam.getBase();
            fkPlayer.getInventory().clear();
            if (Configs.FK_CONFIG.data.isShouldTeleportPlayersToTheirOwnBaseWhenGameIsStarted()) {
                var spawnLoc = base.getSpawnLocation();
                var optServerWorld = GameUtils.getServerWorldByIdentifier(server, spawnLoc.getDimensionName());
                optServerWorld.ifPresent(serverWorld -> fkPlayer.teleport(serverWorld, spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ(), spawnLoc.getYaw(), spawnLoc.getPitch()));
            } else
                Msg.BASE_COORDINATES.formatted(base.getCube().getX(), base.getCube().getY(), base.getCube().getZ()).send(fkPlayer);
        }
    }

    public void pause() {
        FKGameAllData.FK_GAME_DATA.data.setGameState(FKMod.GameState.PAUSED);
        server.getPlayerManager().broadcast(Msg.GAME_HAS_BEEN_PAUSED.text(), MessageType.CHAT, NIL_UUID);
    }

    public void resume() {
        server.getPlayerManager().broadcast(Msg.GAME_HAS_BEEN_RESUMED.text(), MessageType.CHAT, NIL_UUID);

        // If the timeline wasn't started (in the case of a server restart with gamestate at PAUSE OR RUNNING)
        if (!timeline.getIsTimerStartedRef().get())
            timeline.startTimer();
    }

    private void update(ServerPlayerEntity player) {
        if (GameUtils.isFKPlayer(player.getName().asString()))
            updateTeam(player);
        updateSidebar(player);
    }

    private void updateTeam(ServerPlayerEntity player) {
        var playerName = player.getName().asString();

        var serverScoreboard = server.getScoreboard();

        var fkTeam = GameUtils.getFKTeamOfPlayerByName(playerName);
        if (fkTeam == null) return;

        var team = serverScoreboard.getTeam(fkTeam.getName());
        if (team == null) {
            team = serverScoreboard.addTeam(GameUtils.getFKTeamIdentifierByName(fkTeam.getName())); // minecraft internal team name can't have space or special char
            team.setDisplayName(new LiteralText(fkTeam.getName()).setStyle(Style.EMPTY.withColor(Formatting.byName(fkTeam.getColor()))));
            team.setColor(Formatting.byName(fkTeam.getColor()));
        }

        var playerTeam = serverScoreboard.getPlayerTeam(playerName);
        if (playerTeam == null)
            serverScoreboard.addPlayerToTeam(playerName, team);

        serverScoreboard.updateScoreboardTeamAndPlayers(team);
        serverScoreboard.updateScoreboardTeam(team);
    }

    private void updateSidebar(ServerPlayerEntity player) {
        var timelineData = timeline.getTimelineData();
        ScoreboardManager.getInstance().updateSidebar(player, timelineData.getDay(), timelineData.getMinutes(), timelineData.getSeconds());
    }

    private void teleportPlayerToWaitingRoom(ServerPlayerEntity player) {
        if (GameUtils.isGameState_NOT_STARTED()) {
            var spawnLoc = Configs.FK_CONFIG.data.getWaitingRoom().getSpawnLocation();
            GameUtils.getServerWorldByIdentifier(server, spawnLoc.getDimensionName()).ifPresent(serverWorld -> player.teleport(serverWorld, spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ(), spawnLoc.getYaw(), spawnLoc.getPitch()));
        }
    }

    private void setupWorldBorder() {
        var worldBorderCube = Configs.WORLD_CONFIG.data.getWorldBorderData().getCube();
        server.getOverworld().getWorldBorder().setSize(worldBorderCube.getSize() * 2);
        server.getOverworld().getWorldBorder().setCenter(worldBorderCube.getX(), worldBorderCube.getZ());
        server.getOverworld().getWorldBorder().tick();
    }

    private void setWorldSpawn() {
        var spawnLocation = Configs.FK_CONFIG.data.getWorldSpawn();
        server.getOverworld().setSpawnPos(new BlockPos(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ()), 1.0f);
    }

    private void registerEvents() {
        // Event use when the game state is "running"
        PlayerBlockBreakEvents.BEFORE.register(fkGameEvents::cancelPlayerFromBreakingBlocks);
        UseBlockCallback.EVENT.register(FKGameEvents.SECOND, fkGameEvents::cancelPlayerFromFiringATNT);
        UseBlockCallback.EVENT.register(FKGameEvents.SECOND, fkGameEvents::cancelPlayerFromPlacingBlocks);
        BucketFillCallback.EVENT.register(fkGameEvents::cancelPlayerFromFillingABucket);
        BucketEmptyCallback.EVENT.register(fkGameEvents::cancelPlayerFromEmptyingABucket);
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

        public static final Identifier FIRST = new Identifier("fabric", "first");
        public static final Identifier SECOND = new Identifier("fabric", "second");

        public FKGameEvents() {
            UseBlockCallback.EVENT.addPhaseOrdering(FIRST, SECOND);
        }

        private boolean cancelPlayerFromBreakingBlocks(World world, PlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity blockEntity) {
            if (player.hasPermissionLevel(4)) return true;

            if (!GameUtils.isGameState_RUNNING()) return false;

            var breakPlace = (GameUtils.WhereIsThePlayer<Boolean>) (where) -> {
                var block = world.getBlockState(pos).getBlock();
                return switch (where) {
                    case INSIDE_HIS_OWN_BASE -> true;
                    case CLOSE_TO_HIS_OWN_BASE -> true;
                    case INSIDE_AN_ENEMY_BASE, CLOSE_TO_AN_ENEMY_BASE ->
                            block == Blocks.TNT || block == Blocks.REDSTONE_TORCH || block == Blocks.LEVER;
                    case IN_THE_WILD -> true;
                    default -> true;
                };
            };
            return GameUtils.whereIsThePlayer(player, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), breakPlace);
        }

        private ActionResult cancelPlayerFromPlacingBlocks(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS;

            if (!GameUtils.isGameState_RUNNING()) return ActionResult.FAIL;

            var itemInHand = player.getStackInHand(player.getActiveHand());

            var placeBlock = (GameUtils.WhereIsThePlayer<ActionResult>) (where) -> {

                if (!Registry.BLOCK.containsId(Registry.ITEM.getId(itemInHand.getItem()))) // It's not a block
                    return ActionResult.PASS;

                return switch (where) {
                    case INSIDE_HIS_OWN_BASE -> ActionResult.PASS;
                    case CLOSE_TO_HIS_OWN_BASE -> ActionResult.FAIL;
                    case INSIDE_AN_ENEMY_BASE, CLOSE_TO_AN_ENEMY_BASE -> {
                        if (!GameUtils.areAssaultEnabled(timeline.getTimelineData().getDay()))
                            yield ActionResult.FAIL;
                        if (!itemInHand.isOf(Items.TNT) && !itemInHand.isOf(Items.LEVER) && !itemInHand.isOf(Items.REDSTONE_TORCH))
                            yield ActionResult.FAIL;
                        yield ActionResult.PASS;
                    }
                    case IN_THE_WILD -> ActionResult.PASS;
                    default -> ActionResult.PASS;
                };
            };

            var blockPos = hitResult.getBlockPos();
            return GameUtils.whereIsThePlayer(player, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), placeBlock);
        }

        private TypedActionResult<ItemStack> cancelPlayerFromFillingABucket(World world, PlayerEntity player, Hand hand, Fluid fillFluid, BucketItem bucketItem, BlockHitResult blockHitResult) {
            if (player.hasPermissionLevel(4)) return TypedActionResult.pass(player.getStackInHand(hand));

            if (!GameUtils.isGameState_RUNNING()) return TypedActionResult.fail(player.getStackInHand(hand));

            var fillBucketImpl = (GameUtils.WhereIsThePlayer<TypedActionResult<ItemStack>>) (where) -> {

                var placedItemStack = player.getStackInHand(player.getActiveHand());

                return switch (where) {
                    case INSIDE_HIS_OWN_BASE -> TypedActionResult.pass(placedItemStack);
                    case CLOSE_TO_HIS_OWN_BASE -> TypedActionResult.pass(placedItemStack);
                    case INSIDE_AN_ENEMY_BASE -> {
                        if (fillFluid instanceof LavaFluid || fillFluid instanceof WaterFluid)
                            yield TypedActionResult.fail(placedItemStack);
                        yield TypedActionResult.pass(placedItemStack);
                    }
                    case CLOSE_TO_AN_ENEMY_BASE -> {
                        if (fillFluid instanceof LavaFluid || fillFluid instanceof WaterFluid)
                            yield TypedActionResult.fail(placedItemStack);
                        yield TypedActionResult.pass(placedItemStack);
                    }
                    case IN_THE_WILD -> TypedActionResult.pass(placedItemStack);
                    default -> TypedActionResult.pass(placedItemStack);
                };
            };

            var blockPos = blockHitResult.getBlockPos();
            return GameUtils.whereIsThePlayer(player, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), fillBucketImpl);
        }

        private TypedActionResult<ItemStack> cancelPlayerFromEmptyingABucket(World world, PlayerEntity player, Hand hand, Fluid emptyFluid, BucketItem bucketItem, BlockHitResult blockHitResult) {
            if (player.hasPermissionLevel(4)) return TypedActionResult.pass(player.getStackInHand(hand));

            if (!GameUtils.isGameState_RUNNING()) return TypedActionResult.fail(player.getStackInHand(hand));

            var emptyBucketImpl = (GameUtils.WhereIsThePlayer<TypedActionResult<ItemStack>>) (where) -> {

                var placedItemStack = player.getStackInHand(player.getActiveHand());

                return switch (where) {
                    case INSIDE_HIS_OWN_BASE -> TypedActionResult.pass(placedItemStack);
                    case CLOSE_TO_HIS_OWN_BASE -> TypedActionResult.fail(placedItemStack);
                    case INSIDE_AN_ENEMY_BASE -> {
                        if (emptyFluid instanceof LavaFluid || emptyFluid instanceof WaterFluid)
                            yield TypedActionResult.fail(placedItemStack);
                        yield TypedActionResult.pass(placedItemStack);
                    }
                    case CLOSE_TO_AN_ENEMY_BASE -> {
                        if (emptyFluid instanceof LavaFluid || emptyFluid instanceof WaterFluid)
                            yield TypedActionResult.fail(placedItemStack);
                        yield TypedActionResult.pass(placedItemStack);
                    }
                    case IN_THE_WILD -> TypedActionResult.pass(placedItemStack);
                    default -> TypedActionResult.pass(placedItemStack);
                };
            };

            var blockPos = blockHitResult.getBlockPos();
            return GameUtils.whereIsThePlayer(player, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), emptyBucketImpl);
        }

        private ActionResult cancelPlayerFromFiringATNT(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS;

            if (!GameUtils.isGameState_RUNNING()) return ActionResult.FAIL;

            var itemInHand = player.getStackInHand(hand);

            var emptyBucketImpl = (GameUtils.WhereIsThePlayer<ActionResult>) (where) -> {

                if (itemInHand.isOf(Items.FLINT_AND_STEEL)) {
                    var block = world.getBlockState(hitResult.getBlockPos()).getBlock();
                    if (!(block instanceof TntBlock)) return ActionResult.PASS;
                }

                return switch (where) {
                    case INSIDE_HIS_OWN_BASE -> ActionResult.PASS;
                    case CLOSE_TO_HIS_OWN_BASE -> ActionResult.PASS;
                    case INSIDE_AN_ENEMY_BASE -> {
                        if (!GameUtils.areAssaultEnabled(timeline.getTimelineData().getDay()))
                            yield ActionResult.FAIL;
                        yield ActionResult.PASS;
                    }
                    case CLOSE_TO_AN_ENEMY_BASE -> {
                        if (!GameUtils.areAssaultEnabled(timeline.getTimelineData().getDay()))
                            yield ActionResult.FAIL;
                        yield ActionResult.PASS;
                    }
                    case IN_THE_WILD -> ActionResult.PASS;
                    default -> ActionResult.PASS;
                };
            };

            var blockPos = hitResult.getBlockPos();
            return GameUtils.whereIsThePlayer(player, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), emptyBucketImpl);
        }

        private ActionResult cancelPlayerPvP(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS;

            if (!GameUtils.isGameState_RUNNING()) return ActionResult.FAIL;

            if (entity instanceof PlayerEntity)
                if (!GameUtils.isPvPEnabled(timeline.getTimelineData().getDay()))
                    return ActionResult.FAIL;
            return ActionResult.PASS;
        }

        private ActionResult cancelPlayerFromEnteringInPortal(ServerPlayerEntity player, Identifier dimensionId) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS;

            if (!GameUtils.isGameState_RUNNING()) return ActionResult.FAIL;

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

            if (GameUtils.isGameState_NOT_STARTED()) {
                var waitingRoom = Configs.FK_CONFIG.data.getWaitingRoom();
                if (Utils.cancelPlayerFromLeavingAnArea(waitingRoom.getCube(), player, waitingRoom.getSpawnLocation()))
                    return ActionResult.FAIL;
                return ActionResult.PASS;
            }

            // Cancel player from moving.
            if (GameUtils.isGameState_PAUSED())
                return ActionResult.FAIL;

            // Cancel the player from going too far into the map
            if (Utils.cancelPlayerFromLeavingAnArea(Configs.WORLD_CONFIG.data.getWorldBorderData().getCube(), player, null)) {
                player.sendMessage(new LiteralText("You reach the border limit !").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        }

        private ActionResult onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
            if (GameUtils.isGameState_PAUSED() || GameUtils.isGameState_NOT_STARTED())
                return ActionResult.FAIL;
            return ActionResult.PASS;
        }

        private ActionResult onPlayerHungerUpdate(PlayerEntity player) {
            if (GameUtils.isGameState_NOT_STARTED() || GameUtils.isGameState_PAUSED())
                return ActionResult.FAIL;
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
            if (GameUtils.isGameState_RUNNING())
                FKGameAllData.FK_GAME_DATA.data.setGameState(FKMod.GameState.PAUSED);

            if(!player.hasPermissionLevel(4))
                player.changeGameMode(GameMode.SURVIVAL);

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
