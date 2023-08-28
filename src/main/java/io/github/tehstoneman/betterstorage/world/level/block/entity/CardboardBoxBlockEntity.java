package io.github.tehstoneman.betterstorage.world.level.block.entity;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.config.BetterStorageConfig;
import io.github.tehstoneman.betterstorage.world.inventory.CardboardBoxMenu;
import io.github.tehstoneman.betterstorage.world.item.cardboard.BlockItemCardboardBox;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CardboardBoxBlockEntity extends ContainerBlockEntity
{
	public CardboardBoxBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		//TODO Auto-generated constructor stub
	}

	public int		uses		= BlockItemCardboardBox.getMaxUses();
	public boolean	destroyed	= false;
	public int		color		= -1;

	//public CardboardBoxBlockEntity( BlockPos blockPos, BlockState blockState )
	//{
		//super( BetterStorageBlockEntityTypes.CARDBOARD_BOX.get(), blockPos, blockState );
	//}

	@Override
	public AbstractContainerMenu createMenu( int windowID, Inventory playerInventory, Player player )
	{
		return new CardboardBoxMenu( windowID, playerInventory, level, worldPosition );
	}

	public int getColor()
	{
		if( color < 0 )
			return 0xA08060;
		return color;
	}

	@Override
	public Component getName()
	{
		return customName != null ? customName : Component.translatable( ModInfo.CONTAINER_CARDBOARD_BOX_NAME );
	}

	@Override
	public int getRows()
	{
		return BetterStorageConfig.COMMON.cardboardBoxRows.get();
	}

	@Override
	public CompoundTag getUpdateTag()
	{
		final var nbt = super.getUpdateTag();

		if( BlockItemCardboardBox.getMaxUses() > 0 )
			nbt.putInt( "Uses", uses );
		if( color >= 0 )
			nbt.putInt( "Color", color );

		return nbt;
	}

	/*
	 * =================== BlockEntityContainer ===================
	 */

	public int getUses()
	{
		return uses;
	}

	@Override
	public void handleUpdateTag( CompoundTag nbt )
	{
		super.handleUpdateTag( nbt );

		uses	= nbt.contains( "Uses" ) ? nbt.getInt( "Uses" ) : BlockItemCardboardBox.getMaxUses();
		color	= nbt.contains( "Color" ) ? nbt.getInt( "Color" ) : -1;
	}

	public boolean isEmpty()
	{
		return inventory.isEmpty();
	}

	/*
	 * ========================== BlockEntity synchronization ==========================
	 */

	@Override
	public void load( CompoundTag nbt )
	{
		uses	= nbt.contains( "Uses" ) ? nbt.getInt( "Uses" ) : BlockItemCardboardBox.getMaxUses();
		color	= nbt.contains( "Color" ) ? nbt.getInt( "Color" ) : -1;

		super.load( nbt );
	}

	@Override
	public void saveAdditional( CompoundTag nbt )
	{
		if( BlockItemCardboardBox.getMaxUses() > 0 )
			nbt.putInt( "Uses", uses );
		if( color >= 0 )
			nbt.putInt( "Color", color );
		super.saveAdditional( nbt );
	}

	public void setColor( int color )
	{
		this.color = color;
		setChanged();
	}

	public void setUses( int uses )
	{
		this.uses = uses;
		setChanged();
	}
}
