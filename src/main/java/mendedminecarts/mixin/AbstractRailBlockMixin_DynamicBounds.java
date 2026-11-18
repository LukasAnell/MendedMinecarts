package mendedminecarts.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRailBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Marks all rail blocks (rail, powered, detector, activator) as having dynamic bounds, which the
 * DerailingCartFix collision walls need. Since 1.21.2 rails are created through Blocks.register with
 * constructor references, so the Settings are patched where every rail passes them up to Block.
 */
@Mixin(AbstractRailBlock.class)
public class AbstractRailBlockMixin_DynamicBounds {

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"
            )
    )
    private static AbstractBlock.Settings makeBoundsDynamic(AbstractBlock.Settings settings) {
        ((AbstractBlockSettingAccessor) settings).setDynamicBounds(true);
        return settings;
    }
}
