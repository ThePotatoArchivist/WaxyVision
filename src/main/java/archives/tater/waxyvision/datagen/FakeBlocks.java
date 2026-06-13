package archives.tater.waxyvision.datagen;

import archives.tater.waxyvision.OverlayModels;
import archives.tater.waxyvision.WaxyVision;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.function.Function;

public class FakeBlocks {

    private static Block register(Identifier id, Function<BlockBehaviour.Properties, Block> factory) {
        var key = ResourceKey.create(Registries.BLOCK, id);
        var block = factory.apply(BlockBehaviour.Properties.of().setId(key));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }

    private static Block register(String path, Function<BlockBehaviour.Properties, Block> factory) {
        return register(WaxyVision.id(path), factory);
    }

    public static final Block CUBE = register(OverlayModels.CUBE_MODEL, Block::new);
    public static final Block STAIRS = register(OverlayModels.STAIRS_MODEL, properties -> new StairBlock(CUBE.defaultBlockState(), properties));
    public static final Block SLAB = register(OverlayModels.SLAB_MODEL, SlabBlock::new);
    public static final Block DOOR = register("door", properties -> new DoorBlock(BlockSetType.PALE_OAK, properties));
    public static final Block TRAPDOOR = register("trapdoor", properties -> new TrapDoorBlock(BlockSetType.PALE_OAK, properties));
    public static final Block CHAIN = register("chain", ChainBlock::new);
    public static final Block LANTERN = register("lantern", LanternBlock::new);
    public static final Block BARS = register("bars", IronBarsBlock::new);
    public static final Block LIGHTNING_ROD = register("lightning_rod", LightningRodBlock::new);
    public static final Block STRAIGHT_RAIL = register("straight_rail", PoweredRailBlock::new);
    public static final Block CURVED_RAIL = register("curved_rail", RailBlock::new);
    public static final Block BUTTON = register("button", properties -> new ButtonBlock(BlockSetType.PALE_OAK, 0, properties));
    public static final Block PRESSURE_PLATE = register("pressure_plate", properties -> new PressurePlateBlock(BlockSetType.PALE_OAK, properties));
    public static final Block COPPERIER_PRESSURE_PLATE = register("copperier_pressure_plate", CopperierPressurePlateBlock::new);
    public static final Block SIGN = register("sign", properties -> new StandingSignBlock(WoodType.PALE_OAK, properties));
    public static final Block WALL_SIGN = register("wall_sign", properties -> new WallSignBlock(WoodType.PALE_OAK, properties));
    public static final Block HANGING_SIGN = register("hanging_sign", properties -> new CeilingHangingSignBlock(WoodType.PALE_OAK, properties));
    public static final Block WALL_HANGING_SIGN = register("wall_hanging_sign", properties -> new WallHangingSignBlock(WoodType.PALE_OAK, properties));

    public static void init() {

    }

    public static class CopperierPressurePlateBlock extends Block {
        public static final IntegerProperty POWER_10 = IntegerProperty.create("power", 0, 9);

        public CopperierPressurePlateBlock(Properties properties) {
            super(properties);
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(POWER_10);
        }
    }
}
