package mendedminecarts.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import mendedminecarts.MendedMinecartsMod;
import net.minecraft.entity.vehicle.DefaultMinecartController;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * CartSpeed for the default minecart controller.
 * Speed cap is 8 blocks/sec.
 */
@Mixin(DefaultMinecartController.class)
public abstract class DefaultMinecartControllerMixin_ConfiguredSpeed {

    private static double mendedminecarts$speedFactor() {
        return MendedMinecartsMod.CART_SPEED.getState() / (8d / 20d);
    }

    @ModifyReturnValue(method = "getMaxSpeed", at = @At("RETURN"))
    private double scaleMaxSpeed(double original) {
        if (MendedMinecartsMod.CART_SPEED.isDefault()) {
            return original;
        }
        return original * mendedminecarts$speedFactor();
    }

    @ModifyConstant(
        method = "limitSpeed",
        constant = @Constant(doubleValue = 0.4)
    )
    private double scaleLimitMax(double original) {
        if (MendedMinecartsMod.CART_SPEED.isDefault()) {
            return original;
        }
        return original * mendedminecarts$speedFactor();
    }

    @ModifyConstant(
        method = "limitSpeed",
        constant = @Constant(doubleValue = -0.4)
    )
    private double scaleLimitMin(double original) {
        if (MendedMinecartsMod.CART_SPEED.isDefault()) {
            return original;
        }
        return original * mendedminecarts$speedFactor();
    }

    @ModifyConstant(
        method = "moveOnRail",
        constant = @Constant(doubleValue = 2.0, ordinal = 0)
    )
    private double scaleSpeedLimit(double original) {
        if (MendedMinecartsMod.CART_SPEED.isDefault()) {
            return original;
        }
        return original * mendedminecarts$speedFactor();
    }

    @ModifyVariable(method = "moveOnRail", at = @At("STORE"), ordinal = 0)
    private int clampXDistance(int x, @Local BlockPos pos) {
        if (MendedMinecartsMod.CART_SPEED.isDefault()) {
            return x;
        }
        return pos.getX() + MathHelper.clamp(x - pos.getX(), -1, 1);
    }

    @ModifyVariable(method = "moveOnRail", at = @At("STORE"), ordinal = 1)
    private int clampZDistance(int z, @Local BlockPos pos) {
        if (MendedMinecartsMod.CART_SPEED.isDefault()) {
            return z;
        }
        return pos.getZ() + MathHelper.clamp(z - pos.getZ(), -1, 1);
    }
}
