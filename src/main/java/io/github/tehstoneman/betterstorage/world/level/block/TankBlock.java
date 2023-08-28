package io.github.tehstoneman.betterstorage.world.level.block;

import java.util.Map;

import com.google.common.collect.Maps;

import io.github.tehstoneman.betterstorage.world.level.block.entity.TankBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class TankBlock extends BetterStorageContainerBlock
{
	protected static final VoxelShape						SHAPE_TANK				= Block.box( 1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D );
	public static final BooleanProperty						UP						= BlockStateProperties.UP;
	public static final BooleanProperty						DOWN					= BlockStateProperties.DOWN;
	public static final Map< Direction, BooleanProperty >	FACING_TO_PROPERTY_MAP	= Util.make( Maps.newEnumMap( Direction.class ), facing ->
																					{
																						facing.put( Direction.UP, UP );
																						facing.put( Direction.DOWN, DOWN );
																					} );

	public TankBlock()
	{
		super( Block.Properties.copy( Blocks.GLASS ).strength( 3.0f ) );

		//@formatter:off
		registerDefaultState( defaultBlockState().setValue( UP, false )
												 .setValue( DOWN, false ) );
		//@formatter:on
	}

	@Override
	public VoxelShape getOcclusionShape( BlockState state, BlockGetter worldIn, BlockPos pos )
	{
		return SHAPE_TANK;
	}

	@Override
	public BlockEntity newBlockEntity( BlockPos blockPos, BlockState blockState )
	{
		return new TankBlockEntity( blockPos, blockState );
	}

	/*
	 * ======================
	 * BlockEntity / Rendering
	 * ======================
	 */

	@SuppressWarnings( "deprecation" )
	@Override
	public BlockState updateShape(	BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos,
									BlockPos facingPos )
	{
		return facing.getAxis().getPlane() == Direction.Plane.VERTICAL ? stateIn.setValue( FACING_TO_PROPERTY_MAP.get( facing ),
			facingState.getBlock() == this ) : super.updateShape( stateIn, facing, facingState, worldIn, currentPos, facingPos );
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public InteractionResult use( BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit )
	{
		final ItemStack							itemStack	= player.getItemInHand( hand );
		final LazyOptional< IFluidHandlerItem >	capability	= itemStack.getCapability( ForgeCapabilities.FLUID_HANDLER_ITEM );
		if( capability.isPresent() )
		{
			final boolean success = FluidUtil.interactWithFluidHandler( player, hand, world, pos, hit.getDirection() );
			return success ? InteractionResult.SUCCESS : InteractionResult.PASS;
		}
		return super.use( state, world, pos, player, hand, hit );
	}

	/*
	 * ===========
	 * Interaction
	 * ===========
	 */

	@Override
	protected void createBlockStateDefinition( StateDefinition.Builder< Block, BlockState > builder )
	{
		super.createBlockStateDefinition( builder );
		builder.add( UP, DOWN );
	}
}
