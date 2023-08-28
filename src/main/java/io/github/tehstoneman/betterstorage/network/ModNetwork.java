package io.github.tehstoneman.betterstorage.network;

import io.github.tehstoneman.betterstorage.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork
{
	public static ResourceLocation	CHANEL_NAME		= new ResourceLocation( ModInfo.MOD_ID, "network" );
	public static String			NETWORK_VERSION	= "1";

	public static SimpleChannel getNetworkChannel()
	{
		final SimpleChannel channel = NetworkRegistry.ChannelBuilder.named( CHANEL_NAME ).networkProtocolVersion( () -> NETWORK_VERSION )
			.clientAcceptedVersions( version -> true ).serverAcceptedVersions( version -> true ).simpleChannel();

		int index = 1;

		channel.messageBuilder( UpdateCrateMessage.class, index++ ).decoder( UpdateCrateMessage::decode ).encoder( UpdateCrateMessage::encode )
			.consumerNetworkThread( UpdateCrateMessage::handle ).add();
		channel.messageBuilder( UpdateLockMessage.class, index++ ).decoder( UpdateLockMessage::decode ).encoder( UpdateLockMessage::encode )
			.consumerNetworkThread( UpdateLockMessage::handle ).add();

		return channel;
	}
}
