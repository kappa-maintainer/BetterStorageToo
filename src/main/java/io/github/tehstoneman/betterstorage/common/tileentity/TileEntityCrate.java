package io.github.tehstoneman.betterstorage.common.tileentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import io.github.tehstoneman.betterstorage.BetterStorage;
import io.github.tehstoneman.betterstorage.common.block.BetterStorageBlocks;
import io.github.tehstoneman.betterstorage.common.inventory.CrateStackHandler;
import io.github.tehstoneman.betterstorage.common.inventory.Region;
import io.github.tehstoneman.betterstorage.common.world.CrateStackCollection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityCrate extends TileEntity
{
	public static final int	slotsPerCrate	= 18;
	public static final int	maxCrates		= 125;
	public static final int	maxPerSide		= 5;

	private UUID			pileID;
	private String			customTitle;

	private int				numCrates;
	private int				capacity;

	@Override
	public boolean hasCapability( Capability< ? > capability, EnumFacing facing )
	{
		if( capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY )
			if( facing == null || BetterStorage.config.enableCrateInventoryInterface )
				return true;
		return super.hasCapability( capability, facing );
	}

	@Override
	public <T> T getCapability( Capability< T > capability, EnumFacing facing )
	{
		if( capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY )
			if( facing == null || BetterStorage.config.enableCrateInventoryInterface )
				return (T)getCrateStackHandler();
		return super.getCapability( capability, facing );
	}

	/** Helper function to add crate to handler */
	public void onBlockPlaced( EntityLivingBase placer, ItemStack stack )
	{
		if( !getWorld().isRemote && pileID == null )
		{
			getCrateStackHandler().addCrate( this );
			markDirty();
		}
	}

	/** Get the crate stack handler for this tile entity. */
	public CrateStackHandler getCrateStackHandler()
	{
		final CrateStackCollection collection = CrateStackCollection.getCollection( getWorld() );
		if( getWorld().isRemote )
			return new CrateStackHandler( getCapacity() );
		final CrateStackHandler handler = collection.getCratePile( pileID );
		handler.sendUpdatesTo( this );
		return handler;
	}

	/** Get the pile ID for this tile entity */
	public UUID getPileID()
	{
		if( pileID == null )
		{
			final CrateStackCollection collection = CrateStackCollection.getCollection( getWorld() );
			final CrateStackHandler handler = collection.createCratePile();
			pileID = handler.getPileID();
		}
		return pileID;
	}

	/** Sets the pile ID for this tile entity */
	public void setPileID( UUID pileID )
	{
		this.pileID = pileID;
		markDirty();
	}

	/**
	 * Destroys all crates above, and makes sure when piles split,
	 * each pile gets their own CratePileData object.
	 */
	private void checkPileConnections( UUID pileID )
	{
		final int x = pos.getX(), y = pos.getY(), z = pos.getZ();

		// Destroy all crates above.
		final TileEntity tileEntity = getWorld().getTileEntity( pos.up() );
		if( tileEntity instanceof TileEntityCrate )
		{
			final TileEntityCrate crateAbove = (TileEntityCrate)tileEntity;
			if( crateAbove != null && crateAbove.getPileID().equals( pileID ) )
			{
				final CrateStackHandler handler = getCrateStackHandler();
				final NonNullList< ItemStack > overflow = handler.removeCrate( crateAbove );
				if( !overflow.isEmpty() )
					for( final ItemStack stack : overflow )
						if( !stack.isEmpty() )
							getWorld().spawnEntity( new EntityItem( getWorld(), pos.getX(), pos.getY() + 1, pos.getZ(), stack ) );
				getWorld().setBlockToAir( pos.up() );
				getWorld().spawnEntity( new EntityItem( getWorld(), x, y + 1, z, new ItemStack( BetterStorageBlocks.CRATE ) ) );
			}
		}

		// If there's still some crates left and this is a
		// base crate, see which crates are still connected.
		final CrateStackHandler handler = getCrateStackHandler();
		if( getNumCrates() <= 0 || y != handler.getRegion().posMin.getY() )
			return;

		// If there's more than one crate set, they need to split.
		final List< HashSet< TileEntityCrate > > crateSets = getCrateSets( x, y, z, pileID );
		if( crateSets.size() <= 1 )
		{
			handler.trimRegion( getWorld() );
			notifyRegionUpdate( handler.getRegion(), getPileID() );
			return;
		}

		// The first crate set will keep the original pile data.
		// All other sets will get new pile data objects.
		final CrateStackCollection collection = CrateStackCollection.getCollection( getWorld() );
		for( int i = 1; i < crateSets.size(); i++ )
		{
			final HashSet< TileEntityCrate > set = crateSets.get( i );
			final CrateStackHandler newHandler = collection.createCratePile();
			int numCrates = set.size();
			// Add the base crates from the set.
			for( TileEntityCrate newCrate : set )
			{
				newHandler.addCrate( newCrate );
				final NonNullList< ItemStack > overflow = handler.removeCrate( newCrate );
				if( !overflow.isEmpty() )
					for( final ItemStack stack : overflow )
						if( !stack.isEmpty() )
							newHandler.addItems( stack );

				// Add all crates above the base crate.
				while( true )
				{
					newCrate = (TileEntityCrate)getWorld().getTileEntity( newCrate.getPos().up() );
					if( newCrate == null )
						break;
					newHandler.addCrate( newCrate );
					numCrates++;
				}
			}
			notifyRegionUpdate( newHandler.getRegion(), newHandler.getPileID() );
		}
		// Trim the original map to the size it actually is.
		// This is needed because the crates may not be removed in
		// order, from outside to inside.
		handler.trimRegion( getWorld() );
		notifyRegionUpdate( handler.getRegion(), getPileID() );
	}

	/** Compile lists of connected crates */
	private List< HashSet< TileEntityCrate > > getCrateSets( int x, int y, int z, UUID pileID )
	{
		final List< HashSet< TileEntityCrate > > crateSets = new ArrayList<>();
		int checkedCrates = 0;

		neighborLoop: // Suck it :P
		for( final EnumFacing dir : EnumFacing.HORIZONTALS )
		{
			final int nx = x + dir.getXOffset();
			final int nz = z + dir.getXOffset();

			// Continue if this neighbor block is not part of the crate pile.
			final TileEntity tileEntity = getWorld().getTileEntity( pos.add( dir.getDirectionVec() ) );
			if( tileEntity instanceof TileEntityCrate )
			{
				final TileEntityCrate neighborCrate = (TileEntityCrate)tileEntity;
				if( neighborCrate == null || !neighborCrate.getPileID().equals( pileID ) )
					continue;

				// See if the neighbor crate is already in a crate set,
				// in that case continue with the next neighbor block.
				for( final HashSet< TileEntityCrate > set : crateSets )
					if( set.contains( neighborCrate ) )
						continue neighborLoop;

				// Create a new set of crates and fill it with all connecting crates.
				final HashSet< TileEntityCrate > set = new HashSet<>();
				set.add( neighborCrate );
				for( final EnumFacing ndir : EnumFacing.HORIZONTALS )
					checkConnections( nx + ndir.getXOffset(), y, nz + ndir.getZOffset(), pileID, set );
				crateSets.add( set );

				// If we checked all crates, stop the loop.
				checkedCrates += set.size();
			}
			if( checkedCrates >= getNumCrates() )
				break;
		}

		return crateSets;
	}

	/** Checks crate connections */
	private void checkConnections( int x, int y, int z, UUID pileID, HashSet< TileEntityCrate > set )
	{
		final TileEntity tileEntity = getWorld().getTileEntity( new BlockPos( x, y, z ) );
		if( tileEntity instanceof TileEntityCrate )
		{
			final TileEntityCrate crate = (TileEntityCrate)tileEntity;
			if( !pileID.equals( crate.getPileID() ) || set.contains( crate ) )
				return;
			set.add( crate );
		}
		for( final EnumFacing ndir : EnumFacing.HORIZONTALS )
			checkConnections( x + ndir.getXOffset(), y, z + ndir.getZOffset(), pileID, set );
	}

	/** Tries to connect a crate to the given side */
	public boolean attemptConnect( EnumFacing side )
	{
		if( getWorld().isRemote || side == EnumFacing.UP )
			return false;
		final BlockPos neighbourPos = pos.add( side.getDirectionVec() );

		final TileEntity tileEntity = getWorld().getTileEntity( neighbourPos );
		if( tileEntity instanceof TileEntityCrate )
		{
			final TileEntityCrate crateClicked = (TileEntityCrate)tileEntity;

			final CrateStackHandler handler = crateClicked.getCrateStackHandler();
			if( handler.canAdd( this ) )
			{
				handler.addCrate( this );
				pileID = handler.getPileID();
				notifyRegionUpdate( handler.getRegion(), pileID );
				markDirty();
				crateClicked.markDirty();
				return true;
			}
		}
		return false;
	}

	/** Notify all matching crates within a region of an update */
	public void notifyRegionUpdate( Region region, UUID pileID )
	{
		for( final BlockPos blockPos : BlockPos.getAllInBox( region.posMin, region.posMax ) )
		{
			final TileEntity te = getWorld().getTileEntity( blockPos );
			if( te instanceof TileEntityCrate && ( (TileEntityCrate)te ).pileID.equals( pileID ) )
			{
				final IBlockState state = getWorld().getBlockState( blockPos );
				getWorld().notifyBlockUpdate( blockPos, state, state, 3 );
			}
		}
	}

	public void setCustomTitle( String displayName )
	{
		customTitle = displayName;
	}

	@Override
	public void invalidate()
	{
		super.invalidate();
		if( getWorld().isRemote )
			return;
		checkPileConnections( getPileID() );
	}

	/** Get the number of crates connected to this tile entity */
	public int getNumCrates()
	{
		if( getWorld().isRemote )
			return numCrates;
		return getCrateStackHandler().getNumCrates();
	}

	/** Sets the number of crates connected to this tile entity */
	public void setNumCrates( int numCrates )
	{
		getCrateStackHandler().setNumCrates( numCrates );
		markDirty();
	}

	/** Get the total capacity this tile entity */
	public int getCapacity()
	{
		if( getWorld().isRemote )
			return capacity;
		return getCrateStackHandler().getCapacity();
	}

	// Comparator related

	/** Get the comparator level of this tile entity */
	public int getComparatorSignalStrength()
	{
		if( getWorld().isRemote )
			return 0;
		final CrateStackHandler handler = getCrateStackHandler();
		return handler.getOccupiedSlots() > 0 ? 1 + handler.getOccupiedSlots() * 14 / handler.getCapacity() : 0;
	}

	@Override
	public void markDirty()
	{
		final CrateStackCollection collection = CrateStackCollection.getCollection( getWorld() );
		collection.markDirty();
		super.markDirty();
	}

	// TileEntity synchronization

	@Override
	public NBTTagCompound getUpdateTag()
	{
		final NBTTagCompound compound = super.getUpdateTag();

		if( pileID != null )
			compound.setUniqueId( "PileID", pileID );
		compound.setInteger( "NumCrates", getNumCrates() );
		compound.setInteger( "Capacity", getCapacity() );

		return compound;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		final NBTTagCompound compound = getUpdateTag();
		return new SPacketUpdateTileEntity( getPos(), getBlockMetadata(), compound );
	}

	@Override
	public void onDataPacket( NetworkManager net, SPacketUpdateTileEntity packet )
	{
		final NBTTagCompound compound = packet.getNbtCompound();
		if( compound.hasUniqueId( "PileID" ) )
			pileID = compound.getUniqueId( "PileID" );
		numCrates = compound.getInteger( "NumCrates" );
		capacity = compound.getInteger( "Capacity" );

		getWorld().markBlockRangeForRenderUpdate( pos.add( -1, -1, -1 ), pos.add( 1, 1, 1 ) );
	}

	@Override
	public void handleUpdateTag( NBTTagCompound compound )
	{
		super.handleUpdateTag( compound );

		if( compound.hasUniqueId( "PileID" ) )
			pileID = compound.getUniqueId( "PileID" );
		numCrates = compound.getInteger( "NumCrates" );
		capacity = compound.getInteger( "Capacity" );
	}

	// Reading from / writing to NBT

	@Override
	public NBTTagCompound writeToNBT( NBTTagCompound compound )
	{
		super.writeToNBT( compound );

		if( pileID != null )
			compound.setUniqueId( "PileID", pileID );
		compound.setInteger( "NumCrates", getNumCrates() );
		compound.setInteger( "Capacity", getCapacity() );

		return compound;
	}

	@Override
	public void readFromNBT( NBTTagCompound compound )
	{
		super.readFromNBT( compound );

		if( compound.hasUniqueId( "PileID" ) )
			pileID = compound.getUniqueId( "PileID" );
		numCrates = compound.getInteger( "NumCrates" );
		capacity = compound.getInteger( "Capacity" );
	}
}
