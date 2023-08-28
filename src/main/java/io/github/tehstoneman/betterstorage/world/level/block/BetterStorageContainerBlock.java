package io.github.tehstoneman.betterstorage.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BetterStorageContainerBlock extends BaseEntityBlock
{
	protected BetterStorageContainerBlock( Properties properties )
	{
		super( properties );
	}

	@Override
	public boolean triggerEvent( BlockState blockState, Level level, BlockPos blockPos, int id, int param )
	{
		final BlockEntity blockEntity = level.getBlockEntity( blockPos );
		return blockEntity != null ? blockEntity.triggerEvent( id, param ) : false;
	}
}
