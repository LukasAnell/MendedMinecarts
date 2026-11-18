package mendedminecarts.mixin;

import mendedminecarts.AbstractMinecartEntityAccess_Physics;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin_DerailFix implements AbstractMinecartEntityAccess_Physics {

    @Shadow protected abstract float getVelocityMultiplier();

    private boolean isMovingOnRail;

    @Override
    public boolean isSelfMovingOnRail() {
        return this.isMovingOnRail;
    }

    @Override
    public void setSelfMovingOnRail(boolean selfMoving) {
        this.isMovingOnRail = selfMoving;
    }

    @Override
    public float getCartVelocityMultiplier() {
        return this.getVelocityMultiplier();
    }
}
