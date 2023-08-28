package io.github.tehstoneman.betterstorage.world.item.cardboard;

import io.github.tehstoneman.betterstorage.world.item.BetterStorageItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CardboardSheetItem extends BetterStorageItem
{
	public CardboardSheetItem()
	{
		super( new Item.Properties().stacksTo( 8 ) );
		// toolMaterial.setRepairItem( new ItemStack( this ) );
		// armorMaterial.setRepairItem( new ItemStack( this ) );
	}

	public static boolean isEffective( ItemStack stack )
	{
		return stack.getMaxDamage() == 0 || stack.getDamageValue() < stack.getMaxDamage();
	}

	public static boolean canHarvestBlock( ItemStack stack, boolean canHarvestDefault )
	{
		return isEffective( stack ) ? canHarvestDefault : false;
	}

	/*
	 * public static boolean damageItem( ItemStack stack, int damage, EntityLivingBase entity )
	 * {
	 * if( !isEffective( stack ) )
	 * return true;
	 *
	 * final Map< Enchantment, Integer > enchants = EnchantmentHelper.getEnchantments( stack );
	 * final int numEnchants = enchants.size();
	 * int numLevelTotal = 0;
	 * for( final int enchLevel : enchants.values() )
	 * numLevelTotal += enchLevel;
	 *
	 * double changeForNoDamage = -1 / Math.pow( numLevelTotal / 10 + 1, 2 ) + 1;
	 * changeForNoDamage = numEnchants / 10 + changeForNoDamage * ( 1 - numEnchants / 10 );
	 *
	 * if( entity.world.rand.nextDouble() >= changeForNoDamage )
	 * stack.damageItem( 1, entity );
	 *
	 * if( !isEffective( stack ) )
	 * {
	 * entity.renderBrokenItemStack( stack );
	 * stack.setDamage( stack.getMaxDamage() );
	 * stack.setCount( 1 );
	 * }
	 *
	 * return true;
	 * }
	 */

	/*
	 * @Override
	 * public boolean onBlockDestroyed( ItemStack stack, Level world, IBlockState block, BlockPos pos, EntityLivingBase entity )
	 * {
	 * return block.getBlockHardness( world, pos ) > 0 ? ItemCardboardSheet.damageItem( stack, 1, entity ) : true;
	 * }
	 */
}
