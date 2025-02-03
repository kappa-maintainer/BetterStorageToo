package io.github.tehstoneman.betterstorage.common.block;

import java.util.Random;
import java.util.logging.Logger;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.api.BetterStorageAPI;
import io.github.tehstoneman.betterstorage.api.BetterStorageEnchantment;
import io.github.tehstoneman.betterstorage.api.EnumReinforced;
import io.github.tehstoneman.betterstorage.common.enchantment.EnchantmentBetterStorage;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityConnectable;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityLockable;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.Properties;

public abstract class BlockLockable extends BlockContainerBetterStorage
{
	public static final PropertyEnum	MATERIAL	= PropertyEnum.create( "material", EnumReinforced.class );
	public static final PropertyBool	CONNECTED	= PropertyBool.create( "connected" );

	protected BlockLockable( String name, Material material )
	{
		super( name, material );
	}

	@Override
	public void getSubBlocks( CreativeTabs tab, NonNullList< ItemStack > list )
	{
		final BlockStateContainer container = getBlockState();
		if( container.getProperties().contains( MATERIAL ) )
			for( final EnumReinforced material : EnumReinforced.values() )
			{
				final ItemStack itemstack = new ItemStack( this, 1, material.getMetadata() );
				list.add( itemstack );
			}
		else
			super.getSubBlocks( tab, list );
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer( this, new IProperty[] { BlockHorizontal.FACING, MATERIAL, Properties.StaticProperty, CONNECTED } );
	}

	/*@Override
	public EnumBlockRenderType getRenderType( IBlockState state )
	{
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}*/
	
	@Override
	public int quantityDropped( Random rand )
	{
		return 0;
	}

	@Override
	public IBlockState getStateFromMeta( int meta )
	{
		final EnumFacing facing = EnumFacing.byIndex( ( meta & 3 ) + 2 );
		final boolean hidden = ( meta & 4 ) != 0;
		return getDefaultState().withProperty( BlockHorizontal.FACING, facing );
	}

	@Override
	public int getMetaFromState( IBlockState state )
	{
		final int meta = state.getValue( BlockHorizontal.FACING ).getIndex() - 2 & 3;
		return meta;
	}

	@Override
	public IBlockState getActualState( IBlockState state, IBlockAccess worldIn, BlockPos pos )
	{
		final TileEntity tileEntity = worldIn.getTileEntity( pos );
		if( tileEntity instanceof TileEntityLockable )
		{
			final TileEntityLockable lockable = (TileEntityLockable)tileEntity;
			if( lockable.getMaterial() != null )
				state = state.withProperty( MATERIAL, lockable.getMaterial() );
			state = state.withProperty( CONNECTED, lockable.isConnected() );
		}
		return state.withProperty( Properties.StaticProperty, true );
	}

	@Override
	public void onBlockPlacedBy( World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack )
	{
		worldIn.setBlockState( pos, state.withProperty( BlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite() ), 2 );

		final TileEntity tileentity = worldIn.getTileEntity( pos );

		if( tileentity instanceof TileEntityLockable )
		{
			final TileEntityLockable lockable = (TileEntityLockable)tileentity;
			lockable.setMaterial( BetterStorageAPI.materials.get( stack ) );
		}

		if( tileentity instanceof TileEntityConnectable )
			( (TileEntityConnectable)tileentity ).onBlockPlaced( placer, stack );
		else
			super.onBlockPlacedBy( worldIn, pos, state, placer, stack );
	}

	/*
	 * @Override
	 * public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
	 * if (hasMaterial() && !player.capabilities.isCreativeMode)
	 * dropBlockAsItem(world, pos, WorldUtils.get(world, pos.getX(), pos.getY(), pos.getZ(), TileEntityLockable.class).material.setMaterial(new ItemStack(this,
	 * 1, 0)), 0);
	 * return super.removedByPlayer(world, player, x, y, z);
	 * }
	 */

	/*
	 * @Override
	 * public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
	 * if (hasMaterial())
	 * dropBlockAsItem(world, x, y, z, WorldUtils.get(world, x, y, z, TileEntityLockable.class).material.setMaterial(new ItemStack(this, 1, 0)));
	 * super.onBlockExploded(world, x, y, z, explosion);
	 * }
	 */

	/*
	 * @Override
	 * public int quantityDropped(int meta, int fortune, Random random) { return (hasMaterial() ? 0 : 1); }
	 */

	@Override
	public float getBlockHardness( IBlockState blockState, World worldIn, BlockPos pos )
	{
		final TileEntity tileEntity = worldIn.getTileEntity( pos );
		if( tileEntity instanceof TileEntityLockable )
		{
			final TileEntityLockable lockable = (TileEntityLockable)tileEntity;
			if( !lockable.getLock().isEmpty() )
				return -1;
		}
		return super.getBlockHardness( blockState, worldIn, pos );
	}

	@Override
	public float getExplosionResistance( World world, BlockPos pos, Entity exploder, Explosion explosion )
	{
		float modifier = 1.0F;
		final TileEntity tileEntity = world.getTileEntity( pos );
		if( tileEntity instanceof TileEntityLockable )
		{
			final TileEntityLockable lockable = (TileEntityLockable)tileEntity;
			final int persistance = BetterStorageEnchantment.getLevel( lockable.getLock(), EnchantmentBetterStorage.persistance );
			if( persistance > 0 )
				modifier += Math.pow( 2, persistance );
		}
		return super.getExplosionResistance( exploder ) * modifier;
	}

	/*
	 * @Override
	 * public RayTraceResult collisionRayTrace( IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end )
	 * {
	 * return WorldUtils.get( worldIn, pos.getX(), pos.getY(), pos.getZ(), IHasAttachments.class ).getAttachments().rayTrace( worldIn, pos.getX(),
	 * pos.getY(), pos.getZ(), start, end );
	 * }
	 */

	/*
	 * @Override
	 * public void onBlockClicked( World worldIn, BlockPos pos, EntityPlayer playerIn )
	 * {
	 * // TODO: See if we can make a pull request to Forge to get PlayerInteractEvent to fire for left click on client.
	 * final Attachments attachments = WorldUtils.get( worldIn, pos.getX(), pos.getY(), pos.getZ(), IHasAttachments.class ).getAttachments();
	 * final boolean abort = attachments.interact( WorldUtils.rayTrace( playerIn, 1.0F ), playerIn, EnumAttachmentInteraction.attack );
	 * // TODO: Abort block breaking? playerController.resetBlockBreaking doesn't seem to do the job.
	 * }
	 */

	@Override
	public boolean hasComparatorInputOverride( IBlockState state )
	{
		return true;
	}

	// Trigger enchantment related

	@Override
	public boolean canProvidePower( IBlockState state )
	{
		return true;
	}

	@Override
	public int getWeakPower( IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side )
	{
		final TileEntity tileEntity = blockAccess.getTileEntity( pos );
		if( tileEntity instanceof TileEntityLockable )
			return ( (TileEntityLockable)tileEntity ).isPowered() ? 15 : 0;
		return 0;
	}

	@Override
	public int getStrongPower( IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side )
	{
		return getWeakPower( blockState, blockAccess, pos, side );
	}

	@Override
	public void updateTick( World worldIn, BlockPos pos, IBlockState state, Random rand )
	{
		final TileEntity tileEntity = worldIn.getTileEntity( pos );
		if( tileEntity instanceof TileEntityLockable )
			( (TileEntityLockable)tileEntity ).setPowered( false );
	}

	@Override
	public ItemStack getPickBlock( IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player )
	{
		final TileEntity tileentity = world.getTileEntity( pos );
		EnumReinforced material = EnumReinforced.IRON;

		if( tileentity instanceof TileEntityLockable )
		{
			final TileEntityLockable lockable = (TileEntityLockable)tileentity;
			material = lockable.getMaterial();
		}
		return new ItemStack( Item.getItemFromBlock( this ), 1, material.getMetadata() );
	}
}
