package io.github.tehstoneman.betterstorage.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.tehstoneman.betterstorage.client.renderer.Resources;
import io.github.tehstoneman.betterstorage.world.inventory.ReinforcedChestContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ReinforcedChestContainerScreen extends AbstractContainerScreen< ReinforcedChestContainerMenu >
{

	private final int	columns, rows;
	//private final int	xSlice1, xSlice2, xSlice3, xSlice4;
	private final int	offsetY;

	public ReinforcedChestContainerScreen( ReinforcedChestContainerMenu container, Inventory playerInventory, Component title )
	{
		super( container, playerInventory, title );

		columns	= container.getColumns();
		rows	= container.getRows();

		imageWidth	= Math.max( 14 + columns * 18, 176 );
		imageHeight	= 114 + rows * 18;

		// Calculate horizontal texture slices
		//xSlice1	= columns * 18 + 7;
		//xSlice2	= ( imageWidth - 176 ) / 2;
		//xSlice3	= imageWidth - xSlice2;
		//xSlice4	= 248 - xSlice2;

		// Calculate vertical texture slices
		offsetY = rows * 18 + 17;

		// GUI label co-ordinates
		titleLabelX		= 8;
		titleLabelY		= 6;
		//inventoryLabelX	= xSlice2 + 8;
		inventoryLabelY	= offsetY + 3;
	}

	public void render( PoseStack matrixStack, int mouseX, int mouseY, float partialTicks )
	{
		//renderBackground( matrixStack );
		//super.render( matrixStack, mouseX, mouseY, partialTicks );
		//renderTooltip( matrixStack, mouseX, mouseY );
	}

	protected ResourceLocation getResource()
	{
		if( columns <= 9 )
			return Resources.CONTAINER_GENERIC;
		return Resources.CONTAINER_EXPANDABLE;
	}

	protected void renderBg( PoseStack matrixStack, float partialTicks, int x, int y )
	{
		RenderSystem.setShader( GameRenderer::getPositionTexShader );
		RenderSystem.setShaderColor( 1.0F, 1.0F, 1.0F, 1.0F );
		RenderSystem.setShaderTexture( 0, getResource() );

		// Chest inventory
		//blit( matrixStack, getGuiLeft(), getGuiTop(), 0, 0, xSlice1, offsetY );
		//blit( matrixStack, getGuiLeft() + xSlice1, getGuiTop(), 241, 0, 7, offsetY );

		// Player inventory
		//blit( matrixStack, getGuiLeft(), getGuiTop() + offsetY, 0, 125, xSlice2, 17 );
		//blit( matrixStack, getGuiLeft() + xSlice2, getGuiTop() + offsetY, 36, 125, 176, 97 );
		//blit( matrixStack, getGuiLeft() + xSlice3, getGuiTop() + offsetY, xSlice4, 125, xSlice2, 17 );
	}

	@Override
	protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'renderBg'");
	}

}
