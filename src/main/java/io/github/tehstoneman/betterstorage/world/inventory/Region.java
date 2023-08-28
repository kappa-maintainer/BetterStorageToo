package io.github.tehstoneman.betterstorage.world.inventory;

import com.google.common.base.MoreObjects;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class Region
{
	public final static Region	EMPTY	= new Region( BlockPos.ZERO );
	public BlockPos				posMin;
	public BlockPos				posMax;

	public Region( BlockEntity entity )
	{
		this( entity.getBlockPos() );
	}

	public Region( BlockPos pos )
	{
		this( pos, pos );
	}

	public Region( BlockPos posMin, BlockPos posMax )
	{
		this.posMin	= posMin;
		this.posMax	= posMax;
		// set( posMin, posMax );
	}

	/**
	 * Convert {@link CompoundTag} data to region for deserialization
	 *
	 * @param nbt
	 *            The {@link CompoundTag} to read from
	 *
	 * @return The converted region
	 */
	public static Region fromCompound( CompoundTag nbt )
	{
		final BlockPos	posMin	= NbtUtils.readBlockPos( nbt.getCompound( "posMin" ) );
		final BlockPos	posMax	= NbtUtils.readBlockPos( nbt.getCompound( "posMax" ) );
		return new Region( posMin, posMax );
	}

	public Iterable< BlockPos > betweenClosed()
	{
		return BlockPos.betweenClosed( posMin, posMax );
	}

	@Override
	public Region clone()
	{
		return new Region( posMin, posMax );
	}

	/**
	 * Checks if the region contains the given {@link BlockEntity}
	 *
	 * @param entity
	 *            The {@link BlockEntity} to test for
	 *
	 * @return True if the {@link BlockEntity} is within the region
	 */
	public boolean contains( BlockEntity entity )
	{
		return contains( entity.getBlockPos() );
	}

	/**
	 * Checks if the region contains the given position
	 *
	 * @param pos
	 *            The {@link BlockPos} the test for
	 *
	 * @return True if the {@link BlockPos} is within the region
	 */
	public boolean contains( BlockPos pos )
	{
		return pos.getX()	>= posMin.getX()	&& pos.getY() >= posMin.getY() && pos.getZ() >= posMin.getZ() && pos.getX() <= posMax.getX()
				&& pos.getY() <= posMax.getY() && pos.getZ() <= posMax.getZ();
	}

	/**
	 * Contract the region by the given size
	 *
	 * @param posMin
	 *            A {@link BlockPos} that represents minimum size
	 * @param posMax
	 *            A {@link BlockPos} that represents maximum size
	 */
	public void contract( BlockPos posMin, BlockPos posMax )
	{
		//@formatter:off
		this.posMin.offset( new BlockPos( 	Math.min( posMin.getX(), posMax.getX() ),
									   		Math.min( posMin.getY(), posMax.getY() ),
									   		Math.min( posMin.getZ(), posMax.getZ() ) ) );
		this.posMax.subtract( new BlockPos(	Math.max( posMin.getX(), posMax.getX() ),
											Math.max( posMin.getY(), posMax.getY() ),
											Math.max( posMin.getZ(), posMax.getZ() ) ) );
		//@formatter:on
	}

	/**
	 * Get the depth (z-direction) of this region
	 *
	 * @return The depth
	 */
	public int depth()
	{
		return isEmpty() ? 0 : posMax.getZ() - posMin.getZ() + 1;
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj instanceof Region test )
		{
			return test.posMin.equals( posMin ) && test.posMax.equals( posMax );
		}
		return false;
	}

	/**
	 * Expand the region by the given size
	 *
	 * @param posMin
	 *            A {@link BlockPos} that represents minimum size
	 * @param posMax
	 *            A {@link BlockPos} that represents maximum size
	 */
	public void expand( BlockPos posMin, BlockPos posMax )
	{
		if( equals( EMPTY ) )
			set( posMin, posMax );
		//@formatter:off
		this.posMin.subtract( new BlockPos( Math.min( posMin.getX(), posMax.getX() ),
											Math.min( posMin.getY(), posMax.getY() ),
											Math.min( posMin.getZ(), posMax.getZ() ) ) );
		this.posMax.offset( new BlockPos(	Math.max( posMin.getX(), posMax.getX() ),
											Math.max( posMin.getY(), posMax.getY() ),
											Math.max( posMin.getZ(), posMax.getZ() ) ) );
		//@formatter:on
	}

	/**
	 * Expand the region by the given size
	 *
	 * @param size
	 *            The size to expand by
	 */
	public void expand( int size )
	{
		expand( new BlockPos( size, size, size ), new BlockPos( size, size, size ) );
	}

	/**
	 * Expand the region to contain the given {@link BlockEntity}
	 *
	 * @param entity
	 *            The {@link BlockEntity} to contain
	 */
	public void expandToContain( BlockEntity entity )
	{
		expandToContain( entity.getBlockPos() );
	}

	/**
	 * Expand the region to contain the given position
	 *
	 * @param pos
	 *            The {@link BlockPos} to contain
	 */
	public void expandToContain( BlockPos pos )
	{
		if( equals( EMPTY ) )
			set( pos, pos );
		posMin	= new BlockPos( Math.min( posMin.getX(), pos.getX() ), Math.min( posMin.getY(), pos.getY() ), Math.min( posMin.getZ(), pos.getZ() ) );
		posMax	= new BlockPos( Math.max( posMax.getX(), pos.getX() ), Math.max( posMax.getY(), pos.getY() ), Math.max( posMax.getZ(), pos.getZ() ) );
	}

	/**
	 * Expand the region to contain the given {@link Region}
	 *
	 * @param region
	 *            The {@link Region} to contain
	 */
	public void expandToContain( Region region )
	{
		if( equals( EMPTY ) )
			set( region.posMin, region.posMax );
		else
		{
			posMin	= new BlockPos( Math.min( posMin.getX(), region.posMin.getX() ), Math.min( posMin.getY(), region.posMin.getY() ),
				Math.min( posMin.getZ(), region.posMin.getZ() ) );
			posMax	= new BlockPos( Math.max( posMax.getX(), region.posMax.getX() ), Math.max( posMax.getY(), region.posMax.getY() ),
				Math.max( posMax.getZ(), region.posMax.getZ() ) );
		}
	}

	/**
	 * Get the height (y-direction) of this region
	 *
	 * @return The height
	 */
	public int height()
	{
		return isEmpty() ? 0 : posMax.getY() - posMin.getY() + 1;
	}

	/**
	 * Test if this region is equal to empty
	 *
	 * @return True if empty
	 */
	public boolean isEmpty()
	{
		return equals( EMPTY );
	}

	/**
	 * Test if this region only contains the given {@link Block}
	 *
	 * @param world
	 *            The {@link Level} to test in
	 * @param block
	 *            The {@link Block} to test for
	 *
	 * @return True if the region only contains the given {@link Block}
	 */
	public boolean isFull( Level world, Block block )
	{
		for( final BlockPos blockPos : BlockPos.betweenClosed( posMin, posMax ) )
			if( !world.getBlockState( blockPos ).getBlock().equals( block ) )
				return false;
		return true;
	}

	/**
	 * Test if this region only contains the given {@link BlockEntity}
	 *
	 * @param world
	 *            The {@link Level} to test in
	 * @param tileEntity
	 *            The {@link BlockEntity} to test for
	 *
	 * @return True if the region only contains the given {@link BlockEntity}
	 */
	@SuppressWarnings("null")
	public boolean isFull( Level world, BlockEntity tileEntity )
	{
		for( final BlockPos blockPos : BlockPos.betweenClosed( posMin, posMax ) )
			if( !world.getBlockEntity( blockPos ).equals( tileEntity ) )
				return false;
		return true;
	}

	/**
	 * Sets the area of this region
	 * <p>
	 * Automatically adjusts min and max values
	 *
	 * @param posMin
	 *            A {@link BlockPos} that represents minimum corner
	 * @param posMax
	 *            A {@link BlockPos} that represents maximum corner
	 */
	public void set( BlockPos posMin, BlockPos posMax )
	{
		//@formatter:off
		this.posMin = new BlockPos( Math.min( posMin.getX(), posMax.getX() ),
									Math.min( posMin.getY(), posMax.getY() ),
									Math.min( posMin.getZ(), posMax.getZ() ) );
		this.posMax = new BlockPos( Math.max( posMin.getX(), posMax.getX() ),
									Math.max( posMin.getY(), posMax.getY() ),
									Math.max( posMin.getZ(), posMax.getZ() ) );
		//@formatter:on
	}

	/**
	 * Convert region to {@link CompoundTag} data for serialization
	 *
	 * @return The converted region
	 */
	public CompoundTag toCompound()
	{
		final CompoundTag nbt = new CompoundTag();
		nbt.put( "posMin", NbtUtils.writeBlockPos( posMin ) );
		nbt.put( "posMax", NbtUtils.writeBlockPos( posMax ) );
		return nbt;
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper( this ).add( "min", posMin.toString() ).add( "max", posMax.toString() ).toString();
	}

	/**
	 * Get the volume of this region
	 *
	 * @return The volume
	 */
	public int volume()
	{
		return width() * depth() * height();
	}

	/**
	 * Get the width (x-direction) of this region
	 *
	 * @return The width
	 */
	public int width()
	{
		return isEmpty() ? 0 : posMax.getX() - posMin.getX() + 1;
	}
}
