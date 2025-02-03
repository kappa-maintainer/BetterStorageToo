package io.github.tehstoneman.betterstorage.common.block;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.attachment.Attachments;
import io.github.tehstoneman.betterstorage.attachment.EnumAttachmentInteraction;
import io.github.tehstoneman.betterstorage.attachment.IHasAttachments;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityLockableDoor;
import io.github.tehstoneman.betterstorage.utils.WorldUtils;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockDoor.EnumHingePosition;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLockableDoor extends BlockBetterStorage
{
	protected static final AxisAlignedBB	SOUTH_AABB	= new AxisAlignedBB( 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.1875D );
	protected static final AxisAlignedBB	NORTH_AABB	= new AxisAlignedBB( 0.0D, 0.0D, 0.8125D, 1.0D, 1.0D, 1.0D );
	protected static final AxisAlignedBB	WEST_AABB	= new AxisAlignedBB( 0.8125D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D );
	protected static final AxisAlignedBB	EAST_AABB	= new AxisAlignedBB( 0.0D, 0.0D, 0.0D, 0.1875D, 1.0D, 1.0D );

	public BlockLockableDoor()
	{
		super( "lockable_door", Material.IRON );

		setCreativeTab( null );
		// setHardness( 8.0F );
		// setResistance( 20.0F );
		// setHarvestLevel( "axe", 2 );

		//@formatter:off
		setDefaultState( blockState.getBaseState().withProperty( BlockDoor.FACING, EnumFacing.NORTH )
												  .withProperty( BlockDoor.OPEN, Boolean.valueOf( false ) )
												  .withProperty( BlockDoor.HINGE, EnumHingePosition.LEFT )
												  .withProperty( BlockDoor.HALF, EnumDoorHalf.LOWER ) );
		//@formatter:on
	}

	/*@Override
	public void registerBlock()
	{
		setTranslationKey( ModInfo.modId + "." + name );
		this.setRegistryName( name );
		GameRegistry.register( this );
	}*/

	@Override
	public AxisAlignedBB getBoundingBox( IBlockState state, IBlockAccess source, BlockPos pos )
	{
		state = state.getActualState( source, pos );
		final EnumFacing enumfacing = state.getValue( BlockDoor.FACING );
		final boolean flag = !state.getValue( BlockDoor.OPEN ).booleanValue();
		final boolean flag1 = state.getValue( BlockDoor.HINGE ) == EnumHingePosition.RIGHT;

		switch( enumfacing )
		{
		case EAST:
		default:
			return flag ? EAST_AABB : flag1 ? NORTH_AABB : SOUTH_AABB;
		case SOUTH:
			return flag ? SOUTH_AABB : flag1 ? EAST_AABB : WEST_AABB;
		case WEST:
			return flag ? WEST_AABB : flag1 ? SOUTH_AABB : NORTH_AABB;
		case NORTH:
			return flag ? NORTH_AABB : flag1 ? WEST_AABB : EAST_AABB;
		}
	}

	@Override
	public BlockStateContainer createBlockState()
	{
		return new BlockStateContainer( this, new IProperty[] { BlockDoor.FACING, BlockDoor.OPEN, BlockDoor.HINGE, BlockDoor.HALF } );
	}

	@Override
	public boolean isOpaqueCube( IBlockState state )
	{
		return false;
	}

	@Override
	public boolean isPassable( IBlockAccess worldIn, BlockPos pos )
	{
		final IBlockState state = worldIn.getBlockState( pos );
		return state.getValue( BlockDoor.OPEN );
	}

	@Override
	public boolean isFullCube( IBlockState state )
	{
		return false;
	}

	public static int getCloseSound()
	{
		return 1011;
	}

	public static int getOpenSound()
	{
		return 1005;
	}

	@Override
	public IBlockState getStateFromMeta( int meta )
	{
		return ( meta & 8 ) > 0
				? getDefaultState().withProperty( BlockDoor.HALF, EnumDoorHalf.UPPER ).withProperty( BlockDoor.HINGE,
						( meta & 1 ) > 0 ? EnumHingePosition.RIGHT : EnumHingePosition.LEFT )
				: getDefaultState().withProperty( BlockDoor.HALF, EnumDoorHalf.LOWER )
						.withProperty( BlockDoor.FACING, EnumFacing.byHorizontalIndex( meta & 3 ).rotateYCCW() )
						.withProperty( BlockDoor.OPEN, Boolean.valueOf( ( meta & 4 ) > 0 ) );
	}

	@Override
	public int getMetaFromState( IBlockState state )
	{
		int i = 0;

		if( state.getValue( BlockDoor.HALF ) == EnumDoorHalf.UPPER )
		{
			i = i | 8;

			if( state.getValue( BlockDoor.HINGE ) == EnumHingePosition.RIGHT )
				i |= 1;
		}
		else
		{
			i = i | state.getValue( BlockDoor.FACING ).rotateY().getHorizontalIndex();

			if( state.getValue( BlockDoor.OPEN ).booleanValue() )
				i |= 4;
		}

		return i;
	}

	@Override
	public IBlockState getActualState( IBlockState state, IBlockAccess worldIn, BlockPos pos )
	{
		if( state.getValue( BlockDoor.HALF ) == EnumDoorHalf.LOWER )
		{
			final IBlockState iblockstate = worldIn.getBlockState( pos.up() );

			if( iblockstate.getBlock() == this )
				state = state.withProperty( BlockDoor.HINGE, iblockstate.getValue( BlockDoor.HINGE ) );
		}
		else
		{
			final IBlockState iblockstate1 = worldIn.getBlockState( pos.down() );

			if( iblockstate1.getBlock() == this )
				state = state.withProperty( BlockDoor.FACING, iblockstate1.getValue( BlockDoor.FACING ) ).withProperty( BlockDoor.OPEN,
						iblockstate1.getValue( BlockDoor.OPEN ) );
		}

		return state;
	}

	@Override
	public float getBlockHardness( IBlockState state, World world, BlockPos pos )
	{
		// if (world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()) > 0) y -= 1;
		final TileEntityLockableDoor lockable = WorldUtils.get( world, pos.getX(), pos.getY(), pos.getZ(), TileEntityLockableDoor.class );
		if( lockable != null && lockable.getLock() != null )
			return -1;
		else
			return super.getBlockHardness( state, world, pos );
	}

	/*
	 * @Override
	 * public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
	 * //if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
	 * float modifier = 1.0F;
	 * TileEntityLockableDoor lockable = WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class);
	 * if (lockable != null) {
	 * int persistance = BetterStorageEnchantment.getLevel(lockable.getLock(), "persistance");
	 * if (persistance > 0) modifier += Math.pow(2, persistance);
	 * }
	 * return super.getExplosionResistance(entity) * modifier;
	 * }
	 */

	/*
	 * @Override
	 * public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventPar) {
	 * TileEntity te = world.getTileEntity(x, y, z);
	 * return ((te != null) ? te.receiveClientEvent(eventId, eventPar) : false);
	 * }
	 */

	@Override
	public boolean onBlockActivated( World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX,
			float hitY, float hitZ )
	{
		if( state.getValue( BlockDoor.HALF ) == EnumDoorHalf.UPPER )
			pos = pos.down();
		final TileEntity tileentity = world.getTileEntity( pos );
		if( tileentity instanceof TileEntityLockableDoor )
			return ( (TileEntityLockableDoor)tileentity ).onBlockActivated( world, pos, player, side, hitX, hitY, hitZ );
		return false;
	}

	@Override
	public void onBlockClicked( World world, BlockPos pos, EntityPlayer player )
	{
		// if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
		final Attachments attachments = WorldUtils.get( world, pos.getX(), pos.getY(), pos.getZ(), IHasAttachments.class ).getAttachments();
		final boolean abort = attachments.interact( WorldUtils.rayTrace( player, 1.0F ), player, EnumAttachmentInteraction.attack );
	}

	/*
	 * @Override
	 * public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
	 * //int metadata = world.getBlockMetadata(x, y, z);
	 * IHasAttachments te = WorldUtils.get(world, pos.getX(), pos.getY() - (metadata > 0 ? 1 : 0), pos.getZ(), IHasAttachments.class);
	 * if(te == null) return super.collisionRayTrace(world, x, y, z, start, end);
	 * MovingObjectPosition pos = te.getAttachments().rayTrace(world, x, y - (metadata > 0 ? 1 : 0), z, start, end);
	 * return pos != null ? pos : super.collisionRayTrace(world, x, y, z, start, end);
	 * }
	 */

	@Override
	public ItemStack getPickBlock( IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player )
	{
		return new ItemStack( Items.IRON_DOOR );
	}

	@Override
	public void onBlockHarvested( World worldIn, BlockPos pos, IBlockState state, EntityPlayer player )
	{
		final BlockPos blockpos = pos.down();
		final BlockPos blockpos1 = pos.up();

		if( player.capabilities.isCreativeMode && state.getValue( BlockDoor.HALF ) == EnumDoorHalf.UPPER
				&& worldIn.getBlockState( blockpos ).getBlock() == this )
			worldIn.setBlockToAir( blockpos );

		if( state.getValue( BlockDoor.HALF ) == EnumDoorHalf.LOWER && worldIn.getBlockState( blockpos1 ).getBlock() == this )
		{
			if( player.capabilities.isCreativeMode )
				worldIn.setBlockToAir( pos );

			worldIn.setBlockToAir( blockpos1 );
		}
	}

	@Override
	public void breakBlock( World worldIn, BlockPos pos, IBlockState state )
	{
		final TileEntity tileentity = worldIn.getTileEntity( pos );
		if( tileentity instanceof TileEntityLockableDoor )
		{
			final TileEntityLockableDoor lockable = (TileEntityLockableDoor)tileentity;
			final ItemStack lock = lockable.getLock();
			if( !lock.isEmpty() )
				worldIn.spawnEntity( new EntityItem( worldIn, pos.getX(), pos.getY(), pos.getZ(), lock ) );
		}
		super.breakBlock( worldIn, pos, state );
	}
	/*
	 * @Override
	 * public boolean removedByPlayer( IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest )
	 * {
	 * return world.setBlockToAir( pos );
	 * }
	 * 
	 * @Override
	 * public void breakBlock( World world, BlockPos pos, IBlockState state )
	 * {
	 * // if (meta > 0) return;
	 * super.breakBlock( world, pos, state );
	 * }
	 */

	@Override
	public void onNeighborChange( IBlockAccess world, BlockPos pos, BlockPos neighbor )
	{
		/*
		 * int metadata = world.getBlockMetadata(x, y, z);
		 * int targetY = y + ((metadata == 0) ? 1 : -1);
		 * int targetMeta = ((metadata == 0) ? 8 : 0);
		 * if (world.getBlock(x, y - 1, z) == Blocks.air && metadata == 0) world.setBlockToAir(x, y, z);
		 * if ((world.getBlock(x, targetY, z) == this) && (world.getBlockMetadata(x, targetY, z) == targetMeta)) return;
		 * world.setBlockToAir(x, y, z);
		 * if (metadata == 0) WorldUtils.spawnItem(world, x, y, z, new ItemStack(Items.IRON_DOOR));
		 */
	}

	/*
	 * @Override
	 * public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
	 * if(meta == 0) {
	 * TileEntityLockableDoor te = WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class);
	 * WorldUtils.dropStackFromBlock(te, te.getLock());
	 * te.setLockWithUpdate(null);
	 * }
	 * super.onBlockPreDestroy(world, x, y, z, meta);
	 * }
	 */

	@Override
	@SideOnly( Side.CLIENT )
	public EnumBlockRenderType getRenderType( IBlockState state )
	{
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly( Side.CLIENT )
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	/*
	 * @Override
	 * public int quantityDropped(int meta, int fortune, Random random) {
	 * return ((meta == 0) ? 1 : 0);
	 * }
	 */

	@Override
	public boolean canProvidePower( IBlockState state )
	{
		return true;
	}

	@Override
	public int getWeakPower( IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side )
	{
		// if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
		final TileEntityLockableDoor te = WorldUtils.get( world, pos.getX(), pos.getY(), pos.getZ(), TileEntityLockableDoor.class );
		return te == null ? 0 : te.isPowered() ? 15 : 0;
	}

	@Override
	public int getStrongPower( IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side )
	{
		return getWeakPower( state, world, pos, side );
	}

	/*
	 * @Override
	 * public void updateTick(World world, int x, int y, int z, Random random) {
	 * if(world.getBlockMetadata(x, y, z) != 0) return;
	 * WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class).setPowered(false);
	 * }
	 */

	@Override
	public TileEntity createTileEntity( World world, IBlockState state )
	{
		return state.getValue( BlockDoor.HALF ) == EnumDoorHalf.LOWER ? new TileEntityLockableDoor() : null;
	}

	@Override
	public boolean hasTileEntity( IBlockState state )
	{
		return state.getValue( BlockDoor.HALF ) == EnumDoorHalf.LOWER;
	}
}
