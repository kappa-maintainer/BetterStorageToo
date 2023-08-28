package io.github.tehstoneman.betterstorage.world.level.block;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import io.github.tehstoneman.betterstorage.api.ConnectedType;
import io.github.tehstoneman.betterstorage.world.item.CrateStackHandler;
import io.github.tehstoneman.betterstorage.world.level.block.entity.CrateBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

// @Interface( modid = "Botania", iface = "vazkii.botania.api.mana.ILaputaImmobile", striprefs = true )
public class CrateBlock extends ConnectableContainerBlock// implements ILaputaImmobile
{
	public static final BooleanProperty						NORTH					= BlockStateProperties.NORTH;
	public static final BooleanProperty						EAST					= BlockStateProperties.EAST;
	public static final BooleanProperty						SOUTH					= BlockStateProperties.SOUTH;
	public static final BooleanProperty						WEST					= BlockStateProperties.WEST;
	public static final BooleanProperty						UP						= BlockStateProperties.UP;
	public static final BooleanProperty						DOWN					= BlockStateProperties.DOWN;
	public static final Map< Direction, BooleanProperty >	FACING_TO_PROPERTY_MAP	= Util.make( Maps.newEnumMap( Direction.class ), facing ->
																					{
																						facing.put( Direction.NORTH, NORTH );
																						facing.put( Direction.EAST, EAST );
																						facing.put( Direction.SOUTH, SOUTH );
																						facing.put( Direction.WEST, WEST );
																						facing.put( Direction.UP, UP );
																						facing.put( Direction.DOWN, DOWN );
																					} );

	public CrateBlock()
	{
		super( Properties.copy( Blocks.OAK_PLANKS ) );

		//@formatter:off
		registerDefaultState( defaultBlockState().setValue( NORTH, false )
												 .setValue( EAST, false )
												 .setValue( SOUTH, false )
												 .setValue( WEST, false )
												 .setValue( UP, false )
												 .setValue( DOWN, false ) );
		//@formatter:on
	}

	@Override
	@Nullable
	public MenuProvider getMenuProvider( BlockState blockState, Level level, BlockPos blockPos )
	{
		return CrateBlockEntity.getCrateAt( level, blockPos );
	}

	@Override
	public BlockEntity newBlockEntity( BlockPos blockPos, BlockState blockState )
	{
		return new CrateBlockEntity( blockPos, blockState );
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public void onRemove( BlockState blockState, Level level, BlockPos blockPos, BlockState newState, boolean isMoving )
	{
		if( !level.isClientSide && blockState.getBlock() != newState.getBlock() )
		{
			final CrateBlockEntity crate = CrateBlockEntity.getCrateAt( level, blockPos );
			if( crate != null )
			{
				final CrateStackHandler			handler		= crate.getCrateStackHandler();
				final NonNullList< ItemStack >	overflow	= crate.removeCrate();
				if( !overflow.isEmpty() )
					Containers.dropContents( level, blockPos, overflow );
				crate.notifyRegionUpdate( handler.getRegion(), crate.getPileID() );
			}

		}
		super.onRemove( blockState, level, blockPos, newState, isMoving );
	}

	@Override
	public void setPlacedBy( Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity entity, ItemStack itemStack )
	{
		super.setPlacedBy( level, blockPos, blockState, entity, itemStack );
		if( !level.isClientSide() )
		{
			final CrateBlockEntity crate = CrateBlockEntity.getCrateAt( level, blockPos );
			if( crate != null && !crate.hasID() )
			{
				final CrateStackHandler handler = crate.getCrateStackHandler();
				handler.addCrate( crate );
			}
		}
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public BlockState updateShape(	BlockState blockState, Direction facing, BlockState facingState, LevelAccessor level, BlockPos blockPos,
									BlockPos facingPos )
	{
		if( !level.isClientSide() )
		{
			final CrateBlockEntity crate = CrateBlockEntity.getCrateAt( level, blockPos );
			if( crate != null )
			{
				final CrateBlockEntity	facingCrate	= CrateBlockEntity.getCrateAt( level, facingPos );
				final boolean			flag		= crate.tryAddCrate( facingCrate );
				blockState = blockState.setValue( FACING_TO_PROPERTY_MAP.get( facing ), flag ).setValue( TYPE,
					crate.getNumCrates() > 1 ? ConnectedType.PILE : ConnectedType.SINGLE );
				crate.updateConnections();
			}
		}
		return super.updateShape( blockState, facing, facingState, level, blockPos, facingPos );
	}

	@Override
	public InteractionResult use( BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hit )
	{
		if( level.isClientSide )
			return InteractionResult.SUCCESS;
		final MenuProvider menuProvider = getMenuProvider( blockState, level, blockPos );
		if( menuProvider != null )
			NetworkHooks.openScreen( (ServerPlayer)player, menuProvider, blockPos );
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void createBlockStateDefinition( StateDefinition.Builder< Block, BlockState > builder )
	{
		super.createBlockStateDefinition( builder );
		builder.add( NORTH, EAST, SOUTH, WEST, UP, DOWN );
	}

	/*
	 * @Override
	 * public boolean hasComparatorInputOverride( BlockState state )
	 * {
	 * return true;
	 * }
	 */

	/*
	 * @Override
	 * public int getComparatorInputOverride( BlockState blockState, Level worldIn, BlockPos pos )
	 * {
	 * final BlockEntity tileEntity = worldIn.getBlockEntity( pos );
	 * if( !( tileEntity instanceof TileEntityCrate ) )
	 * return 0;
	 * final TileEntityCrate tileCrate = (TileEntityCrate)tileEntity;
	 * return tileCrate.getComparatorSignalStrength();
	 * }
	 */

	/*
	 * @Method( modid = "Botania" )
	 *
	 * @Override
	 * public boolean canMove( Level world, BlockPos pos )
	 * {
	 * return false;
	 * }
	 */
}
