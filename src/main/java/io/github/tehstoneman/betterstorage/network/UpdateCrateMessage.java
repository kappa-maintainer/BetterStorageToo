package io.github.tehstoneman.betterstorage.network;

import java.util.function.Supplier;

import io.github.tehstoneman.betterstorage.world.level.block.entity.CrateBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class UpdateCrateMessage
{
	private final BlockPos	pos;
	private final int		numCrates;
	private final int		capacity;

	public UpdateCrateMessage( BlockPos pos, int numCrates, int capacity )
	{
		this.pos		= pos;
		this.numCrates	= numCrates;
		this.capacity	= capacity;
	}

	public static UpdateCrateMessage decode( FriendlyByteBuf buffer )
	{
		final BlockPos	pos			= buffer.readBlockPos();
		final int		numCrates	= buffer.readInt();
		final int		capacity	= buffer.readInt();
		return new UpdateCrateMessage( pos, numCrates, capacity );
	}

	public static void encode( UpdateCrateMessage message, FriendlyByteBuf buffer )
	{
		buffer.writeBlockPos( message.pos );
		buffer.writeInt( message.numCrates );
		buffer.writeInt( message.capacity );
	}

	@SuppressWarnings("null")
	public static void handle( UpdateCrateMessage message, Supplier< NetworkEvent.Context > context )
	{
		context.get().enqueueWork( () ->
		{
			// DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> UpdateCrateMessage.handlePacket(message, context));
			if( context.get().getDirection().getReceptionSide().isClient() )
			{
				final Minecraft		instance	= Minecraft.getInstance();
				final ClientLevel	level		= instance.level;
				final BlockEntity	blockEntity	= level.getBlockEntity( message.pos );
				if( blockEntity instanceof final CrateBlockEntity crate )
				{
					crate.setNumCrates( message.numCrates );
					crate.setCapacity( message.capacity );
				}
			}
		} );
		context.get().setPacketHandled( true );
	}
}
