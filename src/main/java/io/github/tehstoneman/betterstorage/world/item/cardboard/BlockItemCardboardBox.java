package io.github.tehstoneman.betterstorage.world.item.cardboard;

import java.util.List;

import javax.annotation.Nullable;

import io.github.tehstoneman.betterstorage.api.cardboard.ICardboardItem;
import io.github.tehstoneman.betterstorage.config.BetterStorageConfig;
import io.github.tehstoneman.betterstorage.world.item.BetterStorageBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.ItemStackHandler;

public class BlockItemCardboardBox extends BetterStorageBlockItem implements ICardboardItem
{
	//public BlockItemCardboardBox()
	//{
		//super( BetterStorageBlocks.CARDBOARD_BOX.get(), new Item.Properties().stacksTo( 1 ) );
	//}

	// Damage bar

	public BlockItemCardboardBox(Block block) {
		super(block);
		//TODO Auto-generated constructor stub
	}

	/**
	 * Returns how many times cardboard boxes can be reused.
	 *
	 * @return Total number of uses.
	 */
	public static int getMaxUses()
	{
		return BetterStorageConfig.COMMON.cardboardBoxUses.get();
	}

	@Override
	@SuppressWarnings("null")
	public void appendHoverText( ItemStack stack, @Nullable Level worldIn, List< Component > tooltip, TooltipFlag flagIn )
	{
		super.appendHoverText( stack, worldIn, tooltip, flagIn );
		if( stack.hasTag() )
		{
			if( flagIn.isAdvanced() )
			{
				final int		maxUses		= getMaxUses();
				final boolean	hasItems	= stack.hasTag() && stack.getTag().contains( "Inventory" );

				if( !hasItems )
					tooltip.add( Component.translatable( "tooltip.betterstorage.cardboard_box.use_hint" + ( maxUses > 0 ? ".reusable" : 0 ) ) );

				if( maxUses > 1 )
				{
					int uses = maxUses;
					if( stack.getTag().contains( "Uses" ) )
						uses = Math.min( maxUses, stack.getTag().getInt( "Uses" ) );
					tooltip.add( Component.translatable( "tooltip.betterstorage.cardboard_box.uses", uses ).withStyle( ChatFormatting.ITALIC,
						ChatFormatting.DARK_GRAY ) );
				}
			}
			if( BetterStorageConfig.CLIENT.cardboardBoxShowContents.get() && stack.hasTag() && stack.getTag().contains( "Inventory" ) )
			{
				final ItemStackHandler contents = new ItemStackHandler( 9 );
				contents.deserializeNBT( (CompoundTag)stack.getTag().get( "Inventory" ) );

				final int	limit	= 4;// flagIn.isAdvanced() || GuiScreen.isShiftKeyDown() ? 6 : 3;
				int			count	= 0;
				int			more	= 0;

				for( int i = 0; i < contents.getSlots(); i++ )
				{
					final ItemStack contentStack = contents.getStackInSlot( i );
					if( !contentStack.isEmpty() )
						if( count < limit )
						{
							count++;
							final MutableComponent text = contentStack.getDisplayName().copy();
							text.append( " x" ).append( String.valueOf( contentStack.getCount() ) );
							tooltip.add( text );
						} else
							more++;
				}
				if( more > 0 )
					tooltip.add( Component.translatable( "tooltip.betterstorage.cardboard_box.plus_more", more ).withStyle( ChatFormatting.ITALIC ) );
			}
		}
	}

	@Override
	public int getBarColor( ItemStack itemStack )
	{
		final float	f	= getUsePercent( itemStack );
		final float	r	= Math.min( 2f - f * 2f, 1f );
		final float	g	= Math.min( f * 2f, 1f );
		return Mth.color( r, g, 0f );
	}

	@Override
	public int getBarWidth( ItemStack itemStack )
	{
		return Math.round( getUsePercent( itemStack ) * MAX_BAR_WIDTH );
	}

	@Override
	public boolean isBarVisible( ItemStack itemStack )
	{
		return getUses( itemStack ) < getMaxUses();
	}

	// Hover text

	private float getUsePercent( ItemStack itemStack )
	{
		final int uses = getUses( itemStack );
		return (float)uses / (float)getMaxUses();
	}

	@SuppressWarnings("null")
	private int getUses( ItemStack itemStack )
	{
		final int	maxUses	= getMaxUses();
		int			uses	= maxUses;
		if( itemStack.hasTag() )
		{
			final CompoundTag nbt = itemStack.getTag();
			if( nbt.contains( "Uses" ) )
				uses = Math.min( maxUses, nbt.getInt( "Uses" ) );
		}
		return uses;
	}
}
