package io.github.tehstoneman.betterstorage.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.tehstoneman.betterstorage.client.renderer.Resources;
import io.github.tehstoneman.betterstorage.world.inventory.CardboardBoxMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CardboardBoxContainerScreen extends AbstractContainerScreen< CardboardBoxMenu >
{
	private final int rows, columns;
	private final int offsetX, offsetY;

	public CardboardBoxContainerScreen( final CardboardBoxMenu menu, final Inventory playerInventory, final Component title )
	{
		super( menu, playerInventory, title );

		columns = menu.getColumns();
		rows    = menu.getRows();

		imageHeight = Math.max( 14 + columns * 18, 176 );
		imageWidth  = 114 + rows * 18;

		offsetX = Math.max( ( 176 - imageHeight ) / 2, 0 );
		offsetY = 17 + rows * 18;

		titleLabelX     = 8;
		titleLabelY     = 6;
		inventoryLabelX = offsetX + 8;
		inventoryLabelY = offsetY + 3;
	}

	protected ResourceLocation getResource()
	{
		if( columns <= 9 )
			return Resources.CONTAINER_GENERIC;
		return Resources.CONTAINER_EXPANDABLE;
	}

	public void render( final PoseStack poseStack, final int mouseX, final int mouseY, final float partialTicks )
	{
		//renderBackground( poseStack );
		//super.render( poseStack, mouseX, mouseY, partialTicks );
		//renderTooltip( poseStack, mouseX, mouseY );
	}

	protected void renderBg( final PoseStack poseStack, final float partialTicks, final int x, final int y )
	{
		RenderSystem.setShader( GameRenderer::getPositionTexShader );
		RenderSystem.setShaderColor( 1.0F, 1.0F, 1.0F, 1.0F );
		RenderSystem.setShaderTexture( 0, getResource() );

		//blit( poseStack, getGuiLeft(), getGuiTop(), 0, 0, imageHeight, offsetY );
		//blit( poseStack, getGuiLeft(), getGuiTop() + offsetY, 0, 126, imageHeight, 96 );
	}

	@Override
	protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'renderBg'");
	}
}
