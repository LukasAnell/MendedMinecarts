package mendedminecarts.mixin;

import mendedminecarts.MinecartRenderStateAccess;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(MinecartEntityRenderState.class)
public abstract class MinecartEntityRenderStateMixin implements MinecartRenderStateAccess {

    @Unique
    private List<Text> mendedminecarts$infoTexts = List.of();

    @Override
    public List<Text> getInfoTexts() {
        return this.mendedminecarts$infoTexts;
    }

    @Override
    public void setInfoTexts(List<Text> infoTexts) {
        this.mendedminecarts$infoTexts = infoTexts;
    }
}
