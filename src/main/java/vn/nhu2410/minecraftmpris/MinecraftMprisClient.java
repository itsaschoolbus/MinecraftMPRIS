package vn.nhu2410.minecraftmpris;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.nhu2410.minecraftmpris.config.ConfigHandler;
import vn.nhu2410.minecraftmpris.control.MediaControl;
import vn.nhu2410.minecraftmpris.keybind.KeybindRegistry;
import vn.nhu2410.minecraftmpris.overlay.OverlayRenderHandler;

public class MinecraftMprisClient implements ClientModInitializer {
    public static final String MOD_ID = "minecraftmpris";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        // config
        ConfigHandler.HANDLER.load();

        // keybinds
        KeybindRegistry.registerKeybinds();
        ConfigHandler.handleKeybinds();
        MediaControl.handleKeybinds();

        // overlay
        OverlayRenderHandler.registerMediaOverlay();

        LOGGER.info("Display and control media within Minecraft!");
    }
}
