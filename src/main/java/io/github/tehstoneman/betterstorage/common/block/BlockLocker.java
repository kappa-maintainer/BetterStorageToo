package io.github.tehstoneman.betterstorage.common.block;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.api.EnumReinforced;
import io.github.tehstoneman.betterstorage.common.item.ItemBlockLocker;
import io.github.tehstoneman.betterstorage.common.item.ItemBlockReinforcedChest;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityConnectable;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityLocker;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityReinforcedChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLocker extends BlockLockable
{
	public BlockLocker(Material material)
	{
		super( "locker", material );

		setHardness( 2.5f );

		setHarvestLevel( "axe", 0 );
		setSoundType( SoundType.WOOD );

		//@formatter:off
		setDefaultState( blockState.getBaseState().withProperty( BlockHorizontal.FACING, EnumFacing.NORTH )
												  .withProperty( Properties.StaticProperty, true )
												  .withProperty( BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT )
												  .withProperty( BlockLockable.CONNECTED, false ) );
		//@formatter:on
	}

	public BlockLocker()
	{
		this( Material.WOOD );
	}

	@Override
	public boolean isOpaqueCube( IBlockState state )
	{
		return true;
	}

	@Override
	public boolean isFullCube( IBlockState state )
	{
		return true;
	}

	@Override
	@SideOnly( Side.CLIENT )
	public EnumBlockRenderType getRenderType( IBlockState state )
	{
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockStateContainer createBlockState()
	{
		return new BlockStateContainer( this, new IProperty[] { BlockHorizontal.FACING, Properties.StaticProperty, BlockDoor.HINGE, CONNECTED } );
	}

	@Override
	public IBlockState getStateFromMeta( int meta )
	{
		EnumFacing enumfacing = EnumFacing.byIndex( meta & 7 );

		if( enumfacing.getAxis() == EnumFacing.Axis.Y )
			enumfacing = EnumFacing.NORTH;
		
		boolean mirrored = (meta & 8) > 0;
		

		return getDefaultState().withProperty( BlockHorizontal.FACING, enumfacing ).withProperty(BlockDoor.HINGE, mirrored ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT);
	}

	@Override
	public int getMetaFromState( IBlockState state )
	{
		int meta = state.getValue( BlockHorizontal.FACING ).getIndex();
        if (state.getValue(BlockDoor.HINGE) == BlockDoor.EnumHingePosition.RIGHT)
        {
        	meta |= 8;
        }
		return meta;
	}

	@Override
	public IBlockState getActualState( IBlockState state, IBlockAccess worldIn, BlockPos pos )
	{
		final TileEntity tileentity = worldIn instanceof ChunkCache ? ( (ChunkCache)worldIn ).getTileEntity( pos, Chunk.EnumCreateEntityType.CHECK )
				: worldIn.getTileEntity( pos );

		if( tileentity instanceof TileEntityLocker )
		{
			final TileEntityLocker locker = (TileEntityLocker)tileentity;
		}

		return state.withProperty( Properties.StaticProperty, true );
	}

	@Override
	public void onBlockPlacedBy( World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack )
	{
		worldIn.setBlockState( pos, state.withProperty( BlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite() ), 2 );

		final TileEntity tileentity = worldIn.getTileEntity( pos );

		if( tileentity instanceof TileEntityLocker )
		{
			final TileEntityLocker locker = (TileEntityLocker)tileentity;
			//if( stack.hasDisplayName() ) locker.setCustomInventoryName( stack.getDisplayName() );
		}

		if( tileentity instanceof TileEntityConnectable )
			( (TileEntityConnectable)tileentity ).onBlockPlaced( placer, stack );
		else
			super.onBlockPlacedBy( worldIn, pos, state, placer, stack );
	}

	@Override
	public TileEntity createNewTileEntity( World world, int metadata )
	{
		return new TileEntityLocker();
	}

	@Override
	public boolean onBlockActivated( World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX,
			float hitY, float hitZ )
	{
		EnumFacing facing = state.getValue( BlockHorizontal.FACING );
		if( facing == side)
		{
			if( !world.isRemote )
			{
				final TileEntity tileentity = world.getTileEntity( pos );
				if( tileentity instanceof TileEntityLocker )
				{
					final TileEntityLocker locker = (TileEntityLocker)tileentity;
					return locker.onBlockActivated( pos, state, player, hand, side, hitX, hitY, hitZ );
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean hasComparatorInputOverride( IBlockState state )
	{
		return true;
	}

	@Override
	protected ItemBlock getItemBlock()
	{
		return new ItemBlockLocker( this );
	}
}
