package vn.nhu2410.minecraftmpris.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import vn.nhu2410.minecraftmpris.MinecraftMprisClient;
import vn.nhu2410.minecraftmpris.keybind.KeybindRegistry;

public class ConfigHandler {
    public static ConfigClassHandler<ConfigOptions> HANDLER = ConfigClassHandler.createBuilder(ConfigHandler.ConfigOptions.class)
            .id(Identifier.fromNamespaceAndPath(MinecraftMprisClient.MOD_ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("minecraftmpris.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    public enum AnchorPoints {
        TOP_LEFT, TOP_RIGHT,
        BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public static class ConfigOptions {
        // position
        @SerialEntry
        public AnchorPoints anchor = DefaultConfigOptions.anchor;
        @SerialEntry
        public int xOffset = DefaultConfigOptions.xOffset;
        @SerialEntry
        public int yOffset = DefaultConfigOptions.yOffset;

        // appearance
        @SerialEntry
        public boolean showBackground = DefaultConfigOptions.showBackground;
        @SerialEntry
        public int overlayWidth = DefaultConfigOptions.overlayWidth;
        @SerialEntry
        public int overlayHeight = DefaultConfigOptions.overlayHeight;

        // content
        @SerialEntry
        public int albumArtSize = DefaultConfigOptions.albumArtSize;
        @SerialEntry
        public boolean showAlbumArt = DefaultConfigOptions.showAlbumArt;
        @SerialEntry
        public boolean showArtist = DefaultConfigOptions.showArtist;
        @SerialEntry
        public boolean showCurrentTime = DefaultConfigOptions.showCurrentTime;
        @SerialEntry
        public boolean showPlayingIndicator = DefaultConfigOptions.showPlayingIndicator;
        @SerialEntry
        public boolean showProgressBar = DefaultConfigOptions.showProgressBar;
        @SerialEntry
        public boolean showTextShadow = DefaultConfigOptions.showTextShadow;
    }

    public static class DefaultConfigOptions {
        // position
        public static final AnchorPoints anchor = AnchorPoints.TOP_RIGHT;
        public static final int xOffset = 10;
        public static final int yOffset = 10;

        // appearance
        public static final boolean showBackground = true;
        public static final int overlayWidth = 200;
        public static final int overlayHeight = 58;

        // content
        public static final int albumArtSize = 48;
        public static final boolean showAlbumArt = true;
        public static final boolean showArtist = true;
        public static final boolean showCurrentTime = true;
        public static final boolean showPlayingIndicator = true;
        public static final boolean showProgressBar = true;
        public static final boolean showTextShadow = true;
    }

    public static void handleKeybinds() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (KeybindRegistry.configKey.consumeClick() && client.screen == null) {
                client.setScreen(ConfigScreen.configScreen(Minecraft.getInstance().screen));
            }
        });
    }
}
