package io.github.tehstoneman.betterstorage.init;

import io.github.tehstoneman.betterstorage.ModInfo;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber( modid = ModInfo.MOD_ID, bus = Bus.MOD )
public class BetterStorageCreativeModeTab// extends CreativeModeTab
{
	static CreativeModeTab TAB;

	/*
	@SubscribeEvent
	public static void register( final CreativeModeTabEvent.Register event )
	{
		// final Supplier<ItemStack> sword = () -> SwordUpgrades.upgradeSword(Items.STONE_SWORD);

		// @formatter:off
		TAB = event.registerCreativeModeTab( new ResourceLocation( ModInfo.MOD_ID, "tab" ),
			builder -> builder
				.title( Component.translatable( "itemGroup.better_storage_too" ) )
				.icon( () -> new ItemStack( BetterStorageBlocks.CRATE.get() ) )
				.displayItems( ( enabledFlags, populator, hasPermissions ) ->
					{
						// output.accept(sword.get());

						// add(output, ModBlocks.orderedItems());

						// add(output, ModItems.orderedItems());

						// add(output, ModFluids.orderedItems());
					} ) );
		// @formatter:on
	}
	*/

	/*
	 * public BetterStorageCreativeModeTab()
	 * {
	 * super( "better_storage_too" );
	 * setEnchantmentCategories( KeyEnchantment.KEY, LockEnchantment.LOCK );
	 * }
	 */
}
