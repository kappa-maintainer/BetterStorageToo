package io.github.tehstoneman.betterstorage.world.level.block.entity;

import io.github.tehstoneman.betterstorage.api.lock.IKey;
import io.github.tehstoneman.betterstorage.api.lock.IKeyLockable;
import io.github.tehstoneman.betterstorage.util.WorldUtils;
import io.github.tehstoneman.betterstorage.world.item.enchantment.BetterStorageEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;

public class LockableDoorBlockEntity extends BlockEntity implements IKeyLockable
{

	private ItemStack lock = ItemStack.EMPTY.copy();

	public LockableDoorBlockEntity( BlockEntityType< ? > tileEntityTypeIn, BlockPos blockPos, BlockState blockState )
	{
		super( tileEntityTypeIn, blockPos, blockState );
	}

	public LockableDoorBlockEntity( BlockPos blockPos, BlockState blockState )
	{
		this( BetterStorageBlockEntityTypes.LOCKABLE_DOOR.get(), blockPos, blockState );
	}

	@Override
	public void applyTrigger()
	{}

	@Override
	public boolean canUse( Player player )
	{
		return false;
	}

	@Override
	public ItemStack getLock()
	{
		if( isMain() )
			return lock;
		return ( (LockableDoorBlockEntity)getMain() ).getLock();
	}

	@SuppressWarnings("null")
	public BlockEntity getMain()
	{
		if( isMain() )
			return this;
		return level.getBlockEntity( worldPosition.below() );
	}

	// BlockEntity synchronization

	@Override
	public AABB getRenderBoundingBox()
	{
		return WorldUtils.getAABB( this, 0, 0, 0, 0, 1, 0 );
	}

	@Override
	public CompoundTag getUpdateTag()
	{
		final CompoundTag nbt = super.getUpdateTag();

		if( !lock.isEmpty() )
			nbt.put( "lock", lock.serializeNBT() );

		return nbt;
	}

	/*
	 * @Override
	 * public void handleUpdateTag( CompoundTag nbt )
	 * {
	 * super.handleUpdateTag( nbt );
	 *
	 * if( nbt.contains( "lock" ) )
	 * {
	 * final CompoundTag lockNBT = (CompoundTag)nbt.get( "lock" );
	 * lock = ItemStack.of( lockNBT );
	 * }
	 * else
	 * lock = ItemStack.EMPTY;
	 * }
	 */

	// Reading from / writing to NBT

	/*
	 * @Override
	 * public CompoundTag save( CompoundTag nbt )
	 * {
	 * if( !lock.isEmpty() )
	 * {
	 * final CompoundTag lockNBT = new CompoundTag();
	 * lock.save( lockNBT );
	 * nbt.put( "lock", lockNBT );
	 * }
	 *
	 * return super.save( nbt );
	 * }
	 */

	/*
	 * @Override
	 * public void read( CompoundTag nbt )
	 * {
	 * if( nbt.contains( "lock" ) )
	 * {
	 * final CompoundTag lockNBT = (CompoundTag)nbt.get( "lock" );
	 * lock = ItemStack.of( lockNBT );
	 * }
	 * else
	 * lock = ItemStack.EMPTY;
	 *
	 * super.read( nbt );
	 * }
	 */

	/*
	 * ============
	 * IKeyLockable
	 * ============
	 */

	public boolean isMain()
	{
		return getBlockState().getValue( DoorBlock.HALF ) == DoubleBlockHalf.LOWER;
	}

	public boolean isPowered()
	{
		if( isMain() )
			return getLock().getEnchantmentLevel( BetterStorageEnchantments.TRIGGER.get() ) > 0;
		return ( (LockableDoorBlockEntity)getMain() ).isPowered();
	}

	@Override
	@SuppressWarnings("null")
	public void onDataPacket( Connection network, ClientboundBlockEntityDataPacket packet )
	{
		final CompoundTag nbt = packet.getTag();
		if( nbt.contains( "lock" ) )
		{
			final CompoundTag lockNBT = (CompoundTag)nbt.get( "lock" );
			lock = ItemStack.of( lockNBT );
		} else
			lock = ItemStack.EMPTY;
	}

	@Override
	@SuppressWarnings("null")
	public void setLock( ItemStack lock )
	{
		// Turn it back into a normal iron door
		if( lock.isEmpty() )
		{
			this.lock = ItemStack.EMPTY;
			final BlockState blockState = level.getBlockState( worldPosition );

			//@formatter:off
				level.setBlockAndUpdate( worldPosition, Blocks.IRON_DOOR.defaultBlockState()
						.setValue( DoorBlock.FACING, blockState.getValue( DoorBlock.FACING ) )
						.setValue( DoorBlock.OPEN, blockState.getValue( DoorBlock.OPEN ) )
						.setValue( DoorBlock.HINGE, blockState.getValue( DoorBlock.HINGE ) )
						.setValue( DoorBlock.HALF, DoubleBlockHalf.LOWER ) );
				level.setBlockAndUpdate( worldPosition.above(), Blocks.IRON_DOOR.defaultBlockState()
						.setValue( DoorBlock.FACING, blockState.getValue( DoorBlock.FACING ) )
						.setValue( DoorBlock.OPEN, blockState.getValue( DoorBlock.OPEN ) )
						.setValue( DoorBlock.HINGE, blockState.getValue( DoorBlock.HINGE ) )
						.setValue( DoorBlock.HALF, DoubleBlockHalf.UPPER ) );
				//@formatter:on

			level.sendBlockUpdated( worldPosition, blockState, blockState, 3 );
		} else if( isLockValid( lock ) )
			setLockWithUpdate( lock );

	}

	@SuppressWarnings("null")
	public void setLockWithUpdate( ItemStack lock )
	{
		this.lock = lock;
		final BlockState blockState = level.getBlockState( worldPosition );
		level.sendBlockUpdated( worldPosition, blockState, blockState, 3 );
		setChanged();
	}

	@Override
	public boolean unlockWith( ItemStack heldItem )
	{
		final Item item = heldItem.getItem();
		return item instanceof IKey ? ( (IKey)item ).unlock( heldItem, getLock(), false ) : false;
	}
}
