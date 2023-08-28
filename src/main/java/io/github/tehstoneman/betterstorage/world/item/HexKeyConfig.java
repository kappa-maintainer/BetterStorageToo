package io.github.tehstoneman.betterstorage.world.item;

import io.github.tehstoneman.betterstorage.api.IHexKeyConfig;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Holds the configuration for adjustable storage containers
 *
 * @author TehStoneMan
 */
public class HexKeyConfig extends ItemStackHandler implements IHexKeyConfig
{
	public static int SLOT_APPEARANCE = 0;

	public HexKeyConfig()
	{
		super( 1 );
	}

	@Override
	public int getSlotLimit( int slot )
	{
		return 1;
	}

	@Override
	public boolean isEmpty()
	{
		return getStackInSlot( SLOT_APPEARANCE ).isEmpty();
	}

	@Override
	public boolean isItemValid( int slot, ItemStack stack )
	{
		return stack.isEmpty() || slot == SLOT_APPEARANCE && stack.getItem() instanceof BlockItem;
	}
}
