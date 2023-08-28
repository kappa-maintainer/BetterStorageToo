package io.github.tehstoneman.betterstorage.client.color;

import javax.annotation.Nullable;

import io.github.tehstoneman.betterstorage.world.level.block.entity.CardboardBoxBlockEntity;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CardboardColor implements ItemColor, BlockColor
{
	private static int CARDBOARD_COLOR = 0xA08060;

	@Override
	public int getColor( BlockState state, @Nullable BlockAndTintGetter reader, @Nullable BlockPos pos, int tint )
	{
		if( reader != null )
		{
			final BlockEntity tileEntity = reader.getBlockEntity( pos );
			if( tileEntity instanceof CardboardBoxBlockEntity )
				return ( (CardboardBoxBlockEntity)tileEntity ).getColor();
		}
		return CARDBOARD_COLOR;
	}

	@Override
	public int getColor( ItemStack stack, int tintIndex )
	{
		if( stack.hasTag() )
		{
			final CompoundTag tag = stack.getTag();
			if( tag != null && tag.contains( "Color" ) )
				return tag.getInt( "Color" );
		}
		return CARDBOARD_COLOR;
	}
}
