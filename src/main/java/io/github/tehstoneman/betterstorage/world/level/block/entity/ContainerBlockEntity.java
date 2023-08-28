package io.github.tehstoneman.betterstorage.world.level.block.entity;

import javax.annotation.Nullable;

import io.github.tehstoneman.betterstorage.world.inventory.ExpandableStackHandler;
import io.github.tehstoneman.betterstorage.world.level.block.BetterStorageContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public abstract class ContainerBlockEntity extends BlockEntity implements MenuProvider, Nameable
{
	protected static int			EVENT_PLAYER_USING	= 1;
	public ExpandableStackHandler	inventory;

	private final LazyOptional< IItemHandler > inventoryHandler = LazyOptional.of( () -> inventory );

	/** The custom title of this container, set by an anvil. */
	protected Component customName;

	protected int	numPlayersUsing;
	public int		ticksExisted	= 0;
	public float	lidAngle		= 0;

	public float prevLidAngle = 0;

	public ContainerBlockEntity( BlockEntityType< ? > blockEntityType, BlockPos blockPos, BlockState blockState )
	{
		super( blockEntityType, blockPos, blockState );
		final int size = getSizeContents();
		if( size > 0 )
			inventory = new ExpandableStackHandler( getColumns(), getRows() )
			{
				@Override
				protected void onContentsChanged( int slot )
				{
					ContainerBlockEntity.this.setChanged();
				}
			};
		else
			inventory = null;
	}

	public void closeInventory( Player player )
	{
		if( !player.isSpectator() )
		{
			--numPlayersUsing;
			onOpenOrClose();
		}
	}

	public void dropInventoryItems()
	{
		if( inventory != null )
			for( var i = 0; i < inventory.getSlots(); i++ )
			{
				final ItemStack stack = inventory.getStackInSlot( i );
				if( !stack.isEmpty() )
					Containers.dropItemStack( getLevel(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack );
			}
	}

	@Nullable
	@Override
	public <T> LazyOptional< T > getCapability( Capability< T > capability, @Nullable Direction facing )
	{
		if( capability == ForgeCapabilities.ITEM_HANDLER )
			return ForgeCapabilities.ITEM_HANDLER.orEmpty( capability, inventoryHandler );
		return super.getCapability( capability, facing );
	}

	/**
	 * The amount of columns in the container.
	 *
	 * @return Number of columns
	 */
	public int getColumns()
	{
		return 9;
	}

	/**
	 * Returns the custom title of this container.
	 */
	@Override
	@Nullable
	public Component getCustomName()
	{
		return customName;
	}

	@Override
	public Component getDisplayName()
	{
		if( hasCustomName() )
			return getCustomName();
		return getName();
	}

	/**
	 * The number of players which are currently accessing this container.
	 *
	 * @return Number of players using.
	 */
	public final int getPlayersUsing()
	{
		return numPlayersUsing;
	}

	/**
	 * The amount of rows in the container.
	 *
	 * @return Number of rows.
	 */
	public int getRows()
	{
		return 3;
	}

	@Override
	public CompoundTag getUpdateTag()
	{
		final CompoundTag nbt = super.getUpdateTag();

		if( inventory.getSlots() > 0 )
			nbt.put( "Inventory", inventory.serializeNBT() );

		if( hasCustomName() )
			nbt.putString( "CustomName", Component.Serializer.toJson( getCustomName() ) );

		return nbt;
	}

	@Override
	public void handleUpdateTag( CompoundTag nbt )
	{
		super.handleUpdateTag( nbt );

		if( nbt.contains( "Inventory" ) )
			inventory.deserializeNBT( (CompoundTag)nbt.get( "Inventory" ) );

		if( nbt.contains( "CustomName" ) )
			setCustomName( Component.Serializer.fromJson( nbt.getString( "CustomName" ) ) );
	}

	@Override
	public void load( CompoundTag nbt )
	{
		if( nbt.contains( "Inventory" ) )
			inventory.deserializeNBT( (CompoundTag)nbt.get( "Inventory" ) );

		if( nbt.contains( "CustomName" ) )
			setCustomName( Component.Serializer.fromJson( nbt.getString( "CustomName" ) ) );

		super.load( nbt );
	}

	/**
	 * Marks the block for an update, which will cause a description packet to be send to players.
	 */
	@SuppressWarnings("null")
	public void markForUpdate()
	{
		getLevel().sendBlockUpdated( worldPosition, getLevel().getBlockState( worldPosition ), getLevel().getBlockState( worldPosition ), 3 );
		setChanged();
	}

	public void openInventory( Player player )
	{
		if( !player.isSpectator() )
		{
			if( numPlayersUsing < 0 )
				numPlayersUsing = 0;

			++numPlayersUsing;
			onOpenOrClose();
		}
	}

	@Override
	public void saveAdditional( CompoundTag nbt )
	{
		if( inventory.getSlots() > 0 )
			nbt.put( "Inventory", inventory.serializeNBT() );
		if( hasCustomName() )
			nbt.putString( "CustomName", Component.Serializer.toJson( getCustomName() ) );
		super.saveAdditional( nbt );
	}

	/**
	 * Sets the custom title of this container. Has no effect if it can't be set.
	 *
	 * @param name
	 *            The name of this container.
	 */
	public void setCustomName( @Nullable Component name )
	{
		customName = name;
	}

	@Override
	public boolean triggerEvent( int id, int type )
	{
		if( id == EVENT_PLAYER_USING )
		{
			numPlayersUsing = type;
			return true;
		}
		return super.triggerEvent( id, type );
	}

	/**
	 * The size of the container's contents, or 0 if it's not supposed to have any items.
	 *
	 * @return Number of slots.
	 */
	protected int getSizeContents()
	{
		return getColumns() * getRows();
	}

	@SuppressWarnings("null")
	protected void onOpenOrClose()
	{
		final Block block = getBlockState().getBlock();
		if( block instanceof BetterStorageContainerBlock )
		{
			level.blockEvent( worldPosition, block, EVENT_PLAYER_USING, numPlayersUsing );
			level.updateNeighborsAt( worldPosition, block );
		}
	}
}
