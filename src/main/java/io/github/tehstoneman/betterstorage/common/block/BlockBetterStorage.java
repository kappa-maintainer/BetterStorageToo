package io.github.tehstoneman.betterstorage.common.block;

import io.github.tehstoneman.betterstorage.BetterStorage;
import io.github.tehstoneman.betterstorage.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockBetterStorage extends Block
{
	protected final String name;

	public BlockBetterStorage( String name, Material material )
	{
		super( material );

		setCreativeTab( BetterStorage.creativeTab );

		this.name = name;
	}

	/** Returns the name of this tile, for example "craftingStation". */
	public String getBlockName()
	{
		return name;
	}

	/** Returns the item class used for this block. */
	protected ItemBlock getItemBlock()
	{
		return new ItemBlock( this );
	}

	/**
	 * Registers the block in the GameRegistry.
	 * 
	 * @param registry
	 */
	public void registerBlock( IForgeRegistry< Block > registry )
	{
		setTranslationKey( ModInfo.modId + "." + name );
		this.setRegistryName( name );
		registry.register( this );

		final ItemBlock itemBlock = getItemBlock();

		if( itemBlock != null )
			ForgeRegistries.ITEMS.register( itemBlock.setRegistryName( getRegistryName() ) );
	}

	@SideOnly( Side.CLIENT )
	public void registerItemModels( ItemModelMesher mesher )
	{
		final Item item = Item.getItemFromBlock( this );
		final int meta = 0;
		final ModelResourceLocation model = new ModelResourceLocation( getRegistryName(), "inventory" );
		ModelLoader.setCustomModelResourceLocation( item, meta, model );
		mesher.register( item, meta, model );
	}
}
