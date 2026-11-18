package mendedminecarts;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.util.math.Direction;

public class CartHelper {


    public static double getMaxSpeed(AbstractMinecartEntity entity) {
        double maxSpeed;
        if (entity instanceof FurnaceMinecartEntity) {
            maxSpeed = (entity.isTouchingWater() ? 3.0 : 4.0) / 20.0;
        } else {
            maxSpeed = (entity.isTouchingWater() ? 4.0 : 8.0) / 20.0;
        }
        if (MendedMinecartsMod.CART_SPEED.isDefault()) {
            return maxSpeed;
        }
        return maxSpeed * (MendedMinecartsMod.CART_SPEED.getState() / (8d / 20d));
    }

    /**
     * Snap the cart rotation to the axis of the rail it's on.
     * @return true if the rotation was set
     */
    public static boolean fixRotationToRail(AbstractMinecartEntity cart, float yaw, float pitch) {
        BlockState blockState = cart.getEntityWorld().getBlockState(cart.getBlockPos());

        if (blockState.getBlock() instanceof AbstractRailBlock railBlock) {
            RailShape railShape = blockState.get(railBlock.getShapeProperty());
            Direction.Axis railAxis = RailHitboxHelper.getRailAxis(railShape);

            if (railAxis != null) {
                int yawRounded90 = (Math.round(yaw / 90f) & 3);

                if (yawRounded90 == 0 || yawRounded90 == 2) {
                    if (railAxis == Direction.Axis.Z) {
                        yawRounded90++;
                    }
                } else {
                    if (railAxis == Direction.Axis.X) {
                        yawRounded90--;
                    }
                }

                yaw = yawRounded90 * 90f;

                // same as Entity.setRotation
                cart.setYaw(yaw % 360.0F);
                cart.setPitch(pitch % 360.0F);

                return true;
            }
        }

        return false;
    }
}
