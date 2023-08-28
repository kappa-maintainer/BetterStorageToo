package io.github.tehstoneman.betterstorage.world.level.fluid;

public class MilkFluid// extends ForgeFlowingFluid
{
	/*
	 * public static BetterStorageResource stillTexture = new BetterStorageResource( "block/milk_still" );
	 * public static BetterStorageResource flowingTexture = new BetterStorageResource( "block/milk_flowing" );
	 * 
	 * // public static Material MATERIAL_MILK = new Material.Builder( DyeColor.WHITE.getMaterialColor()
	 * ).noCollider().liquid().nonSolid().replaceable().build();
	 * 
	 * public static FluidAttributes.Builder BUILDER = FluidType.builder( stillTexture, flowingTexture ).color( 0xFFFFFFFF ).density( 1025 )
	 * .temperature( 310 ).viscosity( 2000 );
	 * 
	 * public static Properties PROPERTIES = new Properties( ForgeMod.MILK, ForgeMod.FLOWING_MILK, BUILDER ).bucket( () -> Items.MILK_BUCKET )
	 * .block( BetterStorageBlocks.MILK );
	 * 
	 * protected MilkFluid( Properties properties )
	 * {
	 * super( properties );
	 * }
	 */

	/*
	 * public static boolean interactFluidHandler( @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull IFluidHandler handler )
	 * {
	 * final ItemStack heldItem = new ItemStack( BetterStorageItems.MILK_BUCKET.get() );
	 * return player.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ).map( playerInventory ->
	 * {
	 *
	 * FluidActionResult fluidActionResult = FluidUtil.tryFillContainerAndStow( heldItem, handler, playerInventory, Integer.MAX_VALUE, player,
	 * true );
	 * if( !fluidActionResult.isSuccess() )
	 * fluidActionResult = FluidUtil.tryEmptyContainerAndStow( heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true );
	 *
	 * if( fluidActionResult.isSuccess() )
	 * // player.setItemInHand( hand, fluidActionResult.getResult() );
	 * return true;
	 * return false;
	 * } ).orElse( false );
	 * }
	 */

	/*
	 * @SuppressWarnings( "deprecation" )
	 * public static boolean tryPlaceContainedLiquid( Item bucketItem, @Nullable Player player, Level worldIn, BlockPos posIn,
	 *
	 * @Nullable BlockHitResult rayTraceResult )
	 * {
	 * if( bucketItem != Items.MILK_BUCKET )
	 * return false;
	 * else
	 * {
	 * final Fluid containedBlock = BetterStorageFluids.MILK.get();
	 * final BlockState blockState = worldIn.getBlockState( posIn );
	 * final Material material = blockState.getMaterial();
	 * final boolean flag = blockState.canBeReplaced( containedBlock );
	 * if( blockState.isAir() || flag || blockState.getBlock() instanceof ILiquidContainer
	 * && ( (ILiquidContainer)blockState.getBlock() ).canPlaceLiquid( worldIn, posIn, blockState, containedBlock ) )
	 * {
	 *
	 * if( worldIn.dimension.doesWaterVaporize() && containedBlock == BetterStorageFluids.MILK.get() )
	 * {
	 * final int i = posIn.getX();
	 * final int j = posIn.getY();
	 * final int k = posIn.getZ();
	 * worldIn.playSound( player, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F,
	 * 2.6F + ( worldIn.rand.nextFloat() - worldIn.rand.nextFloat() ) * 0.8F );
	 *
	 * for( int l = 0; l < 8; ++l )
	 * worldIn.addParticle( ParticleTypes.LARGE_SMOKE, i + Math.random(), j + Math.random(), k + Math.random(), 0.0D, 0.0D, 0.0D );
	 * }
	 * else
	 *
	 * if( blockState.getBlock() instanceof ILiquidContainer && containedBlock == BetterStorageFluids.MILK.get() )
	 * {
	 * if( ( (ILiquidContainer)blockState.getBlock() ).placeLiquid( worldIn, posIn, blockState,
	 * ( (FlowingFluid)containedBlock ).getSource( false ) ) )
	 * worldIn.playSound( player, posIn, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F );
	 * }
	 * else
	 * {
	 * if( !worldIn.isClientSide() && flag && !material.isLiquid() )
	 * worldIn.destroyBlock( posIn, true );
	 *
	 * worldIn.playSound( player, posIn, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F );
	 * worldIn.setBlock( posIn, containedBlock.defaultFluidState().createLegacyBlock(), 11 );
	 * }
	 *
	 * return true;
	 * }
	 * else
	 * return rayTraceResult == null ? false
	 * : tryPlaceContainedLiquid( bucketItem, player, worldIn, rayTraceResult.getBlockPos().relative( rayTraceResult.getDirection() ),
	 * (BlockHitResult)null );
	 * }
	 * }
	 */
}
