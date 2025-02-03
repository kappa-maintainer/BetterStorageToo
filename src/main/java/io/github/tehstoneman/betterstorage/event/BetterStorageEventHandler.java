package io.github.tehstoneman.betterstorage.event;

import io.github.tehstoneman.betterstorage.BetterStorage;
import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.api.ICardboardItem;
import io.github.tehstoneman.betterstorage.api.IDyeableItem;
import io.github.tehstoneman.betterstorage.client.BetterStorageResource;
import io.github.tehstoneman.betterstorage.common.block.BetterStorageBlocks;
import io.github.tehstoneman.betterstorage.common.enchantment.EnchantmentBetterStorage;
import io.github.tehstoneman.betterstorage.common.item.BetterStorageItems;
import io.github.tehstoneman.betterstorage.common.item.ItemBlockCrate;
import io.github.tehstoneman.betterstorage.common.item.ItemBlockLocker;
import io.github.tehstoneman.betterstorage.common.item.ItemBlockReinforcedChest;
import io.github.tehstoneman.betterstorage.common.item.ItemBlockReinforcedLocker;
import io.github.tehstoneman.betterstorage.common.item.ItemBucketSlime;
import io.github.tehstoneman.betterstorage.common.item.cardboard.ItemBlockCardboardBox;
import io.github.tehstoneman.betterstorage.common.item.cardboard.ItemCardboardSheet;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityCardboardBox;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityCrate;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityLockableDoor;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityLocker;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityReinforcedChest;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityReinforcedLocker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public class BetterStorageEventHandler
{
	private boolean preventSlimeBucketUse;

	@SubscribeEvent
	public void registerBlocks( Register< Block > event )
	{
		final IForgeRegistry< Block > registry = event.getRegistry();

		if( BetterStorage.config.crateEnabled )
		{
			BetterStorageBlocks.CRATE.setTranslationKey( ModInfo.modId + "." + BetterStorageBlocks.CRATE.getBlockName() );
			BetterStorageBlocks.CRATE.setRegistryName( BetterStorageBlocks.CRATE.getBlockName() );
			registry.register( BetterStorageBlocks.CRATE );
			GameRegistry.registerTileEntity( TileEntityCrate.class, ModInfo.containerCrate );
		}
		if( BetterStorage.config.reinforcedChestEnabled )
		{
			BetterStorageBlocks.REINFORCED_CHEST.setTranslationKey( ModInfo.modId + "." + BetterStorageBlocks.REINFORCED_CHEST.getBlockName() );
			BetterStorageBlocks.REINFORCED_CHEST.setRegistryName( BetterStorageBlocks.REINFORCED_CHEST.getBlockName() );
			registry.register( BetterStorageBlocks.REINFORCED_CHEST );
			GameRegistry.registerTileEntity( TileEntityReinforcedChest.class, ModInfo.containerReinforcedChest );
		}
		if( BetterStorage.config.lockerEnabled )
		{
			BetterStorageBlocks.LOCKER.setTranslationKey( ModInfo.modId + "." + BetterStorageBlocks.LOCKER.getBlockName() );
			BetterStorageBlocks.LOCKER.setRegistryName( BetterStorageBlocks.LOCKER.getBlockName() );
			registry.register( BetterStorageBlocks.LOCKER );
			GameRegistry.registerTileEntity( TileEntityLocker.class, ModInfo.containerLocker );
			if( BetterStorage.config.reinforcedLockerEnabled )
			{
				BetterStorageBlocks.REINFORCED_LOCKER
						.setTranslationKey( ModInfo.modId + "." + BetterStorageBlocks.REINFORCED_LOCKER.getBlockName() );
				BetterStorageBlocks.REINFORCED_LOCKER.setRegistryName( BetterStorageBlocks.REINFORCED_LOCKER.getBlockName() );
				registry.register( BetterStorageBlocks.REINFORCED_LOCKER );
				GameRegistry.registerTileEntity( TileEntityReinforcedLocker.class, ModInfo.containerReinforcedLocker );
			}
		}
		if( BetterStorage.config.flintBlockEnabled )
		{
			BetterStorageBlocks.BLOCK_FLINT.setTranslationKey( ModInfo.modId + "." + BetterStorageBlocks.BLOCK_FLINT.getBlockName() );
			BetterStorageBlocks.BLOCK_FLINT.setRegistryName( BetterStorageBlocks.BLOCK_FLINT.getBlockName() );
			registry.register( BetterStorageBlocks.BLOCK_FLINT );
		}
		if( BetterStorage.config.keyEnabled && BetterStorage.config.lockableDoorEnabled )
		{
			BetterStorageBlocks.LOCKABLE_DOOR.setTranslationKey( ModInfo.modId + "." + BetterStorageBlocks.LOCKABLE_DOOR.getBlockName() );
			BetterStorageBlocks.LOCKABLE_DOOR.setRegistryName( BetterStorageBlocks.LOCKABLE_DOOR.getBlockName() );
			registry.register( BetterStorageBlocks.LOCKABLE_DOOR );
			GameRegistry.registerTileEntity( TileEntityLockableDoor.class, ModInfo.lockableDoor );
		}
		if( BetterStorage.config.cardboardBoxEnabled )
		{
			BetterStorageBlocks.CARDBOARD_BOX.setTranslationKey( ModInfo.modId + "." + BetterStorageBlocks.CARDBOARD_BOX.getBlockName() );
			BetterStorageBlocks.CARDBOARD_BOX.setRegistryName( BetterStorageBlocks.CARDBOARD_BOX.getBlockName() );
			registry.register( BetterStorageBlocks.CARDBOARD_BOX );
			GameRegistry.registerTileEntity( TileEntityCardboardBox.class, ModInfo.containerCardboardBox );
		}

	}

	@SubscribeEvent
	public void registerItems( Register< Item > event )
	{
		final IForgeRegistry< Item > registry = event.getRegistry();

		if( BetterStorage.config.crateEnabled )
			registry.register( new ItemBlockCrate( BetterStorageBlocks.CRATE ).setRegistryName( BetterStorageBlocks.CRATE.getRegistryName() ) );

		if( BetterStorage.config.reinforcedChestEnabled )
			registry.register( new ItemBlockReinforcedChest( BetterStorageBlocks.REINFORCED_CHEST )
					.setRegistryName( BetterStorageBlocks.REINFORCED_CHEST.getRegistryName() ) );

		if( BetterStorage.config.lockerEnabled )
		{
			registry.register( new ItemBlockLocker( BetterStorageBlocks.LOCKER ).setRegistryName( BetterStorageBlocks.LOCKER.getRegistryName() ) );
			if( BetterStorage.config.reinforcedLockerEnabled )
				registry.register( new ItemBlockReinforcedLocker( BetterStorageBlocks.REINFORCED_LOCKER )
						.setRegistryName( BetterStorageBlocks.REINFORCED_LOCKER.getRegistryName() ) );
		}

		if( BetterStorage.config.flintBlockEnabled )
			registry.register(
					new ItemBlock( BetterStorageBlocks.BLOCK_FLINT ).setRegistryName( BetterStorageBlocks.BLOCK_FLINT.getRegistryName() ) );

		if( BetterStorage.config.keyEnabled )
		{
			BetterStorageItems.KEY.register();
			registry.register( BetterStorageItems.KEY );
			if( BetterStorage.config.masterKeyEnabled )
			{
				BetterStorageItems.MASTER_KEY.register();
				registry.register( BetterStorageItems.MASTER_KEY );
			}
			if( BetterStorage.config.keyringEnabled )
			{
				BetterStorageItems.KEYRING.register();
				registry.register( BetterStorageItems.KEYRING );
			}
			if( BetterStorage.config.lockEnabled )
			{
				BetterStorageItems.LOCK.register();
				registry.register( BetterStorageItems.LOCK );
			}
		}

		if( BetterStorage.config.cardboardSheetEnabled )
		{
			BetterStorageItems.CARDBOARD_SHEET.register();
			registry.register( BetterStorageItems.CARDBOARD_SHEET );
			OreDictionary.registerOre( "sheetCardboard", BetterStorageItems.CARDBOARD_SHEET );
		}

		if( BetterStorage.config.cardboardBoxEnabled )
			registry.register( new ItemBlockCardboardBox( BetterStorageBlocks.CARDBOARD_BOX )
					.setRegistryName( BetterStorageBlocks.CARDBOARD_BOX.getRegistryName() ) );

		if( BetterStorage.config.cardboardSwordEnabled )
		{
			BetterStorageItems.CARDBOARD_SWORD.register();
			registry.register( BetterStorageItems.CARDBOARD_SWORD );
		}
		if( BetterStorage.config.cardboardShovelEnabled )
		{
			BetterStorageItems.CARDBOARD_SHOVEL.register();
			registry.register( BetterStorageItems.CARDBOARD_SHOVEL );
		}
		if( BetterStorage.config.cardboardPickaxeEnabled )
		{
			BetterStorageItems.CARDBOARD_PICKAXE.register();
			registry.register( BetterStorageItems.CARDBOARD_PICKAXE );
		}
		if( BetterStorage.config.cardboardAxeEnabled )
		{
			BetterStorageItems.CARDBOARD_AXE.register();
			registry.register( BetterStorageItems.CARDBOARD_AXE );
		}
		if( BetterStorage.config.cardboardHoeEnabled )
		{
			BetterStorageItems.CARDBOARD_HOE.register();
			registry.register( BetterStorageItems.CARDBOARD_HOE );
		}

		if( BetterStorage.config.cardboardHelmetEnabled )
		{
			BetterStorageItems.CARDBOARD_HELMET.register( "cardboard_helmet" );
			registry.register( BetterStorageItems.CARDBOARD_HELMET );
		}
		if( BetterStorage.config.cardboardChestplateEnabled )
		{
			BetterStorageItems.CARDBOARD_CHESTPLATE.register( "cardboard_chestplate" );
			registry.register( BetterStorageItems.CARDBOARD_CHESTPLATE );
		}
		if( BetterStorage.config.cardboardLeggingsEnabled )
		{
			BetterStorageItems.CARDBOARD_LEGGINGS.register( "cardboard_leggings" );
			registry.register( BetterStorageItems.CARDBOARD_LEGGINGS );
		}
		if( BetterStorage.config.cardboardBootsEnabled )
		{
			BetterStorageItems.CARDBOARD_BOOTS.register( "cardboard_boots" );
			registry.register( BetterStorageItems.CARDBOARD_BOOTS );
		}

		if( BetterStorage.config.slimeBucketEnabled )
		{
			BetterStorageItems.SLIME_BUCKET.register();
			registry.register( BetterStorageItems.SLIME_BUCKET );
		}
	}

	@SubscribeEvent
	public void registerEnchantments( Register< Enchantment > event )
	{
		final IForgeRegistry<Enchantment> registry = event.getRegistry();

		// Add key enchantments
		if( BetterStorage.config.keyEnabled )
		{
			if( BetterStorage.config.enchUnlockingEnabled )
			{
				EnchantmentBetterStorage.unlocking.setRegistryName( "unlocking" );
				registry.register( EnchantmentBetterStorage.unlocking );
			}
			if( BetterStorage.config.enchLockpickingEnabled )
			{
				EnchantmentBetterStorage.lockpicking.setRegistryName( "lockpicking" );
				registry.register( EnchantmentBetterStorage.lockpicking );
			}
			if( BetterStorage.config.enchMorphingEnabled )
			{
				EnchantmentBetterStorage.morphing.setRegistryName( "morphing" );
				registry.register( EnchantmentBetterStorage.morphing );
			}

			if( BetterStorage.config.enchLockpickingEnabled && BetterStorage.config.enchMorphingEnabled )
			{
				EnchantmentBetterStorage.lockpicking.setIncompatible( EnchantmentBetterStorage.morphing );
				EnchantmentBetterStorage.morphing.setIncompatible( EnchantmentBetterStorage.lockpicking );
			}
		}

		// Add lock enchantments
		if( BetterStorage.config.lockEnabled )
		{
			if( BetterStorage.config.enchPersistanceEnabled )
			{
				EnchantmentBetterStorage.persistance.setRegistryName( "persistance" );
				registry.register( EnchantmentBetterStorage.persistance );
			}
			if( BetterStorage.config.enchSecurityEnabled )
			{
				EnchantmentBetterStorage.security.setRegistryName( "security" );
				registry.register( EnchantmentBetterStorage.security );
			}
			if( BetterStorage.config.enchShockEnabled )
			{
				EnchantmentBetterStorage.shock.setRegistryName( "shock" );
				registry.register( EnchantmentBetterStorage.shock );
			}
			if( BetterStorage.config.enchTriggerEnabled )
			{
				EnchantmentBetterStorage.trigger.setRegistryName( "trigger" );
				registry.register( EnchantmentBetterStorage.trigger );
			}
		}
	}
	
	@SubscribeEvent
	public void onConfigChangeEvent( OnConfigChangedEvent event )
	{
		if( event.getModID().equals( ModInfo.modId ) && !event.isWorldRunning() )
			BetterStorage.config.syncFromGUI();
	}

	@SubscribeEvent
	public void onPlayerInteract( PlayerInteractEvent event )
	{
		final World world = event.getEntity().world;
		final BlockPos pos = event.getPos();
		final EntityPlayer player = event.getEntityPlayer();
		final ItemStack holding = player.getHeldItemMainhand();
		final IBlockState state = world.getBlockState( pos );
		final Block block = state.getBlock();
		final EnumHand hand = event.getHand();

		// Use cauldron to remove color from dyable items
		if( hand == EnumHand.MAIN_HAND && block == Blocks.CAULDRON && holding.getItem() instanceof IDyeableItem )
		{
			final int level = state.getValue( BlockCauldron.LEVEL );
			if( level > 0 )
			{
				final IDyeableItem dyeable = (IDyeableItem)holding.getItem();
				if( dyeable.canDye( holding ) && holding.hasTagCompound() )
				{
					final NBTTagCompound compound = holding.getTagCompound();
					if( compound.hasKey( "color" ) )
					{
						compound.removeTag( "color" );
						holding.setTagCompound( compound );
						Blocks.CAULDRON.setWaterLevel( world, pos, state, level - 1 );

						event.setResult( Result.DENY );
					}
				}
			}
		}

		// Prevent players from breaking blocks with broken cardboard items.
		if( hand == EnumHand.MAIN_HAND && holding.getItem() instanceof ICardboardItem && !ItemCardboardSheet.isEffective( holding ) )
		{
			if( event.isCancelable() )
				event.setCanceled( true );
			// event.setResult( Result.DENY );
			return;
		}

		// Attach locks to iron doors.
		/*
		 * if( !world.isRemote && BetterStorage.config.lockableDoorEnabled && hand == EnumHand.MAIN_HAND && block == Blocks.IRON_DOOR
		 * && holding.getItem() == BetterStorageItems.LOCK )
		 * player.inventory.setInventorySlotContents( player.inventory.currentItem, ItemStack.EMPTY );
		 */

		// Prevent eating of slime buckets after capturing them.
		if( preventSlimeBucketUse )
		{
			event.setCanceled( true );
			preventSlimeBucketUse = false;
		}
	}

	@SubscribeEvent
	public void onEntityInteract( EntityInteract event )
	{

		if( event.getEntity().world.isRemote || event.isCanceled() )
			return;

		final EntityPlayer player = event.getEntityPlayer();
		final Entity target = event.getTarget();
		final ItemStack holding = player.getHeldItemMainhand();

		/*
		 * if( target.getClass() == EntityChicken.class && holding != null && holding.getItem() == Items.NAME_TAG )
		 * {
		 *
		 * final EntityChicken chicken = (EntityChicken)target;
		 * if( !chicken.isDead && !chicken.isChild() && "Cluckington".equals( holding.getDisplayName() ) )
		 * EntityCluckington.spawn( chicken );
		 *
		 * }
		 */

		if( BetterStorage.config.slimeBucketEnabled && target instanceof EntityLiving && holding != null && holding.getItem() == Items.BUCKET )
		{
			ItemBucketSlime.pickUpSlime( player, (EntityLiving)target );
			if( player.getHeldItemMainhand().getItem() instanceof ItemBucketSlime )
				preventSlimeBucketUse = true;
		}
	}
}
