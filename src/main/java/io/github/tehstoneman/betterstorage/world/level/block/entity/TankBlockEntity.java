package io.github.tehstoneman.betterstorage.world.level.block.entity;

import java.util.ArrayList;

import javax.annotation.Nullable;

import io.github.tehstoneman.betterstorage.config.BetterStorageConfig;
import io.github.tehstoneman.betterstorage.world.inventory.FluidTankHandler;
import io.github.tehstoneman.betterstorage.world.inventory.StackedTankHandler;
import io.github.tehstoneman.betterstorage.world.level.block.TankBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TankBlockEntity extends BlockEntity
{
	private final FluidTankHandler				fluidTank		= new FluidTankHandler( BetterStorageConfig.COMMON.tankBuckets.get() * 1000 )
																{
																	@Override
																	protected void onContentsChanged()
																	{
																		TankBlockEntity.this.setChanged();
																	}
																};
	private final LazyOptional< IFluidHandler >	fluidHandler	= LazyOptional.of( () -> fluidTank );

	private StackedTankHandler					stackedTank;
	private final LazyOptional< IFluidHandler >	stackedHandler	= LazyOptional.of( () -> stackedTank );
	private BlockPos							mainPos			= BlockPos.ZERO;
	//private int									tankCount		= 0;

	public TankBlockEntity( BlockPos blockPos, BlockState blockState )
	{
		super( BetterStorageBlockEntityTypes.GLASS_TANK.get(), blockPos, blockState );
		//tankCount = 1;
	}

	@Override
	public <T> LazyOptional< T > getCapability( Capability< T > capability, @Nullable Direction side )
	{
		if( capability == ForgeCapabilities.FLUID_HANDLER )
		{
			if( !isMain() )
				return getMain().getCapability( capability, side );
			if( isStacked() )
			{
				stackedTank = new StackedTankHandler( getStackedTanks() );
				return ForgeCapabilities.FLUID_HANDLER.orEmpty( capability, stackedHandler );
			} else
				return ForgeCapabilities.FLUID_HANDLER.orEmpty( capability, fluidHandler );
		}
		return super.getCapability( capability, side );
	}

	public int getCapacity()
	{
		return fluidTank.getTankCapacity( 0 );
	}

	public FluidStack getFluid()
	{
		return fluidTank.getFluidInTank( 0 );
	}

	public int getFluidAmountAbove()
	{
		final TankBlockEntity tank = getTankAt( worldPosition.above() );
		return tank != null ? tank.getFluid().getAmount() : 0;
	}

	public int getFluidAmountBelow()
	{
		final TankBlockEntity tank = getTankAt( worldPosition.below() );
		return tank != null ? tank.getFluid().getAmount() : 0;
	}

	public FluidTankHandler getHandler()
	{
		return fluidTank;
	}

	public TankBlockEntity getMain()
	{
		if( !mainPos.equals( BlockPos.ZERO ) )
			return getTankAt( mainPos );
		if( isMain() )
			return this;
		final TankBlockEntity mainTank = getTankAt( worldPosition.below() ).getMain();
		mainPos = mainTank.getBlockPos();
		return mainTank;
	}

	@SuppressWarnings("null")
	public ArrayList< FluidTankHandler > getStackedTanks()
	{
		final ArrayList< FluidTankHandler >	tanks	= new ArrayList<>();
		BlockPos							tankPos	= worldPosition;
		while( level.getBlockState( tankPos ).getBlock() instanceof TankBlock )
		{
			tanks.add( getTankAt( tankPos ).getHandler() );
			tankPos = tankPos.above();
		}
		return tanks;
	}

	@SuppressWarnings("null")
	public TankBlockEntity getTankAt( BlockPos pos )
	{
		return (TankBlockEntity)level.getBlockEntity( pos );
	}

	@Override
	public CompoundTag getUpdateTag()
	{
		final CompoundTag nbt = super.getUpdateTag();

		fluidTank.writeToNBT( nbt );

		return nbt;
	}

	@Override
	public void handleUpdateTag( CompoundTag nbt )
	{
		super.handleUpdateTag( nbt );

		fluidTank.readFromNBT( nbt );
	}

	/*
	 * ==========================
	 * BlockEntity synchronization
	 * ==========================
	 */

	public boolean isMain()
	{
		return !getBlockState().getValue( TankBlock.DOWN );
	}

	public boolean isStacked()
	{
		final BlockState state = getBlockState();
		return state.getValue( TankBlock.UP ) || state.getValue( TankBlock.DOWN );
	}

	/*
	 * @Override
	 * public CompoundTag save( CompoundTag nbt )
	 * {
	 * fluidTank.writeToNBT( nbt );
	 * nbt.putInt( "tankCount", tankCount );
	 *
	 * if( !mainPos.equals( BlockPos.ZERO ) )
	 * nbt.putLong( "mainPos", mainPos.asLong() );
	 *
	 * return super.save( nbt );
	 * }
	 */

	@Override
	public void load( CompoundTag nbt )
	{
		fluidTank.readFromNBT( nbt );
		//tankCount = nbt.getInt( "tankCount" );

		if( nbt.contains( "mainPos" ) )
			mainPos = BlockPos.of( nbt.getLong( "mainPos" ) );

		super.load( nbt );
	}
}
