package io.github.tehstoneman.betterstorage.client.renderer.blockentity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class ModelTankFluid extends Model
{
	protected ModelBlockRenderer fluid;

	public ModelTankFluid()
	{
		super( RenderType::entityCutout );
	}

	public void setLevel( float amount, boolean fillTop, boolean fillBottom, boolean isLighterThanAir )
	{
		// final float height = ( ( fillBottom ? 2.0f : 0.0f ) + 12.0F + ( fillTop ? 2.0f : 0.0f ) ) * amount;
		// final float base = fillBottom ? 0.0f : 2.0f;
		// fluid = new ModelBlockRenderer( 64, 32, 0, 0 ).addBox( 1.1F, isLighterThanAir ? 16.0f - ( base + height ) : base, 1.1F, 13.8F, height, 13.8F );
	}

	@Override
	public void renderToBuffer( PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
			float alpha )
	{
		// fluid.render( matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha );
	}
}
