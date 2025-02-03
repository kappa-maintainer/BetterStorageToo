package io.github.tehstoneman.betterstorage.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockLocker extends ItemBlock
{
	public ItemBlockLocker( Block block )
	{
		super( block );
	}

	@Override
	public EnumActionResult onItemUse( EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ )
	{
		final IBlockState iblockstate = worldIn.getBlockState( pos );
		final Block block = iblockstate.getBlock();

		if( !block.isReplaceable( worldIn, pos ) )
			pos = pos.offset( facing );

		final ItemStack itemstack = player.getHeldItem( hand );

		if( !itemstack.isEmpty() && player.canPlayerEdit( pos, facing, itemstack )
				&& worldIn.mayPlace( this.block, pos, false, facing, (Entity)null ) )
		{
			final EnumFacing enumfacing = EnumFacing.fromAngle( player.rotationYaw );
			final int i = enumfacing.getXOffset();
			final int j = enumfacing.getZOffset();
			final boolean flag = i < 0 && hitZ < 0.5F || i > 0 && hitZ > 0.5F || j < 0 && hitX > 0.5F || j > 0 && hitX < 0.5F;

			final int meta = this.getMetadata( itemstack.getMetadata() );
			IBlockState state = this.block.getStateForPlacement( worldIn, pos, facing, hitX, hitY, hitZ, meta, player, hand );
			state = state.withProperty( BlockDoor.HINGE, flag ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT );

			if( placeBlockAt( itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, state ) )
			{
				final SoundType soundtype = worldIn.getBlockState( pos ).getBlock().getSoundType( worldIn.getBlockState( pos ), worldIn, pos,
						player );
				worldIn.playSound( player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, ( soundtype.getVolume() + 1.0F ) / 2.0F,
						soundtype.getPitch() * 0.8F );
				itemstack.shrink( 1 );
			}

			return EnumActionResult.SUCCESS;
		}
		else
			return EnumActionResult.FAIL;
	}
}
