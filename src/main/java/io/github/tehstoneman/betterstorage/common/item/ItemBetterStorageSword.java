package io.github.tehstoneman.betterstorage.common.item;

import io.github.tehstoneman.betterstorage.ModInfo;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemSword;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemBetterStorageSword extends ItemSword
{
	private final String name;

	public ItemBetterStorageSword( String name, ToolMaterial material )
	{
		super( material );

		this.name = name;
	}

	public void register()
	{
		setTranslationKey( ModInfo.modId + "." + name );
		setRegistryName( name );
		//GameRegistry.register( this );
	}

	@SideOnly( Side.CLIENT )
	public void registerItemModels()
	{
		ModelLoader.setCustomModelResourceLocation( this, 0, new ModelResourceLocation( getRegistryName(), "inventory" ) );
	}
}
