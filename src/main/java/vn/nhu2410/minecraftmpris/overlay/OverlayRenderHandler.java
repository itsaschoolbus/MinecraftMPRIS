package vn.nhu2410.minecraftmpris.overlay;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import vn.nhu2410.minecraftmpris.metadata.MetadataHandler;

public class OverlayRenderHandler {
    private static MediaOverlay mediaOverlay = new MediaOverlay();

    public static void registerMediaOverlay() {
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            render(context, tickDelta);
        });
    }

    private static void render(DrawContext context, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();

        MetadataHandler.refreshMediaInfo(mc);

        if (mc == null ||
            mc.player == null ||
            mc.options.hudHidden ||
            mc.options.debugEnabled ||
            mc.currentScreen != null
        ) return;

        mediaOverlay.renderMediaOverlay(context, mc);
    }
}
