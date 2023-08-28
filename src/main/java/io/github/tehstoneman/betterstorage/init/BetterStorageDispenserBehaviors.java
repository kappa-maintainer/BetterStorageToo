package io.github.tehstoneman.betterstorage.init;

// @EventBusSubscriber( modid = ModInfo.MOD_ID, bus = Bus.MOD )
public class BetterStorageDispenserBehaviors
{
	/*
	 * @SubscribeEvent
	 * public static void registerCapabilities( FMLCommonSetupEvent event )
	 * {
	 * if( BetterStorageConfig.COMMON.cardboardBoxDispenserPlaceable.get() )
	 * DispenserBlock.registerBehavior( BetterStorageBlocks.CARDBOARD_BOX.get(), new OptionalDispenseItemBehavior()
	 * {
	 *//**
		 * Dispense the specified stack, play the dispense sound and spawn particles.
		 *//*
			 * @Override
			 * protected ItemStack execute( BlockSource source, ItemStack stack )
			 * {
			 * setSuccess( false );
			 * final var item = stack.getItem();
			 * if( item instanceof BlockItem )
			 * {
			 * final var direction = source.getBlockState().getValue( DispenserBlock.FACING );
			 * final var blockpos = source.getPos().relative( direction );
			 * final var direction1 = source.getLevel().isEmptyBlock( blockpos.below() ) ? direction : Direction.UP;
			 * setSuccess( ( (BlockItem)item )
			 * .place( new DirectionalPlaceContext( source.getLevel(), blockpos, direction, stack, direction1 ) ).consumesAction() );
			 * }
			 * 
			 * return stack;
			 * }
			 * } );
			 */
	/*
	 * if( BetterStorageConfig.COMMON.useFluidMilk.get() ) DispenserBlock.registerBehavior( Items.MILK_BUCKET, new OptionalDispenseBehavior() { private
	 * final DefaultDispenseItemBehavior dispenseBehavior = new DefaultDispenseItemBehavior();
	 *
	 * @Override protected ItemStack execute( IBlockSource source, ItemStack stack ) { final Item bucketitem = stack.getItem(); final BlockPos blockpos =
	 * source.getPos().relative( source.getBlockState().getValue( DispenserBlock.FACING ) ); final Level world = source.getLevel(); if(
	 * FluidMilk.emptyBucket( bucketitem, (Player)null, world, blockpos, (BlockHitResult)null ) ) return new ItemStack( Items.BUCKET ); else return
	 * dispenseBehavior.dispense( source, stack ); } } );
	 */
	// }
}
