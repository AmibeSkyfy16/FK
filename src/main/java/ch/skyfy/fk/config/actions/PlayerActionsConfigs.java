package ch.skyfy.fk.config.actions;

import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.json.Defaultable;
import ch.skyfy.fk.json.JsonDataClass;
import net.minecraft.block.Blocks;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.skyfy.fk.constants.Where.*;

public class PlayerActionsConfigs {

    public static final JsonDataClass<PlacingBlocksConfig, PlacingBlocksConfigDefault> PLACING_BLOCKS_CONFIG;
    public static final JsonDataClass<BreakingBlocksConfig, BreakingBlocksConfigDefault> BREAKING_BLOCKS_CONFIG;
    public static final JsonDataClass<FillingBucketConfig, FillingBucketConfigDefault> FILLING_BUCKET_CONFIG;
    public static final JsonDataClass<EmptyingBucketConfig, EmptyingBucketConfigDefault> EMPTYING_BUCKET_CONFIG;
    public static final JsonDataClass<BreakingEntitiesConfig, BreakingEntitiesConfigDefault> BREAKING_ENTITIES_CONFIG;
    public static final JsonDataClass<UseItemsConfig, UseItemsConfigDefault> USE_ITEMS_CONFIG;
    public static final JsonDataClass<UseBlocksConfig, UseBlocksConfigDefault> USE_BLOCKS_CONFIG;

    static {
        PLACING_BLOCKS_CONFIG = new JsonDataClass<>("actions\\placingBlocks.json5", PlacingBlocksConfig.class, PlacingBlocksConfigDefault.class);
        BREAKING_BLOCKS_CONFIG = new JsonDataClass<>("actions\\breakingBlocks.json5", BreakingBlocksConfig.class, BreakingBlocksConfigDefault.class);
        FILLING_BUCKET_CONFIG = new JsonDataClass<>("actions\\fillingBucket.json5", FillingBucketConfig.class, FillingBucketConfigDefault.class);
        EMPTYING_BUCKET_CONFIG = new JsonDataClass<>("actions\\emptyingBucket.json5", EmptyingBucketConfig.class, EmptyingBucketConfigDefault.class);
        BREAKING_ENTITIES_CONFIG = new JsonDataClass<>("actions\\breakingEntities.json5", BreakingEntitiesConfig.class, BreakingEntitiesConfigDefault.class);
        USE_ITEMS_CONFIG = new JsonDataClass<>("actions\\useItems.json5", UseItemsConfig.class, UseItemsConfigDefault.class);
        USE_BLOCKS_CONFIG = new JsonDataClass<>("actions\\useBlocks.json5", UseBlocksConfig.class, UseBlocksConfigDefault.class);
    }

    public static class PlacingBlocksConfigDefault implements Defaultable<PlacingBlocksConfig> {
        @Override
        public PlacingBlocksConfig getDefault() {
            var allowedBlocks1 = List.of("block.minecraft.tnt",
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
            nestedDeniedMap.put(INSIDE_HIS_OWN_BASE, null); // Here, when it is null, it's just skipped by the program, so nothing is denied !
            nestedDeniedMap.put(CLOSE_TO_HIS_OWN_BASE, new ArrayList<>()); // Here, the list is empty, so nothing is denied
            nestedDeniedMap.put(INSIDE_AN_ENEMY_BASE, new ArrayList<>());
            nestedDeniedMap.put(CLOSE_TO_AN_ENEMY_BASE, new ArrayList<>());
            nestedDeniedMap.put(IN_THE_WILD, List.of(Blocks.CHEST.getTranslationKey()));
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
            var allowedBlocks1 = List.of("block.minecraft.tnt",
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
    public static class BreakingEntitiesConfigDefault implements Defaultable<BreakingEntitiesConfig> {
        @Override
        public BreakingEntitiesConfig getDefault() {
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

            return new BreakingEntitiesConfig(allowedMap, deniedMap);
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

            var nestedDeniedMap = new HashMap<Where, List<String>>();
            nestedDeniedMap.put(INSIDE_HIS_OWN_BASE, opPotions);
            nestedDeniedMap.put(CLOSE_TO_HIS_OWN_BASE, opPotions);
            nestedDeniedMap.put(INSIDE_AN_ENEMY_BASE, opPotions);
            nestedDeniedMap.put(CLOSE_TO_AN_ENEMY_BASE, opPotions);
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
