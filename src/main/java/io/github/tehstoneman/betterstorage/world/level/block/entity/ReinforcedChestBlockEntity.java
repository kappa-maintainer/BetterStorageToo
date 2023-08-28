package io.github.tehstoneman.betterstorage.world.level.block.entity;

import javax.annotation.Nullable;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.api.ConnectedType;
import io.github.tehstoneman.betterstorage.api.IHasConfig;
import io.github.tehstoneman.betterstorage.api.IHexKeyConfig;
import io.github.tehstoneman.betterstorage.api.lock.IKey;
import io.github.tehstoneman.betterstorage.api.lock.IKeyLockable;
import io.github.tehstoneman.betterstorage.config.BetterStorageConfig;
import io.github.tehstoneman.betterstorage.world.capabilities.ConfigCapability;
import io.github.tehstoneman.betterstorage.world.inventory.ConfigContainerMenu;
import io.github.tehstoneman.betterstorage.world.inventory.ReinforcedChestContainerMenu;
import io.github.tehstoneman.betterstorage.world.item.BetterStorageItems;
import io.github.tehstoneman.betterstorage.world.item.HexKeyConfig;
import io.github.tehstoneman.betterstorage.world.item.enchantment.BetterStorageEnchantments;
import io.github.tehstoneman.betterstorage.world.level.block.ConnectableContainerBlock;
import io.github.tehstoneman.betterstorage.world.level.block.ReinforcedChestBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

public class ReinforcedChestBlockEntity extends ConnectableBlockEntity implements LidBlockEntity, IKeyLockable, IHasConfig
{
	public ReinforcedChestBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		//TODO Auto-generated constructor stub
	}

	protected int								ticksSinceSync;
	private ItemStack							lock			= ItemStack.EMPTY.copy();
	public HexKeyConfig							config;
	private final LazyOptional< IHexKeyConfig >	configHandler	= LazyOptional.of( () -> config );

	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter()
	{

		@Override
		protected boolean isOwnContainer( Player player )
		{
			if( !( player.containerMenu instanceof ReinforcedChestContainerMenu ) )
				return false;
			final ContainerBlockEntity container = ( (ReinforcedChestContainerMenu)player.containerMenu ).getContainer();
			return container == ReinforcedChestBlockEntity.this;
		}

		@Override
		protected void onClose( Level level, BlockPos blockPos, BlockState blockState )
		{
			ReinforcedChestBlockEntity.playSound( level, blockPos, blockState, SoundEvents.CHEST_CLOSE );
		}

		@Override
		protected void onOpen( Level level, BlockPos blockPos, BlockState blockState )
		{
			ReinforcedChestBlockEntity.playSound( level, blockPos, blockState, SoundEvents.CHEST_OPEN );
		}

		@Override
		protected void openerCountChanged( Level level, BlockPos blockPos, BlockState blockState, int p_155466_, int p_155467_ )
		{
			ReinforcedChestBlockEntity.this.signalOpenCount( level, blockPos, blockState, p_155466_, p_155467_ );
		}
	};

	private final ChestLidController chestLidController = new ChestLidController();

	/*public ReinforcedChestBlockEntity( BlockPos blockPos, BlockState blockState )
	{
		//super( BetterStorageBlockEntityTypes.REINFORCED_CHEST.get(), blockPos, blockState );
		config = new HexKeyConfig()
		{
			@Override
			protected void onContentsChanged( int slot )
			{
				ReinforcedChestBlockEntity.this.setChanged();
			}
		};
	}*/

	public static int getOpenCount( BlockGetter level, BlockPos blockPos )
	{
		final BlockState blockState = level.getBlockState( blockPos );
		if( blockState.hasBlockEntity() )
		{
			final BlockEntity blockentity = level.getBlockEntity( blockPos );
			if( blockentity instanceof ReinforcedChestBlockEntity )
				return ( (ReinforcedChestBlockEntity)blockentity ).openersCounter.getOpenerCount();
		}

		return 0;
	}

	public static void lidAnimateTick( Level level, BlockPos blockPos, BlockState blockState, ReinforcedChestBlockEntity blockEntity )
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
	public void applyTrigger()
	{
		setPowered( true );
	}

	@Override
	public boolean canUse( Player player )
	{
		return !isLocked() || getMainBlockEntity().getPlayersUsing() > 0;
	}

	@Override
	public AbstractContainerMenu createMenu( int windowID, Inventory playerInventory, Player player )
	{
		if( !isMain() )
			return getMainBlockEntity().createMenu( windowID, playerInventory, player );
		if( player.getMainHandItem().getItem() == BetterStorageItems.HEX_KEY.get() )
			return new ConfigContainerMenu( windowID, playerInventory, getLevel(), getBlockPos() );
		return new ReinforcedChestContainerMenu( windowID, playerInventory, getLevel(), getBlockPos() );
	}

	@Override
	public void dropInventoryItems()
	{
		super.dropInventoryItems();
		if( !config.isEmpty() )
			if( isConnected() && isMain() )
				( (ReinforcedChestBlockEntity)getConnectedBlockEntity() ).setConfig( config );
			else
				for( int i = 0; i < config.getSlots(); i++ )
				{
					final ItemStack stack = config.getStackInSlot( i );
					if( !stack.isEmpty() )
						Containers.dropItemStack( getLevel(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack );
				}
	}

	@Override
	public <T> LazyOptional< T > getCapability( Capability< T > capability, @Nullable Direction facing )
	{
		if( !isMain() )
			return getMainBlockEntity().getCapability( capability, facing );
		if( capability == ForgeCapabilities.ITEM_HANDLER && isLocked() && facing != null )
			return LazyOptional.empty();
		if( capability == ConfigCapability.CONFIG_CAPABILITY && !isLocked() )
			return ConfigCapability.CONFIG_CAPABILITY.orEmpty( capability, configHandler );
		return super.getCapability( capability, facing );
	}

	@Override
	public int getColumns()
	{
		return BetterStorageConfig.COMMON.reinforcedColumns.get();
	}

	@Override
	public HexKeyConfig getConfig()
	{
		return config;
	}

	@Override
	public BlockPos getConnectedPos()
	{
		if( isConnected() )
			return getBlockPos().relative( ReinforcedChestBlock.getDirectionToAttached( getBlockState() ) );
		return worldPosition;
	}

	@Override
	public ItemStack getLock()
	{
		if( isMain() )
			return lock;
		return ( (IKeyLockable)getMainBlockEntity() ).getLock();
	}

	@Override
	public float getOpenNess( float partialTicks )
	{
		return chestLidController.getOpenness( partialTicks );
		// return prevLidAngle + ( lidAngle - prevLidAngle ) * partialTicks;
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public AABB getRenderBoundingBox()
	{
		return new AABB( worldPosition.offset( -1, 0, -1 ), worldPosition.offset( 2, 2, 2 ) );
	}

	@Override
	public CompoundTag getUpdateTag()
	{
		final CompoundTag nbt = super.getUpdateTag();

		if( !config.isEmpty() )
			nbt.put( "Config", config.serializeNBT() );
		if( !lock.isEmpty() )
			nbt.put( "lock", lock.serializeNBT() );

		return nbt;
	}

	// Trigger enchantment related

	@Override
	public void handleUpdateTag( CompoundTag nbt )
	{
		super.handleUpdateTag( nbt );

		if( nbt.contains( "Config" ) )
			config.deserializeNBT( nbt.getCompound( "Config" ) );
		else
			config = new HexKeyConfig();
		if( nbt.contains( "lock" ) )
		{
			final CompoundTag lockNBT = (CompoundTag)nbt.get( "lock" );
			lock = ItemStack.of( lockNBT );
		} else
			lock = ItemStack.EMPTY;
	}

	/**
	 * Returns if the chest is emitting redstone.
	 *
	 * @return True if powered
	 */
	public boolean isPowered()
	{
		if( isMain() )
			return getLock().getEnchantmentLevel( BetterStorageEnchantments.TRIGGER.get() ) > 0;
		return ( (ReinforcedChestBlockEntity)getMainBlockEntity() ).isPowered();
	}

	@Override
	public void load( CompoundTag nbt )
	{
		if( nbt.contains( "Config" ) )
			config.deserializeNBT( nbt.getCompound( "Config" ) );
		if( nbt.contains( "lock" ) )
		{
			final CompoundTag lockNBT = (CompoundTag)nbt.get( "lock" );
			lock = ItemStack.of( lockNBT );
		} else
			lock = ItemStack.EMPTY;

		super.load( nbt );
	}

	@Override
	@SuppressWarnings("null")
	public void onDataPacket( Connection network, ClientboundBlockEntityDataPacket packet )
	{
		final CompoundTag nbt = packet.getTag();
		if( nbt.contains( "Config" ) )
			config.deserializeNBT( nbt.getCompound( "Config" ) );
		else
			config = new HexKeyConfig();
		if( nbt.contains( "lock" ) )
		{
			final CompoundTag lockNBT = (CompoundTag)nbt.get( "lock" );
			lock = ItemStack.of( lockNBT );
		} else
			lock = ItemStack.EMPTY;
	}

	public void recheckOpen()
	{
		if( !remove )
			openersCounter.recheckOpeners( getLevel(), getBlockPos(), getBlockState() );
	}

	@SuppressWarnings("null")
	public void renderUpdate()
	{
		if( isMain() )
		{
			final BlockState state = getBlockState();

			// Notify nearby blocks
			getLevel().setBlocksDirty( worldPosition, state, state );
		} else
			( (ReinforcedChestBlockEntity)getMainBlockEntity() ).renderUpdate();
	}

	@Override
	public void setConfig( HexKeyConfig config )
	{
		this.config = config;
		setChanged();
	}

	/*
	 * @Override
	 * public ClientboundBlockEntityDataPacket getUpdatePacket()
	 * {
	 * return new ClientboundBlockEntityDataPacket( worldPosition, 1, getUpdateTag() );
	 * }
	 */

	@Override
	@SuppressWarnings("null")
	public void setLock( ItemStack lock )
	{
		if( isMain() )
		{
			if( lock.isEmpty() || isLockValid( lock ) )
			{
				this.lock = lock;
				getLevel().sendBlockUpdated( worldPosition, getBlockState(), getBlockState(), 3 );
				setPowered( lock.getEnchantmentLevel( BetterStorageEnchantments.TRIGGER.get() ) > 0 );
				setChanged();
			}
		} else
			( (IKeyLockable)getMainBlockEntity() ).setLock( lock );
	}

	/*
	 * @Override
	 * public CompoundTag save( CompoundTag nbt )
	 * {
	 * if( !config.isEmpty() )
	 * nbt.put( "Config", config.serializeNBT() );
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

	/**
	 * Sets if the chest is emitting redstone.
	 * <p>
	 * Updates all nearby blocks to make sure they notice it.
	 *
	 * @param powered
	 *            True if powered
	 */
	@SuppressWarnings("null")
	public void setPowered( boolean powered )
	{
		if( !isMain() )
		{
			( (ReinforcedChestBlockEntity)getMainBlockEntity() ).setPowered( powered );
			return;
		}

		final Block block = getBlockState().getBlock();

		// Notify nearby blocks
		getLevel().updateNeighborsAt( worldPosition, block );

		// Notify nearby blocks of adjacent chest
		if( isConnected() )
			getLevel().updateNeighborsAt( getConnectedPos(), block );
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
	public boolean unlockWith( ItemStack heldItem )
	{
		final Item item = heldItem.getItem();
		return item instanceof IKey ? ( (IKey)item ).unlock( heldItem, getLock(), false ) : false;
	}

	@Override
	protected String getConnectableName()
	{
		return ModInfo.CONTAINER_REINFORCED_CHEST_NAME;
	}

	protected void signalOpenCount( Level level, BlockPos blockPos, BlockState blockState, int p_155336_, int p_155337_ )
	{
		final Block block = blockState.getBlock();
		level.blockEvent( blockPos, block, 1, p_155337_ );
	}
}
