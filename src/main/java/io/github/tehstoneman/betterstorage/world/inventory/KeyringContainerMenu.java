package io.github.tehstoneman.betterstorage.world.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class KeyringContainerMenu extends AbstractContainerMenu
{
	private final Inventory				inventoryPlayer;
	private final KeyringStackHandler	inventoryKeyRing;
	public int							indexStart, indexPlayer, indexHotbar;

	public KeyringContainerMenu( int windowID, Inventory playerInventory, ItemStack keyring, int protectedIndex )
	{
		super( BetterStorageMenuTypes.KEYRING.get(), windowID );

		inventoryPlayer		= playerInventory;
		inventoryKeyRing	= (KeyringStackHandler)keyring.getCapability( ForgeCapabilities.ITEM_HANDLER, null ).orElse( null );
		indexStart			= 0;
		indexHotbar			= inventoryKeyRing.getSlots();
		indexPlayer			= indexHotbar + 9;

		for( int i = 0; i < indexHotbar; i++ )
		{
			final int	x	= i % 9 * 18 + 8;
			final int	y	= i / 9 * 18 + 18;
			addSlot( new KeyringSlotHandler( inventoryKeyRing, i, x, y ) );
		}

		for( int i = 0; i < 27; i++ )
		{
			final int	x	= i % 9 * 18 + 8;
			final int	y	= 50 + i / 9 * 18;
			addSlot( new Slot( inventoryPlayer, i + 9, x, y ) );
		}

		for( int i = 0; i < 9; i++ )
		{
			final int	x	= i % 9 * 18 + 8;
			final int	y	= 108;
			addSlot( new Slot( inventoryPlayer, i, x, y ) );
		}
	}

	/*
	 * @Override
	 * public ItemStack clicked( int slotId, int dragType, ClickType clickTypeIn, Player player )
	 * {
	 * if( slotId >= 0 && getSlot( slotId ) != null && getSlot( slotId ).getItem() == player.getMainHandItem() )
	 * return ItemStack.EMPTY;
	 * return super.clicked( slotId, dragType, clickTypeIn, player );
	 * }
	 */

	@Override
	public ItemStack quickMoveStack( Player playerIn, int index )
	{
		final Slot	slot		= slots.get( index );
		ItemStack	returnStack	= ItemStack.EMPTY;

		if( slot != null && slot.hasItem() )
		{
			final ItemStack itemStack = slot.getItem();
			returnStack = itemStack.copy();

			if( index < indexHotbar )
			{
				// Try to transfer from container to player
				if( !moveItemStackTo( itemStack, indexHotbar, slots.size(), true ) )
					return ItemStack.EMPTY;
			} else if( !moveItemStackTo( itemStack, 0, indexHotbar, false ) )
				return ItemStack.EMPTY;

			if( itemStack.isEmpty() )
				slot.set( ItemStack.EMPTY );
			else
				slot.setChanged();
		}

		return returnStack;
	}

	@Override
	public void removed( Player playerIn )
	{
		super.removed( playerIn );
		inventoryKeyRing.updateCount();
	}

	@Override
	public boolean stillValid( Player playerIn )
	{
		return true;
	}
}
