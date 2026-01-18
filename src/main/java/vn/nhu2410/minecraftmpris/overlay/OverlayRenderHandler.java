package vn.nhu2410.minecraftmpris.overlay;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import vn.nhu2410.minecraftmpris.MinecraftMprisClient;
import vn.nhu2410.minecraftmpris.metadata.MetadataHandler;

public class OverlayRenderHandler {
    private static MediaOverlay mediaOverlay = new MediaOverlay();

    public static void registerMediaOverlay() {
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath(MinecraftMprisClient.MOD_ID, "media_overlay"),
            OverlayRenderHandler::render
        );
    }

    private static void render(GuiGraphics gui, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();

        MetadataHandler.refreshMediaInfo(mc);

        if (mc == null ||
            mc.player == null ||
            mc.options.hideGui ||
            mc.getDebugOverlay().showDebugScreen() ||
            mc.screen != null
        ) return;

        mediaOverlay.renderMediaOverlay(gui, mc);
    }
}
