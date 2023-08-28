package io.github.tehstoneman.betterstorage.world.level.block;

import io.github.tehstoneman.betterstorage.api.ConnectedType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class ConnectableContainerBlock extends BetterStorageContainerBlock
{
	public static final EnumProperty< ConnectedType > TYPE = EnumProperty.create( "type", ConnectedType.class );

	protected ConnectableContainerBlock( Properties builder )
	{
		super( builder );

		registerDefaultState( defaultBlockState().setValue( TYPE, ConnectedType.SINGLE ) );
	}

	@Override
	protected void createBlockStateDefinition( StateDefinition.Builder< Block, BlockState > builder )
	{
		super.createBlockStateDefinition( builder );
		builder.add( TYPE );
	}
}
