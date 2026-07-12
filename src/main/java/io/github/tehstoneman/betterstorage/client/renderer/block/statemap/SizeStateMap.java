package io.github.tehstoneman.betterstorage.client.renderer.block.statemap;

import java.util.Map;

import com.google.common.collect.Maps;

import io.github.tehstoneman.betterstorage.common.block.BlockLockable;
import io.github.tehstoneman.betterstorage.common.block.BlockReinforcedChest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockHorizontal;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

public class SizeStateMap extends StateMapperBase
{
	@Override
	protected ModelResourceLocation getModelResourceLocation( IBlockState state )
	{
		String size = state.getValue( BlockLockable.CONNECTED ) ? "_large" : "_small";
		String name = Block.REGISTRY.getNameForObject( state.getBlock() ).toString() + size;
        Map < IProperty<?>, Comparable<? >> map = Maps. < IProperty<?>, Comparable<? >> newLinkedHashMap(state.getProperties());
        map.remove( BlockLockable.CONNECTED );
        if( state.getBlock() instanceof BlockReinforcedChest )
        	map.remove( BlockHorizontal.FACING );
        if( map.containsKey( BlockDoor.HINGE ))
        {
        	name += "_" + state.getValue( BlockDoor.HINGE ).getName();
        	map.remove( BlockDoor.HINGE );
        }
		return new ModelResourceLocation( new ResourceLocation( name ), getPropertyString( map ) );
	}
}
