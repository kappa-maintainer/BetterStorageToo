package io.github.tehstoneman.betterstorage.client.renderer.blockentity;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BetterStorageBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer< T >
{
	protected static ModelManager						modelManager;
	protected static ModelBlockRenderer					modelRenderer;
	protected final BlockEntityRendererProvider.Context	context;

	protected BetterStorageBlockEntityRenderer( BlockEntityRendererProvider.Context context )
	{
		this.context = context;
	}
}
