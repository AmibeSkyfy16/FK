package ch.skyfy.fk.config.actions;

import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.json.Defaultable;
import ch.skyfy.fk.json.JsonData;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;

import java.util.*;

import static ch.skyfy.fk.constants.Where.*;

public class PlayerActionsConfigs {

    public static final JsonData<PlacingBlocksConfig, PlacingBlocksConfigDefault> PLACING_BLOCKS_CONFIG;
    public static final JsonData<BreakingBlocksConfig, BreakingBlocksConfigDefault> BREAKING_BLOCKS_CONFIG;
    public static final JsonData<FillingBucketConfig, FillingBucketConfigDefault> FILLING_BUCKET_CONFIG;
    public static final JsonData<EmptyingBucketConfig, EmptyingBucketConfigDefault> EMPTYING_BUCKET_CONFIG;
    public static final JsonData<KillingEntitiesConfig, KillingEntitiesConfigDefault> KILLING_ENTITIES_CONFIG;
    public static final JsonData<UseItemsConfig, UseItemsConfigDefault> USE_ITEMS_CONFIG;
    public static final JsonData<UseBlocksConfig, UseBlocksConfigDefault> USE_BLOCKS_CONFIG;

    static {
        PLACING_BLOCKS_CONFIG = new JsonData<>("actions\\placeBlocks.json5", PlacingBlocksConfig.class, PlacingBlocksConfigDefault.class);
        BREAKING_BLOCKS_CONFIG = new JsonData<>("actions\\breakBlocks.json5", BreakingBlocksConfig.class, BreakingBlocksConfigDefault.class);
        FILLING_BUCKET_CONFIG = new JsonData<>("actions\\fillBucket.json5", FillingBucketConfig.class, FillingBucketConfigDefault.class);
        EMPTYING_BUCKET_CONFIG = new JsonData<>("actions\\emptyBucket.json5", EmptyingBucketConfig.class, EmptyingBucketConfigDefault.class);
        KILLING_ENTITIES_CONFIG = new JsonData<>("actions\\killEntities.json5", KillingEntitiesConfig.class, KillingEntitiesConfigDefault.class);
        USE_ITEMS_CONFIG = new JsonData<>("actions\\useItems.json5", UseItemsConfig.class, UseItemsConfigDefault.class);
        USE_BLOCKS_CONFIG = new JsonData<>("actions\\useBlocks.json5", UseBlocksConfig.class, UseBlocksConfigDefault.class);
    }

    public static class PlacingBlocksConfigDefault implements Defaultable<PlacingBlocksConfig> {
        @Override
        public PlacingBlocksConfig getDefault() {
            var allowedBlocks1 = List.of(
                    "block.minecraft.tnt",
                    "block.minecraft.lever",
                    "block.minecraft.redstone_torch",
                    "block.minecraft.torch",
                    "block.minecraft.soul_torch");

            var nestedAllowedMap = new HashMap<Where, List<String>>();
            nestedAllowedMap.put(INSIDE_HIS_OWN_BASE, null); // When it is null, it means that all is allowed
            nestedAllowedMap.put(CLOSE_TO_HIS_OWN_BASE, new ArrayList<>()); // Here, the list is empty, so nothing is allowed
            nestedAllowedMap.put(INSIDE_AN_ENEMY_BASE, allowedBlocks1);
            nestedAllowedMap.put(CLOSE_TO_AN_ENEMY_BASE, allowedBlocks1);
            nestedAllowedMap.put(IN_THE_WILD, null); // When it is null, it means that all is allowed
            var allowedMap = new HashMap<String, Map<Where, List<String>>>();
            allowedMap.put("minecraft:overworld", nestedAllowedMap);
            allowedMap.put("minecraft:the_nether", new HashMap<>(nestedAllowedMap)); // When it is null, it means that all is allowed
            allowedMap.put("minecraft:the_end", new HashMap<>(nestedAllowedMap)); // When it is null, it means that all is allowed

            var nestedDeniedMap = new HashMap<Where, List<String>>();
            nestedDeniedMap.put(INSIDE_HIS_OWN_BASE, new ArrayList<>());
            nestedDeniedMap.put(CLOSE_TO_HIS_OWN_BASE, new ArrayList<>());
            nestedDeniedMap.put(INSIDE_AN_ENEMY_BASE, new ArrayList<>());
            nestedDeniedMap.put(CLOSE_TO_AN_ENEMY_BASE, new ArrayList<>());
            nestedDeniedMap.put(IN_THE_WILD, new ArrayList<>());
            var deniedMap = new HashMap<String, Map<Where, List<String>>>();
            deniedMap.put("minecraft:overworld", nestedDeniedMap);
            deniedMap.put("minecraft:the_nether", new HashMap<>(nestedDeniedMap));
            deniedMap.put("minecraft:the_end", new HashMap<>(nestedDeniedMap));

            return new PlacingBlocksConfig(allowedMap, deniedMap);
        }
    }
    public static class BreakingBlocksConfigDefault implements Defaultable<BreakingBlocksConfig> {
        @Override
        public BreakingBlocksConfig getDefault() {
            var allowedBlocks1 = List.of(
                    "block.minecraft.tnt",
                    "block.minecraft.lever",
                    "block.minecraft.redstone_torch",
                    "block.minecraft.torch",
                    "block.minecraft.soul_torch");

            var nestedAllowedMap = new HashMap<Where, List<String>>();
            nestedAllowedMap.put(INSIDE_HIS_OWN_BASE, null);
            nestedAllowedMap.put(CLOSE_TO_HIS_OWN_BASE, null);
            nestedAllowedMap.put(INSIDE_AN_ENEMY_BASE, allowedBlocks1);
            nestedAllowedMap.put(CLOSE_TO_AN_ENEMY_BASE, allowedBlocks1);
            nestedAllowedMap.put(IN_THE_WILD, null);
            var allowedMap = new HashMap<String, Map<Where, List<String>>>();
            allowedMap.put("minecraft:overworld", nestedAllowedMap);
            allowedMap.put("minecraft:the_nether", new HashMap<>(nestedAllowedMap));
            allowedMap.put("minecraft:the_end", new HashMap<>(nestedAllowedMap));

            var nestedDeniedMap = new HashMap<Where, List<String>>();
            nestedDeniedMap.put(INSIDE_HIS_OWN_BASE, new ArrayList<>());
            nestedDeniedMap.put(CLOSE_TO_HIS_OWN_BASE, new ArrayList<>());
            nestedDeniedMap.put(INSIDE_AN_ENEMY_BASE, new ArrayList<>());
            nestedDeniedMap.put(CLOSE_TO_AN_ENEMY_BASE, new ArrayList<>());
            nestedDeniedMap.put(IN_THE_WILD, new ArrayList<>());
            var deniedMap = new HashMap<String, Map<Where, List<String>>>();
            deniedMap.put("minecraft:overworld", nestedDeniedMap);
            deniedMap.put("minecraft:the_nether", new HashMap<>(nestedDeniedMap));
            deniedMap.put("minecraft:the_end", new HashMap<>(nestedDeniedMap));

            return new BreakingBlocksConfig(allowedMap, deniedMap);
        }
    }
    public static class KillingEntitiesConfigDefault implements Defaultable<KillingEntitiesConfig> {
        @Override
        public KillingEntitiesConfig getDefault() {
            var nestedAllowedMap = new HashMap<Where, List<String>>();
            nestedAllowedMap.put(INSIDE_HIS_OWN_BASE, null);
            nestedAllowedMap.put(CLOSE_TO_HIS_OWN_BASE, null);
            nestedAllowedMap.put(INSIDE_AN_ENEMY_BASE, null);
            nestedAllowedMap.put(CLOSE_TO_AN_ENEMY_BASE, null);
            nestedAllowedMap.put(IN_THE_WILD, null);
            var allowedMap = new HashMap<String, Map<Where, List<String>>>();
            allowedMap.put("minecraft:overworld", nestedAllowedMap);
            allowedMap.put("minecraft:the_nether", new HashMap<>(nestedAllowedMap));
            allowedMap.put("minecraft:the_end", new HashMap<>(nestedAllowedMap));

            var deniedList = List.of(
                    EntityType.ITEM_FRAME.getTranslationKey(),
                    EntityType.GLOW_ITEM_FRAME.getTranslationKey(),
                    EntityType.PAINTING.getTranslationKey(),
                    EntityType.MINECART.getTranslationKey(),
                    EntityType.CHEST_MINECART.getTranslationKey(),
                    EntityType.FURNACE_MINECART.getTranslationKey(),
                    EntityType.HOPPER_MINECART.getTranslationKey(),
                    EntityType.TNT_MINECART.getTranslationKey(),
                    EntityType.BOAT.getTranslationKey(),
                    EntityType.ARMOR_STAND.getTranslationKey()
            );

            var nestedDeniedMap = new HashMap<Where, List<String>>();
            nestedDeniedMap.put(INSIDE_HIS_OWN_BASE, new ArrayList<>());
            nestedDeniedMap.put(CLOSE_TO_HIS_OWN_BASE, new ArrayList<>());
            nestedDeniedMap.put(INSIDE_AN_ENEMY_BASE, deniedList);
            nestedDeniedMap.put(CLOSE_TO_AN_ENEMY_BASE, new ArrayList<>());
            nestedDeniedMap.put(IN_THE_WILD, new ArrayList<>());
            var deniedMap = new HashMap<String, Map<Where, List<String>>>();
            deniedMap.put("minecraft:overworld", nestedDeniedMap);
            deniedMap.put("minecraft:the_nether", new HashMap<>(nestedDeniedMap));
            deniedMap.put("minecraft:the_end", new HashMap<>(nestedDeniedMap));

            return new KillingEntitiesConfig(allowedMap, deniedMap);
        }
    }
    public static class FillingBucketConfigDefault implements Defaultable<FillingBucketConfig> {
        @Override
        public FillingBucketConfig getDefault() {
            var nestedAllowedMap = new HashMap<Where, List<String>>();
            nestedAllowedMap.put(INSIDE_HIS_OWN_BASE, null);
            nestedAllowedMap.put(CLOSE_TO_HIS_OWN_BASE, null);
            nestedAllowedMap.put(INSIDE_AN_ENEMY_BASE, new ArrayList<>());
            nestedAllowedMap.put(CLOSE_TO_AN_ENEMY_BASE, new ArrayList<>());
            nestedAllowedMap.put(IN_THE_WILD, null);
            var allowedMap = new HashMap<String, Map<Where, List<String>>>();
            allowedMap.put("minecraft:overworld", nestedAllowedMap);
            allowedMap.put("minecraft:the_nether", new HashMap<>(nestedAllowedMap));
            allowedMap.put("minecraft:the_end", new HashMap<>(nestedAllowedMap));

            var nestedDeniedMap = new HashMap<Where, List<String>>();
            nestedDeniedMap.put(INSIDE_HIS_OWN_BASE, new ArrayList<>());
            nestedDeniedMap.put(CLOSE_TO_HIS_OWN_BASE, new ArrayList<>());
            nestedDeniedMap.put(INSIDE_AN_ENEMY_BASE, new ArrayList<>());
            nestedDeniedMap.put(CLOSE_TO_AN_ENEMY_BASE, new ArrayList<>());
            nestedDeniedMap.put(IN_THE_WILD, new ArrayList<>());
            var deniedMap = new HashMap<String, Map<Where, List<String>>>();
            deniedMap.put("minecraft:overworld", nestedDeniedMap);
            deniedMap.put("minecraft:the_nether", new HashMap<>(nestedDeniedMap));
            deniedMap.put("minecraft:the_end", new HashMap<>(nestedDeniedMap));

            return new FillingBucketConfig(allowedMap, deniedMap);
        }
    }
    public static class EmptyingBucketConfigDefault implements Defaultable<EmptyingBucketConfig> {

        @Override
        public EmptyingBucketConfig getDefault() {

            var nestedAllowedMap = new HashMap<Where, List<String>>();
            nestedAllowedMap.put(INSIDE_HIS_OWN_BASE, null);
            nestedAllowedMap.put(CLOSE_TO_HIS_OWN_BASE, new ArrayList<>());
            nestedAllowedMap.put(INSIDE_AN_ENEMY_BASE, new ArrayList<>());
            nestedAllowedMap.put(CLOSE_TO_AN_ENEMY_BASE, new ArrayList<>());
            nestedAllowedMap.put(IN_THE_WILD, null);
            var allowedMap = new HashMap<String, Map<Where, List<String>>>();
            allowedMap.put("minecraft:overworld", nestedAllowedMap);
            allowedMap.put("minecraft:the_nether", new HashMap<>(nestedAllowedMap));
            allowedMap.put("minecraft:the_end", new HashMap<>(nestedAllowedMap));

            var nestedDeniedMap = new HashMap<Where, List<String>>();
            nestedDeniedMap.put(INSIDE_HIS_OWN_BASE, new ArrayList<>());
            nestedDeniedMap.put(CLOSE_TO_HIS_OWN_BASE, new ArrayList<>());
            nestedDeniedMap.put(INSIDE_AN_ENEMY_BASE, new ArrayList<>());
            nestedDeniedMap.put(CLOSE_TO_AN_ENEMY_BASE, new ArrayList<>());
            nestedDeniedMap.put(IN_THE_WILD, new ArrayList<>());
            var deniedMap = new HashMap<String, Map<Where, List<String>>>();
            deniedMap.put("minecraft:overworld", nestedDeniedMap);
            deniedMap.put("minecraft:the_nether", new HashMap<>(nestedDeniedMap));
            deniedMap.put("minecraft:the_end", new HashMap<>(nestedDeniedMap));

            return new EmptyingBucketConfig(allowedMap, deniedMap);
        }

    }
    public static class UseItemsConfigDefault implements Defaultable<UseItemsConfig>{

        @Override
        public UseItemsConfig getDefault() {

            var nestedAllowedMap = new HashMap<Where, List<String>>();
            nestedAllowedMap.put(INSIDE_HIS_OWN_BASE, null);
            nestedAllowedMap.put(CLOSE_TO_HIS_OWN_BASE, null);
            nestedAllowedMap.put(INSIDE_AN_ENEMY_BASE, null);
            nestedAllowedMap.put(CLOSE_TO_AN_ENEMY_BASE, null);
            nestedAllowedMap.put(IN_THE_WILD, null);
            var allowedMap = new HashMap<String, Map<Where, List<String>>>();
            allowedMap.put("minecraft:overworld", nestedAllowedMap);
            allowedMap.put("minecraft:the_nether", new HashMap<>(nestedAllowedMap));
            allowedMap.put("minecraft:the_end", new HashMap<>(nestedAllowedMap));

            var opPotions = List.of(
                    Registry.POTION.getId(Potions.STRONG_STRENGTH).toString()
            );
            var otherItems = List.of(
                    Items.BIRCH_BOAT.getTranslationKey(),
                    Items.OAK_BOAT.getTranslationKey(),
                    Items.ACACIA_BOAT.getTranslationKey(),
                    Items.DARK_OAK_BOAT.getTranslationKey(),
                    Items.JUNGLE_BOAT.getTranslationKey(),
                    Items.SPRUCE_BOAT.getTranslationKey(),
                    Items.MINECART.getTranslationKey(),
                    Items.CHEST_MINECART.getTranslationKey(),
                    Items.FURNACE_MINECART.getTranslationKey(),
                    Items.TNT_MINECART.getTranslationKey(),
                    Items.HOPPER_MINECART.getTranslationKey()
            );
            var list = new ArrayList<String>();
            list.addAll(opPotions);
            list.addAll(otherItems);

            var nestedDeniedMap = new HashMap<Where, List<String>>();
            nestedDeniedMap.put(INSIDE_HIS_OWN_BASE, opPotions);
            nestedDeniedMap.put(CLOSE_TO_HIS_OWN_BASE, opPotions);
            nestedDeniedMap.put(INSIDE_AN_ENEMY_BASE, list);
            nestedDeniedMap.put(CLOSE_TO_AN_ENEMY_BASE, list);
            nestedDeniedMap.put(IN_THE_WILD, opPotions);
            var deniedMap = new HashMap<String, Map<Where, List<String>>>();
            deniedMap.put("minecraft:overworld", nestedDeniedMap);
            deniedMap.put("minecraft:the_nether", new HashMap<>(nestedDeniedMap));
            deniedMap.put("minecraft:the_end", new HashMap<>(nestedDeniedMap));

            return new UseItemsConfig(allowedMap, deniedMap);
        }
    }
    public static class UseBlocksConfigDefault implements Defaultable<UseBlocksConfig>{

        @Override
        public UseBlocksConfig getDefault() {

            var disableCraft = List.of(
                    Blocks.CRAFTING_TABLE.getTranslationKey(),
                    Blocks.ENCHANTING_TABLE.getTranslationKey(),
                    Blocks.ANVIL.getTranslationKey(),
                    Blocks.CHIPPED_ANVIL.getTranslationKey(),
                    Blocks.DAMAGED_ANVIL.getTranslationKey(),
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
                    Blocks.BLACK_BED.getTranslationKey()
            );

            var nestedAllowedMap = new HashMap<Where, List<String>>();
            nestedAllowedMap.put(INSIDE_HIS_OWN_BASE, null);
            nestedAllowedMap.put(CLOSE_TO_HIS_OWN_BASE, null);
            nestedAllowedMap.put(INSIDE_AN_ENEMY_BASE, null);
            nestedAllowedMap.put(CLOSE_TO_AN_ENEMY_BASE, null);
            nestedAllowedMap.put(IN_THE_WILD, null);

            var allowedMap = new HashMap<String, Map<Where, List<String>>>();
            allowedMap.put("minecraft:overworld", nestedAllowedMap);
            allowedMap.put("minecraft:the_nether", new HashMap<>(nestedAllowedMap));
            allowedMap.put("minecraft:the_end", new HashMap<>(nestedAllowedMap));

            var nestedDeniedMap = new HashMap<Where, List<String>>();
            nestedDeniedMap.put(INSIDE_HIS_OWN_BASE, new ArrayList<>());
            nestedDeniedMap.put(CLOSE_TO_HIS_OWN_BASE, disableCraft);
            nestedDeniedMap.put(INSIDE_AN_ENEMY_BASE, disableCraft);
            nestedDeniedMap.put(CLOSE_TO_AN_ENEMY_BASE, disableCraft);
            nestedDeniedMap.put(IN_THE_WILD, disableCraft);
            var deniedMap = new HashMap<String, Map<Where, List<String>>>();
            deniedMap.put("minecraft:overworld", nestedDeniedMap);
            deniedMap.put("minecraft:the_nether", new HashMap<>(nestedDeniedMap));
            deniedMap.put("minecraft:the_end", new HashMap<>(nestedDeniedMap));

            return new UseBlocksConfig(allowedMap, deniedMap);
        }
    }


}
