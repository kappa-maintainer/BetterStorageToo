package io.github.tehstoneman.betterstorage.world.level.block;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import io.github.tehstoneman.betterstorage.config.BetterStorageConfig;
import io.github.tehstoneman.betterstorage.world.item.cardboard.BlockItemCardboardBox;
import io.github.tehstoneman.betterstorage.world.level.block.entity.CardboardBoxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class CardboardBoxBlock extends BaseEntityBlock implements SimpleWaterloggedBlock
{
	protected CardboardBoxBlock(Properties p_49224_) {
		super(p_49224_);
		//TODO Auto-generated constructor stub
	}

	public static final BooleanProperty		WATERLOGGED	= BlockStateProperties.WATERLOGGED;
	public static final ResourceLocation	CONTENTS	= new ResourceLocation( "contents" );

	protected static final VoxelShape SHAPE_BOX = Block.box( 1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D );

	//public CardboardBoxBlock()
	//{
		//super( Properties.of( Material.WOOL ).strength( 0.8f ).sound( SoundType.WOOL ) );

		//registerDefaultState( stateDefinition.any().setValue( WATERLOGGED, false ) );
	//}

	//@SuppressWarnings( "deprecation" )
	public List< ItemStack > getDrops( BlockState blockState, LootContext.Builder builder )
	{
		//final var blockEntity = builder.getOptionalParameter( LootContextParams.BLOCK_ENTITY );
		//if( blockEntity instanceof final CardboardBoxBlockEntity cardboardBox )
			//if( cardboardBox.uses == 0 )
				return Collections.emptyList();

		//return super.getDrops( blockState, builder );
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public FluidState getFluidState( BlockState state )
	{
		return state.getValue( WATERLOGGED ) ? Fluids.WATER.getSource( false ) : super.getFluidState( state );
	}

	@Override
	public PushReaction getPistonPushReaction( BlockState blockState )
	{
		return BetterStorageConfig.COMMON.cardboardBoxPistonBreakable.get() ? PushReaction.DESTROY : PushReaction.BLOCK;
	}

	@Override
	public RenderShape getRenderShape( BlockState blockState )
	{
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape( BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context )
	{
		return SHAPE_BOX;
	}

	/*
	 * =========
	 * Placement
	 * =========
	 */

	@Override
	public BlockState getStateForPlacement( BlockPlaceContext context )
	{
		final FluidState fluidState = context.getLevel().getFluidState( context.getClickedPos() );
		return defaultBlockState().setValue( WATERLOGGED, fluidState.getType() == Fluids.WATER );
	}

	//@Override
	//public BlockEntity newBlockEntity( BlockPos blockPos, BlockState blockState )
	//{
		//return new CardboardBoxBlockEntity( blockPos, blockState );
	//}

	@SuppressWarnings( "deprecation" )
	@Override
	public void onRemove( BlockState blockState, Level level, BlockPos blockPos, BlockState newState, boolean isMoving )
	{
		if( blockState.getBlock() != newState.getBlock() )
		{
			final BlockEntity blockEntity = level.getBlockEntity( blockPos );
			if( blockEntity instanceof final CardboardBoxBlockEntity cardboardBox )
			{
				if( cardboardBox.uses < 1 )
					cardboardBox.dropInventoryItems();
				level.updateNeighbourForOutputSignal( blockPos, this );
			}
		}
		super.onRemove( blockState, level, blockPos, newState, isMoving );
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public void playerWillDestroy( Level level, BlockPos blockPos, BlockState blockState, Player player )
	{
		if( !level.isClientSide() )
		{
			final BlockEntity blockEntity = level.getBlockEntity( blockPos );
			if( blockEntity instanceof final CardboardBoxBlockEntity cardboardBox	&& player.isCreative() && !cardboardBox.isEmpty()
				&& cardboardBox.uses > 0 )
			{
				final ItemStack		itemStack	= getCloneItemStack( level, blockPos, blockState );
				final CompoundTag	nbt			= new CompoundTag();
				cardboardBox.saveAdditional( nbt );
				if( nbt.contains( "Inventory" ) )
					itemStack.addTagElement( "Inventory", nbt.get( "Inventory" ) );
				if( cardboardBox.hasCustomName() )
					itemStack.setHoverName( cardboardBox.getCustomName() );
				if( nbt.contains( "Color" ) )
					itemStack.addTagElement( "Color", nbt.get( "Color" ) );
				if( nbt.contains( "Uses" ) )
					itemStack.addTagElement( "Uses", nbt.get( "Uses" ) );

				final ItemEntity itemEntity = new ItemEntity( level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack );
				itemEntity.setDefaultPickUpDelay();
				level.addFreshEntity( itemEntity );
			}
		}
		super.playerWillDestroy( level, blockPos, blockState, player );
	}

	/*
	 * ===========
	 * Interaction
	 * ===========
	 */

	@Override
	@SuppressWarnings("null")
	public void setPlacedBy( Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity placer, ItemStack itemStack )
	{
		final var blockEntity = level.getBlockEntity( blockPos );
		if( blockEntity instanceof final CardboardBoxBlockEntity cardboardBox )
		{
			if( itemStack.hasCustomHoverName() )
				cardboardBox.setCustomName( itemStack.getDisplayName() );
			if( itemStack.hasTag() )
			{
				final CompoundTag tag = itemStack.getTag();

				if( tag.contains( "Color" ) )
					cardboardBox.setColor( tag.getInt( "Color" ) );

				if( tag.contains( "Inventory" ) )
					cardboardBox.inventory.deserializeNBT( tag.getCompound( "Inventory" ) );

				int uses = tag.contains( "Uses" ) ? tag.getInt( "Uses" ) : BlockItemCardboardBox.getMaxUses();
				if( !( placer instanceof Player ) || !( (Player)placer ).isCreative() )
					if( !cardboardBox.isEmpty() )
						uses--;
				cardboardBox.setUses( uses );
			}
		}
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public BlockState updateShape(	BlockState blockState, Direction facing, BlockState facingState, LevelAccessor level, BlockPos blockPos,
									BlockPos facingPos )
	{
		if( blockState.getValue( WATERLOGGED ) )
			level.scheduleTick( blockPos, Fluids.WATER, Fluids.WATER.getTickDelay( level ) );

		return super.updateShape( blockState, facing, facingState, level, blockPos, facingPos );
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public InteractionResult use(	BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand,
									BlockHitResult blockHitResult )
	{
		if( level.isClientSide )
			return InteractionResult.SUCCESS;

		final MenuProvider menuProvider = getMenuProvider( blockState, level, blockPos );
		if( menuProvider != null )
		{
			NetworkHooks.openScreen( (ServerPlayer)player, menuProvider, blockPos );
			return InteractionResult.SUCCESS;
		}
		return super.use( blockState, level, blockPos, player, interactionHand, blockHitResult );
	}

	@Override
	protected void createBlockStateDefinition( StateDefinition.Builder< Block, BlockState > builder )
	{
		super.createBlockStateDefinition( builder );
		builder.add( WATERLOGGED );
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'newBlockEntity'");
	}
}
