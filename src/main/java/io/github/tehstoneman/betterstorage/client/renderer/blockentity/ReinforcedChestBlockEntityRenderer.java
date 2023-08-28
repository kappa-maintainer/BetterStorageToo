package io.github.tehstoneman.betterstorage.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.tehstoneman.betterstorage.world.level.block.entity.ReinforcedChestBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ReinforcedChestBlockEntityRenderer extends BetterStorageBlockEntityRenderer< ReinforcedChestBlockEntity >
{
	//private static ItemRenderer	itemRenderer;
	//private HexKeyConfig		config;

	public ReinforcedChestBlockEntityRenderer( BlockEntityRendererProvider.Context context )
	{
		super( context );
	}

	@Override
	public void render( ReinforcedChestBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource,
						int combinedLight, int combinedOverlay )
	{
		/*final BlockState	blockState		= blockEntity.hasLevel()	? blockEntity.getBlockState()
																		: BetterStorageBlocks.REINFORCED_CHEST.get().defaultBlockState()
																			.setValue( ReinforcedChestBlock.FACING, Direction.SOUTH );*/
		/*final ConnectedType	connectedType	= blockState.hasProperty( ConnectableContainerBlock.TYPE )
																										? blockState.getValue(
																											ConnectableContainerBlock.TYPE )
																										: ConnectedType.SINGLE;*/

		/*if( connectedType != ConnectedType.SLAVE )
		{
			// final Direction facing = blockState.getValue( ReinforcedChestBlock.FACING );

			config = blockEntity.getConfig();
			if( modelRenderer == null )
				modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
			if( modelManager == null )
				modelManager = Minecraft.getInstance().getModelManager();

			poseStack.pushPose();

			/*
			 * final float f = facing.toYRot();
			 * poseStack.translate( 0.5, 0.5, 0.5 );
			 * poseStack.mulPose( Vector3f.YP.rotationDegrees( -f ) );
			 * poseStack.translate( -0.5, -0.5, -0.5 );
			 */

			/*final BakedModel	modelBase	= modelManager.getModel( getChestModel( connectedType ) );
			final BakedModel	modelLid	= modelManager.getModel( getDoorModel( connectedType ) );

			final BakedModel	modelFrame		= modelManager.getModel( getFrameModel( connectedType ) );
			final BakedModel	modelLidFrame	= modelManager.getModel( getDoorFrameModel( connectedType ) );

			final Level		level		= blockEntity.getLevel();
			final BlockPos	blockPos	= blockEntity.getBlockPos();

			final Pose				pose			= poseStack.last();
			VertexConsumer renderBuffer = bufferSource.getBuffer( Sheets.solidBlockSheet() );
			final Material			frameMaterial	= getFrameMaterial( level, blockPos, config );
			final VertexConsumer	frameBuffer		= frameMaterial.buffer( bufferSource, RenderType::entitySolid );

			renderModel( pose, renderBuffer, blockState, modelBase, combinedLight, combinedOverlay, level, blockPos );
			renderModel( pose, frameBuffer, blockState, modelFrame, combinedLight, combinedOverlay, level, blockPos );
			rotateLid( blockEntity, partialTicks, poseStack );
			renderModel( pose, renderBuffer, blockState, modelLid, combinedLight, combinedOverlay, level, blockPos );
			renderModel( pose, frameBuffer, blockState, modelLidFrame, combinedLight, combinedOverlay, level, blockPos );

			renderItem( blockEntity, partialTicks, poseStack, bufferSource, combinedLight, blockState );

			poseStack.popPose();
		}*/
	}

	/*private ResourceLocation getChestModel( ConnectedType connectedType )
	{
		return connectedType == ConnectedType.SINGLE ? Resources.MODEL_REINFORCED_CHEST : Resources.MODEL_REINFORCED_CHEST_LARGE;
	}*/

	/*private ResourceLocation getDoorFrameModel( ConnectedType connectedType )
	{
		return connectedType == ConnectedType.SINGLE ? Resources.MODEL_REINFORCED_CHEST_LID_FRAME : Resources.MODEL_REINFORCED_CHEST_LID_LARGE_FRAME;
	}*/

	/*private ResourceLocation getDoorModel( ConnectedType connectedType )
	{
		return connectedType == ConnectedType.SINGLE ? Resources.MODEL_REINFORCED_CHEST_LID : Resources.MODEL_REINFORCED_CHEST_LID_LARGE;
	}*/

	/*private Material getFrameMaterial( Level level, BlockPos blockPos, HexKeyConfig config )
	{
		final ItemStack itemStack = config.getStackInSlot( HexKeyConfig.SLOT_APPEARANCE );
		if( !itemStack.isEmpty() )
		{
			final Item item = itemStack.getItem();
			if( item instanceof BlockItem )
			{
				final BlockState			state	= ( (BlockItem)item ).getBlock().defaultBlockState();
				final TextureAtlasSprite	texture	= Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture( state, level,
					blockPos );
				return new Material( InventoryMenu.BLOCK_ATLAS, texture.atlasLocation() );
			}
		}
		return new Material( InventoryMenu.BLOCK_ATLAS, Resources.TEXTURE_REINFORCED_FRAME );
	}*/

	/*private ResourceLocation getFrameModel( ConnectedType connectedType )
	{
		return connectedType == ConnectedType.SINGLE ? Resources.MODEL_REINFORCED_CHEST_FRAME : Resources.MODEL_REINFORCED_CHEST_LARGE_FRAME;
	}*/

	/**
	 * Renders attached lock on chest. Adapted from vanilla item frame
	 *
	 * @param blockEntity
	 * @param partialTicks
	 * @param matrixStack
	 * @param buffer
	 * @param packedLight
	 * @param state
	 */
	/*private void renderItem(	ReinforcedChestBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
								int packedLight, BlockState state )
	{
		final ItemStack itemStack = blockEntity.getLock();

		if( !itemStack.isEmpty() )
		{
			if( itemRenderer == null )
				itemRenderer = Minecraft.getInstance().getItemRenderer();

			matrixStack.translate( blockEntity.isConnected() ? 1.0 : 0.5, -0.1875, 0.9375 );

			matrixStack.scale( 0.5F, 0.5F, 0.5F );
			//itemRenderer.renderStatic( itemStack, ItemTransforms.TransformType.FIXED, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer, 0 );
		}
	}*/

	/*private void renderModel(	Pose pose, VertexConsumer renderBuffer, BlockState blockState, BakedModel bakedModel, int combinedLight,
								int combinedOverlay, Level level, BlockPos blockPos )
	{
		final ModelData modelData = bakedModel.getModelData( level, blockPos, blockState, level.getModelDataManager().getAt( blockPos ) );
		modelRenderer.renderModel( pose, renderBuffer, blockState, bakedModel, 1.0f, 1.0f, 1.0f, combinedLight, combinedOverlay, modelData,
			RenderType.solid() );
	}*/

	/*private void rotateLid( ReinforcedChestBlockEntity blockEntity, float partialTicks, PoseStack matrixStack )
	{
		float angle = ( (LidBlockEntity)blockEntity ).getOpenNess( partialTicks );
		angle	= 1.0F - angle;
		angle	= 1.0F - angle * angle * angle;
		matrixStack.translate( 0.0, 0.5625, 0.0 );
		//matrixStack.mulPose( Vector3f.XP.rotation( -angle ) );
	}*/
}
