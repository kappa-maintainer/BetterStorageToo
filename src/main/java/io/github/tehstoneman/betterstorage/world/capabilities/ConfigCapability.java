package io.github.tehstoneman.betterstorage.world.capabilities;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.api.IHexKeyConfig;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ConfigCapability
{
	public static class Provider<HANDLER> implements ICapabilitySerializable< CompoundTag >
	{
		// public IHexKeyConfig hexKeyConfig;
		private final Capability< HANDLER >				capability;
		private final Direction							facing;
		private final HANDLER							instance;
		private final LazyOptional< HANDLER >			lazyOptional;
		private final INBTSerializable< CompoundTag >	serializableInstance;
		// private final LazyOptional< IHexKeyConfig > capabilityHandler = LazyOptional.of( () -> hexKeyConfig );

		@SuppressWarnings("unchecked")
		public Provider( final Capability< HANDLER > capability, @Nullable final Direction facing, final HANDLER instance )
		{
			this.capability	= Preconditions.checkNotNull( capability, "capability" );
			this.facing		= facing;
			this.instance	= Preconditions.checkNotNull( instance, "instance" );

			lazyOptional = LazyOptional.of( () -> this.instance );

			Preconditions.checkArgument( instance instanceof INBTSerializable, "instance must implement INBTSerializable" );

			serializableInstance = (INBTSerializable< CompoundTag >)instance;
			// hexKeyConfig = new HexKeyConfig();
		}

		@Override
		public void deserializeNBT( CompoundTag nbt )
		{
			serializableInstance.deserializeNBT( nbt );
		}

		public final Capability< HANDLER > getCapability()
		{
			return capability;
		}

		@Override
		public <T> LazyOptional< T > getCapability( Capability< T > capability, @Nullable Direction side )
		{
			return getCapability().orEmpty( capability, lazyOptional );
		}

		@Nullable
		public Direction getFacing()
		{
			return facing;
		}

		public final HANDLER getInstance()
		{
			return instance;
		}

		@Override
		public CompoundTag serializeNBT()
		{
			return serializableInstance.serializeNBT();
		}
	}

	public static final Capability< IHexKeyConfig > CONFIG_CAPABILITY = CapabilityManager.get( new CapabilityToken<>()
	{} );

	public static ResourceLocation CAPABILITY_RESOURCE = new ResourceLocation( ModInfo.MOD_ID, "config" );

	/*
	 * CapabilityManager.INSTANCE.register( IHexKeyConfig.class, new Capability.IStorage< IHexKeyConfig >()
	 * {
	 *
	 * @Override
	 * public INBT writeNBT( Capability< IHexKeyConfig > capability, IHexKeyConfig instance, Direction side )
	 * {
	 * final ListNBT nbtTagList = new ListNBT();
	 * final int size = instance.getSlots();
	 * for( int i = 0; i < size; i++ )
	 * {
	 * final ItemStack stack = instance.getStackInSlot( i );
	 * if( !stack.isEmpty() )
	 * {
	 * final CompoundTag itemTag = new CompoundTag();
	 * itemTag.putInt( "Slot", i );
	 * stack.save( itemTag );
	 * nbtTagList.add( itemTag );
	 * }
	 * }
	 * return nbtTagList;
	 * }
	 *
	 * @Override
	 * public void readNBT( Capability< IHexKeyConfig > capability, IHexKeyConfig instance, Direction side, INBT nbt )
	 * {
	 * final ListNBT tagList = (ListNBT)nbt;
	 * for( int i = 0; i < tagList.size(); i++ )
	 * {
	 * final CompoundTag itemTags = tagList.getCompound( i );
	 * final int j = itemTags.getInt( "Slot" );
	 *
	 * if( j >= 0 && j < instance.getSlots() )
	 * instance.setStackInSlot( j, ItemStack.of( itemTags ) );
	 * }
	 * }
	 * }, HexKeyConfig::new );
	 * }
	 */

	public static void register( final RegisterCapabilitiesEvent event )
	{
		event.register( IHexKeyConfig.class );
	}
}
