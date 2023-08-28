package io.github.tehstoneman.betterstorage.world.level.block;

import javax.annotation.Nullable;

import io.github.tehstoneman.betterstorage.api.ConnectedType;
import io.github.tehstoneman.betterstorage.api.lock.ILock;
import io.github.tehstoneman.betterstorage.api.lock.LockInteraction;
import io.github.tehstoneman.betterstorage.world.item.HexKeyConfig;
import io.github.tehstoneman.betterstorage.world.item.enchantment.BetterStorageEnchantments;
import io.github.tehstoneman.betterstorage.world.level.block.entity.ContainerBlockEntity;
import io.github.tehstoneman.betterstorage.world.level.block.entity.LockerBlockEntity;
import io.github.tehstoneman.betterstorage.world.level.block.entity.ReinforcedLockerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;

public class ReinforcedLockerBlock extends ConnectableContainerBlock implements SimpleWaterloggedBlock
{
	public static final EnumProperty< DoorHingeSide >	HINGE		= BlockStateProperties.DOOR_HINGE;
	public static final BooleanProperty					WATERLOGGED	= BlockStateProperties.WATERLOGGED;

	protected static final VoxelShape	SHAPE_NORTH	= Block.box( 0.0D, 0.0D, 1.0D, 16.0D, 16.0D, 16.0D );
	protected static final VoxelShape	SHAPE_SOUTH	= Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 15.0D );
	protected static final VoxelShape	SHAPE_WEST	= Block.box( 1.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D );
	protected static final VoxelShape	SHAPE_EAST	= Block.box( 0.0D, 0.0D, 0.0D, 15.0D, 16.0D, 16.0D );

	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	/*public ReinforcedLockerBlock()
	{
		this( Block.Properties.of( Material.WOOD ).strength( 5.0F, 6.0F ).sound( SoundType.WOOD ) );
	}*/

	public ReinforcedLockerBlock( Properties properties )
	{
		super( properties );

		//@formatter:off
		registerDefaultState( defaultBlockState().setValue( FACING, Direction.NORTH )
												 .setValue( HINGE, DoorHingeSide.LEFT )
												 .setValue( WATERLOGGED, false ) );
		//@formatter:on
	}

	public static int calcRedstoneFromInventory( LazyOptional< IItemHandler > lazyOptional )
	{
		if( !lazyOptional.isPresent() )
			return 0;

		final IItemHandler	inventory	= lazyOptional.orElseGet( null );
		int					i			= 0;
		float				f			= 0.0F;

		for( int j = 0; j < inventory.getSlots(); ++j )
		{
			final ItemStack itemstack = inventory.getStackInSlot( j );
			if( !itemstack.isEmpty() )
			{
				f += (float)itemstack.getCount() / itemstack.getMaxStackSize();
				++i;
			}
		}

		f = f / inventory.getSlots();
		return Mth.floor( f * 14.0F ) + ( i > 0 ? 1 : 0 );
	}

	public static Direction getDirectionToAttached( BlockState state )
	{
		return state.getValue( TYPE ) == ConnectedType.SLAVE ? Direction.DOWN : Direction.UP;
	}

	@Nullable
	public static ReinforcedLockerBlockEntity getLockerAt( Level world, BlockPos pos )
	{
		final BlockEntity tileEntity = world.getBlockEntity( pos );
		if( tileEntity instanceof ReinforcedLockerBlockEntity )
			return (ReinforcedLockerBlockEntity)tileEntity;
		return null;
	}

	private static boolean isBehindSolidBlock( BlockGetter reader, BlockPos worldIn )
	{
		final Direction	facing		= reader.getBlockState( worldIn ).getValue( FACING );
		final BlockPos	blockPos	= worldIn.relative( facing );
		return reader.getBlockState( blockPos ).isRedstoneConductor( reader, blockPos );
	}

	private static boolean isBlocked( LevelAccessor world, BlockPos pos )
	{
		return isBehindSolidBlock( world, pos );
	}

	@Override
	@SuppressWarnings("null")
	public int getAnalogOutputSignal( BlockState blockState, Level worldIn, BlockPos pos )
	{
		return calcRedstoneFromInventory( ( (LockerBlockEntity)worldIn.getBlockEntity( pos ) ).getCapability( ForgeCapabilities.ITEM_HANDLER ) );
	}

	public MenuProvider getContainer( BlockState state, Level level, BlockPos pos, boolean allowBlockedChest )
	{
		final ReinforcedLockerBlockEntity blockLocker = (ReinforcedLockerBlockEntity)level.getBlockEntity( pos );
		if( blockLocker == null || !allowBlockedChest && isBlocked( level, pos ) )
			return null;

		final ConnectedType lockerType = state.getValue( TYPE );

		if( lockerType != ConnectedType.SINGLE )
		{
			final BlockPos		masterPos	= pos.relative( getDirectionToAttached( state ) );
			final BlockState	masterState	= level.getBlockState( masterPos );

			if( masterState.getBlock() == this )
			{
				final ConnectedType masterType = masterState.getValue( TYPE );

				if( masterType != ConnectedType.SINGLE && lockerType != masterType && masterState.getValue( FACING ) == state.getValue( FACING ) )
					if( !allowBlockedChest && isBlocked( level, masterPos ) )
						return null;
			}
		}
		return blockLocker;
	}

	@Override
	public int getDirectSignal( BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side )
	{
		return side == Direction.UP ? blockState.getSignal( blockAccess, pos, side ) : 0;
	}

	@Override
	public float getExplosionResistance( BlockState state, BlockGetter world, BlockPos pos, Explosion explosion )
	{
		final ReinforcedLockerBlockEntity chest = getLockerAt( (Level)world, pos );
		if( chest != null && chest.isLocked() )
		{
			final int resist = chest.getLock().getEnchantmentLevel( BetterStorageEnchantments.PERSISTANCE.get() ) + 1;
			return super.getExplosionResistance( state, world, pos, explosion ) * resist * 2;
		}
		return super.getExplosionResistance( state, world, pos, explosion );
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public FluidState getFluidState( BlockState state )
	{
		return state.getValue( WATERLOGGED ) ? Fluids.WATER.getSource( false ) : super.getFluidState( state );
	}

	@Override
	@Nullable
	public MenuProvider getMenuProvider( BlockState state, Level level, BlockPos pos )
	{
		return getContainer( state, level, pos, false );
	}

	@Override
	public VoxelShape getShape( BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context )
	{
		switch( state.getValue( FACING ) )
		{
			case NORTH:
			default:
				return SHAPE_NORTH;

			case SOUTH:
				return SHAPE_SOUTH;

			case WEST:
				return SHAPE_WEST;

			case EAST:
				return SHAPE_EAST;
		}
	}

	@Override
	public int getSignal( BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side )
	{
		final BlockEntity tileEntity = blockAccess.getBlockEntity( pos );
		if( tileEntity instanceof final ReinforcedLockerBlockEntity chest )
			return chest.isPowered() ? Mth.clamp( chest.getPlayersUsing(), 0, 15 ) : 0;
		return 0;
	}

	@Override
	@SuppressWarnings("null")
	public BlockState getStateForPlacement( BlockPlaceContext context )
	{
		ConnectedType		connectedType	= ConnectedType.SINGLE;
		final Direction		direction		= context.getHorizontalDirection().getOpposite();
		final FluidState	fluidState		= context.getLevel().getFluidState( context.getClickedPos() );
		final boolean		sneaking		= context.getPlayer().isShiftKeyDown();
		DoorHingeSide		hingeSide		= getHingeSide( context );
		if( connectedType == ConnectedType.SINGLE && !sneaking )
			if( direction == getDirectionToAttach( context, Direction.DOWN ) )
			{
				connectedType	= ConnectedType.SLAVE;
				hingeSide		= context.getLevel().getBlockState( context.getClickedPos().relative( Direction.DOWN ) ).getValue( HINGE );
			} else if( direction == getDirectionToAttach( context, Direction.UP ) )
			{
				connectedType	= ConnectedType.MASTER;
				hingeSide		= context.getLevel().getBlockState( context.getClickedPos().relative( Direction.UP ) ).getValue( HINGE );
			}

		return defaultBlockState().setValue( FACING, direction ).setValue( HINGE, hingeSide ).setValue( TYPE, connectedType ).setValue( WATERLOGGED,
			fluidState.getType() == Fluids.WATER );
	}

	/*@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker< T > getTicker( Level level, BlockState blockState, BlockEntityType< T > blockEntityType )
	{
		return level.isClientSide ? createTickerHelper( blockEntityType, BetterStorageBlockEntityTypes.REINFORCED_LOCKER.get(),
			ReinforcedLockerBlockEntity::lidAnimateTick ) : null;
	}*/

	@Override
	public boolean hasAnalogOutputSignal( BlockState state )
	{
		return true;
	}

	@Override
	public boolean isSignalSource( BlockState state )
	{
		return true;
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public BlockState mirror( BlockState state, Mirror mirrorIn )
	{
		return state.rotate( mirrorIn.getRotation( state.getValue( FACING ) ) );
	}

	/*@Override
	public BlockEntity newBlockEntity( BlockPos blockPos, BlockState blockState )
	{
		return new ReinforcedLockerBlockEntity( blockPos, blockState );
	}*/

	@SuppressWarnings( {"deprecation","null"} )
	@Override
	public void onRemove( BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving )
	{
		if( state.getBlock() != newState.getBlock() )
		{
			final BlockEntity tileEntity = world.getBlockEntity( pos );
			if( tileEntity instanceof ContainerBlockEntity )
			{
				if( state.getValue( TYPE ) == ConnectedType.MASTER )
				{
					final ReinforcedLockerBlockEntity	thisLocker		= getLockerAt( world, pos );
					final ReinforcedLockerBlockEntity	facingLocker	= getLockerAt( world, pos.relative( getDirectionToAttached( state ) ) );

					facingLocker.setConfig( thisLocker.getConfig() );
					thisLocker.setConfig( new HexKeyConfig() );
				}
				( (ContainerBlockEntity)tileEntity ).dropInventoryItems();
				world.updateNeighbourForOutputSignal( pos, this );
			}

			super.onRemove( state, world, pos, newState, isMoving );
		}
	}

	@Override
	public BlockState rotate( BlockState state, Rotation rot )
	{
		return state.setValue( FACING, rot.rotate( state.getValue( FACING ) ) );
	}

	@Override
	public void setPlacedBy( Level worldIn, BlockPos pos, BlockState state,@Nullable LivingEntity placer, ItemStack stack )
	{
		if( stack.hasCustomHoverName() )
		{
			final BlockEntity tileEntity = worldIn.getBlockEntity( pos );
			if( tileEntity instanceof ContainerBlockEntity )
				( (ContainerBlockEntity)tileEntity ).setCustomName( stack.getDisplayName() );
		}
	}

	@Override
	public void tick( BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource random )
	{
		final BlockEntity blockentity = level.getBlockEntity( blockPos );
		if( blockentity instanceof ReinforcedLockerBlockEntity )
			( (ReinforcedLockerBlockEntity)blockentity ).recheckOpen();

	}

	@SuppressWarnings( {"deprecation","null"} )
	@Override
	public BlockState updateShape(	BlockState thisState, Direction facing, BlockState facingState, LevelAccessor world, BlockPos thisPos,
									BlockPos facingPos )
	{
		if( thisState.getValue( WATERLOGGED ) )
			world.scheduleTick( thisPos, Fluids.WATER, Fluids.WATER.getTickDelay( world ) );

		if( facingState.getBlock() == this && facing.getAxis().isVertical() )
		{
			final ConnectedType facingType = facingState.getValue( TYPE );

			if( thisState.getValue( TYPE )	== ConnectedType.SINGLE && facingType != ConnectedType.SINGLE
				&& thisState.getValue( FACING ) == facingState.getValue( FACING ) && getDirectionToAttached( facingState ) == facing.getOpposite() )
			{
				final ConnectedType					newType			= facingType.opposite();
				final ReinforcedLockerBlockEntity	thisLocker		= getLockerAt( (Level)world, thisPos );
				final ReinforcedLockerBlockEntity	facingLocker	= getLockerAt( (Level)world, facingPos );
				if( newType == ConnectedType.SLAVE )
				{
					facingLocker.setConfig( thisLocker.getConfig() );
					thisLocker.setConfig( new HexKeyConfig() );
				}
				return thisState.setValue( TYPE, newType );
			}
		} else if( getDirectionToAttached( thisState ) == facing )
			return thisState.setValue( TYPE, ConnectedType.SINGLE );

		return super.updateShape( thisState, facing, facingState, world, thisPos, facingPos );
	}

	@Override
	public InteractionResult use( BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit )
	{
		if( hit.getDirection() == state.getValue( FACING ) )
		{
			if( !level.isClientSide )
			{
				final ReinforcedLockerBlockEntity tileChest = getLockerAt( level, pos );
				if( tileChest != null && tileChest.isLocked() )
				{
					if( !tileChest.unlockWith( player.getItemInHand( hand ) ) )
					{
						final ItemStack lock = tileChest.getLock();
						( (ILock)lock.getItem() ).applyEffects( lock, tileChest, player, LockInteraction.OPEN );
						return InteractionResult.PASS;
					}
					if( player.isCrouching() )
					{
						level.addFreshEntity( new ItemEntity( level, player.getX(), player.getY(), player.getZ(), tileChest.getLock().copy() ) );
						tileChest.setLock( ItemStack.EMPTY );
						return InteractionResult.SUCCESS;
					}
				}
				final MenuProvider locker = getMenuProvider( state, level, pos );
				if( locker != null )
				{
					NetworkHooks.openScreen( (ServerPlayer)player, locker, pos );
					player.awardStat( getOpenStat() );
				}
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Nullable
	private Direction getDirectionToAttach( BlockPlaceContext context, Direction facing )
	{
		final BlockState faceState = context.getLevel().getBlockState( context.getClickedPos().relative( facing ) );
		return faceState.getBlock() == this && faceState.getValue( TYPE ) == ConnectedType.SINGLE ? faceState.getValue( FACING ) : null;
	}

	private DoorHingeSide getHingeSide( BlockPlaceContext context )
	{
		final BlockPos	blockPos	= context.getClickedPos();
		final Direction	direction	= context.getHorizontalDirection();

		final Vec3		v		= context.getClickLocation();
		final double	hitX	= v.x - blockPos.getX();
		final double	hitY	= v.z - blockPos.getZ();

		return direction	== Direction.NORTH	&& hitX <= 0.5D || direction == Direction.SOUTH && hitX >= 0.5D
				|| direction == Direction.WEST && hitY >= 0.5D || direction == Direction.EAST && hitY <= 0.5D	? DoorHingeSide.LEFT
																												: DoorHingeSide.RIGHT;
	}

	@Override
	protected void createBlockStateDefinition( StateDefinition.Builder< Block, BlockState > builder )
	{
		super.createBlockStateDefinition( builder );
		builder.add( FACING, HINGE, WATERLOGGED );
	}

	protected Stat< ResourceLocation > getOpenStat()
	{
		return Stats.CUSTOM.get( Stats.OPEN_CHEST );
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'newBlockEntity'");
	}
}
