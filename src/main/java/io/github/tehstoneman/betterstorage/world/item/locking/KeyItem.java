package io.github.tehstoneman.betterstorage.world.item.locking;

import java.util.UUID;

import io.github.tehstoneman.betterstorage.BetterStorage;
import io.github.tehstoneman.betterstorage.api.lock.IKey;
import io.github.tehstoneman.betterstorage.api.lock.IKeyLockable;
import io.github.tehstoneman.betterstorage.api.lock.ILock;
import io.github.tehstoneman.betterstorage.api.lock.KeyLockItem;
import io.github.tehstoneman.betterstorage.api.lock.LockInteraction;
import io.github.tehstoneman.betterstorage.world.item.enchantment.BetterStorageEnchantments;
import io.github.tehstoneman.betterstorage.world.item.enchantment.KeyEnchantment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class KeyItem extends KeyLockItem implements IKey
{
	public KeyItem()
	{
		super( new Properties() );// .tab( BetterStorage.CREATIVE_MODE_TAB ) );
	}

	@Override
	public boolean canApplyEnchantment( ItemStack itemStack, Enchantment enchantment )
	{
		return enchantment instanceof KeyEnchantment;
	}

	@Override
	public ItemStack getCraftingRemainingItem( ItemStack itemStack )
	{
		return itemStack;
	}

	@Override
	public int getEnchantmentValue()
	{
		return 20;
	}

	@Override
	public boolean isEnchantable( ItemStack itemStack )
	{
		return true;
	}

	@Override
	public void onCraftedBy( ItemStack itemStack, Level level, Player player )
	{
		if( !level.isClientSide )
			ensureHasID( itemStack );
	}

	@Override
	@SuppressWarnings("null")
	public boolean unlock( ItemStack keyStack, ItemStack lockStack, boolean useAbility )
	{
		if( lockStack.isEmpty() )
			return false;

		final ILock lockType = (ILock)lockStack.getItem();

		// If the lock type isn't normal, the key can't unlock it.
		if( lockType.getLockType() != "normal" )
			return false;

		final UUID	lockId	= getID( lockStack );
		final UUID	keyId	= getID( keyStack );

		// If the lock and key IDs match, return true.
		if( lockId.equals( keyId ) )
			return true;

		final int	lockSecurity	= lockStack.getEnchantmentLevel( BetterStorageEnchantments.SECURITY.get() );
		final int	unlocking		= keyStack.getEnchantmentLevel( BetterStorageEnchantments.UNLOCKING.get() );
		final int	lockpicking		= keyStack.getEnchantmentLevel( BetterStorageEnchantments.LOCKPICKING.get() );
		final int	morphing		= keyStack.getEnchantmentLevel( BetterStorageEnchantments.MORPHING.get() );

		final int	effectiveUnlocking		= Math.max( 0, unlocking - lockSecurity );
		final int	effectiveLockpicking	= Math.max( 0, lockpicking - lockSecurity );
		final int	effectiveMorphing		= Math.max( 0, morphing - lockSecurity );

		if( effectiveUnlocking > 0 )
		{
			final int roll = BetterStorage.RANDOM.nextInt( 5 );
			if( effectiveUnlocking > roll )
				return true;
		}
		if( effectiveLockpicking > 0 )
		{
			BetterStorageEnchantments.decEnchantment( keyStack, BetterStorageEnchantments.LOCKPICKING.get(), 1 );
			return true;
		}
		if( effectiveMorphing > 0 )
		{
			setID( keyStack, lockId );
			BetterStorageEnchantments.decEnchantment( keyStack, BetterStorageEnchantments.MORPHING.get(), morphing );
			return true;
		}

		return false;
	}

	@Override
	@SuppressWarnings("null")
	public InteractionResult useOn( UseOnContext context )
	{
		final Level level = context.getLevel();

		if( !level.isClientSide && context.getHand() == InteractionHand.MAIN_HAND )
		{
			final Player	player			= context.getPlayer();
			final ItemStack	heldItemStack	= player.getMainHandItem();
			BlockPos		blockPos		= context.getClickedPos();
			BlockEntity		blockEntity		= level.getBlockEntity( blockPos );

			if( blockEntity == null )
			{
				final BlockState blockState = level.getBlockState( blockPos );
				if( blockState.getProperties().contains( DoorBlock.HALF ) && blockState.getValue( DoorBlock.HALF ) == DoubleBlockHalf.UPPER )
				{
					blockPos	= blockPos.below();
					blockEntity	= level.getBlockEntity( blockPos );
				}
			}

			if( blockEntity instanceof final IKeyLockable lockable )
			{
				final Item lock = lockable.getLock().getItem();
				if( lock instanceof ILock )
				{
					if( unlock( heldItemStack, lockable.getLock(), false ) )
					{
						if( player.isCrouching() )
						{
							level.addFreshEntity(
								new ItemEntity( level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), lockable.getLock().copy() ) );
							lockable.setLock( ItemStack.EMPTY );
						}
						return InteractionResult.SUCCESS;
					}
					( (ILock)lock ).applyEffects( lockable.getLock(), lockable, player, LockInteraction.PICK );
				}
			}
		}
		return super.useOn( context );
	}
}
