package io.github.tehstoneman.betterstorage.world.item.locking;

import io.github.tehstoneman.betterstorage.api.lock.IKeyLockable;
import io.github.tehstoneman.betterstorage.api.lock.ILock;
import io.github.tehstoneman.betterstorage.api.lock.KeyLockItem;
import io.github.tehstoneman.betterstorage.api.lock.LockInteraction;
import io.github.tehstoneman.betterstorage.world.item.enchantment.BetterStorageEnchantments;
import io.github.tehstoneman.betterstorage.world.item.enchantment.LockEnchantment;
import io.github.tehstoneman.betterstorage.world.level.block.BetterStorageBlocks;
import io.github.tehstoneman.betterstorage.world.level.block.entity.LockableDoorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class ItemLock extends KeyLockItem implements ILock
{
	public ItemLock()
	{
		super( new Properties() );// .tab( BetterStorage.CREATIVE_MODE_TAB ) );
	}

	/*
	 * @Override
	 * public boolean getIsRepairable( ItemStack stack, ItemStack material )
	 * {
	 * return material.getItem() == Items.GOLD_INGOT;
	 * }
	 *
	 * @Override
	 * public boolean isDamageable()
	 * {
	 * return true;
	 * }
	 */

	@Override
	public void applyEffects( ItemStack lock, IKeyLockable lockable, Player player, LockInteraction interaction )
	{
		final int shock = lock.getEnchantmentLevel( BetterStorageEnchantments.SHOCK.get() );

		if( shock > 0 )
		{
			//int damage = shock;
			//if( interaction == LockInteraction.PICK )
				//damage *= 3;
			//player.hurt( DamageSource.MAGIC, damage );
			final Level world = player.getCommandSenderWorld();
			world.playSound( (Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 0.5f,
				world.random.nextFloat() * 0.1F + 0.9F );
			if( shock >= 3 && interaction != LockInteraction.OPEN )
				player.setSecondsOnFire( 3 );
		}
	}

	@Override
	public boolean canApplyEnchantment( ItemStack lock, Enchantment enchantment )
	{
		return enchantment instanceof LockEnchantment;
	}

	/*
	 * =====
	 * ILock
	 * =====
	 */

	@Override
	public void onCraftedBy( ItemStack stack, Level world, Player player )
	{
		if( !world.isClientSide )
			ensureHasID( stack );
	}

	@Override
	public void onUnlock( ItemStack lock, ItemStack key, IKeyLockable lockable, Player player, boolean success )
	{
		if( success )
			return;
		// Power is 2 when a key was used to open the lock, 1 otherwise.
		applyEffects( lock, lockable, player, key.isEmpty() ? LockInteraction.OPEN : LockInteraction.PICK );
	}

	@Override
	@SuppressWarnings("null")
	public InteractionResult useOn( UseOnContext context )
	{
		final Player			playerIn	= context.getPlayer();
		final InteractionHand	hand		= context.getHand();
		final Level				worldIn		= context.getLevel();
		BlockPos				pos			= context.getClickedPos();
		final ItemStack			stack		= context.getItemInHand();

		if( hand == InteractionHand.MAIN_HAND )
		{
			BlockState	blockState	= worldIn.getBlockState( pos );
			Block		block		= blockState.getBlock();

			if( block == Blocks.IRON_DOOR )
			{
				if( blockState.getValue( DoorBlock.HALF ) == DoubleBlockHalf.UPPER )
				{
					pos			= pos.below();
					blockState	= worldIn.getBlockState( pos );
					block		= blockState.getBlock();
				}

				//@formatter:off
				worldIn.setBlockAndUpdate( pos, BetterStorageBlocks.LOCKABLE_DOOR.get().defaultBlockState()
						.setValue( DoorBlock.FACING, blockState.getValue( DoorBlock.FACING ) )
						.setValue( DoorBlock.OPEN, blockState.getValue( DoorBlock.OPEN ) )
						.setValue( DoorBlock.HINGE, blockState.getValue( DoorBlock.HINGE ) )
						.setValue( DoorBlock.HALF, DoubleBlockHalf.LOWER ) );
				worldIn.setBlockAndUpdate( pos.above(), BetterStorageBlocks.LOCKABLE_DOOR.get().defaultBlockState()
						.setValue( DoorBlock.FACING, blockState.getValue( DoorBlock.FACING ) )
						.setValue( DoorBlock.OPEN, blockState.getValue( DoorBlock.OPEN ) )
						.setValue( DoorBlock.HINGE, blockState.getValue( DoorBlock.HINGE ) )
						.setValue( DoorBlock.HALF, DoubleBlockHalf.UPPER ) );
				//@formatter:on

				final BlockEntity tileEntity = worldIn.getBlockEntity( pos );
				if( tileEntity instanceof final LockableDoorBlockEntity lockable && lockable.isLockValid( stack ) )
				{
					lockable.setLock( stack.copy() );
					if( !playerIn.isCreative() )
						playerIn.setItemInHand( hand, ItemStack.EMPTY );
					return InteractionResult.SUCCESS;
				}
			}

			final BlockEntity tileEntity = worldIn.getBlockEntity( pos );
			if( tileEntity instanceof final IKeyLockable lockable && lockable.isLockValid( stack ) && lockable.getLock().isEmpty() )
			{
				lockable.setLock( stack.copy() );
				if( !playerIn.isCreative() )
					playerIn.setItemInHand( hand, ItemStack.EMPTY );
				return InteractionResult.SUCCESS;
			}
		}
		return super.useOn( context );
	}
}
