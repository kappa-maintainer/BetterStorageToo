package io.github.tehstoneman.betterstorage.api;

import java.util.UUID;

import io.github.tehstoneman.betterstorage.world.capabilities.CratePileCapability;
import io.github.tehstoneman.betterstorage.world.item.CrateStackHandler;
import io.github.tehstoneman.betterstorage.world.storage.CratePile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Interface that describes the storage of crate piles.
 *
 * @author TehStoneMan
 */
public interface ICratePile extends INBTSerializable< CompoundTag >
{
	/**
	 * Get the stack handler for the associated ID.
	 *
	 * @param pileID
	 *            The {@link UUID} of the pile to get.
	 * @return the {@link CrateStackHandler} for the associated crate pile.
	 */
	CrateStackHandler getCratePile( UUID pileID );

	/**
	 * Get or create a crate handler for the given {@link UUID}. Used for syncing client with server.
	 *
	 * @param pileID
	 *            The {@link UUID} of the pile to get.
	 * @return the {@link CrateStackHandler} for the associated crate pile.
	 */
	CrateStackHandler computeIfAbsentCratePile( UUID pileID );

	/**
	 * Creates and adds a new crate to this collection.
	 *
	 * @return a new {@link CrateStackHandler}.
	 */
	CrateStackHandler createCratePile();

	/**
	 * Adds a new crate pile to this collection.
	 *
	 * @param pileID
	 *            The {@link UUID} of the pile to add.
	 * @return the {@link CrateStackHandler} associated with the new crate pile.
	 */
	CrateStackHandler addCrateToPile( UUID pileID );

	/**
	 * Removes the crate pile from the collection, deletes the pile's file.
	 *
	 * @param pileID
	 *            The {@link UUID} of the pile to remove.
	 */
	void removeCratePile( UUID pileID );

	/**
	 * Retrieve the crate storage for the given {@link Level}.
	 *
	 * @param level
	 *            The {@link Level} to get the storage for.
	 * @return the {@link ICratePile} for the given {@link Level}.
	 */
	static ICratePile getCrateStorage( Level level )
	{
		final LazyOptional< ICratePile > capability = level.getCapability( CratePileCapability.CRATE_PILE_CAPABILITY );
		return capability.orElse( new CratePile() );
	}
}
