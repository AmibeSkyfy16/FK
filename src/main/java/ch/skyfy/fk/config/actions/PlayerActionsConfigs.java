package ch.skyfy.fk.config.actions;

import ch.skyfy.fk.constants.Where;
import ch.skyfy.fk.json.JsonDataClass;
import ch.skyfy.fk.utils.ReflectionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerActionsConfigs {

    public static final JsonDataClass<PlacingBlocksConfig> PLACING_BLOCKS_CONFIG;
    public static final JsonDataClass<BreakingBlocksConfig> BREAKING_BLOCKS_CONFIG;
    public static final JsonDataClass<FillingBucketConfig> FILLING_BUCKET_CONFIG;
    public static final JsonDataClass<EmptyingBucketConfig> EMPTYING_BUCKET_CONFIG;

    static {
        PLACING_BLOCKS_CONFIG = new JsonDataClass<>("actions\\placingBlocks.json5", PlacingBlocksConfig.class, PlacingBlocksConfigDefault.PLACING_BLOCKS);
        BREAKING_BLOCKS_CONFIG = new JsonDataClass<>("actions\\breakingBlocks.json5", BreakingBlocksConfig.class, BreakingBlocksConfigDefault.BREAKING_BLOCKS);
        FILLING_BUCKET_CONFIG = new JsonDataClass<>("actions\\fillingBucket.json5", FillingBucketConfig.class, FillingBucketConfigDefault.FILLING_BUCKET);
        EMPTYING_BUCKET_CONFIG = new JsonDataClass<>("actions\\emptyingBucket.json5", EmptyingBucketConfig.class, EmptyingBucketConfigDefault.EMPTYING_BUCKET);
    }

    private static class PlacingBlocksConfigDefault {
        private static final PlacingBlocksConfig PLACING_BLOCKS;

        static {
            var all = ReflectionUtils.getListOfTranslationKey(Blocks.class, Block.class);

            PLACING_BLOCKS = new PlacingBlocksConfig(new HashMap<>() {{
                put("minecraft:overworld", new HashMap<>() {{
                    put(Where.INSIDE_HIS_OWN_BASE, all);
                    put(Where.CLOSE_TO_HIS_OWN_BASE, new ArrayList<>());
                    put(Where.INSIDE_AN_ENEMY_BASE, new ArrayList<>() {{
                        addAll(List.of("block.minecraft.tnt",
                                "block.minecraft.lever",
                                "block.minecraft.redstone_torch",
                                "block.minecraft.torch",
                                "block.minecraft.soul_torch"));
                    }});
                    put(Where.CLOSE_TO_AN_ENEMY_BASE, new ArrayList<>() {{
                        addAll(List.of("block.minecraft.tnt",
                                "block.minecraft.lever",
                                "block.minecraft.redstone_torch",
                                "block.minecraft.torch",
                                "block.minecraft.soul_torch"));
                    }});
                    put(Where.IN_THE_WILD, all);
                }});
            }});
        }
    }

    private static class BreakingBlocksConfigDefault {
        private static final BreakingBlocksConfig BREAKING_BLOCKS;

        static {
            var all = ReflectionUtils.getListOfTranslationKey(Blocks.class, Block.class);

            BREAKING_BLOCKS = new BreakingBlocksConfig(new HashMap<>() {{
                put("minecraft:overworld", new HashMap<>() {{
                    put(Where.INSIDE_HIS_OWN_BASE, all);
                    put(Where.CLOSE_TO_HIS_OWN_BASE, new ArrayList<>());
                    put(Where.INSIDE_AN_ENEMY_BASE, new ArrayList<>() {{
                        addAll(List.of("block.minecraft.tnt",
                                "block.minecraft.lever",
                                "block.minecraft.redstone_torch",
                                "block.minecraft.torch",
                                "block.minecraft.soul_torch"));
                    }});
                    put(Where.CLOSE_TO_AN_ENEMY_BASE, new ArrayList<>() {{
                        addAll(List.of("block.minecraft.tnt",
                                "block.minecraft.lever",
                                "block.minecraft.redstone_torch",
                                "block.minecraft.torch",
                                "block.minecraft.soul_torch"));
                    }});
                    put(Where.IN_THE_WILD, all);
                }});
            }});
        }
    }

    private static class FillingBucketConfigDefault {
        private static final FillingBucketConfig FILLING_BUCKET;

        static {
            FILLING_BUCKET = new FillingBucketConfig(new HashMap<>() {{
                put("minecraft:overworld", new HashMap<>() {{
                    put(Where.INSIDE_HIS_OWN_BASE, List.of(
                            Blocks.LAVA.getTranslationKey(),
                            Blocks.WATER.getTranslationKey()
                    ));
                    put(Where.CLOSE_TO_HIS_OWN_BASE, new ArrayList<>());
                    put(Where.INSIDE_AN_ENEMY_BASE, new ArrayList<>());
                    put(Where.CLOSE_TO_AN_ENEMY_BASE, new ArrayList<>());
                    put(Where.IN_THE_WILD, List.of(
                            Blocks.LAVA.getTranslationKey(),
                            Blocks.WATER.getTranslationKey()
                    ));
                }});
            }});
        }
    }

    private static class EmptyingBucketConfigDefault {
        private static final EmptyingBucketConfig EMPTYING_BUCKET;

        static {
            EMPTYING_BUCKET = new EmptyingBucketConfig(new HashMap<>() {{
                put("minecraft:overworld", new HashMap<>() {{
                    put(Where.INSIDE_HIS_OWN_BASE, List.of(
                            Items.LAVA_BUCKET.getTranslationKey(),
                            Items.WATER_BUCKET.getTranslationKey(),
                            Items.POWDER_SNOW_BUCKET.getTranslationKey(),
                            Items.AXOLOTL_BUCKET.getTranslationKey(),
                            Items.COD_BUCKET.getTranslationKey(),
                            Items.PUFFERFISH_BUCKET.getTranslationKey(),
                            Items.SALMON_BUCKET.getTranslationKey(),
                            Items.TROPICAL_FISH_BUCKET.getTranslationKey()
                    ));
                    put(Where.CLOSE_TO_HIS_OWN_BASE, new ArrayList<>());
                    put(Where.INSIDE_AN_ENEMY_BASE, new ArrayList<>());
                    put(Where.CLOSE_TO_AN_ENEMY_BASE, new ArrayList<>());
                    put(Where.IN_THE_WILD, List.of(
                            Items.LAVA_BUCKET.getTranslationKey(),
                            Items.WATER_BUCKET.getTranslationKey(),
                            Items.POWDER_SNOW_BUCKET.getTranslationKey(),
                            Items.AXOLOTL_BUCKET.getTranslationKey(),
                            Items.COD_BUCKET.getTranslationKey(),
                            Items.PUFFERFISH_BUCKET.getTranslationKey(),
                            Items.SALMON_BUCKET.getTranslationKey(),
                            Items.TROPICAL_FISH_BUCKET.getTranslationKey()
                    ));
                }});
            }});
        }
    }

}
