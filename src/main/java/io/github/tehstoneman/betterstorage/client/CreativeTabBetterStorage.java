package io.github.tehstoneman.betterstorage.client;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.common.block.BetterStorageBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class CreativeTabBetterStorage extends CreativeTabs
{
	public CreativeTabBetterStorage()
	{
		super( ModInfo.modId );
	}

	@Override
	@SideOnly( Side.CLIENT )
	@Nonnull
	public ItemStack createIcon()
	{
		if( BetterStorageBlocks.CRATE != null )
			return new ItemStack( BetterStorageBlocks.CRATE );
		else
			if( BetterStorageBlocks.REINFORCED_CHEST != null )
				return new ItemStack( BetterStorageBlocks.REINFORCED_CHEST );
			else
				return new ItemStack( Blocks.CHEST );
	}
}
