package io.github.tehstoneman.betterstorage.client.gui.screens.inventory;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.tehstoneman.betterstorage.client.renderer.Resources;
import io.github.tehstoneman.betterstorage.world.inventory.KeyringContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class KeyringContainerScreen extends AbstractContainerScreen< KeyringContainerMenu >
{

	public KeyringContainerScreen( KeyringContainerMenu container, Inventory playerInventory, Component title )
	{
		super( container, playerInventory, title );
		height = 131;

		// GUI label co-ordinates
		titleLabelX		= 8;
		titleLabelY		= 6;
		inventoryLabelX	= 8;
		inventoryLabelY	= 38;
	}

	public void render( PoseStack matrixStack, int mouseX, int mouseY, float partialTicks )
	{
		//renderBackground( matrixStack );
		//super.render( matrixStack, mouseX, mouseY, partialTicks );
		//renderTooltip( matrixStack, mouseX, mouseY );
	}

	@SuppressWarnings( "null" )
	protected void renderBg( PoseStack matrixStack, float partialTicks, int x, int y )
	{
		minecraft.getTextureManager().getTexture( Resources.CONTAINER_GENERIC );

		//blit( matrixStack, getGuiLeft(), getGuiTop(), 0, 0, getXSize(), 35 );
		//blit( matrixStack, getGuiLeft(), getGuiTop() + 35, 0, 125, getXSize(), 97 );
	}

	@Override
	protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'renderBg'");
	}

}