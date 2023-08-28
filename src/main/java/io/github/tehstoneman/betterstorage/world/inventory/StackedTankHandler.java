package io.github.tehstoneman.betterstorage.world.inventory;

import java.util.ArrayList;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class StackedTankHandler implements IFluidHandler
{
	private final ArrayList< FluidTankHandler >	tanks;
	private FluidStack							baseFluid;

	public StackedTankHandler( ArrayList< FluidTankHandler > tankList )
	{
		tanks		= tankList;
		baseFluid	= getFluidInTank( 0 );
		if( baseFluid.isEmpty() )
			baseFluid = getFluidInTank( tanks.size() - 1 );
	}

	@Override
	public FluidStack drain( FluidStack resource, FluidAction action )
	{
		if( resource.isEmpty() || !resource.isFluidEqual( baseFluid ) )
			return FluidStack.EMPTY;
		return drain( resource.getAmount(), action );
	}

	@Override
	public FluidStack drain( int maxDrain, FluidAction action )
	{
		if( action.simulate() )
			return new FluidStack( baseFluid, Math.min( getTotalAmount(), maxDrain ) );
		FluidStack returnFluid = FluidStack.EMPTY;
		/*
		 * if( baseFluid.getFluid().getAttributes().isLighterThanAir() )
		 * {
		 * for( final FluidTankHandler tank : tanks )
		 * if( maxDrain > 0 )
		 * {
		 * final FluidStack fluid = tank.drain( maxDrain, action );
		 * if( returnFluid.isEmpty() )
		 * returnFluid = fluid;
		 * else
		 * returnFluid.grow( fluid.getAmount() );
		 * maxDrain -= fluid.getAmount();
		 * }
		 * }
		 * else
		 */
		for( int i = tanks.size() - 1; i >= 0; i-- )
			if( maxDrain > 0 )
			{
				final FluidStack fluid = tanks.get( i ).drain( maxDrain, action );
				if( returnFluid.isEmpty() )
					returnFluid = fluid;
				else
					returnFluid.grow( fluid.getAmount() );
				maxDrain -= fluid.getAmount();
			}
		return returnFluid;
	}

	@Override
	public int fill( FluidStack resource, FluidAction action )
	{
		if( resource.isEmpty() || !isFluidValid( 0, resource ) )
			return 0;
		if( action.simulate() )
		{
			if( baseFluid.isEmpty() )
				return Math.min( getTotalCapacity(), resource.getAmount() );
			if( !baseFluid.isFluidEqual( resource ) )
				return 0;
			return Math.min( getTotalCapacity() - getTotalAmount(), resource.getAmount() );
		}
		if( !baseFluid.isEmpty() && !baseFluid.isFluidEqual( resource ) )
			return 0;

		int					filled	= 0;
		final FluidStack	filler	= resource.copy();
		/*
		 * if( resource.getFluid().getAttributes().isLighterThanAir() )
		 * {
		 * for( int i = tanks.size() - 1; i >= 0; i-- )
		 * if( !filler.isEmpty() )
		 * {
		 * final int result = tanks.get( i ).fill( filler, action );
		 * filler.shrink( result );
		 * filled += result;
		 * }
		 * }
		 * else
		 */
		for( final FluidTankHandler tank : tanks )
			if( !filler.isEmpty() )
			{
				final int result = tank.fill( filler, action );
				filler.shrink( result );
				filled += result;
			}
		return filled;
	}

	@Override
	public FluidStack getFluidInTank( int tank )
	{
		return tank < getTanks() ? tanks.get( tank ).getFluidInTank( 0 ) : FluidStack.EMPTY;
	}

	@Override
	public int getTankCapacity( int tank )
	{
		return tank < getTanks() ? tanks.get( tank ).getTankCapacity( 0 ) : 0;
	}

	@Override
	public int getTanks()
	{
		return tanks.size();
	}

	public int getTotalAmount()
	{
		int amount = 0;
		for( final FluidTankHandler tank : tanks )
			amount += tank.getFluidAmount();
		return amount;
	}

	public int getTotalCapacity()
	{
		int capacity = 0;
		for( final FluidTankHandler tank : tanks )
			capacity += tank.getTankCapacity( 0 );
		return capacity;
	}

	@Override
	public boolean isFluidValid( int tank, FluidStack stack )
	{
		return tanks.get( 0 ).isFluidValid( 0, stack );
	}
}
