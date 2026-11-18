package mendedminecarts;

import net.minecraft.text.Text;

import java.util.List;

/**
 * Lets the minecart renderer pass the overlay lines from updateRenderState (which has the entity)
 * to render (which only has the render state). Implemented on MinecartEntityRenderState by a mixin.
 */
public interface MinecartRenderStateAccess {

    List<Text> getInfoTexts();

    void setInfoTexts(List<Text> infoTexts);
}
