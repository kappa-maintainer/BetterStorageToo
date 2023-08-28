package io.github.tehstoneman.betterstorage.world.item;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.world.item.cardboard.CardboardAxeItem;
import io.github.tehstoneman.betterstorage.world.item.cardboard.CardboardHoeItem;
import io.github.tehstoneman.betterstorage.world.item.cardboard.CardboardPickaxeItem;
import io.github.tehstoneman.betterstorage.world.item.cardboard.CardboardSheetItem;
import io.github.tehstoneman.betterstorage.world.item.cardboard.CardboardShovelItem;
import io.github.tehstoneman.betterstorage.world.item.cardboard.CardboardSwordItem;
import io.github.tehstoneman.betterstorage.world.item.locking.ItemLock;
import io.github.tehstoneman.betterstorage.world.item.locking.ItemMasterKey;
import io.github.tehstoneman.betterstorage.world.item.locking.KeyItem;
import io.github.tehstoneman.betterstorage.world.item.locking.KeyringItem;
import io.github.tehstoneman.betterstorage.world.level.block.BetterStorageBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BetterStorageItems
{
	public static final DeferredRegister< Item > REGISTRY = DeferredRegister.create( ForgeRegistries.ITEMS, ModInfo.MOD_ID );

	public static final RegistryObject< CrateItemBlock >			CRATE				= REGISTRY.register( "crate", CrateItemBlock::new );
	//public static final RegistryObject< ReinforcedChestItemBlock >	REINFORCED_CHEST	= REGISTRY.register( "reinforced_chest", ReinforcedChestItemBlock::new );
	//public static final RegistryObject< LockerItemBlock >			LOCKER				= REGISTRY.register( "locker", LockerItemBlock::new );
	//public static final RegistryObject< ReinforcedLockerItemBlock >	REINFORCED_LOCKER	= REGISTRY.register( "reinforced_locker", ReinforcedLockerItemBlock::new );

	public static final RegistryObject< KeyItem >		KEY			= REGISTRY.register( "key", KeyItem::new );
	public static final RegistryObject< KeyringItem >	KEYRING		= REGISTRY.register( "keyring", KeyringItem::new );
	public static final RegistryObject< ItemMasterKey >	MASTER_KEY	= REGISTRY.register( "master_key", ItemMasterKey::new );
	public static final RegistryObject< ItemLock >		LOCK		= REGISTRY.register( "lock", ItemLock::new );

	public static final RegistryObject< CardboardSheetItem >	CARDBOARD_SHEET	= REGISTRY.register( "cardboard_sheet", CardboardSheetItem::new );
	//public static final RegistryObject< BlockItemCardboardBox >	CARDBOARD_BOX	= REGISTRY.register( "cardboard_box", BlockItemCardboardBox::new );

	public static final RegistryObject< CardboardSwordItem >	CARDBOARD_SWORD		=
																				REGISTRY.register( "cardboard_sword", CardboardSwordItem::new );
	public static final RegistryObject< CardboardShovelItem >	CARDBOARD_SHOVEL	= REGISTRY.register( "cardboard_shovel",
		CardboardShovelItem::new );
	public static final RegistryObject< CardboardPickaxeItem >	CARDBOARD_PICKAXE	= REGISTRY.register( "cardboard_pickaxe",
		CardboardPickaxeItem::new );
	public static final RegistryObject< CardboardAxeItem >		CARDBOARD_AXE		= REGISTRY.register( "cardboard_axe", CardboardAxeItem::new );
	public static final RegistryObject< CardboardHoeItem >		CARDBOARD_HOE		= REGISTRY.register( "cardboard_hoe", CardboardHoeItem::new );

	//public static final RegistryObject< CardboardArmorItem >	CARDBOARD_HELMET		= REGISTRY.register( "cardboard_helmet", () -> new CardboardArmorItem( EquipmentSlot.HEAD ) );
	//public static final RegistryObject< CardboardArmorItem >	CARDBOARD_CHESTPLATE	= REGISTRY.register( "cardboard_chestplate", () -> new CardboardArmorItem( EquipmentSlot.CHEST ) );
	//public static final RegistryObject< CardboardArmorItem >	CARDBOARD_LEGGINGS		= REGISTRY.register( "cardboard_leggings", () -> new CardboardArmorItem( EquipmentSlot.LEGS ) );
	//public static final RegistryObject< CardboardArmorItem >	CARDBOARD_BOOTS			= REGISTRY.register( "cardboard_boots", () -> new CardboardArmorItem( EquipmentSlot.FEET ) );

	//public static final RegistryObject< BlockItem >	BLOCK_FLINT	= REGISTRY.register( "block_flint", () -> new BlockItem( BetterStorageBlocks.BLOCK_FLINT.get(), new Item.Properties() ) );		// .tab( BetterStorage.CREATIVE_MODE_TAB ) ) );
	public static final RegistryObject< BlockItem >	GLASS_TANK	= REGISTRY.register( "glass_tank",
		() -> new BlockItem( BetterStorageBlocks.GLASS_TANK.get(), new Item.Properties() ) );		// .tab( BetterStorage.CREATIVE_MODE_TAB ) ) );

	/*
	 * public static RegistryObject< BucketItem > MILK_BUCKET = REGISTRY.register( "milk_bucket",
	 * () -> new BucketItem( BetterStorageFluids.MILK, new Item.Properties().craftRemainder( Items.BUCKET ) ) );
	 */

	public static final RegistryObject< HexKeyItem > HEX_KEY = REGISTRY.register( "hex_key", HexKeyItem::new );
}
