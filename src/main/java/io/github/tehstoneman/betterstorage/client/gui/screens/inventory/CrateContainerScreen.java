package io.github.tehstoneman.betterstorage.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.tehstoneman.betterstorage.client.renderer.Resources;
import io.github.tehstoneman.betterstorage.world.inventory.CrateMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CrateContainerScreen extends AbstractContainerScreen< CrateMenu >
{
	private final int	rows;
	private final int	offsetY;

	public CrateContainerScreen( CrateMenu container, Inventory playerInventory, Component title )
	{
		super( container, playerInventory, title );

		rows	= container.getRows();
		height	= 114 + rows * 18;
		offsetY	= 17 + rows * 18;

		// GUI label co-ordinates
		titleLabelX		= 8;
		titleLabelY		= 6;
		inventoryLabelX	= 8;
		inventoryLabelY	= offsetY + 3;
	}

	public void render( PoseStack matrixStack, int mouseX, int mouseY, float partialTicks )
	{
		//renderBackground( matrixStack );
		//super.render( matrixStack, mouseX, mouseY, partialTicks );

		if( mouseX >= getGuiLeft() + 115 && mouseX < getGuiLeft() + 169 && mouseY >= getGuiTop() + 7 && mouseY < getGuiTop() + 13 )
		{
			//final Component			toolTip	= Component.translatable( ModInfo.CONTAINER_CAPACITY, menu.getVolume() );
			//final List< Component >	list1	= Lists.newArrayList( toolTip );
			//( matrixStack, list1, mouseX, mouseY );
		} //else
			//renderTooltip( matrixStack, mouseX, mouseY );
	}

	protected void renderBg( PoseStack matrixStack, float partialTicks, int x, int y )
	{
		RenderSystem.setShader( GameRenderer::getPositionTexShader );
		RenderSystem.setShaderColor( 1.0F, 1.0F, 1.0F, 1.0F );
		RenderSystem.setShaderTexture( 0, Resources.CONTAINER_CRATE );

		//blit( matrixStack, getGuiLeft(), getGuiTop(), 0, 0, getXSize(), offsetY );
		//blit( matrixStack, getGuiLeft(), getGuiTop() + offsetY, 0, 126, getXSize(), 96 );

		//final int volume = menu.getVolume();
		//if( volume > 0.0 )
			//blit( matrixStack, getGuiLeft() + 115, getGuiTop() + 7, 176, 0, (int)( 54 * ( volume / 100.0 ) ), 6 );
	}

	@Override
	protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'renderBg'");
	}
}
