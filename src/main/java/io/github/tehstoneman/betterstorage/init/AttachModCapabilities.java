package io.github.tehstoneman.betterstorage.init;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.world.capabilities.CratePileCapability;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber( modid = ModInfo.MOD_ID, bus = Bus.FORGE )
public class AttachModCapabilities
{
	@SubscribeEvent
	public static void attachWorldCapabilities( AttachCapabilitiesEvent< Level > event )
	{
		event.addCapability( CratePileCapability.CAPABILITY_RESOURCE, new CratePileCapability.Provider() );
	}
}
