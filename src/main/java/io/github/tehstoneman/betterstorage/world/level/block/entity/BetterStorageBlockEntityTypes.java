package io.github.tehstoneman.betterstorage.world.level.block.entity;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.world.level.block.BetterStorageBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BetterStorageBlockEntityTypes
{
	public static final DeferredRegister< BlockEntityType< ? > > REGISTERY = DeferredRegister.create( ForgeRegistries.BLOCK_ENTITY_TYPES,
		ModInfo.MOD_ID );

	public static final RegistryObject< BlockEntityType< CrateBlockEntity > > CRATE = REGISTERY.register( "crate",
		() -> BlockEntityType.Builder.of( CrateBlockEntity::new, BetterStorageBlocks.CRATE.get() ).build( null ) );

	//public static final RegistryObject< BlockEntityType< ReinforcedChestBlockEntity > > REINFORCED_CHEST = REGISTERY.register( "reinforced_chest", () -> BlockEntityType.Builder.of( ReinforcedChestBlockEntity::new, BetterStorageBlocks.REINFORCED_CHEST.get() ).build( null ) );

	//public static final RegistryObject< BlockEntityType< LockerBlockEntity > >				LOCKER				= REGISTERY.register( "locker", () -> BlockEntityType.Builder.of( LockerBlockEntity::new, BetterStorageBlocks.LOCKER.get() ).build( null ) );
	/*public static final RegistryObject< BlockEntityType< ReinforcedLockerBlockEntity > >	REINFORCED_LOCKER	= REGISTERY.register(
		"reinforced_locker",
		() -> BlockEntityType.Builder.of( ReinforcedLockerBlockEntity::new, BetterStorageBlocks.REINFORCED_LOCKER.get() ).build( null ) );*/

	public static final RegistryObject< BlockEntityType< LockableDoorBlockEntity > > LOCKABLE_DOOR = REGISTERY.register( "lockable_door",
		() -> BlockEntityType.Builder.of( LockableDoorBlockEntity::new, BetterStorageBlocks.LOCKABLE_DOOR.get() ).build( null ) );

	//public static final RegistryObject< BlockEntityType< CardboardBoxBlockEntity > > CARDBOARD_BOX = REGISTERY.register( "cardboard_box", () -> BlockEntityType.Builder.of( CardboardBoxBlockEntity::new, BetterStorageBlocks.CARDBOARD_BOX.get() ).build( null ) );

	public static final RegistryObject< BlockEntityType< TankBlockEntity > > GLASS_TANK = REGISTERY.register( "glass_tank",
		() -> BlockEntityType.Builder.of( TankBlockEntity::new, BetterStorageBlocks.GLASS_TANK.get() ).build( null ) );
}
