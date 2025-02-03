package io.github.tehstoneman.betterstorage.common.item;

import io.github.tehstoneman.betterstorage.BetterStorage;
import io.github.tehstoneman.betterstorage.api.EnumReinforced;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemBlockReinforcedLocker extends ItemBlockLocker
{
	public ItemBlockReinforcedLocker( Block block )
	{
		super( block );
		setMaxDamage( 0 );
		setHasSubtypes( true );
	}

	@Override
	public int getMetadata( int metadata )
	{
		return metadata;
	}

	@Override
	@Nonnull
	public String getItemStackDisplayName( ItemStack stack )
	{
		final EnumReinforced material = EnumReinforced.byMetadata( stack.getMetadata() );
		if( material != null )
		{
			final String materialName = BetterStorage.proxy.localize( material.getUnlocalizedName() );
			final String name = BetterStorage.proxy.localize( getTranslationKey() + ".name.full", materialName );
			return name.trim();
		}
		return super.getItemStackDisplayName( stack );
	}

	@Override
	@Nonnull
	public String getTranslationKey( ItemStack stack )
	{
		final EnumReinforced material = EnumReinforced.byMetadata( stack.getMetadata() );
		if( material != null )
		{
			final String materialName = BetterStorage.proxy.localize( material.getUnlocalizedName() );
			final String name = BetterStorage.proxy.localize( getTranslationKey() + ".name.full", materialName );
			return super.getTranslationKey() + "." + material.getUnlocalizedName();
		}
		return super.getTranslationKey();
	}
}
