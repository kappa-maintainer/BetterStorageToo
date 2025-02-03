package io.github.tehstoneman.betterstorage.common.block;

import io.github.tehstoneman.betterstorage.attachment.IHasAttachments;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityContainer;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockContainerBetterStorage extends BlockBetterStorage implements ITileEntityProvider
{
	protected BlockContainerBetterStorage( String name, Material material )
	{
		super( name, material );
		//isBlockContainer = true;
	}

	@Override
	public boolean hasTileEntity( IBlockState state )
	{
		return true;
	}

	@Override
	public boolean eventReceived( IBlockState state, World worldIn, BlockPos pos, int id, int param )
	{
		final TileEntity te = worldIn.getTileEntity( pos );
		return te != null ? te.receiveClientEvent( id, param ) : false;
	}

	// Pass actions to TileEntityContainer
	@Override
	public void onBlockPlacedBy( World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack )
	{
		final TileEntity tileEntity = worldIn.getTileEntity( pos );
		if( tileEntity instanceof TileEntityContainer )
			( (TileEntityContainer)tileEntity ).onBlockPlaced( placer, stack );
	}

	@Override
	public void breakBlock( World worldIn, BlockPos pos, IBlockState state )
	{
		final TileEntity tileEntity = worldIn.getTileEntity( pos );
		if( tileEntity instanceof TileEntityContainer )
			( (TileEntityContainer)tileEntity ).onBlockDestroyed();
		super.breakBlock( worldIn, pos, state );
	}

	@Override
	public boolean onBlockActivated( World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side,
			float hitX, float hitY, float hitZ )
	{
		final TileEntity tileEntity = worldIn.getTileEntity( pos );
		if( tileEntity instanceof TileEntityContainer )
			return ( (TileEntityContainer)tileEntity ).onBlockActivated( pos, state, playerIn, hand, side, hitX, hitY, hitZ );

		return false;
	}

	@Override
	public boolean removedByPlayer( IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest )
	{
		final TileEntity tileEntity = world.getTileEntity( pos );
		if( tileEntity instanceof TileEntityContainer )
			if( !( (TileEntityContainer)tileEntity ).onBlockBreak( player ) )
				return false;
		return super.removedByPlayer( state, world, pos, player, willHarvest );
	}

	@Override
	public void onPlayerDestroy( World worldIn, BlockPos pos, IBlockState state )
	{
		final TileEntity tileEntity = worldIn.getTileEntity( pos );
		if( tileEntity instanceof TileEntityContainer )
			( (TileEntityContainer)tileEntity ).onBlockDestroyed();
	}

	@Override
	public ItemStack getPickBlock( IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player )
	{
		final TileEntity tileEntity = world.getTileEntity( pos );
		if( tileEntity instanceof TileEntityContainer )
		{
			final TileEntityContainer container = (TileEntityContainer)tileEntity;
			if( container instanceof IHasAttachments )
			{
				final ItemStack pick = ( (IHasAttachments)container ).getAttachments().pick( target );
				if( pick != null )
					return pick;
			}
			final ItemStack pick = super.getPickBlock( state, target, world, pos, player );
			return container.onPickBlock( pick, target );
		}
		return super.getPickBlock( state, target, world, pos, player );
	}

	@Override
	public boolean hasComparatorInputOverride( IBlockState state )
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride( IBlockState blockState, World worldIn, BlockPos pos )
	{
		final TileEntity tileEntity = worldIn.getTileEntity( pos );
		if( !( tileEntity instanceof TileEntityContainer ) )
			return 0;

		final TileEntityContainer tileContainer = (TileEntityContainer)tileEntity;
		return tileContainer.getComparatorSignalStrength();
	}

	@Override
	public void onNeighborChange( IBlockAccess world, BlockPos pos, BlockPos neighbor )
	{
		final TileEntity tileEntity = world.getTileEntity( neighbor );
		if( tileEntity instanceof TileEntityContainer )
			( (TileEntityContainer)tileEntity ).onNeighborUpdate( world.getBlockState( neighbor ).getBlock() );
	}
}
