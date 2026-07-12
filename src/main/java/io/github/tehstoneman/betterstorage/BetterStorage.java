package io.github.tehstoneman.betterstorage;

import java.util.Random;

import org.apache.logging.log4j.Logger;

import io.github.tehstoneman.betterstorage.api.BetterStorageAPI;
import io.github.tehstoneman.betterstorage.client.CreativeTabBetterStorage;
import io.github.tehstoneman.betterstorage.common.enchantment.EnchantmentBetterStorage;
import io.github.tehstoneman.betterstorage.config.BetterStorageConfig;
import io.github.tehstoneman.betterstorage.event.BetterStorageEventHandler;
import io.github.tehstoneman.betterstorage.proxy.CommonProxy;
import io.github.tehstoneman.betterstorage.utils.MaterialRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

//@formatter:off
@Mod( modid						= ModInfo.modId,
      name						= ModInfo.modName,
      version					= Tags.VERSION,
      dependencies				= ModInfo.dependencies,
      acceptedMinecraftVersions	= ModInfo.acceptedMC,
      guiFactory				= ModInfo.guiFactory,
      updateJSON				= ModInfo.updateJson )
//@formatter:on
public class BetterStorage
{
	@Instance( ModInfo.modId )
	public static BetterStorage			instance;

	@SidedProxy( serverSide = ModInfo.commonProxy, clientSide = ModInfo.clientProxy )
	public static CommonProxy			proxy;

	public static SimpleNetworkWrapper	simpleNetworkWrapper;
	public static Logger				logger;
	public static CreativeTabs			creativeTab;
	public static BetterStorageConfig	config;

	public static Random				random;

	@EventHandler
	public void preInit( FMLPreInitializationEvent event )
	{
		// Register network messages
		final int messageID = 0;
		simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel( ModInfo.modId );

		logger = event.getModLog();
		creativeTab = new CreativeTabBetterStorage();

		// Initialize random numbers
		random = new Random();

		config = new BetterStorageConfig( event.getSuggestedConfigurationFile() );
		config.syncFromFile();

		MinecraftForge.EVENT_BUS.register( new BetterStorageEventHandler() );

		// Addon.initialize();
		// Addon.setupConfigsAll();

		// Initialize API
		BetterStorageAPI.materials = new MaterialRegistry();

		proxy.preInit();

		EnchantmentBetterStorage.initialize();

		// BetterStorageEntities.register();
		// DungeonLoot.add();

	}

	@EventHandler
	public void init( FMLInitializationEvent event )
	{
		// Recipes.add();

		proxy.initialize();
	}

	@EventHandler
	public void postInit( FMLPostInitializationEvent event )
	{
		// Addon.postInitializeAll();

		proxy.postInit();
	}
}
