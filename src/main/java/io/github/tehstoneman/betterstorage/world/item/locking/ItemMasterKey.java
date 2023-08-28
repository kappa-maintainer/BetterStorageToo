package io.github.tehstoneman.betterstorage.world.item.locking;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class ItemMasterKey extends KeyItem
{
	@Override
	public void onCraftedBy( ItemStack stack, Level worldIn, Player playerIn )
	{}

	@Override
	public boolean isFoil( ItemStack stack )
	{
		return true;
	}

	/*
	 * ====
	 * IKey
	 * ====
	 */

	@Override
	public boolean unlock( ItemStack key, ItemStack lock, boolean useAbility )
	{
		return true;
	}

	@Override
	public boolean canApplyEnchantment( ItemStack key, Enchantment enchantment )
	{
		return false;
	}

	@Override
	public boolean isEnchantable( ItemStack stack )
	{
		return false;
	}
}
