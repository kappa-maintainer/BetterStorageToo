package io.github.tehstoneman.betterstorage.world.capabilities;

import io.github.tehstoneman.betterstorage.api.ICratePile;
import io.github.tehstoneman.betterstorage.util.BetterStorageResource;
import io.github.tehstoneman.betterstorage.world.storage.CratePile;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;

public class CratePileCapability
{
	public static final Capability< ICratePile > CRATE_PILE_CAPABILITY = CapabilityManager.get( new CapabilityToken<>()
	{} );

	public static ResourceLocation CAPABILITY_RESOURCE = new BetterStorageResource( "crate_pile" );

	public static void register( final RegisterCapabilitiesEvent event )
	{
		event.register( ICratePile.class );
		/*
		 * CapabilityManager.INSTANCE.register( ICrateStorage.class, new IStorage< ICrateStorage >()
		 * {
		 * @Override
		 * public INBT writeNBT( Capability< ICrateStorage > capability, ICrateStorage instance, Direction side )
		 * {
		 * return instance.serializeNBT();
		 * }
		 * @Override
		 * public void readNBT( Capability< ICrateStorage > capability, ICrateStorage instance, Direction side, INBT nbt )
		 * {
		 * if( !( instance instanceof CrateStorage ) )
		 * throw new IllegalArgumentException( "Can not deserialize to an instance that isn't the default implementation" );
		 * instance.deserializeNBT( (CompoundTag)nbt );
		 * }
		 * }, () -> new CrateStorage() );
		 */
	}

	public static class Provider implements ICapabilitySerializable< CompoundTag >
	{
		public ICratePile                        cratePile;
		private final LazyOptional< ICratePile > capabilityHandler = LazyOptional.of( () -> cratePile );

		public Provider()
		{
			cratePile = new CratePile();
		}

		@Override
		public <T> LazyOptional< T > getCapability( Capability< T > capability, Direction side )
		{
			return CRATE_PILE_CAPABILITY.orEmpty( capability, capabilityHandler );
		}

		@Override
		public CompoundTag serializeNBT()
		{
			return cratePile.serializeNBT();
		}

		@Override
		public void deserializeNBT( CompoundTag nbt )
		{
			cratePile.deserializeNBT( nbt );
		}
	}
}
