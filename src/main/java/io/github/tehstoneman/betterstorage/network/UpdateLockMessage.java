package io.github.tehstoneman.betterstorage.network;

import java.util.function.Supplier;

import io.github.tehstoneman.betterstorage.api.lock.IKeyLockable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class UpdateLockMessage
{
	private final BlockPos	pos;
	private final ItemStack	lock;

	public UpdateLockMessage( BlockPos pos, ItemStack lock )
	{
		this.pos	= pos;
		this.lock	= lock;
	}

	public static UpdateLockMessage decode( FriendlyByteBuf buffer )
	{
		final BlockPos	pos		= buffer.readBlockPos();
		final ItemStack	lock	= buffer.readItem();
		return new UpdateLockMessage( pos, lock );
	}

	public static void encode( UpdateLockMessage message, FriendlyByteBuf buffer )
	{
		buffer.writeBlockPos( message.pos );
		buffer.writeItemStack( message.lock, false );
	}

	@SuppressWarnings( "null" )
	public static void handle( UpdateLockMessage message, Supplier< NetworkEvent.Context > context )
	{
		context.get().enqueueWork( () ->
		{
			// DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> UpdateCrateMessage.handlePacket(message, context));
			if( context.get().getDirection().getReceptionSide().isClient() )
			{
				final Minecraft		instance	= Minecraft.getInstance();
				final ClientLevel	level		= instance.level;
				final BlockEntity	blockEntity	= level.getBlockEntity( message.pos );
				if( blockEntity instanceof final IKeyLockable crate )
					crate.setLock( message.lock );
			}
		} );
		context.get().setPacketHandled( true );
	}
}
