package archives.tater.waxyvision.datagen;

import archives.tater.waxyvision.WaxyVision;
import archives.tater.waxyvision.mixin.datagen.BlockModelGeneratorsAccessor;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.data.BlockFamilies;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;

import java.util.function.Function;

import static net.minecraft.client.data.models.BlockModelGenerators.*;
import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

public class ModelGenerator extends FabricModelProvider {

    public ModelGenerator(FabricPackOutput output) {
        super(output);
    }

    private void createBars(BlockModelGenerators blockModelGenerators, Block block, Block sideTexture, Block topTexture) {
        var side = TextureMapping.bars(sideTexture).put(TextureSlot.EDGE, TextureMapping.getBlockTexture(topTexture));
        var top = TextureMapping.bars(topTexture);
        blockModelGenerators.createBars(
                block,
                ModelTemplates.BARS_POST_ENDS.create(block, top, blockModelGenerators.modelOutput),
                ModelTemplates.BARS_POST.create(block, top, blockModelGenerators.modelOutput),
                ModelTemplates.BARS_CAP.create(block, top, blockModelGenerators.modelOutput),
                ModelTemplates.BARS_CAP_ALT.create(block, top, blockModelGenerators.modelOutput),
                ModelTemplates.BARS_POST_SIDE.create(block, side, blockModelGenerators.modelOutput),
                ModelTemplates.BARS_POST_SIDE_ALT.create(block, side, blockModelGenerators.modelOutput)
        );
    }

    private void createLightningRod(BlockModelGenerators blockModelGenerators, Block block) {
        blockModelGenerators.blockStateOutput
                .accept(
                        MultiVariantGenerator.dispatch(block, plainVariant(ModelTemplates.LIGHTNING_ROD.create(block, TextureMapping.defaultTexture(block), blockModelGenerators.modelOutput)))
                                .with(BlockModelGeneratorsAccessor.getROTATIONS_COLUMN_WITH_FACING())
                );
    }

    private void createTrapdoor(BlockModelGenerators blockModelGenerators, Block trapdoor, Block texture) {
        var mapping = TextureMapping.defaultTexture(texture);
        blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createTrapdoor(
                trapdoor,
                plainVariant(ModelTemplates.TRAPDOOR_TOP.create(trapdoor, mapping, blockModelGenerators.modelOutput)),
                plainVariant(ModelTemplates.TRAPDOOR_BOTTOM.create(trapdoor, mapping, blockModelGenerators.modelOutput)),
                plainVariant(ModelTemplates.TRAPDOOR_OPEN.create(trapdoor, mapping, blockModelGenerators.modelOutput))
        ));
    }

    private void createRails(BlockModelGenerators blockModelGenerators, Identifier id, Block curvedRail, Block straightRail, Block texture) {
        var mapping = TextureMapping.rail(texture);
        var flat = plainVariant(ModelTemplates.RAIL_FLAT.create(id, mapping, blockModelGenerators.modelOutput));
        var risingNE = plainVariant(ModelTemplates.RAIL_RAISED_NE.create(id.withSuffix("_raised_ne"), mapping, blockModelGenerators.modelOutput));
        var risingSW = plainVariant(ModelTemplates.RAIL_RAISED_SW.create(id.withSuffix("_raised_sw"), mapping, blockModelGenerators.modelOutput));

        Function<RailShape, MultiVariant> generator = shape -> switch (shape) {
            case ASCENDING_EAST -> risingNE.with(Y_ROT_90);
            case ASCENDING_WEST -> risingSW.with(Y_ROT_90);
            case ASCENDING_NORTH -> risingNE;
            case ASCENDING_SOUTH -> risingSW;
            default -> flat;
        };
        blockModelGenerators.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(curvedRail).with(
                        PropertyDispatch.initial(BlockStateProperties.RAIL_SHAPE).generate(generator)
                )
        );
        blockModelGenerators.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(straightRail).with(
                        PropertyDispatch.initial(BlockStateProperties.RAIL_SHAPE_STRAIGHT).generate(generator)
                )
        );
    }

    private void createPressurePlate(BlockModelGenerators blockModelGenerators, Block block) {
        var mapping = TextureMapping.defaultTexture(block);
        blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createPressurePlate(
                block,
                plainVariant(ModelTemplates.PRESSURE_PLATE_UP.create(block, mapping, blockModelGenerators.modelOutput)),
                plainVariant(ModelTemplates.PRESSURE_PLATE_DOWN.create(block, mapping, blockModelGenerators.modelOutput))
        ));
    }

    private void createCopperierPressurePlate(BlockModelGenerators blockModelGenerators, Block block, Block normal) {
        var mapping = TextureMapping.defaultTexture(normal);
        var off = plainVariant(ModelTemplates.PRESSURE_PLATE_UP.create(block, mapping, blockModelGenerators.modelOutput));
        var on = plainVariant(ModelTemplates.PRESSURE_PLATE_DOWN.create(block, mapping, blockModelGenerators.modelOutput));
        blockModelGenerators.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(FakeBlocks.CopperierPressurePlateBlock.POWER_10)
                                .generate(power -> power == 0 ? off : on)
        ));
    }

    private void createButton(BlockModelGenerators blockModelGenerators, Block block) {
        var mapping = TextureMapping.defaultTexture(block);
        blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createButton(
                block,
                plainVariant(ModelTemplates.BUTTON.create(block, mapping, blockModelGenerators.modelOutput)),
                plainVariant(ModelTemplates.BUTTON_PRESSED.create(block, mapping, blockModelGenerators.modelOutput))
        ));
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        var family = BlockFamilies.familyBuilder(FakeBlocks.CUBE)
                .stairs(FakeBlocks.STAIRS)
                .slab(FakeBlocks.SLAB)
                .door(FakeBlocks.DOOR)
                .sign(FakeBlocks.SIGN, FakeBlocks.WALL_SIGN)
                .hangingSign(FakeBlocks.HANGING_SIGN, FakeBlocks.WALL_HANGING_SIGN)
                .getFamily();
        blockModelGenerators.family(family.getBaseBlock())
                .generateFor(family);
        blockModelGenerators.createAxisAlignedPillarBlockCustomModel(FakeBlocks.CHAIN,
                plainVariant(ModelLocationUtils.getModelLocation(FakeBlocks.CHAIN))
        );
        blockModelGenerators.createLantern(FakeBlocks.LANTERN);
        createTrapdoor(blockModelGenerators, FakeBlocks.TRAPDOOR, FakeBlocks.CUBE);
        createBars(blockModelGenerators, FakeBlocks.BARS, FakeBlocks.CUBE, FakeBlocks.BARS);
        createLightningRod(blockModelGenerators, FakeBlocks.LIGHTNING_ROD);
        createRails(blockModelGenerators, WaxyVision.id("block/rail"), FakeBlocks.CURVED_RAIL, FakeBlocks.STRAIGHT_RAIL, FakeBlocks.CUBE);
        createPressurePlate(blockModelGenerators, FakeBlocks.PRESSURE_PLATE);
        createCopperierPressurePlate(blockModelGenerators, FakeBlocks.COPPERIER_PRESSURE_PLATE, FakeBlocks.PRESSURE_PLATE);
        createButton(blockModelGenerators, FakeBlocks.BUTTON);
    }


    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {

    }

}
