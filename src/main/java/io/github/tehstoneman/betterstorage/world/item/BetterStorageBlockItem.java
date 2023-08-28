package io.github.tehstoneman.betterstorage.world.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BetterStorageBlockItem extends BlockItem
{
	public BetterStorageBlockItem( Block block )
	{
		this( block, new Item.Properties() );
	}

	public BetterStorageBlockItem( Block block, Properties properties )
	{
		super( block, properties );// .tab( BetterStorage.CREATIVE_MODE_TAB ) );
	}
}
