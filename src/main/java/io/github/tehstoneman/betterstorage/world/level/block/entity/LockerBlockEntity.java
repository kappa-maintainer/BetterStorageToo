package io.github.tehstoneman.betterstorage.world.level.block.entity;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.api.ConnectedType;
import io.github.tehstoneman.betterstorage.world.inventory.LockerContainerMenu;
import io.github.tehstoneman.betterstorage.world.level.block.ConnectableContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class LockerBlockEntity extends ConnectableBlockEntity implements LidBlockEntity
{
	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter()
	{
		@Override
		protected boolean isOwnContainer( Player player )
		{
			if( !( player.containerMenu instanceof LockerContainerMenu ) )
				return false;
			final ContainerBlockEntity container = ( (LockerContainerMenu)player.containerMenu ).getContainer();
			return container == LockerBlockEntity.this;
		}

		@Override
		protected void onClose( Level level, BlockPos blockPos, BlockState blockState )
		{
			LockerBlockEntity.playSound( level, blockPos, blockState, SoundEvents.CHEST_CLOSE );
		}

		@Override
		protected void onOpen( Level level, BlockPos blockPos, BlockState blockState )
		{
			LockerBlockEntity.playSound( level, blockPos, blockState, SoundEvents.CHEST_OPEN );
		}

		@Override
		protected void openerCountChanged( Level level, BlockPos blockPos, BlockState blockState, int p_155466_, int p_155467_ )
		{
			LockerBlockEntity.this.signalOpenCount( level, blockPos, blockState, p_155466_, p_155467_ );
		}
	};

	private final ChestLidController chestLidController = new ChestLidController();

	public LockerBlockEntity( BlockEntityType< ? > blockEntityType, BlockPos blockPos, BlockState blockState )
	{
		super( blockEntityType, blockPos, blockState );
	}

	//public LockerBlockEntity( BlockPos blockPos, BlockState blockState )
	//{
		//super( BetterStorageBlockEntityTypes.LOCKER.get(), blockPos, blockState );
	//}

	public static void lidAnimateTick( Level level, BlockPos blockPos, BlockState blockState, LockerBlockEntity blockEntity )
	{
		blockEntity.chestLidController.tickLid();
	}

	protected static void playSound( Level level, BlockPos blockPos, BlockState blockState, SoundEvent soundEvent )
	{
		final ConnectedType chestType = blockState.getValue( ConnectableContainerBlock.TYPE );
		if( chestType != ConnectedType.SLAVE )
		{
			final double	d0	= blockPos.getX() + 0.5D;
			final double	d1	= blockPos.getY() + 0.5D;
			final double	d2	= blockPos.getZ() + 0.5D;
			/*
			 * if( chestType == ConnectedType.MASTER )
			 * {
			 * final Direction direction = ConnectableContainerBlock.getConnectedDirection( blockState );
			 * d0 += direction.getStepX() * 0.5D;
			 * d2 += direction.getStepZ() * 0.5D;
			 * }
			 */

			level.playSound( (Player)null, d0, d1, d2, soundEvent, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F );
		}
	}

	@Override
	public AbstractContainerMenu createMenu( int windowID, Inventory playerInventory, Player player )
	{
		if( isMain() )
			return new LockerContainerMenu( windowID, playerInventory, level, worldPosition );
		return getMainBlockEntity().createMenu( windowID, playerInventory, player );
	}

	@Override
	public BlockPos getConnectedPos()
	{
		if( isConnected() )
		{
			if( isMain() )
				return worldPosition.above();
			return worldPosition.below();
		}
		return null;
	}

	@Override
	public float getOpenNess( float partialTicks )
	{
		return chestLidController.getOpenness( partialTicks );
		// return prevLidAngle + ( lidAngle - prevLidAngle ) * partialTicks;
	}

	@Override
	public AABB getRenderBoundingBox()
	{
		return new AABB( worldPosition.offset( -1, 0, -1 ), worldPosition.offset( 2, 2, 2 ) );
	}

	public void recheckOpen()
	{
		if( !remove )
			openersCounter.recheckOpeners( getLevel(), getBlockPos(), getBlockState() );
	}

	public void startOpen( Player player )
	{
		if( !remove && !player.isSpectator() )
			openersCounter.incrementOpeners( player, getLevel(), getBlockPos(), getBlockState() );
	}

	public void stopOpen( Player player )
	{
		if( !remove && !player.isSpectator() )
			openersCounter.decrementOpeners( player, getLevel(), getBlockPos(), getBlockState() );
	}

	@Override
	public boolean triggerEvent( int id, int type )
	{
		if( id == EVENT_PLAYER_USING )
		{
			chestLidController.shouldBeOpen( type > 0 );
			return true;
		}
		return super.triggerEvent( id, type );
	}

	@Override
	protected String getConnectableName()
	{
		return ModInfo.CONTAINER_LOCKER_NAME;
	}

	protected void signalOpenCount( Level level, BlockPos blockPos, BlockState blockState, int p_155336_, int p_155337_ )
	{
		final Block block = blockState.getBlock();
		level.blockEvent( blockPos, block, 1, p_155337_ );
	}
}
