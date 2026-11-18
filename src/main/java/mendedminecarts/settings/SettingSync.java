package mendedminecarts.settings;

import mendedminecarts.MendedMinecartsMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SettingSync {
    public static final Identifier CHANNEL = Identifier.of("mendedminecarts", "settings");
    public static final CustomPayload.Id<MendedMinecartsSettingPayload> ID = new CustomPayload.Id<>(CHANNEL);
    public static final PacketCodec<RegistryByteBuf, MendedMinecartsSettingPayload> CODEC =
            PacketCodec.of(MendedMinecartsSettingPayload::write, MendedMinecartsSettingPayload::new);
    public static PlayerManager PLAYER_MANAGER = null;

    /** Registers the payload type. Must run on both client and server (called from the mod initializer). */
    public static void registerPayload() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }

    private static void send(ServerPlayerEntity player, MendedMinecartsSettingPayload payload) {
        // players without the mod can't receive it
        if (ServerPlayNetworking.canSend(player, ID)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void handleData(MendedMinecartsSettingPayload payload) {
        try {
            payload.setting.setFromStringValue(payload.value);
        } catch (Exception ignored) {

        }
    }

    public record MendedMinecartsSettingPayload(Setting setting, String value) implements CustomPayload {

        public MendedMinecartsSettingPayload(Setting setting) {
            this(setting, setting.getStringValue());
        }

        public MendedMinecartsSettingPayload(PacketByteBuf buf) {
            this(checkVersionAndGetSetting(buf), buf.readString());
        }

        private static Setting checkVersionAndGetSetting(PacketByteBuf buf) {
            if (buf.readInt() != MendedMinecartsMod.SETTING_VERSION) {
                throw new IllegalArgumentException("Invalid setting version");
            }
            return MendedMinecartsMod.FLAT_SETTINGS.get(buf.readInt());
        }

        public void write(PacketByteBuf buf) {
            buf.writeInt(MendedMinecartsMod.SETTING_VERSION);
            buf.writeInt(MendedMinecartsMod.FLAT_SETTINGS.indexOf(setting));
            buf.writeString(value);
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static MendedMinecartsSettingPayload makeSettingPacket(Setting setting) {
        return new MendedMinecartsSettingPayload(setting);
    }

    public static void updateAllToPlayer(ServerPlayerEntity player) {
        for (Setting setting : MendedMinecartsMod.FLAT_SETTINGS) {
            MendedMinecartsSettingPayload payload = makeSettingPacket(setting);
            send(player, payload);
        }
    }

    public static void updateToClients(Setting setting) {
        PlayerManager playerManager = PLAYER_MANAGER;
        if (playerManager == null) {
            return;
        }
        MendedMinecartsSettingPayload payload = makeSettingPacket(setting);
        for (ServerPlayerEntity player : playerManager.getPlayerList()) {
            send(player, payload);
        }
    }
}
