package io.github.tehstoneman.betterstorage.client;

import io.github.tehstoneman.betterstorage.ModInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = ModInfo.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientSetup
{
	/*
	 * @SubscribeEvent
	 * public static void onEntityRenderersEvent( final EntityRenderersEvent.RegisterRenderers event )
	 * {
	 * event.registerBlockEntityRenderer( BetterStorageBlockEntityTypes.GLASS_TANK.get(), TankBlockEntityRenderer::new );
	 * event.registerBlockEntityRenderer( BetterStorageBlockEntityTypes.LOCKER.get(), LockerBlockEntityRenderer::new );
	 * event.registerBlockEntityRenderer( BetterStorageBlockEntityTypes.REINFORCED_CHEST.get(), ReinforcedChestBlockEntityRenderer::new );
	 * event.registerBlockEntityRenderer( BetterStorageBlockEntityTypes.REINFORCED_LOCKER.get(), ReinforcedLockerBlockEntityRenderer::new );
	 * }
	 */

	/*
	 * @SubscribeEvent
	 * public static void onFMLClientSetupEvent( final FMLClientSetupEvent event )
	 * {
	 * MenuScreens.register( BetterStorageMenuTypes.CARDBOARD_BOX.get(), CardboardBoxContainerScreen::new );
	 * MenuScreens.register( BetterStorageMenuTypes.CRATE.get(), CrateContainerScreen::new );
	 * MenuScreens.register( BetterStorageMenuTypes.KEYRING.get(), KeyringContainerScreen::new );
	 * MenuScreens.register( BetterStorageMenuTypes.LOCKER.get(), LockerContainerScreen::new );
	 * MenuScreens.register( BetterStorageMenuTypes.REINFORCED_CHEST.get(), ReinforcedChestContainerScreen::new );
	 * MenuScreens.register( BetterStorageMenuTypes.REINFORCED_LOCKER.get(), ReinforcedLockerContainerScreen::new );
	 *
	 * MenuScreens.register( BetterStorageMenuTypes.CONFIG.get(), ConfigContainerScreen::new );
	 * }
	 */

	/*
	 * @SubscribeEvent
	 * public static void onModelEvent( ModelEvent.RegisterAdditional event )
	 * {
	 * event.register( Resources.MODEL_REINFORCED_CHEST );
	 * event.register( Resources.MODEL_REINFORCED_CHEST_FRAME );
	 * event.register( Resources.MODEL_REINFORCED_CHEST_LID );
	 * event.register( Resources.MODEL_REINFORCED_CHEST_LID_FRAME );
	 * event.register( Resources.MODEL_REINFORCED_CHEST_LARGE );
	 * event.register( Resources.MODEL_REINFORCED_CHEST_LARGE_FRAME );
	 * event.register( Resources.MODEL_REINFORCED_CHEST_LID_LARGE );
	 * event.register( Resources.MODEL_REINFORCED_CHEST_LID_LARGE_FRAME );
	 *
	 * event.register( Resources.MODEL_LOCKER );
	 * event.register( Resources.MODEL_LOCKER_DOOR_L );
	 * event.register( Resources.MODEL_LOCKER_DOOR_R );
	 * event.register( Resources.MODEL_LOCKER_LARGE );
	 * event.register( Resources.MODEL_LOCKER_DOOR_LARGE_L );
	 * event.register( Resources.MODEL_LOCKER_DOOR_LARGE_R );
	 * event.register( Resources.MODEL_REINFORCED_LOCKER );
	 * event.register( Resources.MODEL_REINFORCED_LOCKER_FRAME );
	 * event.register( Resources.MODEL_REINFORCED_LOCKER_DOOR_L );
	 * event.register( Resources.MODEL_REINFORCED_LOCKER_DOOR_R );
	 * event.register( Resources.MODEL_REINFORCED_LOCKER_DOOR_FRAME_L );
	 * event.register( Resources.MODEL_REINFORCED_LOCKER_DOOR_FRAME_R );
	 * event.register( Resources.MODEL_REINFORCED_LOCKER_LARGE );
	 * event.register( Resources.MODEL_REINFORCED_LOCKER_LARGE_FRAME );
	 * event.register( Resources.MODEL_REINFORCED_LOCKER_DOOR_LARGE_L );
	 * event.register( Resources.MODEL_REINFORCED_LOCKER_DOOR_LARGE_R );
	 * event.register( Resources.MODEL_REINFORCED_LOCKER_DOOR_LARGE_FRAME_L );
	 * event.register( Resources.MODEL_REINFORCED_LOCKER_DOOR_LARGE_FRAME_R );
	 * }
	 */

	/*
	 * @SubscribeEvent
	 * public static void onModelEvent( ModelEvent.RegisterGeometryLoaders event )
	 * {
	 * event.register( "xobj", XOBJLoader.INSTANCE );
	 * }
	 */

	/*
	 * @SubscribeEvent
	 * public static void onRegisterColorHandlersEvent( RegisterColorHandlersEvent.Block event )
	 * {
	 * event.register( new CardboardColor(), BetterStorageBlocks.CARDBOARD_BOX.get() );
	 * }
	 */

	/*
	 * @SubscribeEvent
	 * public static void onRegisterColorHandlersEvent( RegisterColorHandlersEvent.Item event )
	 * {
	 * event.register( new KeyColor(), BetterStorageItems.KEY.get(), BetterStorageItems.LOCK.get() );
	 * event.register( new CardboardColor(), BetterStorageItems.CARDBOARD_AXE.get(), BetterStorageItems.CARDBOARD_BOOTS.get(),
	 * BetterStorageItems.CARDBOARD_CHESTPLATE.get(), BetterStorageItems.CARDBOARD_HELMET.get(), BetterStorageItems.CARDBOARD_HOE.get(),
	 * BetterStorageItems.CARDBOARD_LEGGINGS.get(), BetterStorageItems.CARDBOARD_PICKAXE.get(), BetterStorageItems.CARDBOARD_SHOVEL.get(),
	 * BetterStorageItems.CARDBOARD_SWORD.get(), BetterStorageBlocks.CARDBOARD_BOX.get() );
	 * }
	 */

	/*
	 * @SubscribeEvent
	 * public static void onTextureStitch( TextureStitchEvent.Pre event )
	 * {
	 * if( !event.getAtlas().location().equals( InventoryMenu.BLOCK_ATLAS ) )
	 * return;
	 *
	 * //event.addSprite( Resources.TEXTURE_CHEST_REINFORCED );
	 * //event.addSprite( Resources.TEXTURE_CHEST_REINFORCED_DOUBLE );
	 * //event.addSprite( Resources.TEXTURE_LOCKER_NORMAL );
	 * //event.addSprite( Resources.TEXTURE_LOCKER_NORMAL_DOUBLE );
	 * //event.addSprite( Resources.TEXTURE_LOCKER_REINFORCED );
	 * //event.addSprite( Resources.TEXTURE_LOCKER_REINFORCED_DOUBLE );
	 * //event.addSprite( Resources.TEXTURE_WHITE );
	 * }
	 */}
