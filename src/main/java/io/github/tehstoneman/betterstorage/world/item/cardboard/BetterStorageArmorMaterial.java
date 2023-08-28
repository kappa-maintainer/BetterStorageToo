package io.github.tehstoneman.betterstorage.world.item.cardboard;

import java.util.function.Supplier;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.world.item.BetterStorageItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public enum BetterStorageArmorMaterial implements ArmorMaterial
{
	CARDBOARD( "cardboard", 3, new int[]
	{ 1, 2, 3, 1 }, 12, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, () -> Ingredient.of( BetterStorageItems.CARDBOARD_SHEET.get() ) ),;

	private static final int[]	MAX_DAMAGE_ARRAY	= { 13, 15, 16, 11 };
	private String				name;
	private int					maxDamageFactor;
	private int[]				damageReductionAmountArray;
	private int					enchantability;
	private SoundEvent			soundEvent;
	private float				toughness;
	// private LazyLoadBase< Ingredient > repairMaterial;

	BetterStorageArmorMaterial( final String name, final int maxDamageFactor, final int[] damageReductionAmountArray, final int enchantability,
								final SoundEvent soundEvent, final float toughness, final Supplier< Ingredient > repairMaterial )
	{
		this.name						= new ResourceLocation( ModInfo.MOD_ID, name ).toString();
		this.maxDamageFactor			= maxDamageFactor;
		this.damageReductionAmountArray	= damageReductionAmountArray;
		this.enchantability				= enchantability;
		this.soundEvent					= soundEvent;
		this.toughness					= toughness;
		// this.repairMaterial = new LazyLoadBase<>( repairMaterial );
	}

	public int getDefenseForSlot( EquipmentSlot slotIn )
	{
		return damageReductionAmountArray[slotIn.getIndex()];
	}

	public int getDurabilityForSlot( EquipmentSlot slotIn )
	{
		return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * maxDamageFactor;
	}

	@Override
	public int getEnchantmentValue()
	{
		return enchantability;
	}

	@Override
	public SoundEvent getEquipSound()
	{
		return soundEvent;
	}

	// Knockback resistance
	@Override
	public float getKnockbackResistance()
	{
		return 0;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public Ingredient getRepairIngredient()
	{
		// return repairMaterial.getValue();
		return null;
	}

	@Override
	public float getToughness()
	{
		return toughness;
	}

	@Override
	public int getDurabilityForType(Type p_266807_) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getDurabilityForType'");
	}

	@Override
	public int getDefenseForType(Type p_267168_) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getDefenseForType'");
	}

}
