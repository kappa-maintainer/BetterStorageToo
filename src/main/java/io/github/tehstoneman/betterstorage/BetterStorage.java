package io.github.tehstoneman.betterstorage;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;

@Mod( ModInfo.MOD_ID )
public class BetterStorage
{
	public static final Logger LOGGER = LogManager.getLogger( ModInfo.MOD_ID );
	// public static final CreativeModeTab CREATIVE_MODE_TAB = new BetterStorageCreativeModeTab();
	// public static final SimpleChannel NETWORK = ModNetwork.getNetworkChannel();

	public static Random RANDOM;

	public BetterStorage()
	{
		// Initialize random numbers
		RANDOM = new Random();

		// BetterStorageConfig.register( ModLoadingContext.get() );

		// final var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		// ForgeMod.enableMilkFluid();

		// BetterStorageBlocks.REGISTERY.register( modEventBus );
		// BetterStorageFluids.REGISTERY.register( modEventBus );
		// BetterStorageItems.REGISTERY.register( modEventBus );
		// BetterStorageEnchantments.REGISTERY.register( modEventBus );
		// BetterStorageBlockEntityTypes.REGISTERY.register( modEventBus );
		// BetterStorageMenuTypes.REGISTERY.register( modEventBus );
	}
}
