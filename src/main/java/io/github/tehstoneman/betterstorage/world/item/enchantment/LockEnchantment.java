package io.github.tehstoneman.betterstorage.world.item.enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.tehstoneman.betterstorage.api.lock.ILock;
import io.github.tehstoneman.betterstorage.world.item.locking.ItemLock;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class LockEnchantment extends Enchantment
{
	public final static EnchantmentCategory	LOCK			= EnchantmentCategory.create( "lock", ( item ) -> ( item instanceof ItemLock ) );

	private final int						maxLevel;
	private final int						minBase, minScaling;
	private final int						maxBase, maxScaling;

	private List< Enchantment >				incompatible	= new ArrayList<>( 0 );

	public LockEnchantment( Rarity rarityIn, int maxLevel, int minBase, int minScaling, int maxBase, int maxScaling )
	{
		super( rarityIn, LOCK, new EquipmentSlot[] { EquipmentSlot.MAINHAND } );

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
		return stack.getItem() instanceof ILock ? true : super.canEnchant( stack );
	}

	@Override
	public boolean canApplyAtEnchantingTable( ItemStack stack )
	{
		if( stack.getItem() instanceof ILock )
		{
			final ILock lock = (ILock)stack.getItem();
			return lock.canApplyEnchantment( stack, this );
		}
		return stack.getItem() instanceof ILock;
	}
}
