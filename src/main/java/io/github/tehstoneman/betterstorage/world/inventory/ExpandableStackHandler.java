package io.github.tehstoneman.betterstorage.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ExpandableStackHandler extends ItemStackHandler
{
	private int	columns;
	private int	rows;

	public ExpandableStackHandler( ExpandableStackHandler inventory )
	{
		super( inventory.stacks );
		columns	= inventory.getColumns();
		rows	= inventory.getRows();
	}

	public ExpandableStackHandler( int columns, int rows )
	{
		super( columns * rows );
		this.columns	= columns;
		this.rows		= rows;
	}

	public NonNullList< ItemStack > copyStacks( int from, int to )
	{
		final int size = to - from;
		return copyStacks( stacks, from, size );
	}

	public NonNullList< ItemStack > copyStacks( NonNullList< ItemStack > stackIn, int size )
	{
		return copyStacks( stackIn, 0, size );
	}

	public NonNullList< ItemStack > copyStacks( NonNullList< ItemStack > stackIn, int from, int to )
	{
		final int						size		= to - from;
		final NonNullList< ItemStack >	stackOut	= NonNullList.withSize( size, ItemStack.EMPTY );
		for( int i = 0; i < Math.min( size, stackIn.size() ); i++ )
			stackOut.set( i, stackIn.get( i + from ) );
		return stackOut;
	}

	@Override
	public void deserializeNBT( CompoundTag nbt )
	{
		super.deserializeNBT( nbt );
		columns	= nbt.contains( "Columns" ) ? columns = nbt.getInt( "Columns" ) : 9;
		rows	= nbt.contains( "Rows" ) ? nbt.getInt( "Rows" ) : stacks.size() / 9;
	}

	public int getColumns()
	{
		return columns;
	}

	public int getRows()
	{
		return rows;
	}

	public boolean isEmpty()
	{
		for( final ItemStack itemStack : stacks )
			if( !itemStack.isEmpty() )
				return false;
		return true;
	}

	@Override
	public CompoundTag serializeNBT()
	{
		final CompoundTag nbt = super.serializeNBT();
		nbt.putInt( "Columns", columns );
		nbt.putInt( "Rows", rows );
		return nbt;
	}

	@Override
	public void setSize( int size )
	{
		stacks	= copyStacks( stacks, size );
		rows	= size / 9;
		columns	= 9;
	}
}
