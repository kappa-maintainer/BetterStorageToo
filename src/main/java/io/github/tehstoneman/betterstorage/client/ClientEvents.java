package io.github.tehstoneman.betterstorage.client;

import io.github.tehstoneman.betterstorage.BetterStorage;
import io.github.tehstoneman.betterstorage.api.EnumReinforced;
import io.github.tehstoneman.betterstorage.client.renderer.block.statemap.SizeStateMap;
import io.github.tehstoneman.betterstorage.common.block.BetterStorageBlocks;
import io.github.tehstoneman.betterstorage.common.item.BetterStorageItems;
import io.github.tehstoneman.betterstorage.common.item.ItemBucketSlime.EnumSlime;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents
{
	@SubscribeEvent
	public void onRegisterModels( ModelRegistryEvent event )
	{
		// ModelLoader.setCustomStateMapper( block, mapper );

		final SizeStateMap sizeStateMap = new SizeStateMap();

		if( BetterStorage.config.crateEnabled )
			registerItemModel( BetterStorageBlocks.CRATE );

		if( BetterStorage.config.reinforcedChestEnabled )
		{
			ModelLoader.setCustomStateMapper( BetterStorageBlocks.REINFORCED_CHEST, sizeStateMap );
			for( final EnumReinforced material : EnumReinforced.values() )
				registerItemModel( BetterStorageBlocks.REINFORCED_CHEST, material.getMetadata(),
						BetterStorageBlocks.REINFORCED_CHEST.getRegistryName() + "_" + material.getName() );
		}

		if( BetterStorage.config.lockerEnabled )
		{
			ModelLoader.setCustomStateMapper( BetterStorageBlocks.LOCKER, sizeStateMap );
			registerItemModel( BetterStorageBlocks.LOCKER );
			if( BetterStorage.config.reinforcedLockerEnabled )
			{
				ModelLoader.setCustomStateMapper( BetterStorageBlocks.REINFORCED_LOCKER, sizeStateMap );
				for( final EnumReinforced material : EnumReinforced.values() )
					registerItemModel( BetterStorageBlocks.REINFORCED_LOCKER, material.getMetadata(),
							BetterStorageBlocks.REINFORCED_LOCKER.getRegistryName() + "_" + material.getName() );
			}
		}

		if( BetterStorage.config.flintBlockEnabled )
			registerItemModel( BetterStorageBlocks.BLOCK_FLINT );

		if( BetterStorage.config.keyEnabled )
		{
			registerItemModel( BetterStorageItems.KEY );
			if( BetterStorage.config.masterKeyEnabled )
				registerItemModel( BetterStorageItems.MASTER_KEY );
			if( BetterStorage.config.keyringEnabled )
				for( int i = 0; i < 4; i++ )
					registerItemModel( BetterStorageItems.KEYRING, i,
							new ModelResourceLocation( BetterStorageItems.KEYRING.getRegistryName() + "_" + i, "inventory" ) );
			if( BetterStorage.config.lockEnabled )
				registerItemModel( BetterStorageItems.LOCK );
		}

		if( BetterStorage.config.cardboardSheetEnabled )
			registerItemModel( BetterStorageItems.CARDBOARD_SHEET );

		if( BetterStorage.config.cardboardBoxEnabled )
			registerItemModel( BetterStorageBlocks.CARDBOARD_BOX );

		if( BetterStorage.config.cardboardSwordEnabled )
			registerItemModel( BetterStorageItems.CARDBOARD_SWORD );
		if( BetterStorage.config.cardboardShovelEnabled )
			registerItemModel( BetterStorageItems.CARDBOARD_SHOVEL );
		if( BetterStorage.config.cardboardPickaxeEnabled )
			registerItemModel( BetterStorageItems.CARDBOARD_PICKAXE );
		if( BetterStorage.config.cardboardAxeEnabled )
			registerItemModel( BetterStorageItems.CARDBOARD_AXE );
		if( BetterStorage.config.cardboardHoeEnabled )
			registerItemModel( BetterStorageItems.CARDBOARD_HOE );

		if( BetterStorage.config.cardboardHelmetEnabled )
			registerItemModel( BetterStorageItems.CARDBOARD_HELMET );
		if( BetterStorage.config.cardboardChestplateEnabled )
			registerItemModel( BetterStorageItems.CARDBOARD_CHESTPLATE );
		if( BetterStorage.config.cardboardLeggingsEnabled )
			registerItemModel( BetterStorageItems.CARDBOARD_LEGGINGS );
		if( BetterStorage.config.cardboardBootsEnabled )
			registerItemModel( BetterStorageItems.CARDBOARD_BOOTS );

		if( BetterStorage.config.slimeBucketEnabled )
			for( final EnumSlime slime : EnumSlime.values() )
				registerItemModel( BetterStorageItems.SLIME_BUCKET, slime.getMetadata(),
						BetterStorageItems.SLIME_BUCKET.getRegistryName() + "_" + slime.getResourceLocation().getPath() );
	}

	/*
	 * =================
	 * Support functions
	 *
	 * Adapted from Choonster's TestMod3
	 * =================
	 */

	/**
	 * A {@link StateMapperBase} used to create property strings.
	 */
	private final StateMapperBase propertyStringMapper = new StateMapperBase()
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation( IBlockState state )
		{
			return new ModelResourceLocation( "minecraft:air" );
		}
	};

	/**
	 * Register a model for a metadata value of the {@link Block}'s {@link Item}.
	 * <p>
	 * Uses the registry name as the domain/path and the {@link IBlockState} as the variant.
	 *
	 * @param state
	 *            The state to use as the variant
	 * @param metadata
	 *            The item metadata to register the model for
	 */
	private void registerBlockItemModelForMeta( IBlockState state, int metadata )
	{
		final Item item = Item.getItemFromBlock( state.getBlock() );

		if( item != Items.AIR )
			registerItemModel( item, metadata, propertyStringMapper.getPropertyString( state.getProperties() ) );
	}

	private void registerItemModel( Block block )
	{
		final Item item = Item.getItemFromBlock( block );

		if( item != Items.AIR )
			registerItemModel( item );
	}

	private void registerItemModel( Block block, int metadata, String modelLocation )
	{
		final Item item = Item.getItemFromBlock( block );

		if( item != Items.AIR )
			registerItemModel( item, metadata, modelLocation );
	}

	/**
	 * Register a single model for an {@link Item}.
	 * <p>
	 * Uses the registry name as the domain/path and {@code "inventory"} as the variant.
	 *
	 * @param item
	 *            The Item
	 */
	private void registerItemModel( Item item )
	{
		final ResourceLocation registryName = item.getRegistryName();
		registerItemModel( item, registryName.toString() );
	}

	/**
	 * Register a single model for an {@link Item}.
	 * <p>
	 * Uses {@code modelLocation} as the domain/path and {@link "inventory"} as the variant.
	 *
	 * @param item
	 *            The Item
	 * @param modelLocation
	 *            The model location
	 */
	private void registerItemModel( Item item, String modelLocation )
	{
		final ModelResourceLocation fullModelLocation = new ModelResourceLocation( modelLocation, "inventory" );
		registerItemModel( item, fullModelLocation );
	}

	private void registerItemModel( Item item, int meta, String modelLocation )
	{
		final ModelResourceLocation fullModelLocation = new ModelResourceLocation( modelLocation, "inventory" );
		registerItemModel( item, meta, fullModelLocation );
	}

	/**
	 * Register a single model for an {@link Item}.
	 * <p>
	 * Uses {@code fullModelLocation} as the domain, path and variant.
	 *
	 * @param item
	 *            The Item
	 * @param fullModelLocation
	 *            The full model location
	 */
	private void registerItemModel( Item item, ModelResourceLocation fullModelLocation )
	{
		ModelBakery.registerItemVariants( item, fullModelLocation );
		registerItemModel( item, stack -> fullModelLocation );
	}

	/**
	 * Register an {@link ItemMeshDifinition} for an {@link Item}.
	 *
	 * @param item
	 *            The Item
	 * @param meshDefinition
	 *            The ItemModelDefinition
	 */
	private void registerItemModel( Item item, ItemMeshDefinition meshDefinition )
	{
		ModelLoader.setCustomMeshDefinition( item, meshDefinition );
	}

	/**
	 * Register a model for a metadata value of an {@link Item}.
	 * <p>
	 * Uses {@code modelResourceLocation} as the domain, path and variant.
	 *
	 * @param item
	 *            The Item
	 * @param metadata
	 *            The metadata
	 * @param modelResourceLocation
	 *            The full model location
	 */
	private void registerItemModel( Item item, int metadata, ModelResourceLocation modelResourceLocation )
	{
		ModelLoader.setCustomModelResourceLocation( item, metadata, modelResourceLocation );
	}
}
