package io.github.tehstoneman.betterstorage.world.level.block;

import javax.annotation.Nullable;

import io.github.tehstoneman.betterstorage.api.lock.ILock;
import io.github.tehstoneman.betterstorage.api.lock.LockInteraction;
import io.github.tehstoneman.betterstorage.world.item.enchantment.BetterStorageEnchantments;
import io.github.tehstoneman.betterstorage.world.level.block.entity.LockableDoorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LockableDoorBlock extends Block implements EntityBlock
{
	protected static final VoxelShape	SOUTH_AABB	= Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D );
	protected static final VoxelShape	NORTH_AABB	= Block.box( 0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D );
	protected static final VoxelShape	WEST_AABB	= Block.box( 13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D );
	protected static final VoxelShape	EAST_AABB	= Block.box( 0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D );

	public LockableDoorBlock()
	{
		super( Properties.copy( Blocks.IRON_DOOR ) );

		//@formatter:off
		registerDefaultState( defaultBlockState().setValue( DoorBlock.FACING, Direction.NORTH )
												 .setValue( DoorBlock.OPEN, false )
												 .setValue( DoorBlock.HINGE, DoorHingeSide.LEFT )
												 .setValue( DoorBlock.HALF, DoubleBlockHalf.LOWER ) );
		//@formatter:on
	}

	public static int getCloseSound()
	{
		return 1011;
	}

	public static int getOpenSound()
	{
		return 1005;
	}

	/*
	 * @Override
	 * public ItemStack getPickBlock( BlockState state, BlockHitResult target, BlockGetter world, BlockPos pos, Player player )
	 * {
	 * return new ItemStack( Items.IRON_DOOR );
	 * }
	 */

	@Override
	public int getDirectSignal( BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side )
	{
		return side == Direction.UP ? blockState.getSignal( blockAccess, pos, side ) : 0;
	}

	@Override
	public float getExplosionResistance( BlockState state, BlockGetter world, BlockPos pos, Explosion explosion )
	{
		final BlockEntity tileEntity = world.getBlockEntity( pos );
		if( ( tileEntity instanceof final LockableDoorBlockEntity door ) && door.isLocked() )
		{
			final int resist = door.getLock().getEnchantmentLevel( BetterStorageEnchantments.PERSISTANCE.get() ) + 1;
			return super.getExplosionResistance( state, world, pos, explosion ) * resist * 2;
		}
		return super.getExplosionResistance( state, world, pos, explosion );
	}

	@Override
	public VoxelShape getShape( BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context )
	{
		final Direction	direction	= state.getValue( DoorBlock.FACING );
		final boolean	open		= !state.getValue( DoorBlock.OPEN );
		final boolean	hinge		= state.getValue( DoorBlock.HINGE ) == DoorHingeSide.RIGHT;
		switch( direction )
		{
			case EAST:
			default:
				return open ? EAST_AABB : hinge ? NORTH_AABB : SOUTH_AABB;

			case SOUTH:
				return open ? SOUTH_AABB : hinge ? EAST_AABB : WEST_AABB;

			case WEST:
				return open ? WEST_AABB : hinge ? SOUTH_AABB : NORTH_AABB;

			case NORTH:
				return open ? NORTH_AABB : hinge ? WEST_AABB : EAST_AABB;
		}
	}

	@Override
	public int getSignal( BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side )
	{
		final BlockEntity tileEntity = blockAccess.getBlockEntity( pos );
		if( tileEntity instanceof final LockableDoorBlockEntity chest )
			return chest.isPowered() && blockState.getValue( DoorBlock.OPEN ) ? 15 : 0;
		return 0;
	}

	@Override
	public boolean isPathfindable( BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type )
	{
		switch( type )
		{
			case LAND:
				return state.getValue( DoorBlock.OPEN );

			case WATER:
				return false;

			case AIR:
				return state.getValue( DoorBlock.OPEN );

			default:
				return false;
		}
	}

	@Override
	public boolean isSignalSource( BlockState state )
	{
		return true;
	}

	@Override
	public BlockEntity newBlockEntity( BlockPos blockPos, BlockState blockState )
	{
		return new LockableDoorBlockEntity( blockPos, blockState );
	}

	/*
	 * @Override
	 * public BlockRenderLayer getRenderLayer()
	 * {
	 * return BlockRenderLayer.CUTOUT;
	 * }
	 */

	@Override
	public void playerDestroy( Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack )
	{
		super.playerDestroy( worldIn, player, pos, Blocks.AIR.defaultBlockState(), te, stack );
	}

	@Override
	public void playerWillDestroy( Level worldIn, BlockPos pos, BlockState state, Player player )
	{
		final DoubleBlockHalf	doubleblockhalf	= state.getValue( DoorBlock.HALF );
		final BlockPos			blockpos		= doubleblockhalf == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
		final BlockState		blockstate		= worldIn.getBlockState( blockpos );
		if( blockstate.getBlock() == this && blockstate.getValue( DoorBlock.HALF ) != doubleblockhalf )
		{
			worldIn.setBlock( blockpos, Blocks.AIR.defaultBlockState(), 35 );
			worldIn.levelEvent( player, 2001, blockpos, Block.getId( blockstate ) );
			final ItemStack itemstack = player.getMainHandItem();
			if( !worldIn.isClientSide && !player.isCreative() )
			{
				Block.dropResources( state, worldIn, pos, (BlockEntity)null, player, itemstack );
				Block.dropResources( blockstate, worldIn, blockpos, (BlockEntity)null, player, itemstack );
			}
		}
	}

	@Override
	public InteractionResult use( BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit )
	{
		if( worldIn.isClientSide() )
			return InteractionResult.SUCCESS;
		if( state.getValue( DoorBlock.HALF ) == DoubleBlockHalf.UPPER )
			pos = pos.below();
		final BlockEntity tileentity = worldIn.getBlockEntity( pos );
		if( tileentity instanceof final LockableDoorBlockEntity tileDoor )
		{
			if( !tileDoor.unlockWith( player.getItemInHand( handIn ) ) )
			{
				final ItemStack lock = tileDoor.getLock();
				( (ILock)lock.getItem() ).applyEffects( lock, tileDoor, player, LockInteraction.OPEN );
				return InteractionResult.PASS;
			}
			if( player.isCrouching() )
			{
				worldIn.addFreshEntity( new ItemEntity( worldIn, pos.getX(), pos.getY(), pos.getZ(), tileDoor.getLock().copy() ) );
				tileDoor.setLock( ItemStack.EMPTY );
				return InteractionResult.SUCCESS;
			}
			state = state.cycle( DoorBlock.OPEN );
			// state = state.cycle( DoorBlock.OPEN );
			worldIn.setBlock( pos, worldIn.getBlockState( pos ).cycle( DoorBlock.OPEN ), 11 );
			worldIn.setBlock( pos.above(), worldIn.getBlockState( pos.above() ).cycle( DoorBlock.OPEN ), 11 );
			worldIn.levelEvent( player, state.getValue( DoorBlock.OPEN ) ? getOpenSound() : getCloseSound(), pos, 0 );
		}
		return InteractionResult.PASS;
	}

	@Override
	protected void createBlockStateDefinition( StateDefinition.Builder< Block, BlockState > builder )
	{
		builder.add( DoorBlock.HALF, DoorBlock.FACING, DoorBlock.OPEN, DoorBlock.HINGE );
	}
}
