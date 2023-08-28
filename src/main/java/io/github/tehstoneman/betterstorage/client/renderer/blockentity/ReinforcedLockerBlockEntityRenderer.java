package io.github.tehstoneman.betterstorage.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.tehstoneman.betterstorage.api.ConnectedType;
import io.github.tehstoneman.betterstorage.client.renderer.Resources;
import io.github.tehstoneman.betterstorage.world.item.HexKeyConfig;
import io.github.tehstoneman.betterstorage.world.level.block.ConnectableContainerBlock;
import io.github.tehstoneman.betterstorage.world.level.block.LockerBlock;
import io.github.tehstoneman.betterstorage.world.level.block.entity.ReinforcedLockerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraftforge.client.model.data.ModelData;

public class ReinforcedLockerBlockEntityRenderer extends BetterStorageBlockEntityRenderer< ReinforcedLockerBlockEntity >
{
	private HexKeyConfig	config;
	private ItemRenderer	itemRenderer;

	public ReinforcedLockerBlockEntityRenderer( BlockEntityRendererProvider.Context context )
	{
		super( context );
	}

	@Override
	public void render( ReinforcedLockerBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource,
						int combinedLight, int combinedOverlay )
	{
		final BlockState	blockState		= blockEntity.getBlockState();
		final ConnectedType	connectedType	= blockState.getValue( ConnectableContainerBlock.TYPE );

		if( connectedType != ConnectedType.SLAVE )
		{
			final DoorHingeSide	hingeSide	= blockState.getValue( BlockStateProperties.DOOR_HINGE );
			final Direction		facing		= blockState.getValue( LockerBlock.FACING );

			config = blockEntity.getConfig();
			if( modelRenderer == null )
				modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
			if( modelManager == null )
				modelManager = Minecraft.getInstance().getModelManager();

			poseStack.pushPose();

			facing.toYRot();

			final BakedModel	modelBase	= modelManager.getModel( getLockerModel( connectedType ) );
			final BakedModel	modelDoor	= modelManager.getModel( getDoorModel( connectedType, hingeSide ) );

			final BakedModel	modelFrame		= modelManager.getModel( getFrameModel( connectedType ) );
			final BakedModel	modelDoorFrame	= modelManager.getModel( getDoorFrameModel( connectedType, hingeSide ) );

			final Level		level		= blockEntity.getLevel();
			final BlockPos	blockPos	= blockEntity.getBlockPos();

			final Pose				pose			= poseStack.last();
			final VertexConsumer	renderBuffer	= bufferSource.getBuffer( Sheets.solidBlockSheet() );
			final Material			frameMaterial	= getFrameMaterial( level, blockPos, config );
			final VertexConsumer	frameBuffer		= frameMaterial.buffer( bufferSource, RenderType::entitySolid );

			renderModel( pose, renderBuffer, blockState, modelBase, combinedLight, combinedOverlay, level, blockPos );
			renderModel( pose, frameBuffer, blockState, modelFrame, combinedLight, combinedOverlay, level, blockPos );
			rotateDoor( blockEntity, partialTicks, poseStack, hingeSide );
			renderModel( pose, renderBuffer, blockState, modelDoor, combinedLight, combinedOverlay, level, blockPos );
			renderModel( pose, frameBuffer, blockState, modelDoorFrame, combinedLight, combinedOverlay, level, blockPos );
			poseStack.translate( hingeSide == DoorHingeSide.LEFT ? 0.0 : -1.0, 0.0, -0.8125 );

			renderItem( blockEntity, partialTicks, poseStack, bufferSource, combinedLight, blockState );

			poseStack.popPose();
		}
	}

	private ResourceLocation getDoorFrameModel( ConnectedType connectedType, DoorHingeSide hingeSide )
	{
		return connectedType == ConnectedType.SINGLE	? hingeSide == DoorHingeSide.LEFT	? Resources.MODEL_REINFORCED_LOCKER_DOOR_FRAME_L
																							: Resources.MODEL_REINFORCED_LOCKER_DOOR_FRAME_R
								: hingeSide == DoorHingeSide.LEFT ? Resources.MODEL_REINFORCED_LOCKER_DOOR_LARGE_FRAME_L
								: Resources.MODEL_REINFORCED_LOCKER_DOOR_LARGE_FRAME_R;
	}

	private ResourceLocation getDoorModel( ConnectedType connectedType, DoorHingeSide hingeSide )
	{
		return connectedType == ConnectedType.SINGLE	? hingeSide == DoorHingeSide.LEFT	? Resources.MODEL_REINFORCED_LOCKER_DOOR_L
																							: Resources.MODEL_REINFORCED_LOCKER_DOOR_R
								: hingeSide == DoorHingeSide.LEFT ? Resources.MODEL_REINFORCED_LOCKER_DOOR_LARGE_L
								: Resources.MODEL_REINFORCED_LOCKER_DOOR_LARGE_R;
	}

	private Material getFrameMaterial( Level level, BlockPos blockPos, HexKeyConfig config )
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
	}

	private ResourceLocation getFrameModel( ConnectedType connectedType )
	{
		return connectedType == ConnectedType.SINGLE ? Resources.MODEL_REINFORCED_LOCKER_FRAME : Resources.MODEL_REINFORCED_LOCKER_LARGE_FRAME;
	}

	private ResourceLocation getLockerModel( ConnectedType connectedType )
	{
		return connectedType == ConnectedType.SINGLE ? Resources.MODEL_REINFORCED_LOCKER : Resources.MODEL_REINFORCED_LOCKER_LARGE;
	}

	private void renderItem(	ReinforcedLockerBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
								int packedLight, BlockState state )
	{
		final ItemStack itemstack = blockEntity.getLock();
		if( !itemstack.isEmpty() )
		{
			if( itemRenderer == null )
				itemRenderer = Minecraft.getInstance().getItemRenderer();
			float openAngle = ( (LidBlockEntity)blockEntity ).getOpenNess( partialTicks );
			openAngle	= 1.0F - openAngle;
			openAngle	= 1.0F - openAngle * openAngle * openAngle;
			final boolean left = state.getValue( DoorBlock.HINGE ) == DoorHingeSide.LEFT;
			matrixStack.translate( 0.0, 0.0, 0.8125 );
			// matrixStack.translate( left ? 0.0 : 1.0, 0.0, 0.0 );
			// matrixStack.rotate( Vector3f.YP.rotationDegrees( left ? -openAngle * 90 : openAngle * 90 ) );
			// matrixStack.translate( left ? -0.0 : -1.0, 0.0, 0.0 );
			matrixStack.translate( left ? 0.8125 : 0.1875, blockEntity.isConnected() ? 0.875 : 0.375, 0.125 );
			matrixStack.scale( 0.5F, 0.5F, 0.5F );
			//itemRenderer.renderStatic( itemstack, ItemTransforms.TransformType.FIXED, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer, 0 );
		}
	}

	private void renderModel(	Pose pose, VertexConsumer renderBuffer, BlockState blockState, BakedModel bakedModel, int combinedLight,
								int combinedOverlay, Level level, BlockPos blockPos )
	{
		final ModelData modelData = bakedModel.getModelData( level, blockPos, blockState, level.getModelDataManager().getAt( blockPos ) );
		modelRenderer.renderModel( pose, renderBuffer, blockState, bakedModel, 1.0f, 1.0f, 1.0f, combinedLight, combinedOverlay, modelData,
			RenderType.solid() );
	}

	private void rotateDoor( ReinforcedLockerBlockEntity tileEntityLocker, float partialTicks, PoseStack matrixStack, DoorHingeSide hingeSide )
	{
		float angle = ( (LidBlockEntity)tileEntityLocker ).getOpenNess( partialTicks );
		angle	= 1.0F - angle;
		angle	= 1.0F - angle * angle * angle;
		matrixStack.translate( hingeSide == DoorHingeSide.LEFT ? 0.0 : 1.0, 0.0, 0.8125 );
		// matrixStack.mulPose( Vector3f.YP.rotation( hingeSide == DoorHingeSide.LEFT ? -angle : angle ) );
	}
}
