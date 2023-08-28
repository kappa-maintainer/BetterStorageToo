package io.github.tehstoneman.betterstorage.world.item.crafting;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import io.github.tehstoneman.betterstorage.api.lock.IKey;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

public class CopyKeyRecipe extends CustomRecipe
{
	public static class Serializer implements RecipeSerializer< CopyKeyRecipe >
	{
		@Override
		public CopyKeyRecipe fromJson( ResourceLocation recipeId, JsonObject json )
		{
			final String					s			= GsonHelper.getAsString( json, "group", "" );
			final Map< String, Ingredient >	map			= deserializeKey( GsonHelper.getAsJsonObject( json, "key" ) );
			final String[]					aString		= shrink( patternFromJson( GsonHelper.getAsJsonArray( json, "pattern" ) ) );
			final int						i			= aString[0].length();
			final int						j			= aString.length;
			final NonNullList< Ingredient >	nonNullList	= deserializeIngredients( aString, map, i, j );
			final ItemStack					itemstack	= ShapedRecipe.itemStackFromJson( GsonHelper.getAsJsonObject( json, "result" ) );
			return new CopyKeyRecipe( recipeId, s, i, j, nonNullList, itemstack );
		}

		@Override
		public CopyKeyRecipe fromNetwork( ResourceLocation recipeId, FriendlyByteBuf buffer )
		{
			final int						i			= buffer.readVarInt();
			final int						j			= buffer.readVarInt();
			final String					s			= buffer.readUtf( 32767 );
			final NonNullList< Ingredient >	nonNullList	= NonNullList.withSize( i * j, Ingredient.EMPTY );

			for( int k = 0; k < nonNullList.size(); ++k )
				nonNullList.set( k, Ingredient.fromNetwork( buffer ) );

			final ItemStack itemstack = buffer.readItem();
			return new CopyKeyRecipe( recipeId, s, i, j, nonNullList, itemstack );
		}

		@Override
		public void toNetwork( FriendlyByteBuf buffer, CopyKeyRecipe recipe )
		{
			buffer.writeVarInt( recipe.recipeWidth );
			buffer.writeVarInt( recipe.recipeHeight );
			buffer.writeUtf( recipe.getGroup() );

			for( final Ingredient ingredient : recipe.recipeItems )
				ingredient.toNetwork( buffer );

			buffer.writeItem( recipe.getResultItem() );
		}
	}

	static final int	MAX_WIDTH	= 3;
	static final int	MAX_HEIGHT	= 3;

	private int							recipeWidth;
	private int							recipeHeight;
	private NonNullList< Ingredient >	recipeItems;
	private ItemStack					recipeOutput;

	public CopyKeyRecipe( ResourceLocation idIn )
	{
		super( idIn, CraftingBookCategory.MISC );
	}

	public CopyKeyRecipe(	ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList< Ingredient > recipeItemsIn,
							ItemStack recipeOutputIn )
	{
		super( idIn, CraftingBookCategory.MISC );
		recipeWidth		= recipeWidthIn;
		recipeHeight	= recipeHeightIn;
		recipeItems		= recipeItemsIn;
		recipeOutput	= recipeOutputIn;
	}

	private static NonNullList< Ingredient > deserializeIngredients(	String[] pattern, Map< String, Ingredient > keys, int patternWidth,
																		int patternHeight )
	{
		final NonNullList< Ingredient >	nonNullList	= NonNullList.withSize( patternWidth * patternHeight, Ingredient.EMPTY );
		final Set< String >				set			= Sets.newHashSet( keys.keySet() );
		set.remove( " " );

		for( int i = 0; i < pattern.length; ++i )
			for( int j = 0; j < pattern[i].length(); ++j )
			{
				final String		s			= pattern[i].substring( j, j + 1 );
				final Ingredient	ingredient	= keys.get( s );
				if( ingredient == null )
					throw new JsonSyntaxException( "Pattern references symbol '" + s + "' but it's not defined in the key" );

				set.remove( s );
				nonNullList.set( j + patternWidth * i, ingredient );
			}

		if( !set.isEmpty() )
			throw new JsonSyntaxException( "Key defines symbols that aren't used in pattern: " + set );
		return nonNullList;
	}

	private static Map< String, Ingredient > deserializeKey( JsonObject json )
	{
		final Map< String, Ingredient > map = Maps.newHashMap();

		for( final Entry< String, JsonElement > entry : json.entrySet() )
		{
			if( entry.getKey().length() != 1 )
				throw new JsonSyntaxException( "Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only)." );

			if( " ".equals( entry.getKey() ) )
				throw new JsonSyntaxException( "Invalid key entry: ' ' is a reserved symbol." );

			map.put( entry.getKey(), Ingredient.fromJson( entry.getValue() ) );
		}

		map.put( " ", Ingredient.EMPTY );
		return map;
	}

	private static int firstNonSpace( String str )
	{
		int i;
		for( i = 0; i < str.length() && str.charAt( i ) == ' '; ++i );

		return i;
	}

	private static int lastNonSpace( String str )
	{
		int i;
		for( i = str.length() - 1; i >= 0 && str.charAt( i ) == ' '; --i );

		return i;
	}

	private static String[] patternFromJson( JsonArray jsonArr )
	{
		final String[] aString = new String[jsonArr.size()];
		if( aString.length > MAX_HEIGHT )
			throw new JsonSyntaxException( "Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum" );
		if( aString.length == 0 )
			throw new JsonSyntaxException( "Invalid pattern: empty pattern not allowed" );
		for( int i = 0; i < aString.length; ++i )
		{
			final String s = GsonHelper.convertToString( jsonArr.get( i ), "pattern[" + i + "]" );
			if( s.length() > MAX_WIDTH )
				throw new JsonSyntaxException( "Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum" );

			if( i > 0 && aString[0].length() != s.length() )
				throw new JsonSyntaxException( "Invalid pattern: each row must be the same width" );

			aString[i] = s;
		}

		return aString;
	}

	static String[] shrink( String... toShrink )
	{
		int	i	= Integer.MAX_VALUE;
		int	j	= 0;
		int	k	= 0;
		int	l	= 0;

		for( int i1 = 0; i1 < toShrink.length; ++i1 )
		{
			final String s = toShrink[i1];
			i = Math.min( i, firstNonSpace( s ) );
			final int j1 = lastNonSpace( s );
			j = Math.max( j, j1 );
			if( j1 < 0 )
			{
				if( k == i1 )
					++k;

				++l;
			} else
				l = 0;
		}

		if( toShrink.length == l )
			return new String[0];
		final String[] aString = new String[toShrink.length - l - k];

		for( int k1 = 0; k1 < aString.length; ++k1 )
			aString[k1] = toShrink[k1 + k].substring( i, j + 1 );

		return aString;
	}

	public ItemStack assemble( CraftingContainer inv )
	{
		// final ItemStack outset = ItemStack.EMPTY;
		final ItemStack outset = getResultItem().copy();

		if( !outset.isEmpty() )
		{
			final CompoundTag tagCompound = outset.getOrCreateTag();

			for( int i = 0; i < inv.getContainerSize(); ++i )
			{
				final ItemStack ingredientStack = inv.getItem( i );

				if( !ingredientStack.isEmpty() && ingredientStack.getItem() instanceof IKey )
					if( ingredientStack.hasTag() )
						tagCompound.merge( ingredientStack.getTag() );
			}

			outset.setTag( tagCompound );
		}

		return outset;
	}

	@Override
	public boolean canCraftInDimensions( int width, int height )
	{
		return width >= recipeWidth && height >= recipeHeight;
	}

	@Override
	public NonNullList< ItemStack > getRemainingItems( CraftingContainer inv )
	{
		final NonNullList< ItemStack > ret = NonNullList.withSize( inv.getContainerSize(), ItemStack.EMPTY );

		for( int i = 0; i < ret.size(); ++i )
		{
			final ItemStack itemStack = inv.getItem( i );
			if( !itemStack.isEmpty() )
				if( itemStack.getItem() instanceof IKey )
					ret.set( i, itemStack.copy() );
				else
					ret.set( i, ForgeHooks.getCraftingRemainingItem( itemStack ) );
		}

		return ret;
	}

	public ItemStack getResultItem()
	{
		return recipeOutput;
	}

	@Override
	public RecipeSerializer< ? > getSerializer()
	{
		return BetterStorageRecipes.COPY_KEY.get();
	}

	@Override
	public boolean isSpecial()
	{
		return false;
	}

	@Override
	public boolean matches( CraftingContainer inv, Level worldIn )
	{
		for( int i = 0; i <= inv.getWidth() - recipeWidth; ++i )
			for( int j = 0; j <= inv.getHeight() - recipeHeight; ++j )
				if( checkMatch( inv, i, j, true ) || checkMatch( inv, i, j, false ) )
					return true;

		return false;
	}

	/**
	 * Checks if the region of a crafting inventory is match for the recipe.
	 */
	private boolean checkMatch( CraftingContainer craftingInventory, int width, int height, boolean p_77573_4_ )
	{
		for( int i = 0; i < craftingInventory.getWidth(); ++i )
			for( int j = 0; j < craftingInventory.getHeight(); ++j )
			{
				final int	k			= i - width;
				final int	l			= j - height;
				Ingredient	ingredient	= Ingredient.EMPTY;
				if( k >= 0 && l >= 0 && k < recipeWidth && l < recipeHeight )
					if( p_77573_4_ )
						ingredient = recipeItems.get( recipeWidth - k - 1 + l * recipeWidth );
					else
						ingredient = recipeItems.get( k + l * recipeWidth );

				if( !ingredient.test( craftingInventory.getItem( i + j * craftingInventory.getWidth() ) ) )
					return false;
			}

		return true;
	}

	@Override
	public ItemStack assemble(CraftingContainer p_44001_, RegistryAccess p_267165_) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'assemble'");
	}
}
