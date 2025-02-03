package io.github.tehstoneman.betterstorage.common.item.cardboard;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.api.ICardboardItem;
import io.github.tehstoneman.betterstorage.client.renderer.Resources;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardboardArmor extends ItemArmor implements ICardboardItem, ISpecialArmor
{
	private String					name;

	private static final String[]	armorText	= { "Helmet", "Chestplate", "Leggings", "Boots" };

	public ItemCardboardArmor( EntityEquipmentSlot armorType )
	{
		super( ItemCardboardSheet.armorMaterial, 0, armorType );
	}

	public void register( String name )
	{
		this.name = name;
		setTranslationKey( ModInfo.modId + "." + name );
		setRegistryName( name );
		//GameRegistry.register( this );
	}

	@SideOnly( Side.CLIENT )
	public void registerItemModels()
	{
		ModelLoader.setCustomModelResourceLocation( this, 0, new ModelResourceLocation( getRegistryName(), "inventory" ) );
	}

	@Override
	public String getArmorTexture( ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type )
	{
		return ( type != null ? Resources.textureEmpty
				: slot == EntityEquipmentSlot.LEGS ? Resources.textureCardboardLeggings : Resources.textureCardboardArmor ).toString();
	}

	// ISpecialArmor implementation
	// Makes sure cardboard armor doesn't get destroyed,
	// and is ineffective when durability is at 0..
	@Override
	public ArmorProperties getProperties( EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot )
	{
		return new ArmorProperties( 0, damageReduceAmount / 25D, armor.getMaxDamage() - armor.getItemDamage() );
	}

	@Override
	public int getArmorDisplay( EntityPlayer player, ItemStack armor, int slot )
	{
		return armor.getItemDamage() < armor.getMaxDamage() ? damageReduceAmount : 0;
	}

	@Override
	public void damageArmor( EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot )
	{
		ItemCardboardSheet.damageItem( stack, damage, entity );
	}

	// Cardboard items
	@Override
	public boolean canDye( ItemStack stack )
	{
		return true;
	}

	@Override
	public int getColor( ItemStack itemstack )
	{
		if( hasColor( itemstack ) )
		{
			final NBTTagCompound compound = itemstack.getTagCompound();
			return compound.getInteger( "color" );
		}
		return 0x705030;
	}

	@Override
	public boolean hasColor( ItemStack itemstack )
	{
		if( itemstack.hasTagCompound() )
		{
			final NBTTagCompound compound = itemstack.getTagCompound();
			return compound.hasKey( "color" );
		}
		return false;
	}

	@Override
	public void setColor( ItemStack itemstack, int colorRGB )
	{
		NBTTagCompound compound;
		if( itemstack.hasTagCompound() )
			compound = itemstack.getTagCompound();
		else
			compound = new NBTTagCompound();
		compound.setInteger( "color", colorRGB );
		itemstack.setTagCompound( compound );
	}
}
