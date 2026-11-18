package mendedminecarts.mixin;

import mendedminecarts.CartHelper;
import mendedminecarts.MendedMinecartsMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityChangeListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin_YawFix extends Entity {

    public AbstractMinecartEntityMixin_YawFix(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void setChangeListener(EntityChangeListener changeListener) {
        super.setChangeListener(changeListener);
        if (MendedMinecartsMod.ROTATE_CART_TO_RAIL.isEnabled()) {
            CartHelper.fixRotationToRail((AbstractMinecartEntity) (Object) this, this.getYaw(), this.getPitch());
        }
    }
}
