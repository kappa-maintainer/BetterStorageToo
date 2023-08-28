package io.github.tehstoneman.betterstorage.world.item.crafting;

import io.github.tehstoneman.betterstorage.ModInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BetterStorageRecipes
{
	public static final DeferredRegister< RecipeSerializer< ? > > REGISTERY = DeferredRegister.create( ForgeRegistries.RECIPE_SERIALIZERS,
		ModInfo.MOD_ID );

	public static final RegistryObject< CopyKeyRecipe.Serializer >			COPY_KEY		= REGISTERY.register( "copy_key_shaped",
		CopyKeyRecipe.Serializer::new );
	public static final RegistryObject< KeyColorRecipe.Serializer >			COLOR_KEY		= REGISTERY.register( "color_key_special",
		KeyColorRecipe.Serializer::new );
	public static final RegistryObject< CardboardColorRecipe.Serializer >	COLOR_CARDBOARD	= REGISTERY.register( "color_cardboard_special",
		CardboardColorRecipe.Serializer::new );
}
