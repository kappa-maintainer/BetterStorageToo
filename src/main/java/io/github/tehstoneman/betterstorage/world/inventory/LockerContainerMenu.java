package io.github.tehstoneman.betterstorage.world.inventory;

import io.github.tehstoneman.betterstorage.world.level.block.entity.ContainerBlockEntity;
import io.github.tehstoneman.betterstorage.world.level.block.entity.LockerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class LockerContainerMenu extends AbstractContainerMenu
{
	private final IItemHandler		inventoryContainer;
	private final LockerBlockEntity	tileContainer;

	private final int	columns;
	private final int	rows;

	public int indexStart, indexPlayer, indexHotbar;

	public LockerContainerMenu( int windowId, Inventory playerInventory, Level world, BlockPos pos )
	{
		super( BetterStorageMenuTypes.LOCKER.get(), windowId );
		tileContainer = (LockerBlockEntity)world.getBlockEntity( pos );
		tileContainer.startOpen( playerInventory.player );
		inventoryContainer = tileContainer.getCapability( ForgeCapabilities.ITEM_HANDLER, null ).orElse( null );

		if( inventoryContainer instanceof ExpandableStackHandler )
		{
			columns	= ( (ExpandableStackHandler)inventoryContainer ).getColumns();
			rows	= ( (ExpandableStackHandler)inventoryContainer ).getRows();
		} else
		{
			columns	= 9;
			rows	= 3;
		}

		indexStart	= 0;
		indexHotbar	= rows * columns;
		indexPlayer	= indexHotbar + 9;

		for( int i = 0; i < indexHotbar; i++ )
		{
			final int	x	= i % columns * 18 + 8;
			final int	y	= i / columns * 18 + 18;
			addSlot( new SlotItemHandler( inventoryContainer, i, x, y ) );
		}

		final int	offsetX	= ( columns - 9 ) / 2 * 18;
		final int	offsetY	= 17 + rows * 18;

		for( int i = 0; i < 27; i++ )
		{
			final int	x	= i % 9 * 18 + 8;
			final int	y	= 15 + i / 9 * 18;
			addSlot( new Slot( playerInventory, i + 9, offsetX + x, offsetY + y ) );
		}

		for( int i = 0; i < 9; i++ )
		{
			final int	x	= i % 9 * 18 + 8;
			final int	y	= 73;
			addSlot( new Slot( playerInventory, i, offsetX + x, offsetY + y ) );
		}
	}

	public int getColumns()
	{
		return columns;
	}

	public ContainerBlockEntity getContainer()
	{
		return tileContainer;
	}

	public int getRows()
	{
		return rows;
	}

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
			} // Otherwise try to transfer from player to container
			else if( !moveItemStackTo( itemStack, 0, indexHotbar, false ) )
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
		tileContainer.stopOpen( playerIn );
	}

	@Override
	public boolean stillValid(Player p_38874_) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'stillValid'");
	}

	//@Override
	//public boolean stillValid( Player playerIn )
	//{
		//return stillValid( ContainerLevelAccess.create( tileContainer.getLevel(), tileContainer.getBlockPos() ), playerIn, BetterStorageBlocks.LOCKER.get() );
	//}
}
