package io.github.tehstoneman.betterstorage.api;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

public final class BetterStorageEnchantment
{
	public static Map< String, EnumEnchantmentType > enchantmentTypes = new HashMap<>();
	// public static Map< String, Enchantment > enchantments = new HashMap<>();

	private BetterStorageEnchantment()
	{}

	/**
	 * Returns a BetterStorage enchantment type with that name. <br>
	 * <b>Types:</b> key, lock
	 */
	public static EnumEnchantmentType getType( String name )
	{
		return enchantmentTypes.get( name );
	}

	/**
	 * Returns a BetterStorage enchantment with that name. <br>
	 * <b>Key:</b> unlocking, lockpicking, morphing. <br>
	 * <b>Lock:</b> persistance, security, shock, trigger.
	 */
	/*
	 * public static Enchantment get( String name )
	 * {
	 * return enchantments.get( name );
	 * }
	 */

	/** Helper method to get the level of that enchantment on the item. */
	public static int getLevel( ItemStack stack, Enchantment enchantment )
	{
		if( stack.isItemEnchanted() )
		{
			final Map< Enchantment, Integer > enchantments = EnchantmentHelper.getEnchantments( stack );
			return enchantments.getOrDefault( enchantment, 0 );
		}
		return 0;
	}

	/** Helper method to decrease the level of an enchantment on the item. */
	public static void decEnchantment( ItemStack stack, Enchantment ench, int level )
	{
		if( stack.getTagCompound() == null )
			return;

		if( !stack.getTagCompound().hasKey( "ench", 9 ) )
			return;

		final NBTTagList list = stack.getTagCompound().getTagList( "ench", 10 );
		final int enchID = Enchantment.getEnchantmentID( ench );
		int count = -1;
		for( int i = 0; i < list.tagCount(); i++ )
			if( list.getCompoundTagAt( i ).getShort( "id" ) == enchID )
				count = i;
		if( count >= 0 )
		{
			final int newLevel = list.getCompoundTagAt( count ).getShort( "lvl" ) - level;
			if( newLevel <= 0 )
			{
				list.removeTag( count );
				if( list.isEmpty() )
					stack.getTagCompound().removeTag( "ench" );
			}
			else
				list.getCompoundTagAt( count ).setShort( "lvl", (byte)level );
		}
	}
}
