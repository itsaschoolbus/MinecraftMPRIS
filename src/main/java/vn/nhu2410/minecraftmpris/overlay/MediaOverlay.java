package vn.nhu2410.minecraftmpris.overlay;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import vn.nhu2410.minecraftmpris.MinecraftMprisClient;
import vn.nhu2410.minecraftmpris.metadata.MetadataHandler;
import vn.nhu2410.minecraftmpris.metadata.MetadataTransformer;

public class MediaOverlay {
    private static String lastArtUrl = null;

    public static void registerMediaOverlay() {
        Identifier overlayId = Identifier.fromNamespaceAndPath(
            MinecraftMprisClient.MOD_ID,
            "music_overlay"
        );
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            overlayId,
            (gui, delta) -> renderOverlay(gui, delta)
        );
    }

    private static void renderOverlay(GuiGraphics gui, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null ||
            mc.player == null ||
            mc.options.hideGui ||
            mc.getDebugOverlay().showDebugScreen() ||
            mc.screen != null
        ) return;

        if (MetadataHandler.title == null ||
            MetadataHandler.title.isEmpty() ||
            MetadataHandler.length <= 0
        ) return;

        // update album art if url changed
        if (MetadataHandler.artUrl != null && !MetadataHandler.artUrl.equals(lastArtUrl)) {
            MetadataTransformer.loadAlbumArt(mc, MetadataHandler.artUrl);
            lastArtUrl = MetadataHandler.artUrl;
        }

        int width = mc.getWindow().getGuiScaledWidth();

        int albumArtSize = 48;
        int boxWidth = 200;
        int boxHeight = 58;

        int x = width - boxWidth - 10;
        int y = 10;

        // background
        gui.fill(x, y, x + boxWidth, y + boxHeight, 0x88000000);

        // album art
        if (MetadataTransformer.albumArtTexture != null && MetadataTransformer.albumArtId != null) {
            gui.blit(
                RenderPipelines.GUI_TEXTURED,
                MetadataTransformer.albumArtId,
                x + 5, y + 5,
                0, 0,
                albumArtSize, albumArtSize,
                albumArtSize, albumArtSize
            );
        } else {
            gui.fill(x + 5, y + 5, x + 5 + albumArtSize, y + 5 + albumArtSize, 0xFF333333);
        }

        // text after album art
        int textX = x + albumArtSize + 12;
        int textWidth = boxWidth - albumArtSize - 17;

        String displayTitle = truncateText(mc, MetadataHandler.title, textWidth);
        String displayArtist = truncateText(mc, MetadataHandler.artist, textWidth);

        // text
        gui.drawString(mc.font, displayTitle, textX, y + 8, 0xFFFFFFFF, true);
        gui.drawString(mc.font, displayArtist, textX, y + 20, 0xFFAAAAAA, true);

        // progress bar
        int barStart = textX;
        int barEnd = x + boxWidth - 5;
        int barWidth = barEnd - barStart;

        float progress = 0f;
        if (MetadataHandler.length > 0 && MetadataHandler.position >= 0) {
            progress = Math.min(1f, (float) MetadataHandler.position / (float) MetadataHandler.length);
            if (Float.isNaN(progress)) progress = 0f;
            if (progress < 0) progress = 0;
        }

        int filled = (int) (barWidth * progress);

        // bar
        gui.fill(barStart, y + 35, barEnd, y + 39, 0xFF333333);
        if (filled > 0) {
            gui.fill(barStart, y + 35, barStart + filled, y + 39, 0xFF00DD00);
        }

        // time
        String timeStr = formatTime(MetadataHandler.position) + " / " + formatTime(MetadataHandler.length);
        gui.drawString(mc.font, timeStr, textX, y + 43, 0xFFCCCCCC, false);

        // playing indicator
        String playStatus = MetadataHandler.playing ? "▶" : "⏸";
        gui.drawString(mc.font, playStatus, x + boxWidth - 15, y + 43, 0xFFFFFFFF, false);
    }

    private static String truncateText(Minecraft mc, String text, int maxWidth) {
        if (mc.font.width(text) <= maxWidth) {
            return text;
        }

        String ellipsis = "...";
        int ellipsisWidth = mc.font.width(ellipsis);

        StringBuilder truncated = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String current = truncated.toString() + text.charAt(i);
            if (mc.font.width(current) + ellipsisWidth > maxWidth) {
                return truncated.toString() + ellipsis;
            }
            truncated.append(text.charAt(i));
        }

        return text;
    }

    private static String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", mins, secs);
    }
}
