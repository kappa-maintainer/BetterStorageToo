package io.github.tehstoneman.betterstorage.world.level.block.entity;

import javax.annotation.Nullable;

import io.github.tehstoneman.betterstorage.api.ConnectedType;
import io.github.tehstoneman.betterstorage.world.inventory.ConnectedStackHandler;
import io.github.tehstoneman.betterstorage.world.level.block.ConnectableContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public abstract class ConnectableBlockEntity extends ContainerBlockEntity
{
	public ConnectedStackHandler				connectedInventory;
	private final LazyOptional< IItemHandler >	connectedHandler	= LazyOptional.of( () -> connectedInventory );

	public ConnectableBlockEntity( BlockEntityType< ? > blockEntityType, BlockPos blockPos, BlockState blockState )
	{
		super( blockEntityType, blockPos, blockState );
	}

	@Override
	public <T> LazyOptional< T > getCapability( Capability< T > capability, @Nullable Direction facing )
	{
		if( isConnected() )
		{
			if( !isMain() )
				return getMainBlockEntity().getCapability( capability, facing );
			if( capability == ForgeCapabilities.ITEM_HANDLER )
			{
				connectedInventory = new ConnectedStackHandler( inventory, getConnectedBlockEntity().inventory );
				return ForgeCapabilities.ITEM_HANDLER.orEmpty( capability, connectedHandler );
			}
		}
		return super.getCapability( capability, facing );
	}

	/**
	 * Returns the connected container.
	 *
	 * @return Connected container
	 */
	@SuppressWarnings("null")
	public ConnectableBlockEntity getConnectedBlockEntity()
	{
		if( getLevel() == null || !isConnected() )
			return null;
		final BlockEntity blockEntity = getLevel().getBlockEntity( getConnectedPos() );
		return blockEntity instanceof ConnectableBlockEntity ? (ConnectableBlockEntity)blockEntity : null;
	}

	/**
	 * Returns position of the connected BlockEntity
	 *
	 * @return Position
	 */
	public abstract BlockPos getConnectedPos();

	/**
	 * Returns the main container.
	 *
	 * @return Main container
	 */
	public ConnectableBlockEntity getMainBlockEntity()
	{
		if( isMain() )
			return this;
		final ConnectableBlockEntity blockEntity = getConnectedBlockEntity();
		if( blockEntity != null )
			return blockEntity;
		return this;
	}

	@Override
	public Component getName()
	{
		return Component.translatable( getConnectableName().concat( isConnected() ? "_large" : "" ) );
	}

	/**
	 * Returns if this container is connected to another one.
	 *
	 * @return True if connected
	 */
	public boolean isConnected()
	{
		final BlockState blockState = getBlockState();
		if( blockState.hasProperty( ConnectableContainerBlock.TYPE ) )
			return blockState.getValue( ConnectableContainerBlock.TYPE ) != ConnectedType.SINGLE;
		return false;
	}

	/**
	 * Returns if this container is the main container, or not connected to another container.
	 *
	 * @return True if main
	 */
	public boolean isMain()
	{
		final BlockState blockState = getBlockState();
		if( blockState.hasProperty( ConnectableContainerBlock.TYPE ) )
			return blockState.getValue( ConnectableContainerBlock.TYPE )	== ConnectedType.SINGLE
					|| blockState.getValue( ConnectableContainerBlock.TYPE ) == ConnectedType.MASTER;
		return true;
	}

	@Override
	public void setChanged()
	{
		if( !isMain() )
			getConnectedBlockEntity().setChanged();
		super.setChanged();
	}

	/**
	 * Returns the unlocalized name of the container. <br>
	 * "Large" will be appended if the container is connected to another one.
	 *
	 * @return The name of the container
	 */
	protected abstract String getConnectableName();
}
