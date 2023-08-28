package io.github.tehstoneman.betterstorage.world.item.enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.tehstoneman.betterstorage.api.lock.IKey;
import io.github.tehstoneman.betterstorage.world.item.BetterStorageItems;
import io.github.tehstoneman.betterstorage.world.item.locking.KeyItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class KeyEnchantment extends Enchantment
{
	public final static EnchantmentCategory	KEY				= EnchantmentCategory.create( "key", ( item ) -> ( item instanceof KeyItem ) );

	private final int					maxLevel;
	private final int					minBase, minScaling;
	private final int					maxBase, maxScaling;

	private List< Enchantment >			incompatible	= new ArrayList<>( 0 );

	public KeyEnchantment( Rarity rarityIn, int maxLevel, int minBase, int minScaling, int maxBase, int maxScaling )
	{
		super( rarityIn, KEY, new EquipmentSlot[] { EquipmentSlot.MAINHAND } );

		this.maxLevel = maxLevel;
		this.minBase = minBase;
		this.minScaling = minScaling;
		this.maxBase = maxBase;
		this.maxScaling = maxScaling;
	}

	public void setIncompatible( Enchantment... incompatible )
	{
		this.incompatible = Arrays.asList( incompatible );
	}

	@Override
	public int getMaxLevel()
	{
		return maxLevel;
	}

	@Override
	public int getMinCost( int level )
	{
		return minBase + ( level - 1 ) * minScaling;
	}

	@Override
	public int getMaxCost( int level )
	{
		return getMinCost( level ) + maxBase + ( level - 1 ) * maxScaling;
	}

	@Override
	public boolean checkCompatibility( Enchantment other )
	{
		return super.checkCompatibility( other ) && !incompatible.contains( other );
	}

	@Override
	public boolean canEnchant( ItemStack stack )
	{
		return stack.getItem() instanceof IKey ? true : super.canEnchant( stack );
	}

	@Override
	public boolean canApplyAtEnchantingTable( ItemStack stack )
	{
		if( stack.getItem() instanceof IKey )
		{
			final IKey key = (IKey)stack.getItem();
			return key.canApplyEnchantment( stack, this );
		}
		return stack.getItem() != BetterStorageItems.MASTER_KEY.get() && stack.getItem() instanceof IKey;
	}

	@Override
	public boolean isTreasureOnly()
	{
		return super.isTreasureOnly();
	}
}
