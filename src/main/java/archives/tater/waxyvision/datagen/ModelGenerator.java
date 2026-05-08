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

import static net.minecraft.client.data.models.BlockModelGenerators.Y_ROT_90;
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

    private void createTrapdoor(BlockModelGenerators blockModelGenerators, Block trapdoor, Block textureBlock) {
        var texture = TextureMapping.defaultTexture(textureBlock);
        blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createTrapdoor(
                trapdoor,
                plainVariant(ModelTemplates.TRAPDOOR_TOP.create(trapdoor, texture, blockModelGenerators.modelOutput)),
                plainVariant(ModelTemplates.TRAPDOOR_BOTTOM.create(trapdoor, texture, blockModelGenerators.modelOutput)),
                plainVariant(ModelTemplates.TRAPDOOR_OPEN.create(trapdoor, texture, blockModelGenerators.modelOutput))
        ));
    }

    private void createRails(BlockModelGenerators blockModelGenerators, Identifier id, Block curvedRail, Block straightRail, Block textureBlock) {
        var texture = TextureMapping.rail(textureBlock);
        var flat = plainVariant(ModelTemplates.RAIL_FLAT.create(id, texture, blockModelGenerators.modelOutput));
        var risingNE = plainVariant(ModelTemplates.RAIL_RAISED_NE.create(id.withSuffix("_raised_ne"), texture, blockModelGenerators.modelOutput));
        var risingSW = plainVariant(ModelTemplates.RAIL_RAISED_SW.create(id.withSuffix("_raised_sw"), texture, blockModelGenerators.modelOutput));

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

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        var family = BlockFamilies.familyBuilder(FakeBlocks.CUBE)
                .stairs(FakeBlocks.STAIRS)
                .slab(FakeBlocks.SLAB)
                .door(FakeBlocks.DOOR)
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
        createRails(blockModelGenerators, WaxyVision.id("rail"), FakeBlocks.CURVED_RAIL, FakeBlocks.STRAIGHT_RAIL, FakeBlocks.CUBE);
    }


    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {

    }

}
