package io.github.tehstoneman.betterstorage.world.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CrateSlotHandler extends SlotItemHandler
{
	private final int index;

	public CrateSlotHandler( IItemHandler itemHandler, int index, int xPosition, int yPosition )
	{
		super( itemHandler, index, xPosition, yPosition );
		this.index = index;
	}

	@Override
	public boolean mayPlace( ItemStack itemStack )
	{
		if( itemStack.isEmpty() )
			return false;

		final IItemHandler itemHandler = getItemHandler();
		if( !( itemHandler instanceof CrateStackHandler crateHandler ) )
			return super.mayPlace( itemStack );

		ItemStack               remainder;
		final ItemStack         currentStack = crateHandler.getItemFixed( index );

		crateHandler.setStackInSlotFixed( index, ItemStack.EMPTY );

		remainder = crateHandler.insertItemFixed( index, itemStack, true );

		crateHandler.setStackInSlotFixed( index, currentStack );

		return remainder.isEmpty() || remainder.getCount() < itemStack.getCount();
	}

	@Override
	public ItemStack getItem()
	{
		final IItemHandler itemHandler = getItemHandler();
		if( !( itemHandler instanceof CrateStackHandler crateStackHandler ) )
			return super.getItem();

		return crateStackHandler.getItemFixed( index );
	}

	@Override
	public void set( ItemStack itemStack )
	{
		final IItemHandler itemHandler = getItemHandler();
		if( !( itemHandler instanceof CrateStackHandler ) )
			super.set( itemStack );

		final CrateStackHandler crateStackHandler = (CrateStackHandler)itemHandler;
		crateStackHandler.setStackInSlotFixed( index, itemStack );
		setChanged();
	}

	@Override
	public int getMaxStackSize( ItemStack itemStack )
	{
		final IItemHandler itemHandler = getItemHandler();
		if( !( itemHandler instanceof CrateStackHandler crateStackHandler ) )
			return super.getMaxStackSize( itemStack );

		final int       maxInput = itemStack.getMaxStackSize();
		final ItemStack maxStack = itemStack.copy();
		maxStack.setCount( maxInput );

		final ItemStack currentStack = crateStackHandler.getItemFixed( index );

		crateStackHandler.setStackInSlotFixed( index, ItemStack.EMPTY );

		final ItemStack remainderStack = crateStackHandler.insertItemFixed( index, maxStack, true );

		crateStackHandler.setStackInSlotFixed( index, currentStack );

		return maxInput - ( !remainderStack.isEmpty() ? remainderStack.getCount() : 0 );
	}

	@Override
	public boolean mayPickup( Player player )
	{
		final IItemHandler itemHandler = getItemHandler();
		if( !( itemHandler instanceof CrateStackHandler crateStackHandler ) )
			return super.mayPickup( player );

		return !crateStackHandler.extractItemFixed( index, 1, true ).isEmpty();
	}

	@Override
	public ItemStack remove( int amount )
	{
		final IItemHandler itemHandler = getItemHandler();
		if( !( itemHandler instanceof CrateStackHandler crateHandler ) )
			return super.remove( amount );

		return crateHandler.extractItemFixed( index, amount, false );
	}
}