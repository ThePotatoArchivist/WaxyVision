package archives.tater.waxyvision.datagen;

import archives.tater.waxyvision.mixin.datagen.BlockModelGeneratorsAccessor;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.data.BlockFamilies;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;

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
        var variant = plainVariant(ModelTemplates.LIGHTNING_ROD.create(block, TextureMapping.defaultTexture(block), blockModelGenerators.modelOutput));
        blockModelGenerators.blockStateOutput
                .accept(
                        MultiVariantGenerator.dispatch(block, variant)
                                .with(BlockModelGeneratorsAccessor.getROTATIONS_COLUMN_WITH_FACING())
                );
    }

    private void createCurvedRail(BlockModelGenerators blockModelGenerators, Block rail, Block textureBlock) {
        var texture = TextureMapping.rail(textureBlock);
        var flat = plainVariant(ModelTemplates.RAIL_FLAT.create(rail, texture, blockModelGenerators.modelOutput));
        var risingNE = plainVariant(ModelTemplates.RAIL_RAISED_NE.create(rail, texture, blockModelGenerators.modelOutput));
        var risingSW = plainVariant(ModelTemplates.RAIL_RAISED_SW.create(rail, texture, blockModelGenerators.modelOutput));
        blockModelGenerators.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(rail).with(PropertyDispatch.initial(BlockStateProperties.RAIL_SHAPE)
                        .select(RailShape.NORTH_SOUTH, flat)
                        .select(RailShape.EAST_WEST, flat)
                        .select(RailShape.ASCENDING_EAST, risingNE.with(Y_ROT_90))
                        .select(RailShape.ASCENDING_WEST, risingSW.with(Y_ROT_90))
                        .select(RailShape.ASCENDING_NORTH, risingNE)
                        .select(RailShape.ASCENDING_SOUTH, risingSW)
                        .select(RailShape.SOUTH_EAST, flat)
                        .select(RailShape.SOUTH_WEST, flat)
                        .select(RailShape.NORTH_WEST, flat)
                        .select(RailShape.NORTH_EAST, flat)
                )
        );
    }

    private void createStraightRail(BlockModelGenerators blockModelGenerators, Block rail, Block textureBlock) {
        var texture = TextureMapping.rail(textureBlock);
        var flat = plainVariant(ModelTemplates.RAIL_FLAT.create(rail, texture, blockModelGenerators.modelOutput));
        var risingNE = plainVariant(ModelTemplates.RAIL_RAISED_NE.create(rail, texture, blockModelGenerators.modelOutput));
        var risingSW = plainVariant(ModelTemplates.RAIL_RAISED_SW.create(rail, texture, blockModelGenerators.modelOutput));
        blockModelGenerators.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(rail).with(PropertyDispatch.initial(BlockStateProperties.RAIL_SHAPE_STRAIGHT)
                        .select(RailShape.NORTH_SOUTH, flat)
                        .select(RailShape.EAST_WEST, flat)
                        .select(RailShape.ASCENDING_EAST, risingNE.with(Y_ROT_90))
                        .select(RailShape.ASCENDING_WEST, risingSW.with(Y_ROT_90))
                        .select(RailShape.ASCENDING_NORTH, risingNE)
                        .select(RailShape.ASCENDING_SOUTH, risingSW)
                )
        );
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        var family = BlockFamilies.familyBuilder(FakeBlocks.CUBE)
                .stairs(FakeBlocks.STAIRS)
                .slab(FakeBlocks.SLAB)
                .door(FakeBlocks.DOOR)
                .trapdoor(FakeBlocks.TRAPDOOR)
                .getFamily();
        blockModelGenerators.family(family.getBaseBlock())
                .generateFor(family);
        blockModelGenerators.createAxisAlignedPillarBlockCustomModel(FakeBlocks.CHAIN,
                plainVariant(ModelLocationUtils.getModelLocation(FakeBlocks.CHAIN))
        );
        blockModelGenerators.createLantern(FakeBlocks.LANTERN);
        createBars(blockModelGenerators, FakeBlocks.BARS, FakeBlocks.CUBE, FakeBlocks.BARS);
        createLightningRod(blockModelGenerators, FakeBlocks.LIGHTNING_ROD);
        createStraightRail(blockModelGenerators, FakeBlocks.STRAIGHT_RAIL, FakeBlocks.CUBE);
        createCurvedRail(blockModelGenerators, FakeBlocks.CURVED_RAIL, FakeBlocks.CUBE);
    }


    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {

    }

}
