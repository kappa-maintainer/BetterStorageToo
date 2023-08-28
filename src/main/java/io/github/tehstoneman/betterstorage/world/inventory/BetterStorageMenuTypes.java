package io.github.tehstoneman.betterstorage.world.inventory;

import com.google.common.base.Preconditions;

import io.github.tehstoneman.betterstorage.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BetterStorageMenuTypes
{
	public static final DeferredRegister< MenuType< ? > > REGISTERY = DeferredRegister.create( ForgeRegistries.MENU_TYPES, ModInfo.MOD_ID );

	public static final RegistryObject< MenuType< CardboardBoxMenu > >			CARDBOARD_BOX		= REGISTERY.register( "cardboard_box",
		() -> IForgeMenuType.create( ( windowID, inv, data ) ->
																										{
																											final var pos = data.readBlockPos();
																											return new CardboardBoxMenu( windowID,
																												inv, getClientLevel(), pos );
																										} ) );
	public static final RegistryObject< MenuType< CrateMenu > >					CRATE				= REGISTERY.register( "crate",
		() -> IForgeMenuType.create( ( windowID, inv, data ) ->
																										{
																											final var pos = data.readBlockPos();
																											return new CrateMenu( windowID, inv,
																												getClientLevel(), pos );
																										} ) );
	public static final RegistryObject< MenuType< KeyringContainerMenu > >		KEYRING				= REGISTERY.register( "keyring",
		() -> IForgeMenuType.create( ( windowID, inv, data ) ->
																										{
																											final var keyring		= data.readItem();
																											final var protectedIndex = data.readInt();
																											return new KeyringContainerMenu( windowID,
																												inv, keyring, protectedIndex );
																										} ) );
	public static final RegistryObject< MenuType< LockerContainerMenu > >			LOCKER				= REGISTERY.register( "locker",
		() -> IForgeMenuType.create( ( windowID, inv, data ) ->
																										{
																											final var pos = data.readBlockPos();
																											return new LockerContainerMenu( windowID, inv,
																												getClientLevel(), pos );
																										} ) );
	public static final RegistryObject< MenuType< ReinforcedChestContainerMenu > >	REINFORCED_CHEST	= REGISTERY.register( "reinforced_chest",
		() -> IForgeMenuType.create( ( windowID, inv, data ) ->
																										{
																											final var pos = data.readBlockPos();
																											return new ReinforcedChestContainerMenu(
																												windowID, inv, getClientLevel(),
																												pos );
																										} ) );
	public static final RegistryObject< MenuType< ReinforcedLockerContainerMenu > >	REINFORCED_LOCKER	= REGISTERY.register( "reinforced_locker",
		() -> IForgeMenuType.create( ( windowID, inv, data ) ->
																										{
																											final var pos = data.readBlockPos();
																											return new ReinforcedLockerContainerMenu(
																												windowID, inv, getClientLevel(),
																												pos );
																										} ) );
	public static final RegistryObject< MenuType< ConfigContainerMenu > >		CONFIG				= REGISTERY.register( "config_container",
		() -> IForgeMenuType.create( ( windowID, inv, data ) ->
																										{
																											final var pos = data.readBlockPos();
																											return new ConfigContainerMenu( windowID,
																												inv, getClientLevel(), pos );
																										} ) );

	public static ClientLevel getClientLevel()
	{
		final var instance = Minecraft.getInstance();
		return Preconditions.checkNotNull( instance.level, "ClientLevel not loaded." );
	}
}
