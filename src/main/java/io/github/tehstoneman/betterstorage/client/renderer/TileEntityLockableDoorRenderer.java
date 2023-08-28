package io.github.tehstoneman.betterstorage.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class TileEntityLockableDoorRenderer// extends BlockEntityRenderer< TileEntityLockableDoor >
{
	/*
	 * public TileEntityLockableDoorRenderer( BlockEntityRenderDispatcher rendererDispatcherIn )
	 * {
	 * super( rendererDispatcherIn );
	 * // TODO Auto-generated constructor stub
	 * }
	 * 
	 * // @Override
	 * 
	 * @SuppressWarnings( "deprecation" )
	 * public void render( TileEntityLockableDoor tileEntityDoor, double x, double y, double z, float partialTicks, int destroyStage )
	 * {
	 * final BlockState iblockstate = tileEntityDoor.hasLevel() ? tileEntityDoor.getBlockState()
	 * : BetterStorageBlocks.LOCKABLE_DOOR.get().defaultBlockState().setValue( DoorBlock.FACING, Direction.SOUTH );
	 * //final DoubleBlockHalf lockertype = iblockstate.has( DoorBlock.HALF ) ? iblockstate.get( DoorBlock.HALF ) : DoubleBlockHalf.LOWER;
	 * 
	 * if( lockertype == DoubleBlockHalf.UPPER )
	 * return;
	 * 
	 * GlStateManager._enableDepthTest();
	 * GlStateManager._depthFunc( 515 );
	 * GlStateManager._depthMask( true );
	 * 
	 * GlStateManager._color4f( 1.0F, 1.0F, 1.0F, 1.0F );
	 * 
	 * GlStateManager._pushMatrix();
	 * GlStateManager._enableRescaleNormal();
	 * GlStateManager._translatef( (float)x + 0.5f, (float)y + 1.0f, (float)z + 0.5f );
	 * 
	 * final float f = iblockstate.getValue( BlockLocker.FACING ).toYRot();
	 * if( Math.abs( f ) > 1.0E-5D )
	 * GlStateManager._rotatef( -f, 0.0F, 1.0F, 0.0F );
	 * 
	 * renderItem( tileEntityDoor, partialTicks, destroyStage, iblockstate );
	 * 
	 * GlStateManager._disableRescaleNormal();
	 * GlStateManager._popMatrix();
	 * GlStateManager._color4f( 1.0F, 1.0F, 1.0F, 1.0F );
	 * }
	 * 
	 *//** Renders attached lock on chest. Adapted from vanilla item frame **//*
																				 * @SuppressWarnings( "deprecation" )
																				 * private void renderItem( TileEntityLockableDoor tileEntityDoor, float
																				 * partialTicks, int destroyStage, BlockState state )
																				 * {
																				 * final ItemStack itemstack = tileEntityDoor.getLock();
																				 * 
																				 * if( !itemstack.isEmpty() )
																				 * {
																				 * //final ItemEntity ItemEntity = new ItemEntity( tileEntityDoor.getLevel(),
																				 * 0.0D, 0.0D, 0.0D, itemstack );
																				 * GlStateManager._pushMatrix();
																				 * GlStateManager._disableLighting();
																				 * 
																				 * //final float openAngle = state.get( DoorBlock.OPEN ) ? 1.0f : 0.0f;
																				 * 
																				 * //final ItemRenderer itemRenderer =
																				 * Minecraft.getInstance().getItemRenderer();
																				 * final boolean left = state.getValue( DoorBlock.HINGE ) == DoorHingeSide.LEFT;
																				 * final boolean open = state.getValue( DoorBlock.OPEN );
																				 * 
																				 * // if( open ) GlStateManager.rotated( left ? 90 : -90, 0.0, 1.0, 0.0 );
																				 * GlStateManager._translated( left ? -0.25 : 0.25, -0.0625, open ? 0.280875 :
																				 * -0.531625 );
																				 * 
																				 * GlStateManager._scaled( 0.5, 0.5, 0.5 );
																				 * 
																				 * RenderHelper.turnOff();
																				 * // itemRenderer.renderItem( ItemEntity.getItem(),
																				 * ItemCameraTransforms.TransformType.FIXED );
																				 * RenderHelper.turnBackOn();
																				 * 
																				 * GlStateManager._enableLighting();
																				 * GlStateManager._popMatrix();
																				 * }
																				 * }
																				 * 
																				 * @Override
																				 * public void render( TileEntityLockableDoor tileEntityIn, float partialTicks,
																				 * PoseStack matrixStackIn, MultiBufferSource bufferIn,
																				 * int combinedLightIn, int combinedOverlayIn )
																				 * {
																				 * // TODO Auto-generated method stub
																				 * 
																				 * }
																				 */
}
