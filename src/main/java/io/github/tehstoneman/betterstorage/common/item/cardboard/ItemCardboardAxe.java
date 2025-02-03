package io.github.tehstoneman.betterstorage.common.item.cardboard;

import io.github.tehstoneman.betterstorage.BetterStorage;
import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.api.ICardboardItem;
import io.github.tehstoneman.betterstorage.utils.MiscUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardboardAxe extends ItemAxe implements ICardboardItem
{
	private String name;

	public ItemCardboardAxe()
	{
		super( ToolMaterial.WOOD );
		//super( ItemCardboardSheet.toolMaterial );
		this.name = "cardboard_axe";
	}

	public void register()
	{
		setTranslationKey( ModInfo.modId + "." + name );
		setRegistryName( name );
		//GameRegistry.register( this );
	}

	@SideOnly( Side.CLIENT )
	public void registerItemModels()
	{
		ModelLoader.setCustomModelResourceLocation( this, 0, new ModelResourceLocation( getRegistryName(), "inventory" ) );
	}

	// Makes sure cardboard tools don't get destroyed,
	// and are ineffective when durability is at 0.
	@Override
	public boolean canHarvestBlock( IBlockState block, ItemStack stack )
	{
		return ItemCardboardSheet.canHarvestBlock( stack, super.canHarvestBlock( block, stack ) );
	}

	@Override
	public boolean onLeftClickEntity( ItemStack stack, EntityPlayer player, Entity entity )
	{
		return !ItemCardboardSheet.isEffective( stack );
	}

	@Override
	public boolean onBlockDestroyed( ItemStack stack, World world, IBlockState block, BlockPos pos, EntityLivingBase entity )
	{
		return block.getBlockHardness( world, pos ) > 0 ? ItemCardboardSheet.damageItem( stack, 1, entity ) : true;
	}

	@Override
	public boolean hitEntity( ItemStack stack, EntityLivingBase target, EntityLivingBase player )
	{
		return ItemCardboardSheet.damageItem( stack, 1, player );
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
