package io.github.tehstoneman.betterstorage.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.tehstoneman.betterstorage.client.renderer.Resources;
import io.github.tehstoneman.betterstorage.world.inventory.ConfigContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ConfigContainerScreen extends AbstractContainerScreen< ConfigContainerMenu >
{
	public ConfigContainerScreen( ConfigContainerMenu screenContainer, Inventory inv, Component titleIn )
	{
		super( screenContainer, inv, titleIn );
		imageHeight		= 132;
		inventoryLabelY	= imageHeight - 94;
	}

	public void render( PoseStack matrixStack, int mouseX, int mouseY, float partialTicks )
	{
		//renderBackground( matrixStack );
		//super.render( matrixStack, mouseX, mouseY, partialTicks );
		//renderTooltip( matrixStack, mouseX, mouseY );
	}

	protected void renderBg( PoseStack matrixStack, float partialTicks, int x, int y )
	{
		RenderSystem.setShader( GameRenderer::getPositionTexShader );
		RenderSystem.setShaderColor( 1.0F, 1.0F, 1.0F, 1.0F );
		RenderSystem.setShaderTexture( 0, Resources.CONTAINER_CONFIG );

		//blit( matrixStack, getGuiLeft(), getGuiTop(), 0, 0, getXSize(), imageHeight );
		for( int i = 0; i < menu.indexHotbar; i++ )
		{
			//final ConfigSlot slot = (ConfigSlot)menu.getSlot( i );
			//if( !slot.hasItem() )
				//blit( matrixStack, getGuiLeft() + slot.x, getGuiTop() + slot.y, slot.getIconX() + 1, slot.getIconY() + 1, 16, 16 );
		}
	}

	@Override
	protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'renderBg'");
	}
}
