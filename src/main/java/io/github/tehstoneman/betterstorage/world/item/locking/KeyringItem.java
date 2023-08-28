package io.github.tehstoneman.betterstorage.world.item.locking;

import java.util.List;

import javax.annotation.Nullable;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.api.lock.IKey;
import io.github.tehstoneman.betterstorage.world.inventory.KeyringCapabilityProvider;
import io.github.tehstoneman.betterstorage.world.inventory.KeyringContainerMenu;
import io.github.tehstoneman.betterstorage.world.item.BetterStorageItem;
import io.github.tehstoneman.betterstorage.world.item.BetterStorageItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;

public class KeyringItem extends BetterStorageItem implements IKey, MenuProvider
{
	public KeyringItem()
	{
		super( new Item.Properties() );
		// addPropertyOverride( new ResourceLocation( "full" ), ( itemStack, level, entityPlayer ) -> getFilledCapacity( itemStack ) );
	}

	@Override
	@SuppressWarnings("null")
	public void appendHoverText( ItemStack itemStack, @Nullable Level level, List< Component > tooltip, TooltipFlag flag )
	{
		super.appendHoverText( itemStack, level, tooltip, flag );
		if( flag.isAdvanced() && itemStack.hasTag() )
		{
			final CompoundTag tag = itemStack.getTag();
			if( tag.contains( "Occupied" ) )
				tooltip.add( Component.translatable( "Keys : " + tag.getInt( "Occupied" ) ) );
		}
	}

	@Override
	public boolean canApplyEnchantment( ItemStack itemStack, Enchantment enchantment )
	{
		return false;
	}

	@Override
	public AbstractContainerMenu createMenu( int windowID, Inventory playerInventory, Player player )
	{
		final ItemStack	itemStack	= player.getMainHandItem();
		final int		index		= player.getInventory().selected;
		return new KeyringContainerMenu( windowID, playerInventory, itemStack, index );
	}

	@Override
	public Component getDisplayName()
	{
		return Component.translatable( ModInfo.CONTAINER_KEYRING_NAME );
	}

	@SuppressWarnings("null")
	public float getFilledCapacity( ItemStack itemStack )
	{
		if( itemStack.hasTag() )
		{
			final CompoundTag tag = itemStack.getTag();
			if( tag.contains( "Occupied" ) )
			{
				final int occupied = tag.getInt( "Occupied" );
				return occupied / 9.0f;
			}
		}
		return 0.0F;
	}

	@Override
	public ICapabilityProvider initCapabilities( ItemStack itemStack, @Nullable CompoundTag tag )
	{
		return new KeyringCapabilityProvider( itemStack );
	}

	@Override
	public boolean unlock( ItemStack keyStack, ItemStack lockStack, boolean useAbility )
	{
		// Loop through all the keys in the keyring,
		// returns if any of the keys fit in the lock.

		final IItemHandler inventory = keyStack.getCapability( ForgeCapabilities.ITEM_HANDLER, null ).orElse( null );
		if( inventory != null )
			for( int i = 0; i < inventory.getSlots(); i++ )
			{
				final ItemStack key = inventory.getStackInSlot( i );
				if( !key.isEmpty() )
				{
					final IKey keyType = (IKey)key.getItem();
					if( keyType.unlock( key, lockStack, false ) )
						return true;
				}
			}

		return false;
	}

	@Override
	public InteractionResultHolder< ItemStack > use( Level level, Player player, InteractionHand hand )
	{
		final ItemStack itemStack = player.getItemInHand( hand );

		if( !player.isCrouching() )
			return new InteractionResultHolder<>( InteractionResult.PASS, itemStack );

		if( !level.isClientSide )
			NetworkHooks.openScreen( (ServerPlayer)player, BetterStorageItems.KEYRING.get(),
				buf -> buf.writeItem( player.getItemInHand( hand ) ).writeInt( player.getInventory().selected ) );
		return new InteractionResultHolder<>( InteractionResult.SUCCESS, itemStack );
	}
}
