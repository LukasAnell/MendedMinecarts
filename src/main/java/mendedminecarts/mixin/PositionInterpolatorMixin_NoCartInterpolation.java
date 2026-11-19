package mendedminecarts.mixin;

import mendedminecarts.MendedMinecartsMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PositionInterpolator.class)
public abstract class PositionInterpolatorMixin_NoCartInterpolation {

    @Shadow
    @Final
    private Entity entity;

    @Shadow
    public abstract void clear();

    @Inject(
        method = "refreshPositionAndAngles",
        at = @At("HEAD"),
        cancellable = true
    )
    private void snapCartToServerPosition(
        Vec3d pos,
        float yaw,
        float pitch,
        CallbackInfo ci
    ) {
        if (
            MendedMinecartsMod.NO_CLIENT_CART_INTERPOLATION.isEnabled() &&
            this.entity instanceof AbstractMinecartEntity
        ) {
            this.entity.refreshPositionAndAngles(pos, yaw, pitch);
            this.clear();
            ci.cancel();
        }
    }
}
