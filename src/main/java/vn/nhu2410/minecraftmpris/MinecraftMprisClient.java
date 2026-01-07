package vn.nhu2410.minecraftmpris;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.nhu2410.minecraftmpris.control.MediaController;
import vn.nhu2410.minecraftmpris.metadata.MediaMetadataHandler;
import vn.nhu2410.minecraftmpris.overlay.MediaOverlay;

public class MinecraftMprisClient implements ClientModInitializer {
    public static final String MOD_ID = "minecraftmpris";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        // overlay
        MediaOverlay.registerMediaOverlay();

        // keybindings
        MediaController.registerKeyBindings();
        MediaController.handleKeyBindings();

        // metadata
        MediaMetadataHandler.refreshTrackInfo();

        LOGGER.info("Control media within Minecraft!");
    }
}
