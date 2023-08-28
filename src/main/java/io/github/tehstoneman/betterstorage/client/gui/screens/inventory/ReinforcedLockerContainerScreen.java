package io.github.tehstoneman.betterstorage.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.tehstoneman.betterstorage.client.renderer.Resources;
import io.github.tehstoneman.betterstorage.world.inventory.ReinforcedLockerContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ReinforcedLockerContainerScreen extends AbstractContainerScreen< ReinforcedLockerContainerMenu >
{
	private final int	columns, rows;
	private final int	xSlice2;
	private final int	offsetY;

	public ReinforcedLockerContainerScreen( ReinforcedLockerContainerMenu container, Inventory playerInventory, Component title )
	{
		super( container, playerInventory, title );

		columns	= container.getColumns();
		rows	= container.getRows();

		imageWidth	= Math.max( 14 + columns * 18, 176 );
		imageHeight	= 114 + rows * 18;
		//container.startOpen( playerInventory.player );

		xSlice2 = ( imageWidth - 176 ) / 2;
		// Calculate vertical texture slices
		offsetY = rows * 18 + 17;

		// GUI label co-ordinates
		titleLabelX		= 8;
		titleLabelY		= 6;
		inventoryLabelX	= xSlice2 + 8;
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

		// Locker inventory
		//blit( matrixStack, getGuiLeft(), getGuiTop(), 0, 0, imageWidth, offsetY );

		// Player inventory
		//blit( matrixStack, getGuiLeft(), getGuiTop() + offsetY, 0, 125, imageWidth, 97 );
	}

	@Override
	protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'renderBg'");
	}
}
