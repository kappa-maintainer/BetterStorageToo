package io.github.tehstoneman.betterstorage.world.inventory;

import io.github.tehstoneman.betterstorage.api.IHexKeyConfig;
import io.github.tehstoneman.betterstorage.world.capabilities.ConfigCapability;
import io.github.tehstoneman.betterstorage.world.item.HexKeyConfig;
import io.github.tehstoneman.betterstorage.world.level.block.entity.ContainerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ConfigContainerMenu extends AbstractContainerMenu
{
	public class ConfigSlot extends SlotItemHandler
	{
		public ConfigSlot( IItemHandler itemHandler, int index, int xPosition, int yPosition )
		{
			super( itemHandler, index, xPosition, yPosition );
		}

		public int getIconX()
		{
			if( isActive() )
				return 176;
			return 194;
		}

		public int getIconY()
		{
			if( getSlotIndex() == HexKeyConfig.SLOT_APPEARANCE )
				return 0;
			return 18;
		}
	}

	private static final int CONFIG_SLOTS = 1;

	private final IHexKeyConfig configContainer;

	private final ContainerBlockEntity tileContainer;

	public int indexStart, indexPlayer, indexHotbar;

	@SuppressWarnings("null")
	public ConfigContainerMenu( int windowId, Inventory playerInventory, Level world, BlockPos pos )
	{
		super( BetterStorageMenuTypes.CONFIG.get(), windowId );
		tileContainer	= (ContainerBlockEntity)world.getBlockEntity( pos );
		configContainer	= tileContainer.getCapability( ConfigCapability.CONFIG_CAPABILITY, null ).orElse( null );

		indexStart	= 0;
		indexHotbar	= CONFIG_SLOTS;
		indexPlayer	= indexHotbar + 9;

		addSlot( new ConfigSlot( configContainer, HexKeyConfig.SLOT_APPEARANCE, 8, 18 ) );

		// Fill player inventory
		for( int i = 0; i < 27; i++ )
		{
			final int	x	= i % 9 * 18;
			final int	y	= i / 9 * 18;
			addSlot( new Slot( playerInventory, i + 9, 8 + x, 50 + y ) );
		}

		for( int i = 0; i < 9; i++ )
		{
			final int x = i % 9 * 18;
			addSlot( new Slot( playerInventory, i, 8 + x, 108 ) );
		}
	}

	@Override
	public ItemStack quickMoveStack( Player playerIn, int index )
	{
		final Slot	slot		= slots.get( index );
		ItemStack	returnStack	= ItemStack.EMPTY;

		if( slot != null && slot.hasItem() )
		{
			final ItemStack itemStack = slot.getItem();
			returnStack = itemStack.copy();

			if( index < indexHotbar )
			{
				// Try to transfer from container to player
				if( !moveItemStackTo( itemStack, indexHotbar, slots.size(), true ) )
					return ItemStack.EMPTY;
			} // Otherwise try to transfer from player to container
			else if( !moveItemStackTo( itemStack, 0, indexHotbar, false ) )
				return ItemStack.EMPTY;

			if( itemStack.isEmpty() )
				slot.set( ItemStack.EMPTY );
			else
				slot.setChanged();
		}

		return returnStack;
	}

	@Override
	@SuppressWarnings("null")
	public boolean stillValid( Player playerIn )
	{
		final Level			world	= tileContainer.getLevel();
		final BlockPos		pos		= tileContainer.getBlockPos();
		final BlockState	state	= world.getBlockState( pos );
		return stillValid( ContainerLevelAccess.create( world, pos ), playerIn, state.getBlock() );
	}
}
