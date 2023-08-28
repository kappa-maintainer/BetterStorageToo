package io.github.tehstoneman.betterstorage.world.item.cardboard;

import io.github.tehstoneman.betterstorage.api.cardboard.ICardboardItem;
import io.github.tehstoneman.betterstorage.client.renderer.Resources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;

public class CardboardArmorItem extends DyeableArmorItem implements ICardboardItem
{
	//public CardboardArmorItem( EquipmentSlot armorSlot )
	//{
		//super( BetterStorageArmorMaterial.CARDBOARD, armorSlot, new Item.Properties() );// .tab( BetterStorage.CREATIVE_MODE_TAB ) );
	//}

	public CardboardArmorItem(ArmorMaterial p_266710_, Type p_267178_, Properties p_267093_) {
		super(p_266710_, p_267178_, p_267093_);
		//TODO Auto-generated constructor stub
	}

	@Override
	public String getArmorTexture( ItemStack stack, Entity entity, EquipmentSlot slot, String type )
	{
		return ( type != null	? Resources.TEXTURE_EMPTY	: slot == EquipmentSlot.LEGS ? Resources.TEXTURE_CARDBOARD_LEGGINGS
						: Resources.TEXTURE_CARDBOARD_ARMOR ).toString();
	}

	@Override
	public int getColor( ItemStack stack )
	{
		return ICardboardItem.super.getColor( stack );
	}

	@Override
	public boolean hasColor( ItemStack stack )
	{
		return ICardboardItem.super.hasColor( stack );
	}

	@Override
	public void setColor( ItemStack itemStack, int colorRGB )
	{
		ICardboardItem.super.setColor( itemStack, colorRGB );
	}
}
