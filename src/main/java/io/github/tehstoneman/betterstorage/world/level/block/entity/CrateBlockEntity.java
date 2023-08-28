package io.github.tehstoneman.betterstorage.world.level.block.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;

import io.github.tehstoneman.betterstorage.BetterStorage;
import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.config.BetterStorageConfig;
import io.github.tehstoneman.betterstorage.world.inventory.CrateMenu;
import io.github.tehstoneman.betterstorage.world.inventory.Region;
import io.github.tehstoneman.betterstorage.world.item.CrateStackHandler;
import io.github.tehstoneman.betterstorage.world.level.CrateStackPile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

// public class CrateBlockEntity extends ContainerBlockEntity
public class CrateBlockEntity extends BlockEntity implements MenuProvider
{
	public static final int	SLOTS_PER_CRATE	= 18;
	public static final int	MAX_CRATES		= 125;
	public static final int	MAX_PER_SIDE	= 5;

	private UUID		pileID;
	protected Component	customName;

	private int	numCrates	= 1;
	private int	capacity	= SLOTS_PER_CRATE;

	public CrateBlockEntity( BlockEntityType< ? > blockEntityType, BlockPos blockPos, BlockState blockState )
	{
		super( blockEntityType, blockPos, blockState );
	}

	public CrateBlockEntity( BlockPos blockPos, BlockState blockState )
	{
		this( BetterStorageBlockEntityTypes.CRATE.get(), blockPos, blockState );
	}

	@Nullable
	public static CrateBlockEntity getCrateAt( BlockGetter level, BlockPos blockPos )
	{
		final BlockEntity blockEntity = level.getBlockEntity( blockPos );
		if( blockEntity instanceof CrateBlockEntity )
			return (CrateBlockEntity)blockEntity;
		return null;
	}

	@Override
	public AbstractContainerMenu createMenu( int windowID, Inventory playerInventory, Player player )
	{
		/*
		 * BetterStorage.NETWORK.send( PacketDistributor.PLAYER.with( () -> (ServerPlayer)player ),
		 * new UpdateCrateMessage( worldPosition, getNumCrates(), getCapacity() ) );
		 */

		return new CrateMenu( windowID, playerInventory, level, worldPosition );
	}

	@Override
	public <T> LazyOptional< T > getCapability( Capability< T > capability, Direction facing )
	{
		if( capability != ForgeCapabilities.ITEM_HANDLER )
			return super.getCapability( capability, facing );
		if( facing != null && !BetterStorageConfig.COMMON.crateAllowAutomation.get() )
			return LazyOptional.empty();
		final LazyOptional< IItemHandler > crateHandler = LazyOptional.of( this::getCrateStackHandler );
		return ForgeCapabilities.ITEM_HANDLER.orEmpty( capability, crateHandler );
	}

	@SuppressWarnings("null")
	public int getCapacity()
	{
		if( level.isClientSide )
			return capacity;
		return getCrateStackHandler().getCapacity();
	}

	@SuppressWarnings("null")
	public CrateStackHandler getCrateStackHandler()
	{
		if( level.isClientSide )
			return new CrateStackHandler( getCapacity() );

		final CrateStackPile	collection	= CrateStackPile.getCollection( level );
		final CrateStackHandler	handler		= collection.computeIfAbsentCratePile( getPileID() );
		handler.sendUpdatesTo( this );

		return handler;
	}

	@Override
	public Component getDisplayName()
	{
		return Component.translatable( ModInfo.CONTAINER_CRATE_NAME );
	}

	@SuppressWarnings("null")
	public int getNumCrates()
	{
		if( level.isClientSide )
			return numCrates;
		return getCrateStackHandler().getNumCrates();
	}

	@SuppressWarnings("null")
	public UUID getPileID()
	{
		if( pileID == null )
		{
			final CrateStackPile	collection	= CrateStackPile.getCollection( level );
			final CrateStackHandler	handler		= collection.createCratePile();
			setPileID( handler.getPileID() );
		}
		return pileID;
	}

	@Override
	public Packet< ClientGamePacketListener > getUpdatePacket()
	{
		// Will get tag from #getUpdateTag
		return ClientboundBlockEntityDataPacket.create( this );
	}

	@Override
	public CompoundTag getUpdateTag()
	{
		final CompoundTag tag = super.getUpdateTag();

		if( hasID() )
			tag.putUUID( "PileID", pileID );
		tag.putInt( "NumCrates", getNumCrates() );
		tag.putInt( "Capacity", getCapacity() );

		return tag;
	}

	@Override
	public void handleUpdateTag( CompoundTag tag )
	{
		super.handleUpdateTag( tag );

		if( tag.hasUUID( "PileID" ) )
			pileID = tag.getUUID( "PileID" );
		numCrates	= tag.getInt( "NumCrates" );
		capacity	= tag.getInt( "Capacity" );
	}

	public boolean hasID()
	{
		return pileID != null;
	}

	public boolean isSamePile( CrateBlockEntity blockEntity )
	{
		return blockEntity.hasID() && getPileID().equals( blockEntity.getPileID() );
	}

	@Override
	public void load( CompoundTag tag )
	{
		if( tag.hasUUID( "PileID" ) )
			pileID = tag.getUUID( "PileID" );

		super.load( tag );
	}

	@SuppressWarnings("null")
	public void notifyRegionUpdate( Region region, UUID pileID )
	{
		if( region == null || region.isEmpty() )
			return;
		for( final BlockPos blockPos : region.betweenClosed() )
		{
			final BlockEntity te = getLevel().getBlockEntity( blockPos );
			if( te instanceof CrateBlockEntity && ( (CrateBlockEntity)te ).pileID.equals( pileID ) )
			{
				te.setChanged();
				final BlockState blockState = getLevel().getBlockState( blockPos );
				getLevel().sendBlockUpdated( blockPos, blockState, blockState, 3 );
			}
		}
	}

	@Override
	@SuppressWarnings("null")
	public void onDataPacket( Connection network, ClientboundBlockEntityDataPacket packet )
	{
		final CompoundTag tag = packet.getTag();

		if( tag.hasUUID( "PileID" ) )
			pileID = tag.getUUID( "PileID" );
		numCrates	= tag.getInt( "NumCrates" );
		capacity	= tag.getInt( "Capacity" );
	}

	public NonNullList< ItemStack > removeCrate()
	{
		final NonNullList< ItemStack > overflow = getCrateStackHandler().removeCrate( this );
		checkPileConnections( pileID );
		return overflow;
	}

	@Override
	public void saveAdditional( CompoundTag tag )
	{
		super.saveAdditional( tag );
		if( pileID != null )
			tag.putUUID( "PileID", pileID );
	}

	// Comparator related

	public void setCapacity( int capacity )
	{
		this.capacity = capacity;
	}

	/*
	 * @Override
	 * public ClientboundBlockEntityDataPacket getUpdatePacket()
	 * {
	 * final CompoundTag nbt = new CompoundTag();
	 * if( hasID() )
	 * nbt.putUUID( "PileID", pileID );
	 * nbt.putInt( "NumCrates", getNumCrates() );
	 * nbt.putInt( "Capacity", getCapacity() );
	 * return new ClientboundBlockEntityDataPacket( getBlockPos(), 1, nbt );
	 * }
	 */

	@Override
	@SuppressWarnings("null")
	public void setChanged()
	{
		super.setChanged();
		if( !level.isClientSide )
			CrateStackPile.getCollection( getLevel() ).setDirty();
	}

	public void setCustomTitle( String displayName )
	{}

	public void setNumCrates( int numCrates )
	{
		this.numCrates = numCrates;
	}

	public void setPileID( UUID pileID )
	{
		this.pileID = pileID;
		setChanged();
	}

	public boolean tryAddCrate( @Nullable CrateBlockEntity crate )
	{
		if( crate == null )
			return false;

		if( crate.getPileID().equals( pileID ) )
			return true;

		// Rule 2 - New region must remain within size limits
		final CrateStackHandler	handler	= getCrateStackHandler();
		final Region			region	= handler.getRegion().clone();
		if( crate.getNumCrates() > 1 )
			region.expandToContain( crate.getCrateStackHandler().getRegion() );
		else
			region.expandToContain( crate );
		if( region.width() > MAX_PER_SIDE || region.height() > MAX_PER_SIDE || region.depth() > MAX_PER_SIDE )
			return false;

		BetterStorage.LOGGER.info( "tryAddCrate {} {} : {}", region, crate.getNumCrates(), crate.getPileID().equals( pileID ) );
		// All rules passed - Add crate to pile
		for( final BlockPos blockPos : BlockPos.betweenClosed( region.posMin, region.posMax ) )
		{
			final CrateBlockEntity newCrate = getCrateAt( level, blockPos );
			//if( newCrate != null && !newCrate.getPileID().equals( pileID ) )
				handler.addCrate( newCrate );
		}
		notifyRegionUpdate( region, pileID );
		checkPileConnections( pileID );
		return crate.getPileID().equals( pileID );
	}

	@SuppressWarnings("null")
	public void updateConnections()
	{
		if( !level.isClientSide )
		{
			final CrateStackHandler handler = getCrateStackHandler();
			pileID		= handler.getPileID();
			numCrates	= handler.getNumCrates();
			checkPileConnections( pileID );
			setChanged();
			notifyRegionUpdate( handler.getRegion(), pileID );
		}
	}

	private void checkConnections( BlockPos blockPos, UUID pileID, HashSet< CrateBlockEntity > set )
	{
		final CrateBlockEntity crateBlockEntity = getCrateAt( level, blockPos );
		if( crateBlockEntity	== null || crateBlockEntity == this || !pileID.equals( crateBlockEntity.getPileID() )
			|| set.contains( crateBlockEntity ) )
			return;
		set.add( crateBlockEntity );
		for( final Direction nDir : Direction.values() )
			checkConnections( blockPos.relative( nDir ), pileID, set );
	}

	@SuppressWarnings("null")
	private void checkPileConnections( UUID pileID )
	{
		final CrateStackHandler	handler		= getCrateStackHandler();
		final CrateStackPile	collection	= CrateStackPile.getCollection( level );

		// Remove empty pile
		if( handler.getNumCrates() <= 0 )
		{
			collection.removeCratePile( pileID );
			final BlockState state = getBlockState();
			getLevel().sendBlockUpdated( worldPosition, state, state, 3 );
			return;
		}

		// If there's more than one crate set, they need to split.
		final List< HashSet< CrateBlockEntity > > crateSets = getCrateSets( worldPosition, pileID );
		if( crateSets.size() > 1 )
			// The first crate set will keep the original pile data.
			// All other sets will get new pile data objects.
			for( final HashSet< CrateBlockEntity > set : Iterables.skip( crateSets, 1 ) )
			{
				// final HashSet< TileEntityCrate > set = crateSets.get( i );
				final CrateStackHandler newHandler = collection.createCratePile();
				// int numCrates = set.size();

				// Add the base crates from the set.
				for( final CrateBlockEntity crate : set )
				{
					final NonNullList< ItemStack > overflow = handler.removeCrate( crate );
					newHandler.addCrate( crate );
					if( !overflow.isEmpty() )
						for( final ItemStack stack : overflow )
							if( !stack.isEmpty() )
								newHandler.addItems( stack );
				}
				notifyRegionUpdate( newHandler.getRegion(), newHandler.getPileID() );
			}
		// Trim the original map to the size it actually is.
		// This is needed because the crates may not be removed in
		// order, from outside to inside.
		handler.trimRegion( getLevel() );
		notifyRegionUpdate( handler.getRegion(), getPileID() );
	}

	private List< HashSet< CrateBlockEntity > > getCrateSets( BlockPos blockPos, UUID pileID )
	{
		final List< HashSet< CrateBlockEntity > > crateSets = new ArrayList<>();
		for( final Direction direction : Direction.values() )
		{
			// Continue if this neighbor block is not part of the crate pile.
			final CrateBlockEntity crateBlockEntity = getCrateAt( level, blockPos.relative( direction ) );
			if( crateBlockEntity != null && !isInSet( crateBlockEntity, crateSets ) )
			{
				// Create a new set of crates and fill it with all connecting crates.
				final HashSet< CrateBlockEntity > set = new HashSet<>();
				set.add( crateBlockEntity );
				for( final Direction nDir : Direction.values() )
					checkConnections( crateBlockEntity.getBlockPos().relative( nDir ), pileID, set );
				crateSets.add( set );
			}
		}

		return crateSets;
	}

	private boolean isInSet( CrateBlockEntity crateBlockEntity, List< HashSet< CrateBlockEntity > > crateSets )
	{
		for( final HashSet< CrateBlockEntity > set : crateSets )
			if( set.contains( crateBlockEntity ) )
				return true;
		return false;
	}
}
