package io.github.tehstoneman.betterstorage.world.inventory;

import io.github.tehstoneman.betterstorage.api.lock.IKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotKeyRing extends Slot
{
	public SlotKeyRing( Inventory inventoryIn, int index, int xPosition, int yPosition )
	{
		super( inventoryIn, index, xPosition, yPosition );
	}

	@Override
	public boolean mayPlace( ItemStack stack )
	{
		return stack == null || stack.getItem() instanceof IKey && ( (IKey)stack.getItem() ).isNormalKey();
	}
}
