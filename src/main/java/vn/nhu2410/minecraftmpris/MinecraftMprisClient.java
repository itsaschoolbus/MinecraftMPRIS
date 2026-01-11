package vn.nhu2410.minecraftmpris;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.nhu2410.minecraftmpris.control.MediaControlHandler;
import vn.nhu2410.minecraftmpris.metadata.MetadataHandler;
import vn.nhu2410.minecraftmpris.overlay.MediaOverlay;

public class MinecraftMprisClient implements ClientModInitializer {
    public static final String MOD_ID = "minecraftmpris";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        // keybindings
        MediaControlHandler.registerKeyBindings();
        MediaControlHandler.handleKeyBindings();

        // metadata
        MetadataHandler.refreshTrackInfo();

        // overlay
        MediaOverlay.registerMediaOverlay();

        LOGGER.info("Control media within Minecraft!");
    }
}
