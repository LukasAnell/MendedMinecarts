package mendedminecarts.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mendedminecarts.AbstractMinecartEntityAccess_Physics;
import mendedminecarts.MendedMinecartsMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.DefaultMinecartController;
import net.minecraft.entity.vehicle.MinecartController;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mark when the cart is moving itself on a rail and restore the velocity lost when hitting a wall.
 */
@Mixin(DefaultMinecartController.class)
public abstract class DefaultMinecartControllerMixin_DerailFix
    extends MinecartController
{

    protected DefaultMinecartControllerMixin_DerailFix(
        AbstractMinecartEntity minecart
    ) {
        super(minecart);
    }

    @Inject(method = "moveOnRail", at = @At("HEAD"))
    private void moveUpdateCachedOnRail0(ServerWorld world, CallbackInfo ci) {
        (
            (AbstractMinecartEntityAccess_Physics) this.minecart
        ).setSelfMovingOnRail(true);
    }

    @Inject(
        method = "moveOnRail",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;applySlowdown(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            shift = At.Shift.BEFORE
        )
    )
    private void fixVelocityLoss(
        ServerWorld world,
        CallbackInfo ci,
        @Local(ordinal = 0) BlockPos previousPos,
        @Local(ordinal = 1) Vec3d previousVelocity
    ) {
        if (
            MendedMinecartsMod.DERAILING_CART_FIX.isEnabled() &&
            this.minecart.horizontalCollision
        ) {
            markPostOnlyCollision(world, previousVelocity);
        }

        if (this.minecart.getBlockPos().equals(previousPos)) {
            return;
        }

        float velocityMultiplier = (
            (AbstractMinecartEntityAccess_Physics) this.minecart
        ).getCartVelocityMultiplier();
        boolean hasHitWall = false;
        Vec3d velocity = this.minecart.getVelocity();

        if (velocity.x == 0 && Math.abs(previousVelocity.x) > 0.5) {
            velocity = velocity.withAxis(
                Direction.Axis.X,
                previousVelocity.x * velocityMultiplier
            );
            hasHitWall = true;
        }
        if (velocity.z == 0 && Math.abs(previousVelocity.z) > 0.5) {
            velocity = velocity.withAxis(
                Direction.Axis.Z,
                previousVelocity.z * velocityMultiplier
            );
            hasHitWall = true;
        }
        if (!hasHitWall) {
            return;
        }

        BlockState blockState = this.minecart
            .getEntityWorld()
            .getBlockState(this.minecart.getBlockPos());
        if (blockState.isOf(Blocks.RAIL)) {
            this.minecart.setVelocity(velocity);
        }
    }

    private void markPostOnlyCollision(
        ServerWorld world,
        Vec3d previousVelocity
    ) {
        Vec3d velocity = this.minecart.getVelocity();
        Box box = this.minecart.getBoundingBox();
        boolean postHit = false;
        boolean otherHit = false;

        if (velocity.x == 0 && Math.abs(previousVelocity.x) > 0.05) {
            if (isPostPosition(previousVelocity.x > 0 ? box.maxX : box.minX)) {
                postHit = true;
            } else {
                otherHit = true;
            }
        }

        if (velocity.z == 0 && Math.abs(previousVelocity.z) > 0.05) {
            if (isPostPosition(previousVelocity.z > 0 ? box.maxZ : box.minZ)) {
                postHit = true;
            } else {
                otherHit = true;
            }
        }

        if (postHit && !otherHit) {
            (
                (AbstractMinecartEntityAccess_Physics) this.minecart
            ).setLastPostCollisionTick(world.getTime());
        }
    }

    private static boolean isPostPosition(double face) {
        double fraction = face - Math.floor(face);
        return (
            Math.abs(fraction - 0.48) < 0.005 ||
            Math.abs(fraction - 0.52) < 0.005
        );
    }

    @Inject(method = "moveOnRail", at = @At("RETURN"))
    private void moveUpdateCachedOnRail1(ServerWorld world, CallbackInfo ci) {
        (
            (AbstractMinecartEntityAccess_Physics) this.minecart
        ).setSelfMovingOnRail(false);
    }
}
