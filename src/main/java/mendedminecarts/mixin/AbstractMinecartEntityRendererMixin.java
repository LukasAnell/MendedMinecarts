package mendedminecarts.mixin;

import mendedminecarts.AbstractMinecartEntityAccess;
import mendedminecarts.MendedMinecartsMod;
import mendedminecarts.MinecartDisplayData;
import mendedminecarts.MinecartRenderStateAccess;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Draws the DisplayCartData text lines above minecarts.
 * Since 1.21.2 renderers work on a render state: the lines are collected in updateRenderState (which
 * has the entity) and submitted as labels in render.
 */
@Mixin(AbstractMinecartEntityRenderer.class)
public abstract class AbstractMinecartEntityRendererMixin {

    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;Lnet/minecraft/client/render/entity/state/MinecartEntityRenderState;F)V",
            at = @At("RETURN")
    )
    private void collectInfo(AbstractMinecartEntity entity, MinecartEntityRenderState state, float tickProgress, CallbackInfo ci) {
        List<Text> infoTexts = List.of();
        if (MendedMinecartsMod.DISPLAY_CART_DATA.isEnabled()
                && entity instanceof AbstractMinecartEntityAccess entityAccess
                && state.squaredDistanceToCamera <= MendedMinecartsMod.DATA_RENDER_DISTANCE_SQ) {
            MinecartDisplayData displayInfo = entityAccess.getDisplayInfo();
            if (displayInfo != null) {
                infoTexts = displayInfo.getInfoTexts();
            }
        }
        ((MinecartRenderStateAccess) state).setInfoTexts(infoTexts);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/render/entity/state/MinecartEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At("RETURN")
    )
    private void renderInfo(MinecartEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState, CallbackInfo ci) {
        List<Text> infoTexts = ((MinecartRenderStateAccess) state).getInfoTexts();
        if (infoTexts.isEmpty()) {
            return;
        }
        Vec3d labelPos = new Vec3d(0.0, state.height + 0.5, 0.0);
        // lines are 10 label pixels apart; the first line is at the top
        int y = 10 - infoTexts.size() * 10;
        for (Text infoText : infoTexts) {
            queue.submitLabel(matrices, labelPos, y, infoText, true, state.light, state.squaredDistanceToCamera, cameraState);
            y += 10;
        }
    }
}
