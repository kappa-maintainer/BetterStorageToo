package io.github.tehstoneman.betterstorage.world.level.block;

import io.github.tehstoneman.betterstorage.ModInfo;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BetterStorageBlocks
{
	public static final DeferredRegister< Block > REGISTRY = DeferredRegister.create( ForgeRegistries.BLOCKS, ModInfo.MOD_ID );

	public static final RegistryObject< CrateBlock > CRATE = REGISTRY.register( "crate", CrateBlock::new );

	//public static final RegistryObject< ReinforcedChestBlock > REINFORCED_CHEST = REGISTRY.register( "reinforced_chest", ReinforcedChestBlock::new );

	//public static final RegistryObject< LockerBlock >			LOCKER				= REGISTRY.register( "locker", LockerBlock::new );
	//public static final RegistryObject< ReinforcedLockerBlock >	REINFORCED_LOCKER	= REGISTRY.register( "reinforced_locker", ReinforcedLockerBlock::new );

	//public static final RegistryObject< Block > BLOCK_FLINT = REGISTRY.register( "block_flint", () -> new Block( BlockBehavior.Properties.of( Material.STONE, MaterialColor.COLOR_BLACK ).strength( 5.0F, 6.0F ) ) );

	public static final RegistryObject< LockableDoorBlock >	LOCKABLE_DOOR	= REGISTRY.register( "lockable_door", LockableDoorBlock::new );
	//public static final RegistryObject< CardboardBoxBlock >	CARDBOARD_BOX	= REGISTRY.register( "cardboard_box", CardboardBoxBlock::new );
	public static final RegistryObject< TankBlock >			GLASS_TANK		= REGISTRY.register( "glass_tank", TankBlock::new );
	/*
	 * public static RegistryObject< FlowingFluidBlock > MILK = REGISTRY.register( "milk",
	 * () -> new FlowingFluidBlock( BetterStorageFluids.MILK, AbstractBlock.Properties.of( FluidMilk.MATERIAL_MILK, DyeColor.WHITE ) ) );
	 */
}
