package io.github.tehstoneman.betterstorage.world.inventory;

import io.github.tehstoneman.betterstorage.world.level.block.entity.ContainerBlockEntity;
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

public class CardboardBoxMenu extends AbstractContainerMenu
{
	private final IItemHandler			containerInventory;
	private final ContainerBlockEntity	containerEntity;

	private final int	columns;
	private final int	rows;

	public int indexStart, indexPlayer, indexHotbar;

	@SuppressWarnings("null")
	public CardboardBoxMenu( int containerID, Inventory playerInventory, Level level, BlockPos blockPos )
	{
		super( BetterStorageMenuTypes.CARDBOARD_BOX.get(), containerID );
		containerEntity = (ContainerBlockEntity)level.getBlockEntity( blockPos );
		containerEntity.openInventory( playerInventory.player );
		containerInventory = containerEntity.getCapability( ForgeCapabilities.ITEM_HANDLER, null ).orElse( null );

		if( containerInventory instanceof ExpandableStackHandler )
		{
			columns	= ( (ExpandableStackHandler)containerInventory ).getColumns();
			rows	= ( (ExpandableStackHandler)containerInventory ).getRows();
		} else
		{
			columns	= 9;
			rows	= 3;
		}

		indexStart	= 0;
		indexHotbar	= rows * columns;
		indexPlayer	= indexHotbar + 9;

		for( var i = 0; i < indexHotbar; i++ )
		{
			final var	x	= i % columns * 18 + 8;
			final var	y	= i / columns * 18 + 18;
			addSlot( new SlotItemHandler( containerInventory, i, x, y ) );
		}

		final var	offsetX	= ( columns - 9 ) / 2 * 18;
		final var	offsetY	= 17 + rows * 18;

		for( var i = 0; i < 27; i++ )
		{
			final var	x	= i % 9 * 18 + 8;
			final var	y	= 14 + i / 9 * 18;
			addSlot( new Slot( playerInventory, i + 9, offsetX + x, offsetY + y ) );
		}

		for( var i = 0; i < 9; i++ )
		{
			final var	x	= i % 9 * 18 + 8;
			final var	y	= 72;
			addSlot( new Slot( playerInventory, i, offsetX + x, offsetY + y ) );
		}
	}

	public int getColumns()
	{
		return columns;
	}

	public int getRows()
	{
		return rows;
	}

	@Override
	public ItemStack quickMoveStack( Player playerIn, int index )
	{
		final var	slot		= slots.get( index );
		var			returnStack	= ItemStack.EMPTY;

		if( slot != null && slot.hasItem() )
		{
			final var itemStack = slot.getItem();
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
		containerEntity.closeInventory( playerIn );
		super.removed( playerIn );
	}

	@Override
	public boolean stillValid(Player p_38874_) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'stillValid'");
	}

	//@Override
	//public boolean stillValid( Player playerIn )
	//{
		//return stillValid( ContainerLevelAccess.create( containerEntity.getLevel(), containerEntity.getBlockPos() ), playerIn, BetterStorageBlocks.CARDBOARD_BOX.get() );
	//}
}
