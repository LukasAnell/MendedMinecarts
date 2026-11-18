package mendedminecarts;

import mendedminecarts.settings.SettingSync;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class MendedMinecartsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SettingSync.ID, (payload, context) -> SettingSync.handleData(payload));
    }
}
