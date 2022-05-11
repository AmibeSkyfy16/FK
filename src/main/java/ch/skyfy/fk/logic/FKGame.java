package ch.skyfy.fk.logic;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.ScoreboardManager;
import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.config.actions.AbstractPlayerActionConfig;
import ch.skyfy.fk.config.actions.PlayerActionsConfigs;
import ch.skyfy.fk.constants.MsgBase;
import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.events.*;
import ch.skyfy.fk.features.VaultFeature;
import ch.skyfy.fk.logic.data.FKGameAllData;
import ch.skyfy.fk.utils.ReflectionUtils;
import lombok.Getter;
import me.bymartrixx.playerevents.api.event.PlayerJoinCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TntBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.network.MessageType;
import net.minecraft.potion.PotionUtil;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final Map<String, Vec3d> onUsedEnderPearlMap;

    public FKGame(MinecraftServer server) {
        this.server = server;
        this.timeline = new Timeline();
        pauseEvents = new PauseEvents();
        fkGameEvents = new FKGameEvents();
        vaultFeature = new VaultFeature(server);
        onUsedEnderPearlMap = new HashMap<>();

        initialize();
        registerEvents();
    }

    private void initialize() {

        if (GameUtils.isGameState_RUNNING())
            FKGameAllData.FK_GAME_DATA.data.setGameState(FKMod.GameState.PAUSED);

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
            } else {
                Msg.BASE_COORDINATES.formatted((int) base.getCube().getX(), (int) base.getCube().getY(), (int) base.getCube().getZ()).send(fkPlayer);
            }
        }
    }

    public void pause() {
        FKGameAllData.FK_GAME_DATA.data.setGameState(FKMod.GameState.PAUSED);
        server.getPlayerManager().broadcast(Msg.GAME_HAS_BEEN_PAUSED.text(), MessageType.CHAT, NIL_UUID);
        GameUtils.getAllConnectedFKPlayers(server.getPlayerManager().getPlayerList()).forEach(this::updateSidebar);
    }

    public void resume() {
        FKGameAllData.FK_GAME_DATA.data.setGameState(FKMod.GameState.RUNNING);
        server.getPlayerManager().broadcast(Msg.GAME_HAS_BEEN_RESUMED.text(), MessageType.CHAT, NIL_UUID);

        // If the timeline wasn't started (in the case of a server restart with gamestate at PAUSE OR RUNNING)
        if (!timeline.getIsTimerStartedRef().get())
            timeline.startTimer();

        GameUtils.getAllConnectedFKPlayers(server.getPlayerManager().getPlayerList()).forEach(this::updateSidebar);
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

        var team = serverScoreboard.getTeam(GameUtils.getFKTeamIdentifierByName(fkTeam.getName()));
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

    private void registerEvents() {
        System.out.println(PlayerActionsConfigs.USE_BLOCKS_CONFIG.data.getDenied().toString());
        // Event use when the game state is "running"
        PlayerBlockBreakEvents.BEFORE.register(fkGameEvents::cancelPlayerFromBreakingBlocks);
        UseBlockCallback.EVENT.register(FKGameEvents.SECOND, fkGameEvents::onUseBlockEvent);
        UseItemCallback.EVENT.register(fkGameEvents::cancelPlayerFromUsingAItem);
        BucketFillCallback.EVENT.register(fkGameEvents::cancelPlayerFromFillingABucket);
        BucketEmptyCallback.EVENT.register(fkGameEvents::cancelPlayerFromEmptyingABucket);
        AttackEntityCallback.EVENT.register(fkGameEvents::cancelPlayerPvP);
        PlayerEnterPortalCallback.EVENT.register(fkGameEvents::cancelPlayerFromEnteringInPortal);
        PlayerMoveCallback.EVENT.register(fkGameEvents::onPlayerMove);
        EntityMoveCallback.EVENT.register(fkGameEvents::onEntityMove);
        PlayerDamageCallback.EVENT.register(fkGameEvents::onPlayerDamage);
        PlayerHungerCallback.EVENT.register(fkGameEvents::onPlayerHungerUpdate);
        PlayerJoinCallback.EVENT.register(fkGameEvents::onPlayerJoin);
        EntitySpawnCallback.EVENT.register(fkGameEvents::onEntitySpawn);
        ItemDespawnCallback.EVENT.register(fkGameEvents::onItemDespawn);
        EnderPearlCollisionCallback.EVENT.register(fkGameEvents::cancelPlayerFromAssaultWithEnderPearl);

//        // Event use when the game state is "pause"
        TimeOfDayUpdatedCallback.EVENT.register(pauseEvents::cancelTimeOfDayToBeingUpdated);
    }

    /**
     * This class contains events that will be used when the game state is "RUNNING
     */
    public class FKGameEvents {

        public static final List<String> interactiveBlocks = new ArrayList<>() {{
            addAll(List.of(
                    Blocks.ANVIL.getTranslationKey(),
                    Blocks.CHIPPED_ANVIL.getTranslationKey(),
                    Blocks.DAMAGED_ANVIL.getTranslationKey(),

                    Blocks.RESPAWN_ANCHOR.getTranslationKey(),

                    Blocks.WHITE_BED.getTranslationKey(),
                    Blocks.ORANGE_BED.getTranslationKey(),
                    Blocks.MAGENTA_BED.getTranslationKey(),
                    Blocks.LIGHT_BLUE_BED.getTranslationKey(),
                    Blocks.YELLOW_BED.getTranslationKey(),
                    Blocks.LIME_BED.getTranslationKey(),
                    Blocks.PINK_BED.getTranslationKey(),
                    Blocks.GRAY_BED.getTranslationKey(),
                    Blocks.LIGHT_GRAY_BED.getTranslationKey(),
                    Blocks.CYAN_BED.getTranslationKey(),
                    Blocks.PURPLE_BED.getTranslationKey(),
                    Blocks.BLUE_BED.getTranslationKey(),
                    Blocks.BROWN_BED.getTranslationKey(),
                    Blocks.GREEN_BED.getTranslationKey(),
                    Blocks.RED_BED.getTranslationKey(),
                    Blocks.BLACK_BED.getTranslationKey(),

                    Blocks.BELL.getTranslationKey(),
                    Blocks.BLAST_FURNACE.getTranslationKey(),
                    Blocks.FURNACE.getTranslationKey(),
                    Blocks.CRAFTING_TABLE.getTranslationKey(),
                    Blocks.BREWING_STAND.getTranslationKey(),
                    Blocks.LEVER.getTranslationKey(),
                    Blocks.LOOM.getTranslationKey(),
                    Blocks.NOTE_BLOCK.getTranslationKey(),
                    Blocks.CARTOGRAPHY_TABLE.getTranslationKey(),
                    Blocks.CHEST.getTranslationKey(),
                    Blocks.SMITHING_TABLE.getTranslationKey(),
                    Blocks.SMOKER.getTranslationKey(),
                    Blocks.ENCHANTING_TABLE.getTranslationKey(),
                    Blocks.ENDER_CHEST.getTranslationKey(),
                    Blocks.STONECUTTER.getTranslationKey(),
                    Blocks.GRINDSTONE.getTranslationKey(),

                    Blocks.BIRCH_BUTTON.getTranslationKey(),
                    Blocks.ACACIA_BUTTON.getTranslationKey(),
                    Blocks.SPRUCE_BUTTON.getTranslationKey(),
                    Blocks.OAK_BUTTON.getTranslationKey(),
                    Blocks.JUNGLE_BUTTON.getTranslationKey(),
                    Blocks.DARK_OAK_BUTTON.getTranslationKey(),
                    Blocks.CRIMSON_BUTTON.getTranslationKey(),
                    Blocks.WARPED_BUTTON.getTranslationKey(),
                    Blocks.STONE_BUTTON.getTranslationKey(),
                    Blocks.POLISHED_BLACKSTONE_BUTTON.getTranslationKey()
            ));
        }};

        public static final Identifier FIRST = new Identifier("fabric", "first");
        public static final Identifier SECOND = new Identifier("fabric", "second");

        public FKGameEvents() {
            UseBlockCallback.EVENT.addPhaseOrdering(FIRST, SECOND);
        }

        private boolean cancelPlayerFromBreakingBlocks(World world, PlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity blockEntity) {
            if (player.hasPermissionLevel(4)) return true;

            if (!GameUtils.isGameState_RUNNING()) return false;

            var breakPlace = (GameUtils.WhereIsThePlayer<Boolean>) (where) -> {
                var currentDimId = player.getWorld().getDimension().getEffects().toString();
                var block = world.getBlockState(pos).getBlock();
                return playerActionImpl(block.getTranslationKey(), currentDimId, where, true, false, PlayerActionsConfigs.BREAKING_BLOCKS_CONFIG.data);
            };
            return GameUtils.whereIsThePlayer(player, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), breakPlace);
        }

        private ActionResult cancelPlayerFromBreakingEntities(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS;
            if (!GameUtils.isGameState_RUNNING()) return ActionResult.FAIL;

            var where = GameUtils.whereIsThePlayer(player, entity.getPos(), w -> w);
            var currentDimId = player.getWorld().getDimension().getEffects().toString();
            return playerActionImpl(entity.getType().getTranslationKey(), currentDimId, where, ActionResult.PASS, ActionResult.FAIL, PlayerActionsConfigs.BREAKING_ENTITIES_CONFIG.data);
        }

        private ActionResult cancelPlayerFromPlacingBlocks(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
            var currentDimId = player.getWorld().getDimension().getEffects().toString();
            var itemInHand = player.getStackInHand(player.getActiveHand());

            // It's not a block
            if (!Registry.BLOCK.containsId(Registry.ITEM.getId(itemInHand.getItem())))
                return ActionResult.PASS;

            var placeBlock = (GameUtils.WhereIsThePlayer<ActionResult>) (where) -> switch (where) {
                case INSIDE_HIS_OWN_BASE ->
                        playerActionImpl(itemInHand.getTranslationKey(), currentDimId, where, ActionResult.PASS, ActionResult.FAIL, PlayerActionsConfigs.PLACING_BLOCKS_CONFIG.data);
                case CLOSE_TO_HIS_OWN_BASE ->
                        playerActionImpl(itemInHand.getTranslationKey(), currentDimId, where, ActionResult.PASS, ActionResult.FAIL, PlayerActionsConfigs.PLACING_BLOCKS_CONFIG.data);
                case INSIDE_AN_ENEMY_BASE, CLOSE_TO_AN_ENEMY_BASE -> {
                    if (!GameUtils.areAssaultEnabled(timeline.getTimelineData().getDay()))
                        yield ActionResult.FAIL;
                    yield playerActionImpl(itemInHand.getTranslationKey(), currentDimId, where, ActionResult.PASS, ActionResult.FAIL, PlayerActionsConfigs.PLACING_BLOCKS_CONFIG.data);
                }
                case IN_THE_WILD ->
                        playerActionImpl(itemInHand.getTranslationKey(), currentDimId, where, ActionResult.PASS, ActionResult.FAIL, PlayerActionsConfigs.PLACING_BLOCKS_CONFIG.data);
                default -> ActionResult.PASS;
            };

            var blockPos = hitResult.getBlockPos();
            return GameUtils.whereIsThePlayer(player, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), placeBlock);
        }

        private TypedActionResult<ItemStack> cancelPlayerFromFillingABucket(World world, PlayerEntity player, Hand hand, Fluid fillFluid, Block targetBlock, BucketItem bucketItem, BlockHitResult blockHitResult) {
            if (player.hasPermissionLevel(4)) return TypedActionResult.pass(player.getStackInHand(hand));

            if (!GameUtils.isGameState_RUNNING()) return TypedActionResult.fail(player.getStackInHand(hand));

            var fillBucketImpl = (GameUtils.WhereIsThePlayer<TypedActionResult<ItemStack>>) (where) -> {
                var currentDimId = player.getWorld().getDimension().getEffects().toString();
                var placedItemStack = player.getStackInHand(player.getActiveHand());
                return playerActionImpl(targetBlock.getTranslationKey(), currentDimId, where, TypedActionResult.pass(placedItemStack), TypedActionResult.fail(placedItemStack), PlayerActionsConfigs.FILLING_BUCKET_CONFIG.data);
            };

            var blockPos = blockHitResult.getBlockPos();
            return GameUtils.whereIsThePlayer(player, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), fillBucketImpl);
        }

        private TypedActionResult<ItemStack> cancelPlayerFromEmptyingABucket(World world, PlayerEntity player, Hand hand, Fluid emptyFluid, BucketItem bucketItem, BlockHitResult blockHitResult) {
            if (player.hasPermissionLevel(4)) return TypedActionResult.pass(player.getStackInHand(hand));

            if (!GameUtils.isGameState_RUNNING()) return TypedActionResult.fail(player.getStackInHand(hand));

            var emptyBucketImpl = (GameUtils.WhereIsThePlayer<TypedActionResult<ItemStack>>) (where) -> {
                var currentDimId = player.getWorld().getDimension().getEffects().toString();
                var placedItemStack = player.getStackInHand(player.getActiveHand());
                return playerActionImpl(bucketItem.getTranslationKey(), currentDimId, where, TypedActionResult.pass(placedItemStack), TypedActionResult.fail(placedItemStack), PlayerActionsConfigs.EMPTYING_BUCKET_CONFIG.data);
            };

            var blockPos = blockHitResult.getBlockPos();
            return GameUtils.whereIsThePlayer(player, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), emptyBucketImpl);
        }

        private TypedActionResult<ItemStack> cancelPlayerFromUsingAItem(PlayerEntity player, World world, Hand hand) {
            if (player.hasPermissionLevel(4)) return TypedActionResult.pass(player.getStackInHand(hand));

            if (!GameUtils.isGameState_RUNNING()) return TypedActionResult.fail(player.getStackInHand(hand));

            var item = player.getStackInHand(hand);
            var wherePlayer = GameUtils.whereIsThePlayer(player, player.getPos(), w -> w);

            // Use with allowEnderPearlAssault config
            if (item.isOf(Items.ENDER_PEARL))
                onUsedEnderPearlMap.compute(player.getUuidAsString(), (s, vec3d) -> player.getPos());

            String translationKey;
            var currentDimId = player.getWorld().getDimension().getEffects().toString();
            if (item.getItem() instanceof PotionItem)
                translationKey = Registry.POTION.getId(PotionUtil.getPotion(item)).toString();
            else
                translationKey = item.getTranslationKey();

            return playerActionImpl(translationKey, currentDimId, wherePlayer, TypedActionResult.pass(item), TypedActionResult.fail(item), PlayerActionsConfigs.USE_ITEMS_CONFIG.data);
        }

        private ActionResult cancelPlayerFromAssaultWithEnderPearl(ServerPlayerEntity player, Vec3d pos) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS;
            if (!GameUtils.isGameState_RUNNING()) return ActionResult.FAIL;

            if (Configs.FK_CONFIG.data.isAllowEnderPearlAssault()) return ActionResult.PASS;

            var playerLastPos = onUsedEnderPearlMap.get(player.getUuidAsString());
            if (playerLastPos == null) return ActionResult.PASS;

            var where = GameUtils.whereIsThePlayer(player, pos, w -> w);
            if (where == Where.INSIDE_AN_ENEMY_BASE) return ActionResult.FAIL;

            return ActionResult.PASS;
        }

        private ActionResult onUseBlockEvent(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS; // Admin can bypass this

            // If the game is NOT_STARTED OR PAUSED, players can't destroy block, block entity (painting, item frame, ...) or firing tnt
            if (!GameUtils.isGameState_RUNNING()) return ActionResult.FAIL;

            // check if a player is trying to use an interactive bloc like crafting table, respawn anchor, etc.
            var result2 = cancelPlayerFromUsingABlock(player, world, hand, hitResult);
            if (result2 == ActionResult.FAIL) return result2;

            // Check if player has a block in his hand and if we need to cancel
            var result = cancelPlayerFromPlacingBlocks(player, world, hand, hitResult);
            if (result == ActionResult.FAIL) return result;

            // Check if player has a flint and steel in his hand and if the target block is a tnt
            // if true -> we fail. Otherwise -> pass
            return cancelPlayerFromFiringATNT(player, world, hand, hitResult);
        }

        private ActionResult cancelPlayerFromFiringATNT(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
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

        private ActionResult cancelPlayerFromUsingABlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
            var targetBlock = world.getBlockState(hitResult.getBlockPos()).getBlock();

            if (!interactiveBlocks.contains(targetBlock.getTranslationKey())) return ActionResult.PASS;

            // if player try to use an interactive bloc like anvil, crafting table, ...
            if (!player.isSneaking() || (player.isSneaking() && player.getStackInHand(hand) == ItemStack.EMPTY)) {
                var currentDimId = player.getWorld().getDimension().getEffects().toString();
                var where = GameUtils.whereIsThePlayer(player, hitResult.getPos(), w -> w);
                return playerActionImpl(targetBlock.getTranslationKey(), currentDimId, where, ActionResult.PASS, ActionResult.FAIL, PlayerActionsConfigs.USE_BLOCKS_CONFIG.data);
            }

            return ActionResult.PASS;
        }

        private ActionResult cancelPlayerPvP(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
            if (player.hasPermissionLevel(4)) return ActionResult.PASS;

            if (!GameUtils.isGameState_RUNNING()) return ActionResult.FAIL;

            if (entity instanceof PlayerEntity)
                if (!GameUtils.isPvPEnabled(timeline.getTimelineData().getDay()))
                    return ActionResult.FAIL;

            if (entity instanceof AbstractDecorationEntity)
                return cancelPlayerFromBreakingEntities(player, world, hand, entity, hitResult);

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
                // if player is not in a waiting room
                if (!player.getWorld().getDimension().getEffects().toString().equals(waitingRoom.getSpawnLocation().getDimensionName()))
                    return ActionResult.PASS;
                if (Utils.cancelPlayerFromLeavingAnArea(waitingRoom.getCube(), player, waitingRoom.getSpawnLocation()))
                    return ActionResult.FAIL;
                return ActionResult.PASS;
            }

            // Cancel player from moving.
            if (GameUtils.isGameState_PAUSED())
                return ActionResult.FAIL;

            // Cancel the player from going too far into the map
            var opt = Configs.WORLD_CONFIG.data.getWorldBorderData().getSpawns()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().equals(player.getWorld().getDimension().getEffects().toString()))
                    .findFirst();
            if (opt.isPresent()) {
                if (Utils.cancelPlayerFromLeavingAnArea(opt.get().getValue(), player, null)) {
                    player.sendMessage(new LiteralText("You reach the border limit !").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        }

        private ActionResult onEntityMove(Entity entity, MovementType movementType, Vec3d movement) {
            if (GameUtils.isGameState_PAUSED())
                if (entity instanceof MobEntity)
                    return ActionResult.FAIL;
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
            if (!player.hasPermissionLevel(4))
                player.changeGameMode(GameMode.SURVIVAL);

            teleportPlayerToWaitingRoom(player);
            update(player);
        }

        private static <D extends AbstractPlayerActionConfig, T> T playerActionImpl(String translationKey, String currentDimId, Where where, T pass, T fail, D data) {

            // Make a check for denied block in first
            // if a denied block is found, its fail, otherwise, if not or if a null value, its just ignored
            var deniedMap = data.getDenied();
            if (deniedMap != null) {
                var deniedNestedMap = deniedMap.get(currentDimId);
                if (deniedNestedMap != null) {
                    var deniedTranslationKeys = deniedNestedMap.get(where);
                    if (deniedTranslationKeys.contains(translationKey)) return fail;
                }
            }

            var allowedMap = data.getAllowed();
            var allowedNestedMap = allowedMap.get(currentDimId);
            if (allowedNestedMap == null) return pass;

            var allowedTranslationKeys = allowedNestedMap.get(where);

            if (allowedTranslationKeys == null) return pass;

            if (allowedTranslationKeys.contains(translationKey)) return pass;
            return fail;
        }

    }

    static class PauseEvents {

        private ActionResult cancelTimeOfDayToBeingUpdated(long time) {
            if (!GameUtils.isGameState_PAUSED()) return ActionResult.PASS;
            return ActionResult.FAIL;
        }

    }

}
