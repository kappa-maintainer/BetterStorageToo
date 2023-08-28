package io.github.tehstoneman.betterstorage.client.model.xobj;

import java.io.FileNotFoundException;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjTokenizer;

/**
 * Special variant of the OBJ model loader
 *
 * @author TehStoneMan
 */
@OnlyIn( Dist.CLIENT )
public class XOBJLoader implements IGeometryLoader< XOBJModel >, ResourceManagerReloadListener
{
	public static XOBJLoader INSTANCE = new XOBJLoader();

	private final Map< XOBJModel.ModelSettings, XOBJModel >		modelCache		= Maps.newHashMap();
	private final Map< ResourceLocation, ObjMaterialLibrary >	materialCache	= Maps.newHashMap();

	private ResourceManager manager = Minecraft.getInstance().getResourceManager();

	public XOBJModel loadModel( XOBJModel.ModelSettings settings )
	{
		return loadModel( settings, Map.of() );
	}

	@Override
	public void onResourceManagerReload( ResourceManager resourceManager )
	{
		modelCache.clear();
		materialCache.clear();
		manager = resourceManager;
	}

	@Override
	public XOBJModel read( JsonObject jsonObject, JsonDeserializationContext deserializationContext ) throws JsonParseException
	{
		if( !jsonObject.has( "model" ) )
			throw new RuntimeException( "XOBJ Loader requires a 'model' key that points to a valid .OBJ model." );

		final String modelLocation = jsonObject.get( "model" ).getAsString();

		boolean	automaticCulling	= GsonHelper.getAsBoolean( jsonObject, "automatic_culling", true );
		boolean	shadeQuads			= GsonHelper.getAsBoolean( jsonObject, "shade_quads", true );
		boolean	flipV				= GsonHelper.getAsBoolean( jsonObject, "flip_v", false );
		boolean	emissiveAmbient		= GsonHelper.getAsBoolean( jsonObject, "emissive_ambient", true );
		String	mtlOverride			= GsonHelper.getAsString( jsonObject, "mtl_override", null );

		// TODO: Deprecated names. To be removed in 1.20
		final var deprecationWarningsBuilder = ImmutableMap.<String, String> builder();
		if( jsonObject.has( "detectCullableFaces" ) )
		{
			automaticCulling = GsonHelper.getAsBoolean( jsonObject, "detectCullableFaces" );
			deprecationWarningsBuilder.put( "detectCullableFaces", "automatic_culling" );
		}
		if( jsonObject.has( "diffuseLighting" ) )
		{
			shadeQuads = GsonHelper.getAsBoolean( jsonObject, "diffuseLighting" );
			deprecationWarningsBuilder.put( "diffuseLighting", "shade_quads" );
		}
		if( jsonObject.has( "flip-v" ) )
		{
			flipV = GsonHelper.getAsBoolean( jsonObject, "flip-v" );
			deprecationWarningsBuilder.put( "flip-v", "flip_v" );
		}
		if( jsonObject.has( "ambientToFullbright" ) )
		{
			emissiveAmbient = GsonHelper.getAsBoolean( jsonObject, "ambientToFullbright" );
			deprecationWarningsBuilder.put( "ambientToFullbright", "emissive_ambient" );
		}
		if( jsonObject.has( "materialLibraryOverride" ) )
		{
			mtlOverride = GsonHelper.getAsString( jsonObject, "materialLibraryOverride" );
			deprecationWarningsBuilder.put( "materialLibraryOverride", "mtl_override" );
		}

		return loadModel(
			new XOBJModel.ModelSettings( new ResourceLocation( modelLocation ), automaticCulling, shadeQuads, flipV, emissiveAmbient, mtlOverride ),
			deprecationWarningsBuilder.build() );
	}

	private XOBJModel loadModel( XOBJModel.ModelSettings settings, Map< String, String > deprecationWarnings )
	{
		return modelCache.computeIfAbsent( settings, data ->
		{
			final Resource resource = manager.getResource( settings.modelLocation() ).orElseThrow();
			try( ObjTokenizer tokenizer = new ObjTokenizer( resource.open() ) )
			{
				return XOBJModel.parse( tokenizer, settings, deprecationWarnings );
			} catch( final FileNotFoundException e )
			{
				throw new RuntimeException( "Could not find XOBJ model", e );
			} catch( final Exception e )
			{
				throw new RuntimeException( "Could not read XOBJ model", e );
			}
		} );
	}
}
