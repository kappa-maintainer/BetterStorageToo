package io.github.tehstoneman.betterstorage.world.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

public class SlimeBucketItem extends BetterStorageItem
{
	private static Map< ResourceLocation, Handler > handlers = new HashMap<>();

	public SlimeBucketItem()
	{
		super( new Item.Properties() );
		// setContainerItem( Items.BUCKET );
		// setHasSubtypes( true );
		// setMaxStackSize( 0 );
		// setMaxDamage( 0 );
		// setHasSubtypes( true );
		// this.setCreativeTab( BetterStorage.creativeTab );
	}

	/*
	 * @Override
	 * public int getMetadata( int damage )
	 * {
	 * return damage;
	 * }
	 */

	/*
	 * @Override
	 * public void getSubItems( CreativeTabs tab, NonNullList< ItemStack > subItems )
	 * {
	 * for( final EnumSlime slime : EnumSlime.values() )
	 * subItems.add( new ItemStack( this, 1, slime.getMetadata() ) );
	 * }
	 */

	/*
	 * @Override
	 * public String getUnlocalizedName( ItemStack stack )
	 * {
	 * // final int meta = stack.getMetadata();
	 * return super.getUnlocalizedName() + "." + EnumSlime.byMetadata( 0 ).getUnlocalizedName();
	 * }
	 */

	/*
	 * @Override
	 *
	 * @SideOnly( Side.CLIENT )
	 * public boolean hasEffect( ItemStack stack )
	 * {
	 * return stack.isItemEnchanted() || stack.hasTagCompound() && stack.getTagCompound().hasKey( "effects" );
	 * }
	 */

	/*
	 * @Override
	 *
	 * @SideOnly( Side.CLIENT )
	 * public void appendHoverText( ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag toolTipFlag )
	 * {
	 * final EnumSlime slime = EnumSlime.byMetadata( stack.getMetadata() );
	 * final ResourceLocation resourceLocation = slime.getResourceLocation();
	 * final Handler handler = getHandler( resourceLocation );
	 * String name = null;
	 * if( stack.hasTagCompound() )
	 * {
	 * final NBTTagCompound compound = stack.getTagCompound();
	 * if( compound.hasKey( "name" ) )
	 * name = compound.getString( "name" );
	 * }
	 * if( name != null || toolTipFlag.isAdvanced() )
	 * {
	 * final String localName = name != null
	 * ? "\"" + name + "\"" + ( toolTipFlag.isAdvanced() ? " (" + BetterStorage.proxy.localize( handler.entityName ) + ")" : "" )
	 * : BetterStorage.proxy.localize( handler.entityName );
	 * tooltip.add( BetterStorage.proxy.localize( "tooltip.betterstorage.bucket.slime.contains", localName ) );
	 * }
	 *
	 * if( stack.hasTagCompound() )
	 * {
	 * final NBTTagCompound compound = stack.getTagCompound();
	 * if( compound.hasKey( "effects" ) )
	 * {
	 * final NBTTagList effectList = compound.getTagList( "effects", NBT.TAG_COMPOUND );
	 * if( effectList != null && handler != null )
	 * {
	 * final int max = toolTipFlag.isAdvanced() || GuiScreen.isShiftKeyDown() ? 6 : 3;
	 *
	 * for( int i = 0; i < Math.min( effectList.tagCount(), max ); i++ )
	 * {
	 * final PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT( effectList.getCompoundTagAt( i ) );
	 * final Potion potion = effect.getPotion();
	 * final int duration = (int)( effect.getDuration() * handler.durationMultiplier() );
	 *
	 * final StringBuilder str = new StringBuilder().append( potion.isBadEffect() ? ChatFormatting.RED : ChatFormatting.GRAY )
	 * .append( BetterStorage.proxy.localize( effect.getEffectName() ) );
	 *
	 * if( effect.getAmplifier() > 0 )
	 * str.append( " " ).append( BetterStorage.proxy.localize( "potion.potency." + effect.getAmplifier() ) );
	 *
	 * str.append( " (" ).append( StringUtils.ticksToElapsedTime( duration ) ).append( ")" );
	 *
	 * tooltip.add( str.toString() );
	 * }
	 *
	 * final int more = effectList.tagCount() - max;
	 *
	 * if( more > 0 )
	 * tooltip.add( ChatFormatting.DARK_GRAY.toString() + ChatFormatting.ITALIC + LanguageUtils
	 * .translateTooltip( "bucketSlime.more." + ( more == 1 ? "1" : "x" ), "%X%", Integer.toString( more ) ) );
	 * }
	 * }
	 * }
	 * }
	 */

	/*
	 * @Override
	 * public EnumActionResult useOn( EntityPlayer player, Level worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,
	 * float hitZ )
	 * {
	 * final ItemStack stack = player.getItemInHand( hand );
	 * // If player is sneaking, eat slime
	 * // instead of placing it in the world.
	 * if( player.isSneaking() )
	 * return EnumActionResult.FAIL;
	 *
	 * if( worldIn.isRemote )
	 * return EnumActionResult.PASS;
	 *
	 * final ResourceLocation resourceLocation = getSlimeId( stack );
	 *
	 * final BlockPos blockpos = pos.offset( facing );
	 * final Handler handler = getHandler( resourceLocation );
	 * final double d0 = getYOffset( worldIn, blockpos );
	 * final Entity entity = ItemMonsterPlacer.spawnCreature( worldIn, handler.resourceLocation, blockpos.getX() + 0.5D, blockpos.getY() + d0,
	 * blockpos.getZ() + 0.5D );
	 *
	 * if( entity != null && handler != null && entity instanceof EntityLiving )
	 * {
	 * final EntityLiving slime = (EntityLiving)entity;
	 *
	 * handler.setSize( slime, 1 );
	 *
	 * if( stack.hasTagCompound() )
	 * {
	 * final NBTTagCompound compound = stack.getTagCompound();
	 * if( compound.hasKey( "name" ) )
	 * entity.setCustomNameTag( compound.getString( "name" ) );
	 * final NBTTagList effectList = compound.getTagList( "effects", NBT.TAG_COMPOUND );
	 * if( effectList != null )
	 * for( int i = 0; i < effectList.tagCount(); i++ )
	 * slime.addPotionEffect( PotionEffect.readCustomPotionEffectFromNBT( effectList.getCompoundTagAt( i ) ) );
	 * }
	 *
	 * if( !player.capabilities.isCreativeMode )
	 * player.setItemInHand( EnumHand.MAIN_HAND, new ItemStack( Items.BUCKET ) );
	 * }
	 *
	 * return EnumActionResult.SUCCESS;
	 * }
	 */

	// Eating slime

	/*
	 * @Override
	 * public EnumAction getItemUseAction( ItemStack stack )
	 * {
	 * return EnumAction.DRINK;
	 * }
	 */

	/*
	 * @Override
	 * public int getMaxItemUseDuration( ItemStack stack )
	 * {
	 * return 48;
	 * }
	 */

	/*
	 * @Override
	 * public ActionResult< ItemStack > use( Level worldIn, EntityPlayer playerIn, EnumHand handIn )
	 * {
	 * if( handIn == EnumHand.MAIN_HAND )
	 * {
	 * final ItemStack stack = playerIn.getItemInHand( handIn );
	 * // playerIn.canEat( true );
	 * playerIn.setActiveHand( handIn );
	 * return new ActionResult( EnumActionResult.SUCCESS, playerIn.getItemInHand( handIn ) );
	 * }
	 *
	 * return super.use( worldIn, playerIn, handIn );
	 * }
	 */

	/*
	 * @Override
	 * public ItemStack useOnFinish( ItemStack stack, Level worldIn, EntityLivingBase entityLiving )
	 * {
	 * if( entityLiving instanceof EntityPlayer )
	 * {
	 * final EntityPlayer player = (EntityPlayer)entityLiving;
	 * final Handler handler = getHandler( stack );
	 * if( handler != null )
	 * {
	 * player.getFoodStats().addStats( handler.foodAmount(), handler.saturationAmount() );
	 * if( stack.hasTagCompound() )
	 * {
	 * final NBTTagCompound compound = stack.getTagCompound();
	 * if( compound.hasKey( "effects" ) )
	 * {
	 * final NBTTagList effectList = compound.getTagList( "effects", NBT.TAG_COMPOUND );
	 * for( int i = 0; i < effectList.tagCount(); i++ )
	 * {
	 * PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT( effectList.getCompoundTagAt( i ) );
	 * final int duration = (int)( effect.getDuration() * handler.durationMultiplier() );
	 * effect = new PotionEffect( effect.getPotion(), duration, effect.getAmplifier() );
	 * player.addPotionEffect( effect );
	 * }
	 * }
	 * }
	 * handler.onEaten( player, false );
	 * }
	 * }
	 * return new ItemStack( Items.BUCKET );
	 * }
	 */

	// Helper functions

	/**
	 * Called when a player right clicks an entity with an empty bucket.
	 * If the entity clicked is a small slime, attempts to pick it up and
	 * sets the held item to a Slime in a Bucket containing that slime.
	 */
	/*
	 * public static void pickUpSlime( EntityPlayer player, EntityLiving slime )
	 * {
	 * final Handler handler = getHandler( slime );
	 * if( slime.isDead || handler == null || handler.getSize( slime ) != 1 )
	 * return;
	 *
	 * final ItemStack stack = new ItemStack( BetterStorageItems.SLIME_BUCKET, 1, EnumSlime.byName( handler.name ).getMetadata() );
	 *
	 * final String entityId = EntityList.getEntityString( slime );
	 * if( slime.hasCustomName() )
	 * {
	 * NBTTagCompound compound = new NBTTagCompound();
	 * if( stack.hasTagCompound() )
	 * compound = stack.getTagCompound();
	 * compound.setString( "name", slime.getCustomNameTag() );
	 * stack.setTagCompound( compound );
	 * }
	 *
	 * final Collection< PotionEffect > effects = slime.getActivePotionEffects();
	 * if( !effects.isEmpty() )
	 * {
	 * NBTTagCompound compound = new NBTTagCompound();
	 * if( stack.hasTagCompound() )
	 * compound = stack.getTagCompound();
	 * final NBTTagList effectList = new NBTTagList();
	 * for( final PotionEffect effect : effects )
	 * effectList.appendTag( effect.writeCustomPotionEffectToNBT( new NBTTagCompound() ) );
	 * compound.setTag( "effects", effectList );
	 * stack.setTagCompound( compound );
	 * }
	 * if( player.getMainHandItem().getCount() <= 1 )
	 * player.setItemInHand( EnumHand.MAIN_HAND, stack );
	 *
	 * else
	 * {
	 * player.getMainHandItem().shrink( 1 );
	 * if( !player.inventory.addItemStackToInventory( stack ) )
	 * player.dropItem( stack, true, false );
	 *
	 * else
	 * ( (EntityPlayerMP)player ).inventoryContainer.broadcastChanges();
	 * }
	 *
	 * slime.playSound( SoundEvents.ENTITY_SLIME_JUMP, 1.2F, 0.8F );
	 * slime.isDead = true;
	 * }
	 */

	/** Returns the slime ID from a slime bucket. */
	/*
	 * public static ResourceLocation getSlimeId( ItemStack stack )
	 * {
	 * return EnumSlime.byMetadata( stack.getMetadata() ).getResourceLocation();
	 * }
	 */

	/**
	 * Registers a slime handler.
	 *
	 * @param handler
	 *            The handler
	 */
	public static void registerHandler( Handler handler )
	{
		handlers.put( handler.resourceLocation, handler );
	}

	/**
	 * Returns the handler for an entity id, null if none.
	 *
	 * @param resourceLocation
	 *            ResourceLocation to query
	 * @return The handler
	 */
	public static Handler getHandler( ResourceLocation resourceLocation )
	{
		return handlers.get( resourceLocation );
	}

	/** Returns the handler for an entity, null if none. */
	/*
	 * public static Handler getHandler( EntityLiving slime )
	 * {
	 * return getHandler( EntityList.getKey( slime ) );
	 * }
	 */

	/** Returns the handler for slime bucket, null if none. */
	/*
	 * public static Handler getHandler( ItemStack stack )
	 * {
	 * return getHandler( getSlimeId( stack ) );
	 * }
	 */

	/*
	 * @Override
	 *
	 * @SideOnly( Side.CLIENT )
	 * public void registerItemModels()
	 * {
	 * for( final EnumSlime slime : EnumSlime.values() )
	 * ModelLoader.setCustomModelResourceLocation( this, slime.getMetadata(),
	 * new ModelResourceLocation( getRegistryName() + "_" + slime.getUnlocalizedName(), "inventory" ) );
	 * }
	 */

	// Copied from ItemMonsterPlacer (Spawn eggs)

	/*
	 * protected double getYOffset( Level p_190909_1_, BlockPos p_190909_2_ )
	 * {
	 * final AABB axisalignedbb = new AABB( p_190909_2_ ).expand( 0.0D, -1.0D, 0.0D );
	 * final List< AABB > list = p_190909_1_.getCollisionBoxes( (Entity)null, axisalignedbb );
	 *
	 * if( list.isEmpty() )
	 * return 0.0D;
	 * else
	 * {
	 * double d0 = axisalignedbb.minY;
	 *
	 * for( final AABB axisalignedbb1 : list )
	 * d0 = Math.max( axisalignedbb1.maxY, d0 );
	 *
	 * return d0 - p_190909_2_.getY();
	 * }
	 * }
	 */

	// Slime handlers

	static
	{
		/*
		 * registerHandler( new Handler( EnumSlime.GREEN_SLIME.name, new ResourceLocation( EnumSlime.GREEN_SLIME.getUnlocalizedName() ) )
		 * {
		 *
		 * @Override
		 * public int foodAmount()
		 * {
		 * return 3;
		 * }
		 *
		 * @Override
		 * public float saturationAmount()
		 * {
		 * return 0.2F;
		 * }
		 * } );
		 */
		/*
		 * registerHandler( new Handler( EnumSlime.MAGMA_CUBE.name, new ResourceLocation( EnumSlime.MAGMA_CUBE.getUnlocalizedName() ) )
		 * {
		 *
		 * @Override
		 * public float durationMultiplier()
		 * {
		 * return 0.4F;
		 * }
		 *
		 * @Override
		 * public void onEaten( EntityPlayer player, boolean potionEffects )
		 * {
		 * player.setFire( 2 );
		 * player.addPotionEffect( new PotionEffect( Potion.getPotionFromResourceLocation( "jump_boost" ), ( potionEffects ? 10 : 20 ) * 20,
		 * potionEffects ? 2 : 3 ) );
		 * player.addPotionEffect( new PotionEffect( Potion.getPotionFromResourceLocation( "strength" ), ( potionEffects ? 24 : 32 ) * 20, 0 ) );
		 * }
		 * } );
		 */
		/*
		 * registerHandler( new Handler( EnumSlime.MAZE_SLIME.name, "TwilightForest.Maze Slime" )
		 * {
		 *
		 * @Override
		 * public void onEaten(EntityPlayer player, boolean potionEffects) {
		 * player.hurt(DamageSource.magic, 3.0F);
		 * player.addPotionEffect(new PotionEffect(Potion.jump.id, (potionEffects ? 4 : 8) * 20, 0));
		 * player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, (potionEffects ? 4 : 8) * 20, 1));
		 * player.addPotionEffect(new PotionEffect(Potion.resistance.id, 30 * 20, 1));
		 * }
		 * } );
		 */
		/*
		 * registerHandler( new Handler( EnumSlime.PINK_SLIME.name, "MineFactoryReloaded.mfrEntityPinkSlime" )
		 * {
		 *
		 * @Override
		 * public int foodAmount()
		 * {
		 * return 6;
		 * }
		 *
		 * @Override
		 * public float saturationAmount()
		 * {
		 * return 0.75F;
		 * }
		 *
		 * @Override
		 * public void onEaten(EntityPlayer player, boolean potionEffects) {
		 * super.onEaten(player, potionEffects);
		 * player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, (potionEffects ? 40 : 60) * 20, 4));
		 * }
		 *
		 * } );
		 */
		/*
		 * registerHandler( new Handler( EnumSlime.THAUM_SLIME.name, "Thaumcraft.ThaumSlime" )
		 * {
		 * private final int potionFluxId = -1;
		 *
		 * @Override
		 * public float durationMultiplier()
		 * {
		 * return 1.0F;
		 * }
		 *
		 * @Override
		 * public void onEaten(EntityPlayer player, boolean potionEffects) {
		 * if (potionFluxId == -1) {
		 * // Look for flux potion effect.
		 * for (Potion potion : Potion.potionTypes)
		 * if (potion != null)
		 * if (potion.getName() == "potion.fluxtaint") {
		 * potionFluxId = potion.id; break; }
		 * // If not found, just use wither.
		 * if (potionFluxId == -1)
		 * potionFluxId = Potion.wither.id;
		 * }
		 * super.onEaten(player, potionEffects);
		 * player.addPotionEffect(new PotionEffect(potionFluxId, 8 * 20, 0));
		 * }
		 *
		 * } );
		 */
		/*
		 * registerHandler( new Handler( EnumSlime.BLUE_SLIME.name, "TConstruct.EdibleSlime" )
		 * {
		 *
		 * @Override
		 * public float durationMultiplier()
		 * {
		 * return 0.2F;
		 * }
		 * } );
		 */
	}

	public static class Handler
	{

		public final String				name;
		// public final String entityName;
		public final ResourceLocation	resourceLocation;

		public Handler( String name, String entityName )
		{
			this.name = name;
			// this.entityName = entityName;
			resourceLocation = null;
		}

		public Handler( String name, ResourceLocation resource )
		{
			this.name = name;
			// entityName = EntityList.getTranslationName( resource );
			resourceLocation = resource;
		}

		/** Returns the size of the slime. */
		/*
		 * public int getSize( EntityLiving slime )
		 * {
		 * if( slime instanceof EntitySlime )
		 * return ( (EntitySlime)slime ).getSlimeSize();
		 * else
		 * return 0;
		 * }
		 */

		/** Sets the size of the slime. */
		/*
		 * public void setSize( EntityLiving slime, int size )
		 * {
		 * final NBTTagCompound compound = new NBTTagCompound();
		 *
		 * slime.writeToNBT( compound );
		 * compound.setInteger( "Size", size - 1 );
		 * slime.readFromNBT( compound );
		 *
		 * }
		 */

		/**
		 * How much food is restored when eating this slime.
		 *
		 * @return Amount
		 */
		public int foodAmount()
		{
			return 4;
		}

		/**
		 * The satuation amount added when eating this slime.
		 *
		 * @return Saturation
		 */
		public float saturationAmount()
		{
			return 0.3F;
		}

		/**
		 * Duration will get multiplied by this value.
		 *
		 * @return Multiplier
		 */
		public float durationMultiplier()
		{
			return 0.25F;
		}

		/**
		 * Called when this slime is eaten, allows adding effects to the player
		 * in addition to the potion effects of the slime itself.
		 */
		/*
		 * public void onEaten( EntityPlayer player, boolean potionEffects )
		 * {
		 * player.addPotionEffect( new PotionEffect( Potion.getPotionFromResourceLocation( "jump_boost" ), ( potionEffects ? 6 : 16 ) * 20, 1 ) );
		 * }
		 */

	}

	public enum EnumSlime implements StringRepresentable
	{
		//@formatter:off
		GREEN_SLIME	( 0, "Slime", new ResourceLocation("slime") ),
		MAGMA_CUBE	( 1, "LavaSlime", new ResourceLocation("magma_cube") );
		// MAZE_SLIME	( 2, "maze_slime" ),
		// PINK_SLIME	( 3, "pink_slime" ),
		// THAUM_SLIME	( 4, "thaumic_slime" ),
		// BLUE_SLIME	( 5, "blue_slime" );
		//@formatter:on

		private final int						meta;
		private final String					name;
		private final ResourceLocation			resourceLocation;

		private static EnumSlime[]				META_LOOKUP	= new EnumSlime[values().length];
		private static Map< String, EnumSlime >	NAME_LOOKUP	= new HashMap<>();

		static
		{
			for( final EnumSlime slime : values() )
			{
				META_LOOKUP[slime.getMetadata()] = slime;
				NAME_LOOKUP.put( slime.name, slime );
			}
		}

		private EnumSlime( int meta, String name, ResourceLocation resourceLocation )
		{
			this.meta = meta;
			this.name = name;
			this.resourceLocation = resourceLocation;
		}

		public static EnumSlime byMetadata( int meta )
		{
			if( meta < 0 || meta >= META_LOOKUP.length )
				meta = 0;
			return META_LOOKUP[meta];
		}

		public static EnumSlime byName( String name )
		{
			return NAME_LOOKUP.get( name );
		}

		@Override
		public String toString()
		{
			return name;
		}

		public String getName()
		{
			return name;
		}

		public ResourceLocation getResourceLocation()
		{
			return resourceLocation;
		}

		/*
		 * public String getUnlocalizedName()
		 * {
		 * return resourceLocation.getResourcePath();
		 * }
		 */

		public int getMetadata()
		{
			return meta;
		}

		@Override
		public String getSerializedName()
		{
			return getName();
		}
	}
}
