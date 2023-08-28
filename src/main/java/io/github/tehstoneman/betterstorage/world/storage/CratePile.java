package io.github.tehstoneman.betterstorage.world.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.github.tehstoneman.betterstorage.api.ICratePile;
import io.github.tehstoneman.betterstorage.world.item.CrateStackHandler;
import net.minecraft.nbt.CompoundTag;

/** Holds all CratePile objects for one world / dimension. */
public class CratePile implements ICratePile
{
	private final Map< UUID, CrateStackHandler > pileDataMap = new HashMap<>();

	public CratePile()
	{}

	@Override
	public CrateStackHandler addCrateToPile( UUID pileID )
	{
		final CrateStackHandler cratePile = new CrateStackHandler( 18 );
		pileDataMap.put( pileID, cratePile );
		cratePile.setPileID( pileID );
		return cratePile;
	}

	@Override
	public CrateStackHandler computeIfAbsentCratePile( UUID pileID )
	{
		if( !pileDataMap.containsKey( pileID ) )
		{
			final CrateStackHandler cratePile = new CrateStackHandler( 18 );
			pileDataMap.put( pileID, cratePile );
			cratePile.setPileID( pileID );
		}
		return pileDataMap.get( pileID );
	}

	@Override
	public CrateStackHandler createCratePile()
	{
		UUID pileID = UUID.randomUUID();
		while( pileDataMap.containsKey( pileID ) )
			pileID = UUID.randomUUID();
		return addCrateToPile( pileID );
	}

	@Override
	public void deserializeNBT( CompoundTag compound )
	{
		if( !compound.isEmpty() )
			for( final String key : compound.getAllKeys() )
			{
				final CrateStackHandler crateStackHandler = new CrateStackHandler( 18 );
				crateStackHandler.deserializeNBT( compound.getCompound( key ) );
				pileDataMap.put( UUID.fromString( key ), crateStackHandler );
			}
	}

	/** Get the stack handler for the associated ID */
	@Override
	public CrateStackHandler getCratePile( UUID pileID )
	{
		if( pileID != null && pileDataMap.containsKey( pileID ) )
			return pileDataMap.get( pileID );
		return createCratePile();
	}

	@Override
	public void removeCratePile( UUID pileID )
	{
		pileDataMap.remove( pileID );
	}

	@Override
	public CompoundTag serializeNBT()
	{
		final CompoundTag compound = new CompoundTag();
		if( !pileDataMap.isEmpty() )
			for( final Map.Entry< UUID, CrateStackHandler > entry : pileDataMap.entrySet() )
				compound.put( entry.getKey().toString(), entry.getValue().serializeNBT() );
		return compound;
	}
}
