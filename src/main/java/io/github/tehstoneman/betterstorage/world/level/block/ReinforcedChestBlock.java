package io.github.tehstoneman.betterstorage.world.level.block;

import java.util.List;

import javax.annotation.Nullable;

import io.github.tehstoneman.betterstorage.api.ConnectedType;
import io.github.tehstoneman.betterstorage.api.lock.ILock;
import io.github.tehstoneman.betterstorage.api.lock.LockInteraction;
import io.github.tehstoneman.betterstorage.world.item.HexKeyConfig;
import io.github.tehstoneman.betterstorage.world.item.enchantment.BetterStorageEnchantments;
import io.github.tehstoneman.betterstorage.world.level.block.entity.ContainerBlockEntity;
import io.github.tehstoneman.betterstorage.world.level.block.entity.ReinforcedChestBlockEntity;
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
import net.minecraft.world.entity.animal.Cat;
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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;

public class ReinforcedChestBlock extends ConnectableContainerBlock implements SimpleWaterloggedBlock
{
	public static final BooleanProperty		WATERLOGGED		= BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape		SHAPE_NORTH		= Block.box( 1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D );
	protected static final VoxelShape		SHAPE_SOUTH		= Block.box( 1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D );
	protected static final VoxelShape		SHAPE_WEST		= Block.box( 0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D );
	protected static final VoxelShape		SHAPE_EAST		= Block.box( 1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D );
	protected static final VoxelShape		SHAPE_SINGLE	= Block.box( 1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D );
	public static final DirectionProperty	FACING			= BlockStateProperties.FACING;

	//public ReinforcedChestBlock()
	//{
		//this( Block.Properties.of( Material.WOOD ).strength( 5.0F, 6.0F ).sound( SoundType.WOOD ).noOcclusion() );
	//}

	public ReinforcedChestBlock( Properties properties )
	{
		super( properties );

		//@formatter:off
		registerDefaultState( defaultBlockState().setValue( FACING, Direction.NORTH )
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

	@Nullable
	public static ReinforcedChestBlockEntity getChestAt( Level world, BlockPos pos )
	{
		final BlockEntity tileEntity = world.getBlockEntity( pos );
		if( tileEntity instanceof ReinforcedChestBlockEntity )
			return (ReinforcedChestBlockEntity)tileEntity;
		return null;
	}

	/**
	 * Returns a facing pointing from the given state to its attached double chest
	 *
	 * @param state
	 *            The BlockState of the block to attach to
	 *
	 * @return The direction of the attached block
	 */
	public static Direction getDirectionToAttached( BlockState state )
	{
		final Direction direction = state.getValue( FACING );
		return state.getValue( TYPE ) == ConnectedType.SLAVE ? direction.getClockWise() : direction.getCounterClockWise();
	}

	private static boolean isBelowSolidBlock( BlockGetter reader, BlockPos worldIn )
	{
		final BlockPos blockPos = worldIn.above();
		return reader.getBlockState( blockPos ).isRedstoneConductor( reader, blockPos );
	}

	private static boolean isBlocked( LevelAccessor world, BlockPos pos )
	{
		return isBelowSolidBlock( world, pos ) || isCatSittingOn( world, pos );
	}

	private static boolean isCatSittingOn( LevelAccessor world, BlockPos pos )
	{
		final List< Cat > list = world.getEntitiesOfClass( Cat.class,
			new AABB( pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1 ) );
		if( !list.isEmpty() )
			for( final Cat catEntity : list )
				if( catEntity.isInSittingPose() )
					return true;

		return false;
	}

	@Override
	@SuppressWarnings("null")
	public int getAnalogOutputSignal( BlockState blockState, Level worldIn, BlockPos pos )
	{
		return calcRedstoneFromInventory(
			( (ReinforcedChestBlockEntity)worldIn.getBlockEntity( pos ) ).getCapability( ForgeCapabilities.ITEM_HANDLER ) );
	}

	/**
	 * Gets the chest inventory at the given location, returning null if there is no chest at that location or optionally
	 * if the chest is blocked. Handles large chests.
	 *
	 * @param state
	 *            The current state
	 * @param worldIn
	 *            The world
	 * @param pos
	 *            The position to check
	 * @param allowBlockedChest
	 *            If false, then if the chest is blocked then <code>null</code> will be returned. If true,
	 *            then the chest can still be blocked (used by hoppers).
	 *
	 * @return The chest at the position, or null if none.
	 */
	@Nullable
	public ReinforcedChestBlockEntity getContainer( BlockState state, Level worldIn, BlockPos pos, boolean allowBlockedChest )
	{
		final ReinforcedChestBlockEntity blockEntity = (ReinforcedChestBlockEntity)worldIn.getBlockEntity( pos );
		if( blockEntity == null || !allowBlockedChest && isBlocked( worldIn, pos ) )
			return null;

		final ConnectedType chestType = state.getValue( TYPE );
		if( chestType != ConnectedType.SINGLE )
		{
			final BlockPos		blockPos	= pos.relative( getDirectionToAttached( state ) );
			final BlockState	iBlockState	= worldIn.getBlockState( blockPos );
			if( iBlockState.getBlock() == this )
			{
				final ConnectedType chestType1 = iBlockState.getValue( TYPE );
				if( chestType1 != ConnectedType.SINGLE && chestType != chestType1 && iBlockState.getValue( FACING ) == state.getValue( FACING ) )
					if( !allowBlockedChest && isBlocked( worldIn, blockPos ) )
						return null;
			}
		}
		return blockEntity;
	}

	@Override
	public int getDirectSignal( BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side )
	{
		return side == Direction.UP ? blockState.getSignal( blockAccess, pos, side ) : 0;
	}

	@Override
	public float getExplosionResistance( BlockState state, BlockGetter world, BlockPos pos, Explosion explosion )
	{
		final ReinforcedChestBlockEntity chest = getChestAt( (Level)world, pos );
		if( chest != null && chest.isLocked() )
		{
			final int resist = chest.getLock().getEnchantmentLevel( BetterStorageEnchantments.PERSISTANCE.get() ) + 1;
			return super.getExplosionResistance( state, world, pos, explosion ) * resist * 2;
		}
		return super.getExplosionResistance( state, world, pos, explosion );
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public FluidState getFluidState( BlockState blockState )
	{
		return blockState.getValue( WATERLOGGED ) ? Fluids.WATER.getSource( false ) : super.getFluidState( blockState );

	}

	@Override
	@Nullable
	public MenuProvider getMenuProvider( BlockState state, Level worldIn, BlockPos pos )
	{
		return getContainer( state, worldIn, pos, false );
	}

	@Override
	public RenderShape getRenderShape( BlockState state )
	{
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public VoxelShape getShape( BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context )
	{
		if( state.getValue( TYPE ) == ConnectedType.SINGLE )
			return SHAPE_SINGLE;

		final Direction direction = state.getValue( FACING );
		if( direction.getAxis().isVertical() )
			return SHAPE_SINGLE;

		switch( getDirectionToAttached( state ) )
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
		if( tileEntity instanceof final ReinforcedChestBlockEntity chest )
			return chest.isPowered() ? Mth.clamp( chest.getPlayersUsing(), 0, 15 ) : 0;
		return 0;
	}

	@Override
	@SuppressWarnings("null")
	public BlockState getStateForPlacement( BlockPlaceContext context )
	{
		ConnectedType		connectedType	= ConnectedType.SINGLE;
		Direction			direction		= context.getHorizontalDirection().getOpposite();
		final FluidState	fluidState		= context.getLevel().getFluidState( context.getClickedPos() );
		final boolean		sneaking		= context.getPlayer().isShiftKeyDown();

		final Direction direction1 = context.getClickedFace();
		if( direction1.getAxis().isHorizontal() && sneaking )
		{
			final Direction direction2 = getDirectionToAttach( context, direction1.getOpposite() );
			if( direction2 != null && direction2.getAxis() != direction1.getAxis() )
			{
				direction		= direction2;
				connectedType	= direction2.getCounterClockWise() == direction1.getOpposite() ? ConnectedType.MASTER : ConnectedType.SLAVE;
			}
		}

		if( connectedType == ConnectedType.SINGLE && !sneaking )
			if( direction == getDirectionToAttach( context, direction.getClockWise() ) )
				connectedType = ConnectedType.SLAVE;
			else if( direction == getDirectionToAttach( context, direction.getCounterClockWise() ) )
				connectedType = ConnectedType.MASTER;

		return defaultBlockState().setValue( FACING, direction ).setValue( TYPE, connectedType ).setValue( WATERLOGGED,
			fluidState.getType() == Fluids.WATER );
	}

	/*@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker< T > getTicker( Level level, BlockState blockState, BlockEntityType< T > blockEntityType )
	{
		return level.isClientSide ? createTickerHelper( blockEntityType, BetterStorageBlockEntityTypes.REINFORCED_CHEST.get(),
			ReinforcedChestBlockEntity::lidAnimateTick ) : null;
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
		return new ReinforcedChestBlockEntity( blockPos, blockState );
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
					final ReinforcedChestBlockEntity	thisChest	= getChestAt( world, pos );
					final ReinforcedChestBlockEntity	facingChest	= getChestAt( world, pos.relative( getDirectionToAttached( state ) ) );

					facingChest.setConfig( thisChest.getConfig() );
					thisChest.setConfig( new HexKeyConfig() );
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
			if( tileEntity instanceof ReinforcedChestBlockEntity )
				( (ReinforcedChestBlockEntity)tileEntity ).setCustomName( stack.getDisplayName() );
		}
	}

	@Override
	public void tick( BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource random )
	{
		final BlockEntity blockentity = level.getBlockEntity( blockPos );
		if( blockentity instanceof ReinforcedChestBlockEntity )
			( (ReinforcedChestBlockEntity)blockentity ).recheckOpen();

	}

	@SuppressWarnings( {"deprecation","null"} )
	@Override
	public BlockState updateShape(	BlockState thisState, Direction facing, BlockState facingState, LevelAccessor world, BlockPos thisPos,
									BlockPos facingPos )
	{
		if( thisState.getValue( WATERLOGGED ) )
			world.scheduleTick( thisPos, Fluids.WATER, Fluids.WATER.getTickDelay( world ) );

		if( facingState.getBlock() == this && facing.getAxis().isHorizontal() )
		{
			final ConnectedType facingType = facingState.getValue( TYPE );

			if( thisState.getValue( TYPE )	== ConnectedType.SINGLE && facingType != ConnectedType.SINGLE
				&& thisState.getValue( FACING ) == facingState.getValue( FACING ) && getDirectionToAttached( facingState ) == facing.getOpposite() )
			{
				final ConnectedType					newType		= facingType.opposite();
				final ReinforcedChestBlockEntity	thisChest	= getChestAt( (Level)world, thisPos );
				final ReinforcedChestBlockEntity	facingChest	= getChestAt( (Level)world, facingPos );
				if( newType == ConnectedType.SLAVE )
				{
					facingChest.setConfig( thisChest.getConfig() );
					thisChest.setConfig( new HexKeyConfig() );
				}
				return thisState.setValue( TYPE, newType );
			}
		} else if( getDirectionToAttached( thisState ) == facing )
			return thisState.setValue( TYPE, ConnectedType.SINGLE );

		return super.updateShape( thisState, facing, facingState, world, thisPos, facingPos );
	}

	@Override
	public InteractionResult use( BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit )
	{
		if( worldIn.isClientSide )
			return InteractionResult.SUCCESS;

		final ReinforcedChestBlockEntity tileChest = getChestAt( worldIn, pos );
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
				worldIn.addFreshEntity( new ItemEntity( worldIn, pos.getX(), pos.getY(), pos.getZ(), tileChest.getLock().copy() ) );
				tileChest.setLock( ItemStack.EMPTY );
				return InteractionResult.SUCCESS;
			}
		}
		final MenuProvider chest = getMenuProvider( state, worldIn, pos );
		if( chest != null )
		{
			NetworkHooks.openScreen( (ServerPlayer)player, chest, pos );
			player.awardStat( getOpenStat() );
		}

		return InteractionResult.SUCCESS;
	}

	/**
	 * Returns facing pointing to a chest to form a double chest with, null otherwise
	 *
	 * @param context
	 *            Context
	 * @param facing
	 *            The offset direction
	 *
	 * @return Facing direction
	 */
	@Nullable
	private Direction getDirectionToAttach( BlockPlaceContext context, Direction facing )
	{
		final BlockState blockState = context.getLevel().getBlockState( context.getClickedPos().relative( facing ) );
		return blockState.getBlock() == this && blockState.getValue( TYPE ) == ConnectedType.SINGLE ? blockState.getValue( FACING ) : null;
	}

	@Override
	protected void createBlockStateDefinition( StateDefinition.Builder< Block, BlockState > builder )
	{
		super.createBlockStateDefinition( builder );
		builder.add( FACING, WATERLOGGED );
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
