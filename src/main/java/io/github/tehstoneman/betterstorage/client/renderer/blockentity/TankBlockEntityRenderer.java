package io.github.tehstoneman.betterstorage.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.tehstoneman.betterstorage.world.level.block.TankBlock;
import io.github.tehstoneman.betterstorage.world.level.block.entity.TankBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.FluidStack;

public class TankBlockEntityRenderer extends BetterStorageBlockEntityRenderer< TankBlockEntity >
{
	private static final int TANK_THICKNESS = 2;

	public TankBlockEntityRenderer( BlockEntityRendererProvider.Context context )
	{
		super( context );
	}

	@Override
	public void render( TankBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight,
						int combinedOverlay )
	{
		blockEntity.getCapacity();
		final FluidStack	fluid		= blockEntity.getFluid();
		final Fluid			renderFluid	= fluid.getFluid();

		if( !fluid.isEmpty() )
		{
			poseStack.pushPose();

			final TextureAtlasSprite[] fluidTextures = ForgeHooksClient.getFluidSprites( blockEntity.getLevel(), blockEntity.getBlockPos(),
				renderFluid.defaultFluidState() );

			// final ResourceLocation fluidStill = renderFluid.getFluidType().getStillTexture();
			// final ResourceLocation fluidFlow = renderFluid.getFluidType().getFlowingTexture();
			// final TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas( InventoryMenu.BLOCK_ATLAS ).apply( fluidStill );
			// final TextureAtlasSprite flow = Minecraft.getInstance().getTextureAtlas( InventoryMenu.BLOCK_ATLAS ).apply( fluidFlow );
			final TextureAtlasSprite	sprite	= fluidTextures[0];
			final VertexConsumer		builder	= buffer.getBuffer( RenderType.translucent() );

			fluid.getAmount();
			blockEntity.getCapacity();

			// Vector3f.YP.rotationDegrees( 0 );

			// final int color = renderFluid.getFluidType().getColor();

			final float a = 1.0F;
			// final float r = ( color >> 16 & 0xFF ) / 255.0F;
			// final float g = ( color >> 8 & 0xFF ) / 255.0F;
			// final float b = ( color & 0xFF ) / 255.0F;
			final float	r	= 1.0F;
			final float	g	= 1.0F;
			final float	b	= 1.0F;

			final BlockState	blockState	= blockEntity.getBlockState();
			final float			level		= fluid.getAmount() / (float)blockEntity.getCapacity();
			final float			top			= blockState.getValue( TankBlock.UP ) ? 1.0f : ( 16 - TANK_THICKNESS ) / 16f;
			final float			btm			= blockState.getValue( TankBlock.DOWN ) ? 0.0f : TANK_THICKNESS / 16f;

			final float	x1	= TANK_THICKNESS / 16f;
			final float	x2	= ( 16 - TANK_THICKNESS ) / 16f;

			final float diff = top - btm;

			final float	y1	= renderFluid.getFluidType().isLighterThanAir() ? top - diff * level : btm;
			final float	y2	= renderFluid.getFluidType().isLighterThanAir() ? top : btm + diff * level;

			final float	u1	= x1 * 16;
			final float	u2	= x2 * 16;
			final float	v1	= ( 1 - y1 ) * 16;
			final float	v2	= ( 1 - y2 ) * 16;

			if( blockEntity.getFluidAmountAbove() <= 0 )
			{
				// Top Face
				add( builder, poseStack, x1, y2, x1, sprite.getU( u1 ), sprite.getV( u1 ), r, g, b, a );
				add( builder, poseStack, x1, y2, x2, sprite.getU( u1 ), sprite.getV( u2 ), r, g, b, a );
				add( builder, poseStack, x2, y2, x2, sprite.getU( u2 ), sprite.getV( u2 ), r, g, b, a );
				add( builder, poseStack, x2, y2, x1, sprite.getU( u2 ), sprite.getV( u1 ), r, g, b, a );

				// add( builder, matrixStack, x2, y2, x1, sprite.getU( u2 ), sprite.getV( u1 ), r, g, b, a );
				// add( builder, matrixStack, x2, y2, x2, sprite.getU( u2 ), sprite.getV( u2 ), r, g, b, a );
				// add( builder, matrixStack, x1, y2, x2, sprite.getU( u1 ), sprite.getV( u2 ), r, g, b, a );
				// add( builder, matrixStack, x1, y2, x1, sprite.getU( u1 ), sprite.getV( u1 ), r, g, b, a );
			}

			// North Face
			add( builder, poseStack, x1, y1, x1, sprite.getU( u2 ), sprite.getV( v1 ), r, g, b, a );
			add( builder, poseStack, x1, y2, x1, sprite.getU( u2 ), sprite.getV( v2 ), r, g, b, a );
			add( builder, poseStack, x2, y2, x1, sprite.getU( u1 ), sprite.getV( v2 ), r, g, b, a );
			add( builder, poseStack, x2, y1, x1, sprite.getU( u1 ), sprite.getV( v1 ), r, g, b, a );

			// South Face
			add( builder, poseStack, x2, y1, x2, sprite.getU( u2 ), sprite.getV( v1 ), r, g, b, a );
			add( builder, poseStack, x2, y2, x2, sprite.getU( u2 ), sprite.getV( v2 ), r, g, b, a );
			add( builder, poseStack, x1, y2, x2, sprite.getU( u1 ), sprite.getV( v2 ), r, g, b, a );
			add( builder, poseStack, x1, y1, x2, sprite.getU( u1 ), sprite.getV( v1 ), r, g, b, a );

			// East face
			add( builder, poseStack, x2, y1, x1, sprite.getU( u2 ), sprite.getV( v1 ), r, g, b, a );
			add( builder, poseStack, x2, y2, x1, sprite.getU( u2 ), sprite.getV( v2 ), r, g, b, a );
			add( builder, poseStack, x2, y2, x2, sprite.getU( u1 ), sprite.getV( v2 ), r, g, b, a );
			add( builder, poseStack, x2, y1, x2, sprite.getU( u1 ), sprite.getV( v1 ), r, g, b, a );

			// West face
			add( builder, poseStack, x1, y1, x2, sprite.getU( u2 ), sprite.getV( v1 ), r, g, b, a );
			add( builder, poseStack, x1, y2, x2, sprite.getU( u2 ), sprite.getV( v2 ), r, g, b, a );
			add( builder, poseStack, x1, y2, x1, sprite.getU( u1 ), sprite.getV( v2 ), r, g, b, a );
			add( builder, poseStack, x1, y1, x1, sprite.getU( u1 ), sprite.getV( v1 ), r, g, b, a );

			if( blockEntity.getFluidAmountBelow() <= 0 )
			{
				// Bottom Face
				add( builder, poseStack, x2, y1, x1, sprite.getU( u2 ), sprite.getV( u1 ), r, g, b, a );
				add( builder, poseStack, x2, y1, x2, sprite.getU( u2 ), sprite.getV( u2 ), r, g, b, a );
				add( builder, poseStack, x1, y1, x2, sprite.getU( u1 ), sprite.getV( u2 ), r, g, b, a );
				add( builder, poseStack, x1, y1, x1, sprite.getU( u1 ), sprite.getV( u1 ), r, g, b, a );

				// add( builder, matrixStack, x1, y1, x1, sprite.getU( u1 ), sprite.getV( u1 ), r, g, b, a );
				// add( builder, matrixStack, x1, y1, x2, sprite.getU( u1 ), sprite.getV( u2 ), r, g, b, a );
				// add( builder, matrixStack, x2, y1, x2, sprite.getU( u2 ), sprite.getV( u2 ), r, g, b, a );
				// add( builder, matrixStack, x2, y1, x1, sprite.getU( u2 ), sprite.getV( u1 ), r, g, b, a );
			}

			poseStack.popPose();

		}
	}

	private void add( VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v, float r, float g, float b, float a )
	{
		renderer.vertex( stack.last().pose(), x, y, z ).color( r, g, b, a ).uv( u, v ).uv2( 0, 240 ).normal( 1, 0, 0 ).endVertex();
	}
}
