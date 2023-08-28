package io.github.tehstoneman.betterstorage.world.item.enchantment;

import java.util.Map;

import io.github.tehstoneman.betterstorage.ModInfo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BetterStorageEnchantments
{
	public static final DeferredRegister< Enchantment > REGISTERY = DeferredRegister.create( ForgeRegistries.ENCHANTMENTS, ModInfo.MOD_ID );

	public static final RegistryObject< KeyEnchantment >	UNLOCKING	= REGISTERY.register( "unlocking",
		() -> new KeyEnchantment( Rarity.COMMON, 5, 5, 10, 30, 0 ) );
	public static final RegistryObject< KeyEnchantment >	LOCKPICKING	= REGISTERY.register( "lockpicking",
		() -> new KeyEnchantment( Rarity.COMMON, 5, 5, 8, 30, 0 ) );
	public static final RegistryObject< KeyEnchantment >	MORPHING	= REGISTERY.register( "morphing",
		() -> new KeyEnchantment( Rarity.COMMON, 5, 10, 12, 30, 0 ) );

	public static final RegistryObject< LockEnchantment >	PERSISTANCE	= REGISTERY.register( "persistance",
		() -> new LockEnchantment( Rarity.COMMON, 5, 1, 8, 30, 0 ) );
	public static final RegistryObject< LockEnchantment >	SECURITY	= REGISTERY.register( "security",
		() -> new LockEnchantment( Rarity.COMMON, 5, 1, 8, 30, 0 ) );
	public static final RegistryObject< LockEnchantment >	SHOCK		= REGISTERY.register( "shock",
		() -> new LockEnchantment( Rarity.COMMON, 5, 1, 8, 30, 0 ) );
	public static final RegistryObject< LockEnchantment >	TRIGGER		= REGISTERY.register( "trigger",
		() -> new LockEnchantment( Rarity.COMMON, 1, 1, 8, 30, 0 ) );

	public static void decEnchantment( ItemStack stack, Enchantment ench, int level )
	{
		if( stack.isEmpty() )
			return;

		final Map< Enchantment, Integer > list = EnchantmentHelper.getEnchantments( stack );

		final int newLevel = list.getOrDefault( ench, 0 ) - level;

		if( newLevel <= 0 )
			list.remove( ench );
		else
			list.put( ench, newLevel );

		EnchantmentHelper.setEnchantments( list, stack );
	}

	public static int getLevel( ItemStack stack, Enchantment enchantment )
	{
		if( stack.isEnchanted() )
		{
			final Map< Enchantment, Integer > enchantments = EnchantmentHelper.getEnchantments( stack );
			return enchantments.getOrDefault( enchantment, 0 );
		}
		return 0;
	}
}
