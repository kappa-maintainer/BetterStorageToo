package io.github.tehstoneman.betterstorage.client.model.xobj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.math.Transformation;

import io.github.tehstoneman.betterstorage.ModInfo;
import joptsimple.internal.Strings;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjTokenizer;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.textures.UnitTextureAtlasSprite;

@OnlyIn( Dist.CLIENT )
public class XOBJModel extends SimpleUnbakedGeometry< XOBJModel >
{
	public class ModelGroup extends ModelObject
	{
		final Map< String, ModelObject > parts = Maps.newHashMap();

		ModelGroup( String name )
		{
			super( name );
		}

		@Override
		public void addQuads(	IGeometryBakingContext owner, IModelBuilder< ? > modelBuilder, ModelBaker baker,
								Function< Material, TextureAtlasSprite > spriteGetter, ModelState modelTransform, ResourceLocation modelLocation )
		{
			super.addQuads( owner, modelBuilder, baker, spriteGetter, modelTransform, modelLocation );

			parts.values().stream().filter( part -> owner.isComponentVisible( part.name(), true ) )
				.forEach( part -> part.addQuads( owner, modelBuilder, baker, spriteGetter, modelTransform, modelLocation ) );
		}

		@Override
		public void bake( CompositeRenderable.PartBuilder< ? > builder, IGeometryBakingContext configuration )
		{
			super.bake( builder, configuration );

			for( final var entry : parts.entrySet() )
			{
				final var	name	= entry.getKey();
				final var	part	= entry.getValue();
				part.bake( builder.child( name ), configuration );
			}
		}

		@Override
		public Collection< Material > getTextures(	IGeometryBakingContext owner, Function< ResourceLocation, UnbakedModel > modelGetter,
													Set< com.mojang.datafixers.util.Pair< String, String > > missingTextureErrors )
		{
			final Set< Material > combined = Sets.newHashSet();
			combined.addAll( super.getTextures( owner, modelGetter, missingTextureErrors ) );
			for( final ModelObject part : parts.values() )
				combined.addAll( part.getTextures( owner, modelGetter, missingTextureErrors ) );
			return combined;
		}

		@Override
		protected void addNamesRecursively( Set< String > names )
		{
			super.addNamesRecursively( names );
			for( final ModelObject object : parts.values() )
				object.addNamesRecursively( names );
		}
	}

	public class ModelObject
	{
		public final String name;

		List< ModelMesh > meshes = Lists.newArrayList();

		ModelObject( String name )
		{
			this.name = name;
		}

		public void addQuads(	IGeometryBakingContext owner, IModelBuilder< ? > modelBuilder, ModelBaker baker,
								Function< Material, TextureAtlasSprite > spriteGetter, ModelState modelTransform, ResourceLocation modelLocation )
		{
			for( final ModelMesh mesh : meshes )
				mesh.addQuads( owner, modelBuilder, spriteGetter, modelTransform );
		}

		public void bake( CompositeRenderable.PartBuilder< ? > builder, IGeometryBakingContext configuration )
		{
			for( final ModelMesh mesh : meshes )
				mesh.bake( builder, configuration );
		}

		@SuppressWarnings( "null" )
		public Collection< Material > getTextures(	IGeometryBakingContext owner, Function< ResourceLocation, UnbakedModel > modelGetter,
													Set< com.mojang.datafixers.util.Pair< String, String > > missingTextureErrors )
		{
			return meshes.stream().flatMap(
				mesh -> mesh.mat != null ? Stream.of( UnbakedGeometryHelper.resolveDirtyMaterial( mesh.mat.diffuseColorMap, owner ) ) : Stream.of() )
				.collect( Collectors.toSet() );
		}

		public String name()
		{
			return name;
		}

		protected void addNamesRecursively( Set< String > names )
		{
			names.add( name() );
		}
	}

	public record ModelSettings(	@NotNull ResourceLocation modelLocation, boolean automaticCulling, boolean shadeQuads, boolean flipV,
									boolean emissiveAmbient, @Nullable String mtlOverride )
	{}

	private class ModelMesh
	{
		@Nullable
		public ObjMaterialLibrary.Material	mat;
		@Nullable
		public String						smoothingGroup;
		public final List< int[][] >		faces	= Lists.newArrayList();

		public ModelMesh( @Nullable ObjMaterialLibrary.Material currentMat, @Nullable String currentSmoothingGroup )
		{
			mat				= currentMat;
			smoothingGroup	= currentSmoothingGroup;
		}

		@SuppressWarnings( "null" )
		public void addQuads(	IGeometryBakingContext owner, IModelBuilder< ? > modelBuilder, Function< Material, TextureAtlasSprite > spriteGetter,
								ModelState modelTransform )
		{
			if( mat == null )
				return;
			final TextureAtlasSprite	texture		= spriteGetter.apply( UnbakedGeometryHelper.resolveDirtyMaterial( mat.diffuseColorMap, owner ) );
			final int					tintIndex	= mat.diffuseTintIndex;
			final Vector4f				colorTint	= mat.diffuseColor;

			for( final int[][] face : faces )
			{
				final Pair< BakedQuad,
							Direction > quad = makeQuad( face, tintIndex, colorTint, mat.ambientColor, texture, modelTransform.getRotation() );
				if( quad.getRight() == null )
					modelBuilder.addUnculledFace( quad.getLeft() );
				else
					modelBuilder.addCulledFace( quad.getRight(), quad.getLeft() );
			}
		}

		public void bake( CompositeRenderable.PartBuilder< ? > builder, IGeometryBakingContext configuration )
		{
			final ObjMaterialLibrary.Material mat = this.mat;
			if( mat == null )
				return;
			final int		tintIndex	= mat.diffuseTintIndex;
			final Vector4f	colorTint	= mat.diffuseColor;

			final List< BakedQuad > quads = new ArrayList<>();

			for( final var face : faces )
			{
				final var pair = makeQuad( face, tintIndex, colorTint, mat.ambientColor, UnitTextureAtlasSprite.INSTANCE, Transformation.identity() );
				quads.add( pair.getLeft() );
			}

			final ResourceLocation	textureLocation	= UnbakedGeometryHelper.resolveDirtyMaterial( mat.diffuseColorMap, configuration ).texture();
			final ResourceLocation	texturePath		= new ResourceLocation( textureLocation.getNamespace(),
				"textures/" + textureLocation.getPath() + ".png" );

			builder.addMesh( texturePath, quads );
		}
	}

	public static final Logger LOGGER = LogManager.getLogger( ModInfo.MOD_ID );

	private static Vector4f					COLOR_WHITE		= new Vector4f( 1, 1, 1, 1 );
	private static Vec2[]					DEFAULT_COORDS	= { new Vec2( 0, 0 ), new Vec2( 0, 1 ), new Vec2( 1, 1 ), new Vec2( 1, 0 ), };
	private final Map< String, ModelGroup >	parts			= Maps.newHashMap();

	private final List< Vector3f >	positions	= Lists.newArrayList();
	private final List< Vec2 >		texCoords	= Lists.newArrayList();
	private final List< Vector3f >	normals		= Lists.newArrayList();
	private final List< Vector4f >	colors		= Lists.newArrayList();

	public final boolean	automaticCulling;
	public final boolean	shadeQuads;
	public final boolean	flipV;
	public final boolean	emissiveAmbient;
	@Nullable
	public final String		mtlOverride;

	public final ResourceLocation modelLocation;

	private final Map< String, String > deprecationWarnings;

	private XOBJModel( ModelSettings settings, Map< String, String > deprecationWarnings )
	{
		modelLocation				= settings.modelLocation;
		automaticCulling			= settings.automaticCulling;
		shadeQuads					= settings.shadeQuads;
		flipV						= settings.flipV;
		emissiveAmbient				= settings.emissiveAmbient;
		mtlOverride					= settings.mtlOverride;
		this.deprecationWarnings	= deprecationWarnings;
	}

	public static XOBJModel parse( ObjTokenizer tokenizer, ModelSettings settings ) throws IOException
	{
		return parse( tokenizer, settings, Map.of() );
	}

	@SuppressWarnings( "null" )
	public static XOBJModel parse( ObjTokenizer tokenizer, ModelSettings settings, Map< String, String > deprecationWarnings ) throws IOException
	{
		final ResourceLocation	modelLocation					= settings.modelLocation;
		final String			materialLibraryOverrideLocation	= settings.mtlOverride;
		final XOBJModel			model							= new XOBJModel( settings, deprecationWarnings );

		// for relative references to material libraries
		final String	modelDomain	= modelLocation.getNamespace();
		String			modelPath	= modelLocation.getPath();
		final int		lastSlash	= modelPath.lastIndexOf( '/' );
		if( lastSlash >= 0 )
			modelPath = modelPath.substring( 0, lastSlash + 1 ); // include the '/'
		else
			modelPath = "";

		ObjMaterialLibrary			mtllib					= ObjMaterialLibrary.EMPTY;
		ObjMaterialLibrary.Material	currentMat				= null;
		String						currentSmoothingGroup	= null;
		ModelGroup					currentGroup			= null;
		ModelObject					currentObject			= null;
		ModelMesh					currentMesh				= null;

		boolean objAboveGroup = false;

		if( materialLibraryOverrideLocation != null )
		{
			final String lib = materialLibraryOverrideLocation;
			if( lib.contains( ":" ) )
				mtllib = ObjLoader.INSTANCE.loadMaterialLibrary( new ResourceLocation( lib ) );
			else
				mtllib = ObjLoader.INSTANCE.loadMaterialLibrary( new ResourceLocation( modelDomain, modelPath + lib ) );
		}

		String[] line;
		while( ( line = tokenizer.readAndSplitLine( true ) ) != null )
			switch( line[0] )
			{
				case "mtllib": // Loads material library
				{
					if( materialLibraryOverrideLocation != null )
						break;

					final String lib = line[1];
					if( lib.contains( ":" ) )
						mtllib = ObjLoader.INSTANCE.loadMaterialLibrary( new ResourceLocation( lib ) );
					else
						mtllib = ObjLoader.INSTANCE.loadMaterialLibrary( new ResourceLocation( modelDomain, modelPath + lib ) );
					break;
				}

				case "usemtl": // Sets the current material (starts new mesh)
				{
					final String						mat		= Strings.join( Arrays.copyOfRange( line, 1, line.length ), " " );
					final ObjMaterialLibrary.Material	newMat	= mtllib.getMaterial( mat );
					if( !Objects.equals( newMat, currentMat ) )
					{
						currentMat = newMat;
						if( currentMesh != null && currentMesh.mat == null && currentMesh.faces.size() == 0 )
							currentMesh.mat = currentMat;
						else
							// Start new mesh
							currentMesh = null;
					}
					break;
				}

				case "v": // Vertex
					model.positions.add( parseVector4To3( line ) );
					break;

				case "vt": // Vertex tex coord
					model.texCoords.add( parseVector2( line ) );
					break;

				case "vn": // Vertex normal
					model.normals.add( parseVector3( line ) );
					break;

				case "vc": // Vertex color (non-standard)
					model.colors.add( parseVector4( line ) );
					break;

				case "f": // Face
				{
					if( currentMesh == null )
					{
						currentMesh = model.new ModelMesh( currentMat, currentSmoothingGroup );
						if( currentObject != null )
							currentObject.meshes.add( currentMesh );
						else
						{
							if( currentGroup == null )
							{
								currentGroup = model.new ModelGroup( "" );
								model.parts.put( "", currentGroup );
							}
							currentGroup.meshes.add( currentMesh );
						}
					}

					final int[][] vertices = new int[line.length - 1][];
					for( int i = 0; i < vertices.length; i++ )
					{
						final String	vertexData	= line[i + 1];
						final String[]	vertexParts	= vertexData.split( "/" );
						final int[]		vertex		= Arrays.stream( vertexParts )
							.mapToInt( num -> Strings.isNullOrEmpty( num ) ? 0 : Integer.parseInt( num ) ).toArray();
						if( vertex[0] < 0 )
							vertex[0] = model.positions.size() + vertex[0];
						else
							vertex[0]--;
						if( vertex.length > 1 )
						{
							if( vertex[1] < 0 )
								vertex[1] = model.texCoords.size() + vertex[1];
							else
								vertex[1]--;
							if( vertex.length > 2 )
							{
								if( vertex[2] < 0 )
									vertex[2] = model.normals.size() + vertex[2];
								else
									vertex[2]--;
								if( vertex.length > 3 )
									if( vertex[3] < 0 )
										vertex[3] = model.colors.size() + vertex[3];
									else
										vertex[3]--;
							}
						}
						vertices[i] = vertex;
					}

					currentMesh.faces.add( vertices );

					break;
				}

				case "s": // Smoothing group (starts new mesh)
				{
					final String smoothingGroup = "off".equals( line[1] ) ? null : line[1];
					if( !Objects.equals( currentSmoothingGroup, smoothingGroup ) )
					{
						currentSmoothingGroup = smoothingGroup;
						if( currentMesh != null && currentMesh.smoothingGroup == null && currentMesh.faces.size() == 0 )
							currentMesh.smoothingGroup = currentSmoothingGroup;
						else
							// Start new mesh
							currentMesh = null;
					}
					break;
				}

				case "g":
				{
					final String name = line[1];
					if( objAboveGroup )
					{
						currentObject = model.new ModelObject( currentGroup.name() + "/" + name );
						currentGroup.parts.put( name, currentObject );
					} else
					{
						currentGroup = model.new ModelGroup( name );
						model.parts.put( name, currentGroup );
						currentObject = null;
					}
					// Start new mesh
					currentMesh = null;
					break;
				}

				case "o":
				{
					final String name = line[1];
					if( objAboveGroup || currentGroup == null )
					{
						objAboveGroup = true;

						currentGroup = model.new ModelGroup( name );
						model.parts.put( name, currentGroup );
						currentObject = null;
					} else
					{
						currentObject = model.new ModelObject( currentGroup.name() + "/" + name );
						currentGroup.parts.put( name, currentObject );
					}
					// Start new mesh
					currentMesh = null;
					break;
				}
			}
		return model;
	}

	private static Vec2 parseVector2( String[] line )
	{
		return switch( line.length )
		{
			case 1 -> new Vec2( 0, 0 );
			case 2 -> new Vec2( Float.parseFloat( line[1] ), 0 );
			default -> new Vec2( Float.parseFloat( line[1] ), Float.parseFloat( line[2] ) );
		};
	}

	private static Vector3f parseVector3( String[] line )
	{
		return switch( line.length )
		{
			case 1 -> new Vector3f( 0, 0, 0 );
			case 2 -> new Vector3f( Float.parseFloat( line[1] ), 0, 0 );
			case 3 -> new Vector3f( Float.parseFloat( line[1] ), Float.parseFloat( line[2] ), 0 );
			default -> new Vector3f( Float.parseFloat( line[1] ), Float.parseFloat( line[2] ), Float.parseFloat( line[3] ) );
		};
	}

	private static Vector3f parseVector4To3( String[] line )
	{
		final Vector4f vec4 = parseVector4( line );
		return new Vector3f( vec4.x() / vec4.w(), vec4.y() / vec4.w(), vec4.z() / vec4.w() );
	}

	static Vector4f parseVector4( String[] line )
	{
		return switch( line.length )
		{
			case 1 -> new Vector4f( 0, 0, 0, 1 );
			case 2 -> new Vector4f( Float.parseFloat( line[1] ), 0, 0, 1 );
			case 3 -> new Vector4f( Float.parseFloat( line[1] ), Float.parseFloat( line[2] ), 0, 1 );
			case 4 -> new Vector4f( Float.parseFloat( line[1] ), Float.parseFloat( line[2] ), Float.parseFloat( line[3] ), 1 );
			default -> new Vector4f( Float.parseFloat( line[1] ), Float.parseFloat( line[2] ), Float.parseFloat( line[3] ),
				Float.parseFloat( line[4] ) );
		};
	}

	public Collection< Material > getMaterials( IGeometryBakingContext context, Function< ResourceLocation, UnbakedModel > modelGetter,
												Set< com.mojang.datafixers.util.Pair< String, String > > missingTextureErrors )
	{
		final Set< Material > combined = Sets.newHashSet();
		for( final ModelGroup part : parts.values() )
			combined.addAll( part.getTextures( context, modelGetter, missingTextureErrors ) );
		return combined;
	}

	private Pair< BakedQuad, Direction > makeQuad(	int[][] indices, int tintIndex, Vector4f colorTint, Vector4f ambientColor,
													TextureAtlasSprite texture, Transformation transform )
	{
		boolean needsNormalRecalculation = false;
		for( final int[] ints : indices )
			needsNormalRecalculation |= ints.length < 3;
		Vector3f faceNormal = new Vector3f( 0, 0, 0 );
		if( needsNormalRecalculation )
		{
			final Vector3f	a	= positions.get( indices[0][0] );
			final Vector3f	ab	= positions.get( indices[1][0] );
			final Vector3f	ac	= positions.get( indices[2][0] );
			final Vector3f	abs	= new Vector3f( ab );
			abs.sub( a );
			final Vector3f acs = new Vector3f( ac );
			acs.sub( a );
			abs.cross( acs );
			abs.normalize();
			faceNormal = abs;
		}

		final var quadBaker = new QuadBakingVertexConsumer.Buffered();

		quadBaker.setSprite( texture );
		quadBaker.setTintIndex( tintIndex );

		int uv2 = 0;
		if( emissiveAmbient )
		{
			final int fakeLight = (int)( ( ambientColor.x() + ambientColor.y() + ambientColor.z() ) * 15 / 3.0f );
			uv2 = LightTexture.pack( fakeLight, fakeLight );
			quadBaker.setShade( fakeLight == 0 && shadeQuads );
		} else
			quadBaker.setShade( shadeQuads );

		final boolean hasTransform = !transform.isIdentity();
		// The incoming transform is referenced on the center of the block, but our coords are referenced on the corner
		final Transformation transformation = hasTransform ? transform.blockCenterToCorner() : transform;

		final Vector4f[]	pos		= new Vector4f[4];
		final Vector3f[]	norm	= new Vector3f[4];

		for( int i = 0; i < 4; i++ )
		{
			final int[]		index		= indices[Math.min( i, indices.length - 1 )];
			final Vector4f	position	= new Vector4f( positions.get( index[0] ), 1 );
			final Vec2		texCoord	= index.length >= 2 && texCoords.size() > 0 ? texCoords.get( index[1] ) : DEFAULT_COORDS[i];
			final Vector3f	norm0		= !needsNormalRecalculation && index.length >= 3 && normals.size() > 0 ? normals.get( index[2] ) : faceNormal;
			Vector3f		normal		= norm0;
			final Vector4f	color		= index.length >= 4 && colors.size() > 0 ? colors.get( index[3] ) : COLOR_WHITE;
			if( hasTransform )
			{
				normal = new Vector3f( norm0 );
				transformation.transformPosition( position );
				transformation.transformNormal( normal );
			}
			final Vector4f tintedColor = new Vector4f( color.x() * colorTint.x(), color.y() * colorTint.y(), color.z() * colorTint.z(),
				color.w() * colorTint.w() );
			quadBaker.vertex( position.x(), position.y(), position.z() );
			quadBaker.color( tintedColor.x(), tintedColor.y(), tintedColor.z(), tintedColor.w() );
			// UV co-ordinates are taken directly from the model, not adjusted for texture atlas
			// This allows for changing the texture to anything we want
			quadBaker.uv( texCoord.x, flipV ? 1 - texCoord.y : texCoord.y );
			quadBaker.uv2( uv2 );
			quadBaker.normal( normal.x(), normal.y(), normal.z() );
			if( i == 0 )
				quadBaker.setDirection( Direction.getNearest( normal.x(), normal.y(), normal.z() ) );
			quadBaker.endVertex();
			pos[i]	= position;
			norm[i]	= normal;
		}

		Direction cull = null;
		if( automaticCulling )
			if( Mth.equal( pos[0].x(), 0 ) && // vertex.position.x
				Mth.equal( pos[1].x(), 0 ) && Mth.equal( pos[2].x(), 0 ) && Mth.equal( pos[3].x(), 0 ) && norm[0].x() < 0 )
				cull = Direction.WEST;
			else if( Mth.equal( pos[0].x(), 1 ) && // vertex.position.x
						Mth.equal( pos[1].x(), 1 ) && Mth.equal( pos[2].x(), 1 ) && Mth.equal( pos[3].x(), 1 ) && norm[0].x() > 0 )
				cull = Direction.EAST;
			else if( Mth.equal( pos[0].z(), 0 ) && // vertex.position.z
						Mth.equal( pos[1].z(), 0 ) && Mth.equal( pos[2].z(), 0 ) && Mth.equal( pos[3].z(), 0 ) && norm[0].z() < 0 )
				cull = Direction.NORTH; // can never remember
			else if( Mth.equal( pos[0].z(), 1 ) && // vertex.position.z
						Mth.equal( pos[1].z(), 1 ) && Mth.equal( pos[2].z(), 1 ) && Mth.equal( pos[3].z(), 1 ) && norm[0].z() > 0 )
				cull = Direction.SOUTH;
			else if( Mth.equal( pos[0].y(), 0 ) && // vertex.position.y
						Mth.equal( pos[1].y(), 0 ) && Mth.equal( pos[2].y(), 0 ) && Mth.equal( pos[3].y(), 0 ) && norm[0].y() < 0 )
				cull = Direction.DOWN; // can never remember
			else if( Mth.equal( pos[0].y(), 1 ) && // vertex.position.y
						Mth.equal( pos[1].y(), 1 ) && Mth.equal( pos[2].y(), 1 ) && Mth.equal( pos[3].y(), 1 ) && norm[0].y() > 0 )
				cull = Direction.UP;

		return Pair.of( quadBaker.getQuad(), cull );
	}

	@Override
	protected void addQuads(	IGeometryBakingContext owner, IModelBuilder< ? > modelBuilder, ModelBaker baker,
								Function< Material, TextureAtlasSprite > spriteGetter, ModelState modelTransform, ResourceLocation modelLocation )
	{
		for( final var entry : deprecationWarnings.entrySet() )
			LOGGER.warn( "Model \"" + modelLocation + "\" is using the deprecated \"" + entry.getKey() + "\" field in its OBJ model instead of \""
							+ entry.getValue() + "\". This field will be removed in 1.20." );

		parts.values().stream().filter( part -> owner.isComponentVisible( part.name(), true ) )
			.forEach( part -> part.addQuads( owner, modelBuilder, baker, spriteGetter, modelTransform, modelLocation ) );
	}
}
