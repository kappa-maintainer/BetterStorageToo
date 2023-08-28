package io.github.tehstoneman.betterstorage.client.renderer.blockentity;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.tehstoneman.betterstorage.api.ConnectedType;
import io.github.tehstoneman.betterstorage.client.renderer.Resources;
import io.github.tehstoneman.betterstorage.world.level.block.ConnectableContainerBlock;
import io.github.tehstoneman.betterstorage.world.level.block.entity.LockerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraftforge.client.model.data.ModelData;

public class LockerBlockEntityRenderer extends BetterStorageBlockEntityRenderer< LockerBlockEntity >
{
	public LockerBlockEntityRenderer( BlockEntityRendererProvider.Context context )
	{
		super( context );
	}

	@Override
	public void render( LockerBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight,
						int combinedOverlay )
	{
		final BlockState	blockState	= blockEntity.getBlockState();
		final ConnectedType	lockerType	= blockState.getValue( ConnectableContainerBlock.TYPE );
		if( lockerType != ConnectedType.SLAVE )
		{
			final DoorHingeSide	hingeSide	= blockState.getValue( BlockStateProperties.DOOR_HINGE );
			//final Direction		facing		= blockState.getValue( LockerBlock.FACING );

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
			final BakedModel	modelBase	= modelManager.getModel( getLockerModel( lockerType ) );
			final BakedModel	modelDoor	= modelManager.getModel( getDoorModel( lockerType, hingeSide ) );

			final Level		level		= blockEntity.getLevel();
			final BlockPos	blockPos	= blockEntity.getBlockPos();

			final Pose				pose			= poseStack.last();
			final VertexConsumer	renderBuffer	= bufferSource.getBuffer( Sheets.solidBlockSheet() );

			renderModel( pose, renderBuffer, blockState, modelBase, combinedLight, combinedOverlay, level, blockPos );
			rotateDoor( blockEntity, partialTicks, poseStack, hingeSide );
			renderModel( pose, renderBuffer, blockState, modelDoor, combinedLight, combinedOverlay, level, blockPos );
			poseStack.translate( hingeSide == DoorHingeSide.LEFT ? 0.0 : -1.0, 0.0, -0.8125 );

			poseStack.popPose();
		}
	}

	private ResourceLocation getDoorModel( ConnectedType connectedType, DoorHingeSide hingeSide )
	{
		return connectedType == ConnectedType.SINGLE	? hingeSide == DoorHingeSide.LEFT ? Resources.MODEL_LOCKER_DOOR_L : Resources.MODEL_LOCKER_DOOR_R
								: hingeSide == DoorHingeSide.LEFT ? Resources.MODEL_LOCKER_DOOR_LARGE_L : Resources.MODEL_LOCKER_DOOR_LARGE_R;
	}

	private ResourceLocation getLockerModel( ConnectedType lockerType )
	{
		return lockerType == ConnectedType.SINGLE ? Resources.MODEL_LOCKER : Resources.MODEL_LOCKER_LARGE;
	}

	private void renderModel(	Pose pose, VertexConsumer renderBuffer, BlockState blockState, BakedModel bakedModel, int combinedLight,
								int combinedOverlay, Level level, BlockPos blockPos )
	{
		final ModelData modelData = bakedModel.getModelData( level, blockPos, blockState, level.getModelDataManager().getAt( blockPos ) );
		modelRenderer.renderModel( pose, renderBuffer, blockState, bakedModel, 1.0f, 1.0f, 1.0f, combinedLight, combinedOverlay, modelData,
			RenderType.solid() );
	}

	private void rotateDoor( LockerBlockEntity tileEntityLocker, float partialTicks, PoseStack matrixStack, DoorHingeSide hingeSide )
	{
		float angle = ( (LidBlockEntity)tileEntityLocker ).getOpenNess( partialTicks );
		angle	= 1.0F - angle;
		angle	= 1.0F - angle * angle * angle;
		matrixStack.translate( hingeSide == DoorHingeSide.LEFT ? 0.0 : 1.0, 0.0, 0.8125 );
		//matrixStack.mulPose( Vector3f.YP.rotation( hingeSide == DoorHingeSide.LEFT ? -angle : angle ) );
	}
}
