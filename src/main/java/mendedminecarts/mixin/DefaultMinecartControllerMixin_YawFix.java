package mendedminecarts.mixin;

import mendedminecarts.CartHelper;
import mendedminecarts.MendedMinecartsMod;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.DefaultMinecartController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * After the server tick has computed the cart's yaw and pitch, snap them to the rail axis.
 */
@Mixin(DefaultMinecartController.class)
public abstract class DefaultMinecartControllerMixin_YawFix {

    @Shadow
    @Final
    protected AbstractMinecartEntity minecart;

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/vehicle/DefaultMinecartController;handleCollision()Z"
        )
    )
    private void snapRotationToRail(CallbackInfo ci) {
        if (MendedMinecartsMod.ROTATE_CART_TO_RAIL.isEnabled()) {
            CartHelper.fixRotationToRail(
                this.minecart,
                this.minecart.getYaw(),
                this.minecart.getPitch()
            );
        }
    }
}
