package io.github.tehstoneman.betterstorage.init;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.world.capabilities.ConfigCapability;
import io.github.tehstoneman.betterstorage.world.capabilities.CratePileCapability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber( modid = ModInfo.MOD_ID, bus = Bus.MOD )
public class InitModCapabilities
{
	@SubscribeEvent
	public static void registerCapabilities( RegisterCapabilitiesEvent event )
	{
		ConfigCapability.register( event );
		CratePileCapability.register( event );
	}
}
