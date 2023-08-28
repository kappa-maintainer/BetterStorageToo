package io.github.tehstoneman.betterstorage.world.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;

import io.github.tehstoneman.betterstorage.world.inventory.Region;
import io.github.tehstoneman.betterstorage.world.level.CrateStackPile;
import io.github.tehstoneman.betterstorage.world.level.block.entity.CrateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;

public class CrateStackHandler extends ItemStackHandler
{
	private static final int MAX_CRATE_PILE_SIZE = 125;

	// private int numCrates;
	private Region					region;
	private UUID					pileID;
	private List< Integer >			indexSlots;
	private final List< BlockPos >	listCrates	= new ArrayList<>();
	private boolean					doShuffle	= true;

	private CrateBlockEntity crateToUpdate;

	public CrateStackHandler( int size )
	{
		super( size );
		indexSlots = getShuffledIndexes( size );
		// listCrates = NonNullList.withSize( 0, BlockPos.ZERO );
	}

	public static int getCapacity( int numCrates )
	{
		return numCrates * ( 18 + Math.min( numCrates / 6, 18 ) );
	}

	public void addCrate( @Nullable CrateBlockEntity crate )
	{
		if( crate == null )
			return;

		if( getNumCrates() == 0 || region == null )
			region = new Region( crate );
		else
			region.expandToContain( crate );

		final BlockPos blockPos = crate.getBlockPos();
		if( !listCrates.contains( blockPos ) )
		{
			listCrates.add( blockPos );
			stacks = copyStack( stacks, getCapacity() );
		}

		if( crate.hasID() && !crate.getPileID().equals( getPileID() ) )
		{
			final NonNullList< ItemStack > overflow = crate.getCrateStackHandler().removeCrate( crate );
			mergeStack( overflow );
		}

		crate.setPileID( getPileID() );
		crate.setChanged();
	}

	public void addItems( ItemStack stack )
	{
		int i = 0;
		while( i < stacks.size() && !stacks.get( i ).isEmpty() )
			i++;

		if( i < stacks.size() && !stacks.get( i ).isEmpty() )
			stacks.set( i, stack.copy() );
	}

	public boolean canAdd( CrateBlockEntity crate )
	{
		return getNumCrates() < MAX_CRATE_PILE_SIZE && ( region.contains( crate.getBlockPos() ) || canExpand( crate ) );
	}

	/**
	 * Tries to fit contents into full stacks, and moves empty slots to the end of
	 * the inventory
	 */
	public void consolidateStacks()
	{
		for( int i = 1; i < stacks.size(); i++ )
			if( !stacks.get( i ).isEmpty() )
				if( moveItemStackTo( stacks.get( i ), 0, i ) == 0 )
					stacks.set( i, ItemStack.EMPTY );
	}

	public NonNullList< ItemStack > copyStack( NonNullList< ItemStack > stackIn, int size )
	{
		return copyStack( stackIn, 0, size );
	}

	public NonNullList< ItemStack > copyStack( NonNullList< ItemStack > stackIn, int from, int to )
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

		if( nbt.contains( "Crates" ) )
		{
			final ListTag list = nbt.getList( "Crates", 10 );
			listCrates.clear();
			for( final Tag tag : list )
				listCrates.add( NbtUtils.readBlockPos( (CompoundTag)tag ) );
		}
		if( nbt.hasUUID( "PileID" ) )
			pileID = nbt.getUUID( "PileID" );

		if( nbt.contains( "Region" ) )
			region = Region.fromCompound( nbt.getCompound( "Region" ) );
		onLoad();
	}

	@Override
	public ItemStack extractItem( int slot, int amount, boolean simulate )
	{
		return super.extractItem( getIndexedSlot( slot ), amount, simulate );
	}

	/**
	 * Bypass the randomization of the overridden version of this function
	 *
	 * @param slot
	 *            Slot to extract from.
	 * @param amount
	 *            Amount to extract (may be greater than the current stack's max
	 *            limit)
	 * @param simulate
	 *            If true, the extraction is only simulated
	 *
	 * @return ItemStack extracted from the slot, must be empty if nothing can be
	 *             extracted. The returned ItemStack can be safely modified after, so
	 *             item handlers should return a new or copied stack.
	 */
	public ItemStack extractItemFixed( int slot, int amount, boolean simulate )
	{
		doShuffle = false;
		final ItemStack result = super.extractItem( slot, amount, simulate );
		doShuffle = true;
		return result;
	}

	public int getCapacity()
	{
		return getCapacity( getNumCrates() );
	}

	/**
	 * Returns a slot pointed to by a randomized index
	 *
	 * @param slot
	 *            The index pointing to a lot
	 *
	 * @return The actual slot pointed to by the index
	 */
	public int getIndexedSlot( int slot )
	{
		if( slot < 0 )
			slot = 0;
		if( slot >= indexSlots.size() )
			slot = indexSlots.size() - 1;
		return indexSlots.get( slot );
	}

	/**
	 * Bypass the randomization of the overridden version of this function
	 * <p>
	 * <strong>IMPORTANT:</strong> This ItemStack <em>MUST NOT</em> be modified.
	 * This method is not for altering an inventory's contents. Any implementers who
	 * are able to detect modification through this method should throw an
	 * exception.
	 * </p>
	 * <p>
	 * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK</em></strong>
	 * </p>
	 *
	 * @param slot
	 *            Slot to query
	 *
	 * @return ItemStack in given slot. Empty Itemstack if the slot is empty.
	 */
	public ItemStack getItemFixed( int slot )
	{
		return super.getStackInSlot( slot );
	}

	public int getNumCrates()
	{
		return listCrates == null ? 0 : listCrates.size();
	}

	public int getOccupiedSlots()
	{
		int count = 0;
		for( final ItemStack stack : stacks )
			if( !stack.isEmpty() )
				count++;
		return count;
	}

	public UUID getPileID()
	{
		return pileID;
	}

	public ItemStack[] getRandomStacks( int count )
	{
		final List< ItemStack > items = stacks.subList( 0, stacks.size() );
		Collections.shuffle( items );
		return (ItemStack[])Arrays.copyOf( items.toArray(), count );
	}

	public Region getRegion()
	{
		if( region == null )
			region = Region.EMPTY.clone();
		return region;
	}

	/**
	 * Returns a shuffled list of slot indexes<br>
	 * Tries to fill list with only occupied slots
	 *
	 * @param count
	 *            The number of slots to return
	 *
	 * @return The list of shuffled indexes
	 */
	public List< Integer > getShuffledIndexes( int count )
	{
		final List< Integer >	list		= new ArrayList<>();
		final int				occupied	= getOccupiedSlots();
		for( int i = 0; i < Math.max( count, occupied ); i++ )
			list.add( i );
		Collections.shuffle( list );
		return list.subList( 0, count );
	}

	@Override
	public ItemStack getStackInSlot( int slot )
	{
		return super.getStackInSlot( getIndexedSlot( slot ) );
	}

	@Override
	public ItemStack insertItem( int slot, ItemStack stack, boolean simulate )
	{
		return super.insertItem( getIndexedSlot( slot ), stack, simulate );
	}

	/**
	 * Bypass the randomization of the overridden version of this function
	 *
	 * @param slot
	 *            Slot to insert into.
	 * @param stack
	 *            ItemStack to insert. This must not be modified by the item
	 *            handler.
	 * @param simulate
	 *            If true, the insertion is only simulated
	 *
	 * @return The remaining ItemStack that was not inserted (if the entire stack is
	 *             accepted, then return an empty ItemStack). May be the same as the
	 *             input ItemStack if unchanged, otherwise a new ItemStack. The returned
	 *             ItemStack can be safely modified after.
	 */
	public ItemStack insertItemFixed( int slot, ItemStack stack, boolean simulate )
	{
		doShuffle = false;
		final ItemStack result = super.insertItem( slot, stack, simulate );
		doShuffle = true;
		return result;
	}

	public void mergeStack( NonNullList< ItemStack > stackIn )
	{
		for( final ItemStack itemStack : stackIn )
			if( !itemStack.isEmpty() )
				if( moveItemStackTo( itemStack, 0, stacks.size() ) != 0 )
					addItems( itemStack );
	}

	/**
	 * Merges partial stacks into each other
	 *
	 * @param stack
	 *            The stack to merge
	 * @param startIndex
	 *            The index to start merging from
	 * @param endIndex
	 *            The index to end merging from
	 *
	 * @return The final size of the merged stack
	 */
	public int moveItemStackTo( ItemStack stack, int startIndex, int endIndex )
	{
		int i = startIndex;

		if( stack.isStackable() )
			while( stack.getCount() > 0 && i < endIndex )
			{
				final ItemStack itemstack = stacks.get( i );

				if( itemstack.isEmpty() )
				{
					stacks.set( i, stack.copy() );
					stack.setCount( 0 );
					break;
				}
				/*if( ItemStack.isSame( stack, itemstack ) )
				{
					final int newSize = itemstack.getCount() + stack.getCount();

					if( newSize <= stack.getMaxStackSize() )
					{
						stack.setCount( 0 );
						itemstack.setCount( newSize );
					} else if( itemstack.getCount() < stack.getMaxStackSize() )
					{
						final int count = stack.getMaxStackSize() - itemstack.getCount();
						stack.setCount( stack.getCount() - count );
						itemstack.setCount( stack.getMaxStackSize() );
					}
				}*/

				++i;
			}
		return stack.getCount();
	}

	/**
	 * Removes a crate from the crate pile, decreasing the number of crates and
	 * removing it from the crate pile map.
	 *
	 * @param crate
	 *            The {@link CrateBlockEntity} to remove.
	 *
	 * @return A list of items that no longer fit in resized crate pile
	 */
	@SuppressWarnings("null")
	public NonNullList< ItemStack > removeCrate( CrateBlockEntity crate )
	{
		if( listCrates != null && listCrates.contains( crate.getBlockPos() ) )
		{
			final int i = listCrates.indexOf( crate.getBlockPos() );
			listCrates.remove( i );
			final CrateStackPile collection = CrateStackPile.getCollection( crate.getLevel() );
			collection.setDirty();
			if( getNumCrates() > 0 )
			{
				final NonNullList< ItemStack > overflow = copyStack( stacks, getCapacity(), stacks.size() );
				setSize( getCapacity() );
				return overflow;
			}
			collection.removeCratePile( pileID );
			return stacks;
		}
		return NonNullList.withSize( 0, ItemStack.EMPTY );
	}

	public void removeItems( ItemStack itemStack )
	{
		int i = 0;
		while( i < stacks.size() && !ItemStack.matches( stacks.get( i ), itemStack ) )
			i++;

		if( i < stacks.size() && !stacks.get( i ).isEmpty() )
			stacks.set( i, ItemStack.EMPTY );
	}

	/**
	 * Records a tileEntity to notify if contents has changed.
	 *
	 * @param tileEntityCrate
	 *            The TileEntityCrate to update.
	 */
	public void sendUpdatesTo( CrateBlockEntity tileEntityCrate )
	{
		crateToUpdate = tileEntityCrate;
	}

	@Override
	public CompoundTag serializeNBT()
	{
		final CompoundTag nbt = super.serializeNBT();

		if( getNumCrates() > 0 )
		{
			final ListTag list = new ListTag();
			for( final BlockPos pos : listCrates )
				list.add( NbtUtils.writeBlockPos( pos ) );
			nbt.put( "Crates", list );
		}
		if( pileID != null )
			nbt.putUUID( "PileID", pileID );

		if( region != null )
			nbt.put( "Region", region.toCompound() );
		return nbt;
	}

	public void setPileID( UUID pileID )
	{
		this.pileID = pileID;
	}

	public void setRegion( Region region )
	{
		this.region = region;
	}

	@Override
	public void setSize( int size )
	{
		stacks		= copyStack( stacks, size );
		indexSlots	= getShuffledIndexes( size );
	}

	@Override
	public void setStackInSlot( int slot, ItemStack stack )
	{
		super.setStackInSlot( getIndexedSlot( slot ), stack );
	}

	/**
	 * Bypass the randomization of the overridden version of this function
	 *
	 * @param slot
	 *            Slot to modify
	 * @param stack
	 *            ItemStack to set slot to (may be empty).
	 */
	public void setStackInSlotFixed( int slot, ItemStack stack )
	{
		doShuffle = false;
		super.setStackInSlot( slot, stack );
		doShuffle = true;
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper( this ).add( "pileID", pileID ).add( "numCrates", getNumCrates() ).toString();
	}

	public void trimRegion( Level world )
	{
		final Region region = Region.EMPTY.clone();
		for( final BlockPos pos : BlockPos.betweenClosed( this.region.posMin, this.region.posMax ) )
		{
			final CrateBlockEntity tileCrate = CrateBlockEntity.getCrateAt( world, pos );
			if( tileCrate != null )
				if( tileCrate.getPileID().equals( pileID ) )
					region.expandToContain( pos );
		}
		if( !region.isEmpty() )
			this.region = region;
	}

	private boolean canExpand( CrateBlockEntity crate )
	{
		// Can't expand if there's not enough crates in the bounding box.
		final int volume = region.volume();
		if( getNumCrates() < volume )
			return false;

		final int	width	= region.width();
		final int	height	= region.height();
		final int	depth	= region.depth();

		if( crate.getBlockPos().getX() < region.posMin.getX() || crate.getBlockPos().getX() > region.posMax.getX() )
		{
			if( width >= MAX_CRATE_PILE_SIZE )
				return false;
			final int maxDiff = height == 1 ? 1 : 3;
			if( width >= maxDiff + Math.min( height, depth ) )
				return false;
		} else if( crate.getBlockPos().getZ() < region.posMin.getZ() || crate.getBlockPos().getZ() > region.posMax.getZ() )
		{
			if( depth >= MAX_CRATE_PILE_SIZE )
				return false;
			final int maxDiff = width == 1 ? 1 : 3;
			if( depth >= maxDiff + Math.min( height, depth ) )
				return false;
		} else if( crate.getBlockPos().getY() < region.posMin.getY() || crate.getBlockPos().getY() > region.posMax.getY() )
		{
			if( height >= MAX_CRATE_PILE_SIZE )
				return false;
			final int maxDiff = width == 1 || depth == 1 ? 1 : 4;
			if( height >= maxDiff + Math.min( width, depth ) )
				return false;
		}

		return true;
	}

	@Override
	protected void onContentsChanged( int slot )
	{
		if( doShuffle )
		{
			consolidateStacks();
			indexSlots = getShuffledIndexes( stacks.size() );
		}
		if( crateToUpdate != null )
			crateToUpdate.setChanged();
	}

	@Override
	protected void onLoad()
	{
		indexSlots = getShuffledIndexes( stacks.size() );
	}
}
