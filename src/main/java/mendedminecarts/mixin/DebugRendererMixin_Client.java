package mendedminecarts.mixin;

import mendedminecarts.AbstractMinecartEntityAccess;
import mendedminecarts.MendedMinecartsMod;
import mendedminecarts.MinecartDisplayData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws the DisplayCartData boxes. Entity rendering no longer has a per-entity immediate-mode hook
 * (renderEntity is gone since 1.21.9), so the boxes are drawn from the debug renderer pass, which
 * gets the buffers and the camera position. Same camera-relative coordinates as the old hook.
 */
@Mixin(DebugRenderer.class)
public abstract class DebugRendererMixin_Client {

    @Inject(method = "render", at = @At("RETURN"))
    private void renderMinecartBoxes(MatrixStack matrices, Frustum frustum, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, boolean lateDebug, CallbackInfo ci) {
        if (lateDebug) {
            return;
        }
        if (!(MendedMinecartsMod.DISPLAY_CART_DATA_BOX.isEnabled() ||
                MendedMinecartsMod.DISPLAY_CART_DATA_HOPPER_PICKUP_VOLUME.isEnabled() ||
                MendedMinecartsMod.DISPLAY_CART_DATA_HOPPER_EXTRACT_VOLUME.isEnabled() ||
                MendedMinecartsMod.DISPLAY_CART_DATA_HOPPER_EXTRACT_BLOCK.isEnabled() ||
                MendedMinecartsMod.DISPLAY_CART_DATA_ENTITY_PICKUP_VOLUME.isEnabled())) {
            return;
        }
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) {
            return;
        }

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getLines());
        matrices.push();
        matrices.translate(-cameraX, -cameraY, -cameraZ);
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof AbstractMinecartEntityAccess entityAccess)) {
                continue;
            }
            if (entity.squaredDistanceTo(cameraX, cameraY, cameraZ) > MendedMinecartsMod.DATA_RENDER_DISTANCE_SQ) {
                continue;
            }
            MinecartDisplayData displayInfo = entityAccess.getDisplayInfo();
            if (displayInfo != null) {
                this.drawBoxes(matrices, buffer, entity, displayInfo);
            }
        }
        matrices.pop();
    }

    private void drawBoxes(MatrixStack matrices, VertexConsumer buffer, Entity entity, MinecartDisplayData displayInfo) {
        if (MendedMinecartsMod.DISPLAY_CART_DATA_BOX.isEnabled()) {
            Box box = displayInfo.boundingBox();
            if (box != null) {
                VertexRendering.drawBox(matrices.peek(), buffer, box, 0.5f, 0.5f, 1.0f, 1.0f);
            }
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_ENTITY_PICKUP_VOLUME.isEnabled() && entity.getType() == EntityType.MINECART) {
            Box box = displayInfo.boundingBox().expand(0.2f, 0.0, 0.2f);
            VertexRendering.drawBox(matrices.peek(), buffer, box, 0.7f, 0.3f, 0.3f, 1.0f);
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_HOPPER_PICKUP_VOLUME.isEnabled()) {
            Box pickupArea2 = displayInfo.hopperPickupArea2();
            if (pickupArea2 != null) {
                VertexRendering.drawBox(matrices.peek(), buffer, pickupArea2, 0.7f, 0.3f, 0.3f, 1.0f);
            }
            for (Object pickupArea : displayInfo.hopperPickupArea1()) {
                if (pickupArea instanceof Box box) {
                    VertexRendering.drawBox(matrices.peek(), buffer, box, 0.7f, 0.3f, 0.3f, 1.0f);
                }
            }
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_HOPPER_EXTRACT_BLOCK.isEnabled()) {
            BlockPos pos = displayInfo.hopperExtractBlock();
            if (pos != null) {
                VertexRendering.drawBox(matrices.peek(), buffer, new Box(pos), 0.3f, 0.7f, 0.3f, 1.0f);
            }
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_HOPPER_EXTRACT_VOLUME.isEnabled()) {
            Box box = displayInfo.hopperExtractBox();
            if (box != null) {
                VertexRendering.drawBox(matrices.peek(), buffer, box, 0.3f, 0.3f, 0.7f, 1.0f);
            }
        }
    }
}
