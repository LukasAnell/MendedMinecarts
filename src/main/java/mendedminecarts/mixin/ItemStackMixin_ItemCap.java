package mendedminecarts.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin_ItemCap {

    @ModifyConstant(
        method = "method_57371",
        constant = @Constant(intValue = 99)
    )
    private static int liftCountCodecLimit(int original) {
        return Integer.MAX_VALUE;
    }
}
