package mendedminecarts.mixin;

import mendedminecarts.AbstractMinecartEntityAccess;
import mendedminecarts.MendedMinecartsMod;
import mendedminecarts.MinecartDisplayData;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperMinecartEntity.class)
public abstract class HopperMinecartEntityMixin extends AbstractMinecartEntity {

    protected HopperMinecartEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
        method = "getDefaultContainedBlock",
        at = @At("RETURN"),
        cancellable = true
    )
    private void displayLockedHopperWhenLocked(
        CallbackInfoReturnable<BlockState> cir
    ) {
        if (
            MendedMinecartsMod.VISUAL_HOPPER_CART_LOCKING.isEnabled() &&
            this instanceof AbstractMinecartEntityAccess entityAccess
        ) {
            MinecartDisplayData displayInfo = entityAccess.getDisplayInfo();
            if (displayInfo == null) {
                return;
            }
            if (displayInfo.hopperLocked()) {
                BlockState disabledHopper = cir.getReturnValue();
                disabledHopper = disabledHopper.withIfExists(
                    HopperBlock.ENABLED,
                    false
                );
                cir.setReturnValue(disabledHopper);
            }
        }
    }

    @Override
    public int getBlockOffset() {
        int offset = super.getBlockOffset();

        if (
            MendedMinecartsMod.VISUAL_HOPPER_CART_LOCKING.isEnabled() &&
            this instanceof AbstractMinecartEntityAccess entityAccess
        ) {
            MinecartDisplayData displayInfo = entityAccess.getDisplayInfo();

            if (displayInfo != null && displayInfo.hopperLocked()) {
                return -1;
            }
        }

        return offset;
    }
}
