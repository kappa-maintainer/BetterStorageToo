package io.github.tehstoneman.betterstorage.world.inventory;

import net.minecraftforge.items.ItemStackHandler;

public class KeyringStackHandler extends ItemStackHandler
{
	public KeyringStackHandler( int size )
	{
		super( size );
	}

	@Override
	public int getSlotLimit( int slot )
	{
		return 1;
	}

	public void updateCount()
	{
		onContentsChanged( 0 );
	}
}
