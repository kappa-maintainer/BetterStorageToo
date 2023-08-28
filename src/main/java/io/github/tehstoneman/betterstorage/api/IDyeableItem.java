package io.github.tehstoneman.betterstorage.api;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Interface used to describe an item that can be dyed.
 *
 * @author TehStoneMan
 */
public interface IDyeableItem
{
	/**
	 * Returns if the {@link ItemStack} can be dyed.
	 *
	 * @param itemStack
	 *            The {@link ItemStack} to check.
	 * @return True if this {@link ItemStack} can be dyed.
	 */
	default boolean canDye( ItemStack itemStack )
	{
		return true;
	}

	/**
	 * Check if the {@link ItemStack} has a color.
	 *
	 * @param itemStack
	 *            The {@link ItemStack} to check
	 * @return True of the {@link ItemStack} has a color applied.
	 */
	default boolean hasColor( ItemStack itemStack )
	{
		if( itemStack.hasTag() )
		{
			final CompoundTag compound = itemStack.getTag();
			if( compound != null) return compound.contains( "Color" );
		}
		return false;
	}

	/**
	 * Get the color of the {@link ItemStack}.
	 *
	 * @param itemStack
	 *            The {@link ItemStack} to get the color for.
	 * @return The color expressed as an int.
	 */
	default int getColor( ItemStack itemStack )
	{
		if( hasColor( itemStack ) )
		{
			final CompoundTag compound = itemStack.getTag();
			if( compound != null)  return compound.getInt( "Color" );
		}
		return getDefaultColor();
	}

	/**
	 * Get the default color when no dye is applied
	 *
	 * @return The color expressed as an int.
	 */
	int getDefaultColor();

	/**
	 * Set the color of an {@link ItemStack}.
	 *
	 * @param itemStack
	 *            The {@link ItemStack} to apply the color to.
	 * @param colorRGB
	 *            The color to apply expressed as an int.
	 */
	default void setColor( ItemStack itemStack, int colorRGB )
	{
		final CompoundTag compound = itemStack.getOrCreateTag();
		compound.putInt( "Color", colorRGB );
		itemStack.setTag( compound );
	}

	/**
	 * Dyes the {@link ItemStack} with the given list of dyes.
	 *
	 * @param itemStack
	 *            The {@link ItemStack} to dye.
	 * @param dyeList
	 *            A list of {@link DyeColor}s to apply.
	 * @return an {@link ItemStack} with the applied dyes.
	 */
	static ItemStack dyeItem( ItemStack itemStack, List< DyeColor > dyeList )
	{
		ItemStack resultStack = ItemStack.EMPTY;

		final int[]  aInt        = new int[3];
		int          i           = 0;
		int          count       = 0;
		IDyeableItem dyeableItem = null;
		final Item   item        = itemStack.getItem();

		if( item instanceof IDyeableItem )
		{
			dyeableItem      = (IDyeableItem)item;
			resultStack = itemStack.copy();
			resultStack.setCount( 1 );

			if( dyeableItem.hasColor( itemStack ) )
			{
				final int   color = dyeableItem.getColor( resultStack );
				final float r     = ( color >> 16 & 255 ) / 255.0F;
				final float g     = ( color >> 8 & 255 ) / 255.0F;
				final float b     = ( color & 255 ) / 255.0F;
				i       = (int)( i + Math.max( r, Math.max( g, b ) ) * 255.0F );
				aInt[0] = (int)( aInt[0] + r * 255.0F );
				aInt[1] = (int)( aInt[1] + g * 255.0F );
				aInt[2] = (int)( aInt[2] + b * 255.0F );
				++count;
			}

			for( final DyeColor dyeItem : dyeList )
			{
				final float[] afloat = dyeItem.getTextureDiffuseColors();
				final int     r      = (int)( afloat[0] * 255.0F );
				final int     g      = (int)( afloat[1] * 255.0F );
				final int     b      = (int)( afloat[2] * 255.0F );
				i       += Math.max( r, Math.max( g, b ) );
				aInt[0] += r;
				aInt[1] += g;
				aInt[2] += b;
				++count;
			}
		}

		if( dyeableItem == null )
			return ItemStack.EMPTY;
		int         r  = aInt[0] / count;
		int         g  = aInt[1] / count;
		int         b  = aInt[2] / count;
		final float f3 = (float)i / (float)count;
		final float f4 = Math.max( r, Math.max( g, b ) );
		r = (int)( r * f3 / f4 );
		g = (int)( g * f3 / f4 );
		b = (int)( b * f3 / f4 );
		int j2 = ( r << 8 ) + g;
		j2 = ( j2 << 8 ) + b;
		dyeableItem.setColor( resultStack, j2 );
		return resultStack;
	}
}
