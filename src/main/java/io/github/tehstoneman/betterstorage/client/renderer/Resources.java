package io.github.tehstoneman.betterstorage.client.renderer;

import io.github.tehstoneman.betterstorage.util.BetterStorageResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class Resources
{
	// Generic textures
	public static final ResourceLocation	TEXTURE_EMPTY	= new BetterStorageResource( "textures/empty.png" );
	//public static final ResourceLocation	TEXTURE_WHITE	= new BetterStorageResource( "white" );

	// Gui Textures
	public static final ResourceLocation	CONTAINER_CRATE			= new BetterStorageResource( "textures/gui/crate.png" );
	public static final ResourceLocation	CONTAINER_EXPANDABLE	= new BetterStorageResource( "textures/gui/reinforcedchest.png" );
	public static final ResourceLocation	CONTAINER_GENERIC		= new ResourceLocation( "textures/gui/container/generic_54.png" );
	public static final ResourceLocation	CONTAINER_CONFIG		= new BetterStorageResource( "textures/gui/key_config.png" );

	// Models
	public static final ResourceLocation	MODEL_REINFORCED_CHEST				= new BetterStorageResource( "block/reinforced_chest" );
	public static final ResourceLocation	MODEL_REINFORCED_CHEST_FRAME		= new BetterStorageResource( "block/reinforced_chest_frame" );
	public static final ResourceLocation	MODEL_REINFORCED_CHEST_LID			= new BetterStorageResource( "block/reinforced_chest_lid" );
	public static final ResourceLocation	MODEL_REINFORCED_CHEST_LID_FRAME	= new BetterStorageResource( "block/reinforced_chest_lid_frame" );

	public static final ResourceLocation	MODEL_REINFORCED_CHEST_LARGE			= new BetterStorageResource( "block/reinforced_chest_large" );
	public static final ResourceLocation	MODEL_REINFORCED_CHEST_LARGE_FRAME		=
																				new BetterStorageResource( "block/reinforced_chest_large_frame" );
	public static final ResourceLocation	MODEL_REINFORCED_CHEST_LID_LARGE		= new BetterStorageResource( "block/reinforced_chest_lid_large" );
	public static final ResourceLocation	MODEL_REINFORCED_CHEST_LID_LARGE_FRAME	= new BetterStorageResource(
		"block/reinforced_chest_lid_large_frame" );

	public static final ResourceLocation	MODEL_LOCKER								= new BetterStorageResource( "block/locker" );
	public static final ResourceLocation	MODEL_LOCKER_DOOR_L							= new BetterStorageResource( "block/locker_door_l" );
	public static final ResourceLocation	MODEL_LOCKER_DOOR_R							= new BetterStorageResource( "block/locker_door_r" );
	public static final ResourceLocation	MODEL_LOCKER_LARGE							= new BetterStorageResource( "block/locker_large" );
	public static final ResourceLocation	MODEL_LOCKER_DOOR_LARGE_L					= new BetterStorageResource( "block/locker_door_large_l" );
	public static final ResourceLocation	MODEL_LOCKER_DOOR_LARGE_R					= new BetterStorageResource( "block/locker_door_large_r" );
	public static final ResourceLocation	MODEL_REINFORCED_LOCKER						= new BetterStorageResource( "block/reinforced_locker" );
	public static final ResourceLocation	MODEL_REINFORCED_LOCKER_FRAME				=
																			new BetterStorageResource( "block/reinforced_locker_frame" );
	public static final ResourceLocation	MODEL_REINFORCED_LOCKER_DOOR_L				=
																			new BetterStorageResource( "block/reinforced_locker_door_l" );
	public static final ResourceLocation	MODEL_REINFORCED_LOCKER_DOOR_R				=
																			new BetterStorageResource( "block/reinforced_locker_door_r" );
	public static final ResourceLocation	MODEL_REINFORCED_LOCKER_DOOR_FRAME_L		= new BetterStorageResource(
		"block/reinforced_locker_door_frame_l" );
	public static final ResourceLocation	MODEL_REINFORCED_LOCKER_DOOR_FRAME_R		= new BetterStorageResource(
		"block/reinforced_locker_door_frame_r" );
	public static final ResourceLocation	MODEL_REINFORCED_LOCKER_LARGE				=
																			new BetterStorageResource( "block/reinforced_locker_large" );
	public static final ResourceLocation	MODEL_REINFORCED_LOCKER_LARGE_FRAME			=
																				new BetterStorageResource( "block/reinforced_locker_large_frame" );
	public static final ResourceLocation	MODEL_REINFORCED_LOCKER_DOOR_LARGE_L		= new BetterStorageResource(
		"block/reinforced_locker_door_large_l" );
	public static final ResourceLocation	MODEL_REINFORCED_LOCKER_DOOR_LARGE_R		= new BetterStorageResource(
		"block/reinforced_locker_door_large_r" );
	public static final ResourceLocation	MODEL_REINFORCED_LOCKER_DOOR_LARGE_FRAME_L	= new BetterStorageResource(
		"block/reinforced_locker_door_large_frame_l" );
	public static final ResourceLocation	MODEL_REINFORCED_LOCKER_DOOR_LARGE_FRAME_R	= new BetterStorageResource(
		"block/reinforced_locker_door_large_frame_r" );

	// Model textures
	//public static final ResourceLocation	TEXTURE_CHEST_REINFORCED			= new BetterStorageResource( "model/reinforced_chest" );
	//public static final ResourceLocation	TEXTURE_CHEST_REINFORCED_DOUBLE		= new BetterStorageResource( "entity/chest/reinforced_double" );
	//public static final ResourceLocation	TEXTURE_LOCKER_NORMAL				= new BetterStorageResource( "entity/locker/normal" );
	//public static final ResourceLocation	TEXTURE_LOCKER_NORMAL_DOUBLE		= new BetterStorageResource( "entity/locker/normal_double" );
	//public static final ResourceLocation	TEXTURE_LOCKER_REINFORCED			= new BetterStorageResource( "entity/locker/reinforced" );
	//public static final ResourceLocation	TEXTURE_LOCKER_REINFORCED_DOUBLE	= new BetterStorageResource( "entity/locker/reinforced_double" );
	public static final ResourceLocation	TEXTURE_REINFORCED_FRAME			= new BetterStorageResource( "model/reinforced_frame" );

	public static final ResourceLocation	TEXTURE_CARDBOARD_ARMOR		= new BetterStorageResource( "textures/model/armor/cardboard_armor.png" );
	public static final ResourceLocation	TEXTURE_CARDBOARD_LEGGINGS	= new BetterStorageResource(
		"textures/model/armor/cardboard_armor_leggings.png" );
}
